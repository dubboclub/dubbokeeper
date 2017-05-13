package com.dubboclub.dk.storage.lucene;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.fastjson.JSON;
import com.dubboclub.dk.monitor.DubboKeeperMonitorService;
import com.dubboclub.dk.storage.StatisticsStorage;
import com.dubboclub.dk.storage.model.ApplicationInfo;
import com.dubboclub.dk.storage.model.BaseItem;
import com.dubboclub.dk.storage.model.ConcurrentItem;
import com.dubboclub.dk.storage.model.ElapsedItem;
import com.dubboclub.dk.storage.model.FaultItem;
import com.dubboclub.dk.storage.model.MethodMonitorOverview;
import com.dubboclub.dk.storage.model.ServiceInfo;
import com.dubboclub.dk.storage.model.Statistics;
import com.dubboclub.dk.storage.model.StatisticsOverview;
import com.dubboclub.dk.storage.model.SuccessItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.SearchGroup;
import org.apache.lucene.search.grouping.term.TermFirstPassGroupingCollector;
import org.apache.lucene.search.grouping.term.TermSecondPassGroupingCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * Created by bieber on 2015/9/25.
 */
public class LuceneStatisticsStorage implements StatisticsStorage,InitializingBean {

    private static final ConcurrentHashMap<String, Directory> LUCENE_DIRECTORY_MAP = new ConcurrentHashMap<String, Directory>();

    private static final ConcurrentHashMap<String, ApplicationIndexWriter> LUCENE_WRITER_MAP = new ConcurrentHashMap<String, ApplicationIndexWriter>();

    private static final int MAX_GROUP_SIZE=100000;

    private static final String LUCENE_COMMIT_FREQUENCY = "monitor.lucene.commit.frequency";

    private static final String LUCENE_MAX_SEGMENT = "monitor.lucene.max.segment";

    private static Logger logger = LoggerFactory.getLogger(LuceneStatisticsStorage.class);

    private Analyzer analyzer = new EnglishAnalyzer();

    private volatile boolean running = false;

    public LuceneStatisticsStorage() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.debug("shutdown lucene storage");
                running = false;
                Collection<ApplicationIndexWriter> writers = LUCENE_WRITER_MAP.values();
                for (ApplicationIndexWriter writer : writers) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        logger.error("failed to close index writer", e);
                    }
                }
                Collection<Directory> directories = LUCENE_DIRECTORY_MAP.values();
                for (Directory directory : directories) {
                    try {
                        directory.close();
                    } catch (IOException e) {
                        logger.error("failed to close directory", e);
                    }
                }
            }
        });
        running = true;
    }

    class ApplicationIndexWriter extends Thread{

        private String application;

        private ConcurrentLinkedQueue<Statistics> statisticses;

        private IndexWriter writer;

        private AtomicLong counter;

        private volatile long maxElapsed;

        private volatile long maxConcurrent;

        private volatile int maxFault;

        private volatile int maxSuccess;

        private volatile boolean running=false;



        public ApplicationIndexWriter(String application) throws IOException {
            this.application = application;
            statisticses = new ConcurrentLinkedQueue<Statistics>();
            running=true;
            init();
            this.setName(application+"-IndexWriter");
        }

        private void init() throws IOException {
            if(running){
                counter = new AtomicLong(getCommitFrequency());
                Directory directory = getDirectory(application);
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                writer = new IndexWriter(directory, config);
                long end = System.currentTimeMillis();
                long start = System.currentTimeMillis()-24*60*60*1000;
                maxConcurrent = Long.parseLong(queryMaxRecord(application, DubboKeeperMonitorService.CONCURRENT, SortField.Type.LONG,start,end));
                maxElapsed = Long.parseLong(queryMaxRecord(application, DubboKeeperMonitorService.ELAPSED, SortField.Type.LONG,start,end));
                maxFault = Integer.parseInt(queryMaxRecord(application, DubboKeeperMonitorService.FAILURE, SortField.Type.INT,start,end));
                maxSuccess = Integer.parseInt(queryMaxRecord(application,DubboKeeperMonitorService.SUCCESS, SortField.Type.INT,start,end));
            }
        }


        public void addDocument(Statistics statistics){
            statisticses.offer(statistics);
        }

        public void close() throws IOException {
            running=false;
            if(writer.isOpen()){
                writer.forceMerge(getMaxSegment());
                writer.close();
            }
        }

        @Override
        public void run() {
            while(running){
                try {
                    Statistics statistics = statisticses.poll();
                    if(statistics==null){//queue is empty
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            //do nothing
                        }
                        continue;
                    }
                    logger.debug("store statistics [" + JSON.toJSONString(statistics) + "]");
                    Document document = new Document();
                    if(maxFault<statistics.getFailureCount()){
                        maxFault=statistics.getFailureCount();
                    }
                    if(maxSuccess<statistics.getSuccessCount()){
                        maxSuccess=statistics.getSuccessCount();
                    }
                    if(maxConcurrent<statistics.getConcurrent()){
                        maxConcurrent = statistics.getConcurrent();
                    }
                    if(maxElapsed<statistics.getElapsed()){
                        maxElapsed = statistics.getElapsed();
                    }
                    document.add(new StoredAndSortStringField(DubboKeeperMonitorService.APPLICATION, statistics.getApplication()));
                    document.add(new StoredAndSortStringField(DubboKeeperMonitorService.INTERFACE, statistics.getServiceInterface()));
                    document.add(new StoredAndSortStringField(DubboKeeperMonitorService.METHOD, statistics.getMethod()));
                    document.add(new StoredAndSortStringField(DubboKeeperMonitorService.REMOTE_ADDRESS, statistics.getRemoteAddress()));
                    document.add(new StoredAndSortStringField(DubboKeeperMonitorService.REMOTE_TYPE, statistics.getRemoteType().toString()));
                    document.add(new StoredAndSortStringField(DubboKeeperMonitorService.APPLICATION_TYPE, statistics.getType().toString()));
                    document.add(new StoredAndSortStringField(DubboKeeperMonitorService.HOST_KEY, statistics.getHost()));
                    document.add(new StoredAndSortNumericField(DubboKeeperMonitorService.CONCURRENT, statistics.getConcurrent()));
                    document.add(new StoredAndSortNumericField(DubboKeeperMonitorService.ELAPSED, statistics.getElapsed()));
                    document.add(new StoredAndSortNumericField(DubboKeeperMonitorService.INPUT, statistics.getInput()));
                    document.add(new StoredAndSortNumericField(DubboKeeperMonitorService.OUTPUT, statistics.getOutput()));
                    document.add(new LongField(DubboKeeperMonitorService.TIMESTAMP, statistics.getTimestamp(), Field.Store.YES));
                    document.add(new StoredAndSortNumericField(DubboKeeperMonitorService.FAILURE,statistics.getFailureCount()));
                    document.add(new StoredAndSortNumericField(DubboKeeperMonitorService.SUCCESS,statistics.getSuccessCount()));
                    document.add(new StoredAndSortNumericField(DubboKeeperMonitorService.TPS,statistics.getTps()));
                    document.add(new StoredAndSortNumericField(DubboKeeperMonitorService.KBPS,statistics.getKbps()));
                    writer.addDocument(document);
                    long remain = counter.decrementAndGet();
                    if(remain==0&&running){
                        writer.forceMerge(getMaxSegment());
                        writer.commit();
                        counter = new AtomicLong(getCommitFrequency());
                        //init();
                    }
                } catch (IOException e) {
                    logger.error("Failed to add statistics to lucene.",e);
                }
            }
        }

        public String getApplication() {
            return application;
        }

        public long getMaxElapsed() {
            return maxElapsed;
        }


        public long getMaxConcurrent() {
            return maxConcurrent;
        }

        public int getMaxFault() {
            return maxFault;
        }

        public int getMaxSuccess() {
            return maxSuccess;
        }
    }

    @Override
    public void storeStatistics(Statistics statistics) {
        if (!running) {
            return;
        }
        try {
            if (!LUCENE_WRITER_MAP.containsKey(statistics.getApplication())) {
                ApplicationIndexWriter applicationIndexWriter= new ApplicationIndexWriter(statistics.getApplication());
                applicationIndexWriter =  LUCENE_WRITER_MAP.putIfAbsent(statistics.getApplication(), applicationIndexWriter);
                if(applicationIndexWriter==null){
                    LUCENE_WRITER_MAP.get(statistics.getApplication()).start();
                }
            }
            ApplicationIndexWriter indexWriter = LUCENE_WRITER_MAP.get(statistics.getApplication());
            indexWriter.addDocument(statistics);
        } catch (Exception e) {
            logger.error("failed to store statistics info", e);
        }
    }

    @Override
    public List<Statistics> queryStatisticsForMethod(String application, String serviceInterface, String method, long startTime, long endTime) {
        try {
            IndexSearcher searcher = generateSearcher(application);
            TermQuery applicationQuery = new TermQuery(new Term(DubboKeeperMonitorService.APPLICATION, new BytesRef(application)));
            TermQuery interfaceQuery = new TermQuery(new Term(DubboKeeperMonitorService.INTERFACE, new BytesRef(serviceInterface)));
            TermQuery methodQuery = new TermQuery(new Term(DubboKeeperMonitorService.METHOD, new BytesRef(method)));
            NumericRangeQuery<Long> timeQuery = NumericRangeQuery.newLongRange(DubboKeeperMonitorService.TIMESTAMP, startTime, endTime, true, true);
            BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
            queryBuilder.add(new BooleanClause(applicationQuery, BooleanClause.Occur.MUST));
            queryBuilder.add(new BooleanClause(interfaceQuery, BooleanClause.Occur.MUST));
            queryBuilder.add(new BooleanClause(methodQuery, BooleanClause.Occur.MUST));
            queryBuilder.add(new BooleanClause(timeQuery, BooleanClause.Occur.FILTER));
            TopDocs topDocs = searcher.search(queryBuilder.build(),Integer.MAX_VALUE);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            List<Statistics> statisticsList = new ArrayList<Statistics>();
            if (scoreDocs.length > 0) {
                for (int i = 0; i < scoreDocs.length; i++) {
                    Document document = searcher.doc(scoreDocs[i].doc);
                    statisticsList.add(parseDocToStatistics(document));
                }
                return statisticsList;
            }
        }catch(IndexNotFoundException e){
            //do nothing
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("failed to search for application " + application, e);
            return new ArrayList<Statistics>();
        }
        return new ArrayList<Statistics>();
    }

    private Statistics parseDocToStatistics(Document document) {
        Statistics statistics = new Statistics();
        statistics.setApplication(document.getBinaryValue(DubboKeeperMonitorService.APPLICATION).utf8ToString());
        statistics.setConcurrent(Long.parseLong(document.get(DubboKeeperMonitorService.CONCURRENT)));
        statistics.setElapsed(Long.parseLong(document.get(DubboKeeperMonitorService.ELAPSED)));
        statistics.setHost(document.getBinaryValue(DubboKeeperMonitorService.HOST_KEY).utf8ToString());
        statistics.setInput(Long.parseLong(document.get(DubboKeeperMonitorService.INPUT)));
        statistics.setOutput(Long.parseLong(document.get(DubboKeeperMonitorService.OUTPUT)));
        statistics.setRemoteAddress(document.getBinaryValue(DubboKeeperMonitorService.REMOTE_ADDRESS).utf8ToString());
        statistics.setRemoteType(Statistics.ApplicationType.valueOf(document.getBinaryValue(DubboKeeperMonitorService.REMOTE_TYPE).utf8ToString()));
        statistics.setTimestamp(Long.parseLong(document.get(DubboKeeperMonitorService.TIMESTAMP)));
        statistics.setServiceInterface(document.getBinaryValue(DubboKeeperMonitorService.INTERFACE).utf8ToString());
        statistics.setKbps(Double.parseDouble(document.get(DubboKeeperMonitorService.KBPS)));
        statistics.setTps(Double.parseDouble(document.get(DubboKeeperMonitorService.TPS)));
        statistics.setFailureCount(Integer.parseInt(document.get(DubboKeeperMonitorService.FAILURE)));
        statistics.setSuccessCount(Integer.parseInt(document.get(DubboKeeperMonitorService.SUCCESS)));
        statistics.setMethod(document.getBinaryValue(DubboKeeperMonitorService.METHOD).utf8ToString());
        statistics.setType(Statistics.ApplicationType.valueOf(document.getBinaryValue(DubboKeeperMonitorService.APPLICATION_TYPE).utf8ToString()));
        return statistics;
    }

    @Override
    public Collection<MethodMonitorOverview> queryMethodMonitorOverview(String application, String serviceInterface, int methodSize,long startTime,long endTime) {
        /*if(!LUCENE_DIRECTORY_MAP.containsKey(application)){
            return new ArrayList<MethodMonitorOverview>();
        }*/
        TermQuery applicationQuery = new TermQuery(new Term(DubboKeeperMonitorService.APPLICATION, new BytesRef(application)));
        TermQuery interfaceQuery = new TermQuery(new Term(DubboKeeperMonitorService.INTERFACE, new BytesRef(serviceInterface)));
        NumericRangeQuery<Long> timeQuery = NumericRangeQuery.newLongRange(DubboKeeperMonitorService.TIMESTAMP, startTime, endTime, true, true);
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.add(new BooleanClause(applicationQuery, BooleanClause.Occur.MUST));
        queryBuilder.add(new BooleanClause(interfaceQuery, BooleanClause.Occur.MUST));
        queryBuilder.add(new BooleanClause(timeQuery, BooleanClause.Occur.FILTER));
        Sort groupSort = new Sort();
        SortField groupSortField = new SortField(DubboKeeperMonitorService.METHOD, SortField.Type.STRING);
        groupSort.setSort(groupSortField);
        try {
            TermFirstPassGroupingCollector firstPassCollector = new TermFirstPassGroupingCollector(DubboKeeperMonitorService.METHOD, groupSort, methodSize==0?MAX_GROUP_SIZE:methodSize);
            IndexSearcher searcher = generateSearcher(application);
            Query query = queryBuilder.build();
            searcher.search(query, firstPassCollector);
            Collection<SearchGroup<BytesRef>> topSearchGroups = firstPassCollector.getTopGroups(0, false);
            if(topSearchGroups!=null){
                GroupDocs[] groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.ELAPSED, true, searcher, query, groupSort);
                Map<String,MethodMonitorOverview> methodMonitorOverviews = new HashMap<String,MethodMonitorOverview>();
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    MethodMonitorOverview methodMonitorOverview = new MethodMonitorOverview();
                    methodMonitorOverview.setMethod(method);
                    methodMonitorOverview.setMaxElapsed(Long.parseLong(searcher.doc(doc.doc).get(DubboKeeperMonitorService.ELAPSED)));
                    methodMonitorOverviews.put(method,methodMonitorOverview);
                }
                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.ELAPSED, false, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMinElapsed(Long.parseLong(searcher.doc(doc.doc).get(DubboKeeperMonitorService.ELAPSED)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.CONCURRENT, true, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMaxConcurrent(Long.parseLong(searcher.doc(doc.doc).get(DubboKeeperMonitorService.CONCURRENT)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.CONCURRENT, false, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMinConcurrent(Long.parseLong(searcher.doc(doc.doc).get(DubboKeeperMonitorService.CONCURRENT)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.INPUT, true, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMaxInput(Long.parseLong(searcher.doc(doc.doc).get(DubboKeeperMonitorService.INPUT)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.INPUT, false, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMinInput(Long.parseLong(searcher.doc(doc.doc).get(DubboKeeperMonitorService.INPUT)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.OUTPUT, false, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMinOutput(Long.parseLong(searcher.doc(doc.doc).get(DubboKeeperMonitorService.OUTPUT)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.OUTPUT, true, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMaxOutput(Long.parseLong(searcher.doc(doc.doc).get(DubboKeeperMonitorService.OUTPUT)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.FAILURE, true, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMaxFailure(Integer.parseInt(searcher.doc(doc.doc).get(DubboKeeperMonitorService.FAILURE)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.FAILURE, false, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMinFailure(Integer.parseInt(searcher.doc(doc.doc).get(DubboKeeperMonitorService.FAILURE)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.SUCCESS, true, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMaxSuccess(Integer.parseInt(searcher.doc(doc.doc).get(DubboKeeperMonitorService.SUCCESS)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.SUCCESS, false, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMinSuccess(Integer.parseInt(searcher.doc(doc.doc).get(DubboKeeperMonitorService.SUCCESS)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.TPS, true, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMaxTps(Double.parseDouble(searcher.doc(doc.doc).get(DubboKeeperMonitorService.TPS)));
                }


                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.TPS, false, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMinTps(Double.parseDouble(searcher.doc(doc.doc).get(DubboKeeperMonitorService.TPS)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.KBPS, true, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMaxKbps(Double.parseDouble(searcher.doc(doc.doc).get(DubboKeeperMonitorService.KBPS)));
                }

                groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.KBPS, false, searcher, query, groupSort);
                for (GroupDocs group : groupDocs) {
                    ScoreDoc doc = group.scoreDocs[0];
                    String method = ((BytesRef) group.groupValue).utf8ToString();
                    methodMonitorOverviews.get(method).setMinKbps(Double.parseDouble(searcher.doc(doc.doc).get(DubboKeeperMonitorService.KBPS)));
                }
                return methodMonitorOverviews.values();
            }
        }catch (IndexNotFoundException e){
            //do nothing
        }catch (IOException e) {
            logger.error("failed to grouping search", e);
        }
        return new ArrayList<MethodMonitorOverview>();
    }


    @Override
    public Collection<ApplicationInfo> queryApplications() {
        Collection<ApplicationIndexWriter> applicationIndexWriters =  LUCENE_WRITER_MAP.values();
        List<ApplicationInfo> applicationInfos = new ArrayList<ApplicationInfo>(applicationIndexWriters.size());
        for(ApplicationIndexWriter writer:applicationIndexWriters){
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.setApplicationName(writer.getApplication());
            applicationInfo.setMaxElapsed(writer.getMaxElapsed());
            applicationInfo.setMaxSuccess(writer.getMaxSuccess());
            applicationInfo.setMaxConcurrent(writer.getMaxConcurrent());
            applicationInfo.setMaxFault(writer.getMaxFault());
            applicationInfo.setApplicationType(queryApplicationType(writer.getApplication()));
            applicationInfos.add(applicationInfo);
        }

        return applicationInfos;
    }

    @Override
    public ApplicationInfo queryApplicationInfo(String application, long start, long end) {
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setApplicationName(application);
        applicationInfo.setMaxFault(Integer.parseInt(queryMaxRecord(application,DubboKeeperMonitorService.FAILURE, SortField.Type.INT,start,end)));
        applicationInfo.setMaxConcurrent(Long.parseLong(queryMaxRecord(application,DubboKeeperMonitorService.CONCURRENT, SortField.Type.LONG,start,end)));
        applicationInfo.setMaxElapsed(Long.parseLong(queryMaxRecord(application,DubboKeeperMonitorService.ELAPSED, SortField.Type.LONG,start,end)));
        applicationInfo.setMaxSuccess(Integer.parseInt(queryMaxRecord(application,DubboKeeperMonitorService.SUCCESS, SortField.Type.LONG,start,end)));
        applicationInfo.setApplicationType(queryApplicationType(application));
        return applicationInfo;
    }

    @Override
    public StatisticsOverview queryApplicationOverview(String application,long start,long end) {
        TermQuery applicationQuery = new TermQuery(new Term(DubboKeeperMonitorService.APPLICATION, new BytesRef(application)));
        return queryOverview(application,start,end,applicationQuery);
    }


    private String queryMaxRecord(String application, String field,SortField.Type type,long start,long end,TermQuery...queries){
        NumericRangeQuery<Long> timeQuery = NumericRangeQuery.newLongRange(DubboKeeperMonitorService.TIMESTAMP, start, end, true, true);
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        if(queries.length>0){
            for(TermQuery termQuery:queries){
                queryBuilder.add(new BooleanClause(termQuery, BooleanClause.Occur.MUST));
            }
        }
        queryBuilder.add(new BooleanClause(timeQuery, BooleanClause.Occur.FILTER));
        try {
            IndexSearcher searcher = generateSearcher(application);
            Query query = queryBuilder.build();
            SortField sortField = new SortField(field, type,true);
            Sort sort = new Sort();
            sort.setSort(sortField);
            TopFieldDocs resultDocs =  searcher.search(query, 1, sort);
            ScoreDoc[] docs = resultDocs.scoreDocs;
            if(docs.length<=0){
                return "0";
            }
            Set<String> needFields = generateQueryField(field);
            Document document = searcher.doc(docs[0].doc,needFields);
            return document.get(field);
        }catch (IndexNotFoundException e){
            //do nothing
        }catch (IOException e) {
            logger.error("failed to grouping search", e);
        }
        return "0";
    }

    private StatisticsOverview queryOverview(String application,long start,long end,TermQuery... queries){
        NumericRangeQuery<Long> timeQuery = NumericRangeQuery.newLongRange(DubboKeeperMonitorService.TIMESTAMP, start, end, true, true);
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        if(queries.length>0){
            for(TermQuery termQuery:queries){
                queryBuilder.add(new BooleanClause(termQuery, BooleanClause.Occur.MUST));
            }
        }
        queryBuilder.add(new BooleanClause(timeQuery, BooleanClause.Occur.FILTER));
        StatisticsOverview statisticsOverview = new StatisticsOverview();
        int maxSize = 200;

        try{
            IndexSearcher searcher = generateSearcher(application);
            Query query = queryBuilder.build();
            SortField sortField = new SortField(DubboKeeperMonitorService.CONCURRENT, SortField.Type.LONG,true);
            Sort sort = new Sort();
            sort.setSort(sortField);
            TopFieldDocs resultDocs =  searcher.search(query, maxSize, sort);
            ScoreDoc[] docs = resultDocs.scoreDocs;
            if(docs.length<=0){
                return statisticsOverview;
            }
            Set<String> needFields = generateQueryField(DubboKeeperMonitorService.CONCURRENT);
            List<ConcurrentItem> concurrentItems = new ArrayList<ConcurrentItem>(docs.length);
            statisticsOverview.setConcurrentItems(concurrentItems);
            for(int i=0;i<docs.length;i++){
                ScoreDoc doc = docs[i];
                Document document = searcher.doc(doc.doc,needFields);
                ConcurrentItem concurrentItem = new ConcurrentItem();
                convertItem(concurrentItem,document);
                concurrentItem.setConcurrent(Long.parseLong(document.get(DubboKeeperMonitorService.CONCURRENT)));
                concurrentItems.add(concurrentItem);
                if(concurrentItem.getConcurrent()<=0){
                    break;
                }
            }
            needFields.clear();

            needFields = generateQueryField(DubboKeeperMonitorService.ELAPSED);
            sortField = new SortField(DubboKeeperMonitorService.ELAPSED,SortField.Type.LONG,true);
            sort.setSort(sortField);
            resultDocs = searcher.search(query,maxSize,sort);
            docs = resultDocs.scoreDocs;
            List<ElapsedItem> elapsedItems = new ArrayList<ElapsedItem>(docs.length);
            statisticsOverview.setElapsedItems(elapsedItems);
            for(int i=0;i<docs.length;i++){
                ScoreDoc doc = docs[i];
                Document document = searcher.doc(doc.doc, needFields);
                ElapsedItem elapsedItem = new ElapsedItem();
                convertItem(elapsedItem,document);
                elapsedItem.setElapsed(Long.parseLong(document.get(DubboKeeperMonitorService.ELAPSED)));
                elapsedItems.add(elapsedItem);
                if(elapsedItem.getElapsed()<=0){
                    break;
                }
            }
            needFields.clear();



            needFields = generateQueryField(DubboKeeperMonitorService.FAILURE);
            sortField = new SortField(DubboKeeperMonitorService.FAILURE,SortField.Type.INT,true);
            sort.setSort(sortField);
            resultDocs = searcher.search(query,maxSize,sort);
            docs = resultDocs.scoreDocs;
            List<FaultItem> faultItems = new ArrayList<FaultItem>(docs.length);
            statisticsOverview.setFaultItems(faultItems);
            for(int i=0;i<docs.length;i++){
                ScoreDoc doc = docs[i];
                Document document = searcher.doc(doc.doc, needFields);
                FaultItem faultItem = new FaultItem();
                convertItem(faultItem,document);
                faultItem.setFault(Integer.parseInt(document.get(DubboKeeperMonitorService.FAILURE)));
                faultItems.add(faultItem);
                if(faultItem.getFault()<=0){
                    break;
                }
            }
            needFields.clear();


            needFields = generateQueryField(DubboKeeperMonitorService.SUCCESS);
            sortField = new SortField(DubboKeeperMonitorService.SUCCESS,SortField.Type.INT,true);
            sort.setSort(sortField);
            resultDocs = searcher.search(query,maxSize,sort);
            List<SuccessItem> successItems = new ArrayList<SuccessItem>(docs.length);
            statisticsOverview.setSuccessItems(successItems);
            docs = resultDocs.scoreDocs;
            for(int i=0;i<docs.length;i++){
                ScoreDoc doc = docs[i];
                Document document = searcher.doc(doc.doc, needFields);
                SuccessItem successItem = new SuccessItem();
                convertItem(successItem,document);
                successItem.setSuccess(Integer.parseInt(document.get(DubboKeeperMonitorService.SUCCESS)));
                successItems.add(successItem);
                if(successItem.getSuccess()<=0){
                    break;
                }
            }
        }catch (IndexNotFoundException e){
            //do nothing
        }catch (IOException e){
            logger.error("failed to grouping search", e);
        }
        return statisticsOverview;
    }

    private String queryServiceType(String application,String service) throws IOException {
        TermQuery applicationQuery = new TermQuery(new Term(DubboKeeperMonitorService.APPLICATION, new BytesRef(application)));
        TermQuery serviceQuery = new TermQuery(new Term(DubboKeeperMonitorService.INTERFACE, new BytesRef(service)));
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.add(new BooleanClause(applicationQuery, BooleanClause.Occur.MUST));
        queryBuilder.add(new BooleanClause(serviceQuery, BooleanClause.Occur.FILTER));
        IndexSearcher searcher = generateSearcher(application);
        Query query = queryBuilder.build();
        TopDocs resultDocs =  searcher.search(query, 1);
        ScoreDoc[] docs = resultDocs.scoreDocs;
        if(docs.length<0){
            return null;
        }
        Set<String> searchField = new HashSet<String>();
        searchField.add(DubboKeeperMonitorService.REMOTE_TYPE);
        Document document = searcher.doc(docs[0].doc, searchField);
        return document.getBinaryValue(DubboKeeperMonitorService.REMOTE_TYPE).utf8ToString();
    }

    @Override
    public StatisticsOverview queryServiceOverview(String application, String service, long start, long end) {
        TermQuery applicationQuery = new TermQuery(new Term(DubboKeeperMonitorService.APPLICATION, new BytesRef(application)));
        TermQuery serviceQuery = new TermQuery(new Term(DubboKeeperMonitorService.INTERFACE, new BytesRef(service)));
        return queryOverview(application,start,end,applicationQuery,serviceQuery);
    }

    private int queryApplicationType(String application)  {
        try{
            TermQuery applicationQuery = new TermQuery(new Term(DubboKeeperMonitorService.APPLICATION, new BytesRef(application)));
            BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
            queryBuilder.add(new BooleanClause(applicationQuery, BooleanClause.Occur.MUST));
            Sort groupSort = new Sort();
            SortField groupSortField = new SortField(DubboKeeperMonitorService.INTERFACE, SortField.Type.STRING);
            groupSort.setSort(groupSortField);
            TermFirstPassGroupingCollector firstPassCollector = new TermFirstPassGroupingCollector(DubboKeeperMonitorService.REMOTE_TYPE, groupSort,2);
            IndexSearcher searcher = generateSearcher(application);
            Query query = queryBuilder.build();
            searcher.search(query, firstPassCollector);
            Collection<SearchGroup<BytesRef>> topSearchGroups = firstPassCollector.getTopGroups(0, false);
            int size =  topSearchGroups.size();
            if(size==2){
                return size;
            }
            for(SearchGroup<BytesRef> group:topSearchGroups){
                if(Statistics.ApplicationType.PROVIDER.toString().equals(group.groupValue.utf8ToString())){
                    return 0;
                }else{
                    return 1;
                }
            }
            return -1;
        }catch (IndexNotFoundException e){
            //do nothing
            return -1;
        }catch (Exception e){
            return -1;
        }
    }

    @Override
    public Collection<ServiceInfo> queryServiceByApp(String application,long start,long end) {
        TermQuery applicationQuery = new TermQuery(new Term(DubboKeeperMonitorService.APPLICATION, new BytesRef(application)));
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.add(new BooleanClause(applicationQuery, BooleanClause.Occur.MUST));
        Sort groupSort = new Sort();
        SortField groupSortField = new SortField(DubboKeeperMonitorService.INTERFACE, SortField.Type.STRING);
        groupSort.setSort(groupSortField);
        try {
            TermFirstPassGroupingCollector firstPassCollector = new TermFirstPassGroupingCollector(DubboKeeperMonitorService.INTERFACE, groupSort,MAX_GROUP_SIZE);
            IndexSearcher searcher = generateSearcher(application);
            Query query = queryBuilder.build();
            searcher.search(query, firstPassCollector);
            Collection<SearchGroup<BytesRef>> topSearchGroups = firstPassCollector.getTopGroups(0, false);
            List<ServiceInfo> services = new ArrayList<ServiceInfo>();
            for(SearchGroup<BytesRef> group:topSearchGroups){
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setName(group.groupValue.utf8ToString());
                serviceInfo.setRemoteType(queryServiceType(application, serviceInfo.getName()));
                TermQuery serviceQuery = new TermQuery(new Term(DubboKeeperMonitorService.INTERFACE,new BytesRef(serviceInfo.getName())));
                serviceInfo.setMaxElapsed(Long.parseLong(queryMaxRecord(application,DubboKeeperMonitorService.ELAPSED, SortField.Type.LONG,start,end,serviceQuery)));
                serviceInfo.setMaxFault(Integer.parseInt(queryMaxRecord(application, DubboKeeperMonitorService.FAILURE, SortField.Type.INT, start, end, serviceQuery)));
                serviceInfo.setMaxConcurrent(Long.parseLong(queryMaxRecord(application, DubboKeeperMonitorService.CONCURRENT, SortField.Type.LONG, start, end, serviceQuery)));
                serviceInfo.setMaxSuccess(Integer.parseInt(queryMaxRecord(application,DubboKeeperMonitorService.SUCCESS, SortField.Type.INT,start,end,serviceQuery)));
                services.add(serviceInfo);
            }
            return services;
        }catch (IndexNotFoundException e){
            //do nothing
        }catch (IOException e) {
            logger.error("failed to grouping search", e);
        }
        return new ArrayList<ServiceInfo>();
    }


    private Set<String> generateQueryField(String field){
        Set<String> needFields = new HashSet<String>();
        needFields.add(field);
        needFields.add(DubboKeeperMonitorService.INTERFACE);
        needFields.add(DubboKeeperMonitorService.METHOD);
        needFields.add(DubboKeeperMonitorService.TIMESTAMP);
        needFields.add(DubboKeeperMonitorService.REMOTE_TYPE);
        return needFields;
    }

    private void convertItem(BaseItem item,Document doc){
        item.setMethod( doc.getBinaryValue(DubboKeeperMonitorService.METHOD).utf8ToString());
        item.setService(doc.getBinaryValue(DubboKeeperMonitorService.INTERFACE).utf8ToString());
        item.setTimestamp(Long.parseLong(doc.get(DubboKeeperMonitorService.TIMESTAMP)));
        item.setRemoteType(doc.getBinaryValue(DubboKeeperMonitorService.REMOTE_TYPE).utf8ToString());
    }



    private GroupDocs[] groupSearch(Collection<SearchGroup<BytesRef>> topSearchGroups, String withinGroupField, boolean reverse, IndexSearcher searcher, Query query, Sort groupSort) throws IOException {
        Sort withinGroupSort = new Sort();
        SortField withinGroupSortField = new SortField(withinGroupField, SortField.Type.LONG, reverse);
        withinGroupSort.setSort(withinGroupSortField);
        TermSecondPassGroupingCollector secondPassCollector = new TermSecondPassGroupingCollector(DubboKeeperMonitorService.METHOD,
                topSearchGroups, groupSort, withinGroupSort, 1, true, true, false);
        searcher.search(query, secondPassCollector);
        return secondPassCollector.getTopGroups(0).groups;
    }


    private IndexSearcher generateSearcher(String application) throws IOException {
        return new IndexSearcher(DirectoryReader.open(generateLuceneDirectory(application)));
    }

    private Directory generateLuceneDirectory(String application) throws IOException {

        String directory = ConfigUtils.getProperty("monitor.lucene.directory", System.getProperty("user.home") + "/monitor");

        Path path = null;
        if (!StringUtils.isEmpty(directory)) {
            path = Paths.get(directory + File.separator + application);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        }
        return generateLuceneDirectory(path);
    }
    private Directory generateLuceneDirectory(Path path) throws IOException {
        String directoryType = ConfigUtils.getProperty("monitor.lucene.directory.type");
        LuceneDirectoryType type = LuceneDirectoryType.typeOf(directoryType);
        Directory luceneDirectory = null;
        switch (type) {
            case MMAP:
                luceneDirectory = new MMapDirectory(path);
                break;
            case NIOFS:
                luceneDirectory = new NIOFSDirectory(path);
                break;
            case SIMPLE:
                luceneDirectory = new SimpleFSDirectory(path);
                break;
        }
        return luceneDirectory;
    }

    private int getCommitFrequency() {
        return Integer.parseInt(ConfigUtils.getProperty(LUCENE_COMMIT_FREQUENCY, "100"));
    }

    private int getMaxSegment() {
        return Integer.parseInt(ConfigUtils.getProperty(LUCENE_MAX_SEGMENT, "2"));
    }

    private Directory getDirectory(String application) throws IOException {
        if (!LUCENE_DIRECTORY_MAP.containsKey(application)) {
            LUCENE_DIRECTORY_MAP.putIfAbsent(application, generateLuceneDirectory(application));
        }
        return LUCENE_DIRECTORY_MAP.get(application);
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        String directory = ConfigUtils.getProperty("monitor.lucene.directory", System.getProperty("user.home") + "/monitor");
        final Path path = Paths.get(directory);
        if(Files.exists(path)&&Files.isDirectory(path)){
            Files.walkFileTree(path, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if(dir.equals(path)){
                        return FileVisitResult.CONTINUE;
                    }
                    LUCENE_DIRECTORY_MAP.put(dir.getFileName().toString(), generateLuceneDirectory(dir));
                    ApplicationIndexWriter applicationIndexWriter = new ApplicationIndexWriter(dir.getFileName().toString());
                    LUCENE_WRITER_MAP.put(dir.getFileName().toString(),applicationIndexWriter);
                    applicationIndexWriter.start();
                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.TERMINATE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.TERMINATE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if(dir.equals(path)){
                        return FileVisitResult.CONTINUE;
                    }
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        }
    }
}

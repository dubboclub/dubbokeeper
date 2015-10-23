package com.dubboclub.monitor.storage.lucene;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.fastjson.JSON;
import com.dubboclub.monitor.DubboKeeperMonitorService;
import com.dubboclub.monitor.model.MethodMonitorOverview;
import com.dubboclub.monitor.model.Statistics;
import com.dubboclub.monitor.model.Usage;
import com.dubboclub.monitor.storage.StatisticsStorage;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.SearchGroup;
import org.apache.lucene.search.grouping.term.TermFirstPassGroupingCollector;
import org.apache.lucene.search.grouping.term.TermSecondPassGroupingCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bieber on 2015/9/25.
 */
public class LuceneStatisticsStorage implements StatisticsStorage {

    private static final ConcurrentHashMap<String, Directory> LUCENE_DIRECTORY_MAP = new ConcurrentHashMap<String, Directory>();

    private static final ConcurrentHashMap<String, IndexWriter> LUCENE_WRITER_MAP = new ConcurrentHashMap<String, IndexWriter>();

    private static final ConcurrentHashMap<String, Semaphore> APPLICATION_WRITE_SEMAPHORE = new ConcurrentHashMap<String, Semaphore>();

    private static final ConcurrentHashMap<String, AtomicInteger> APPLICATION_WRITE_COUNTER = new ConcurrentHashMap<String, AtomicInteger>();

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
                Collection<IndexWriter> writers = LUCENE_WRITER_MAP.values();
                for (IndexWriter writer : writers) {
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

    @Override
    public void storeStatistics(Statistics statistics) {
        if (!running) {
            return;
        }
        try {
            if (!APPLICATION_WRITE_SEMAPHORE.containsKey(statistics.getApplication())) {
                APPLICATION_WRITE_SEMAPHORE.putIfAbsent(statistics.getApplication(), new Semaphore(getCommitFrequency()));
                APPLICATION_WRITE_COUNTER.putIfAbsent(statistics.getApplication(), new AtomicInteger(getCommitFrequency()));
            }
            APPLICATION_WRITE_SEMAPHORE.get(statistics.getApplication()).acquire();
            int remain = APPLICATION_WRITE_COUNTER.get(statistics.getApplication()).decrementAndGet();
            logger.debug("store statistics [" + JSON.toJSONString(statistics) + "]");
            if (!LUCENE_WRITER_MAP.containsKey(statistics.getApplication())) {
                Directory directory = getDirectory(statistics.getApplication());
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                IndexWriter indexWriter = new IndexWriter(directory, config);
                LUCENE_WRITER_MAP.putIfAbsent(statistics.getApplication(), indexWriter);
            }
            IndexWriter indexWriter = LUCENE_WRITER_MAP.get(statistics.getApplication());
            Document document = new Document();
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
            indexWriter.addDocument(document);
            if (remain == 0) {
                logger.debug("start commit lucene");
                indexWriter.forceMerge(getMaxSegment());
                indexWriter.close();
                LUCENE_WRITER_MAP.remove(statistics.getApplication());
                APPLICATION_WRITE_COUNTER.get(statistics.getApplication()).set(getCommitFrequency());
                APPLICATION_WRITE_SEMAPHORE.get(statistics.getApplication()).release(getCommitFrequency());
                logger.debug("finished commit lucene");
            }
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
            TopDocs topDocs = searcher.search(queryBuilder.build(), Integer.MAX_VALUE);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            List<Statistics> statisticsList = new ArrayList<Statistics>();
            if (scoreDocs.length > 0) {
                for (int i = 0; i < scoreDocs.length; i++) {
                    Document document = searcher.doc(scoreDocs[i].doc);
                    statisticsList.add(parseDocToStatistics(document));
                }
                Collections.sort(statisticsList);
                return statisticsList;
            }
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
            TermFirstPassGroupingCollector firstPassCollector = new TermFirstPassGroupingCollector(DubboKeeperMonitorService.METHOD, groupSort, methodSize);
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
        } catch (IOException e) {
            logger.error("failed to grouping search", e);
        }
        return new ArrayList<MethodMonitorOverview>();
    }


    @Override
    public Collection<Usage> queryMethodUsage(String application, String service, String method, long startTime, long endTime) {
        /*if(!LUCENE_DIRECTORY_MAP.containsKey(application)){
            return new ArrayList<Usage>();
        }*/
        TermQuery applicationQuery = new TermQuery(new Term(DubboKeeperMonitorService.APPLICATION, new BytesRef(application)));
        TermQuery interfaceQuery = new TermQuery(new Term(DubboKeeperMonitorService.INTERFACE, new BytesRef(service)));
        TermQuery methodQuery = new TermQuery(new Term(DubboKeeperMonitorService.METHOD, new BytesRef(method)));
        NumericRangeQuery<Long> timeQuery = NumericRangeQuery.newLongRange(DubboKeeperMonitorService.TIMESTAMP, startTime, endTime, true, true);
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.add(new BooleanClause(applicationQuery, BooleanClause.Occur.MUST));
        queryBuilder.add(new BooleanClause(interfaceQuery, BooleanClause.Occur.MUST));
        queryBuilder.add(new BooleanClause(methodQuery, BooleanClause.Occur.MUST));
        queryBuilder.add(new BooleanClause(timeQuery, BooleanClause.Occur.FILTER));
        Sort groupSort = new Sort();
        SortField groupSortField = new SortField(DubboKeeperMonitorService.REMOTE_ADDRESS, SortField.Type.STRING);
        groupSort.setSort(groupSortField);
        try{
            TermFirstPassGroupingCollector firstPassCollector = new TermFirstPassGroupingCollector(DubboKeeperMonitorService.REMOTE_ADDRESS, groupSort, MAX_GROUP_SIZE);
            IndexSearcher searcher = generateSearcher(application);
            Query query = queryBuilder.build();
            searcher.search(query, firstPassCollector);
            Collection<SearchGroup<BytesRef>> topSearchGroups = firstPassCollector.getTopGroups(0, false);
            GroupDocs[] groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.REMOTE_ADDRESS, false, searcher, query, groupSort);
            return generateUsage(groupDocs,searcher);
        }catch (IOException e) {
            logger.error("failed to grouping search", e);
        }
        return new ArrayList<Usage>();
    }
    

    @Override
    public Collection<Usage> queryServiceUsage(String application, String service, long startTime, long endTime) {
       /* if(!LUCENE_DIRECTORY_MAP.containsKey(application)){
            return new ArrayList<Usage>();
        }*/
        TermQuery applicationQuery = new TermQuery(new Term(DubboKeeperMonitorService.APPLICATION, new BytesRef(application)));
        TermQuery interfaceQuery = new TermQuery(new Term(DubboKeeperMonitorService.INTERFACE, new BytesRef(service)));
        NumericRangeQuery<Long> timeQuery = NumericRangeQuery.newLongRange(DubboKeeperMonitorService.TIMESTAMP, startTime, endTime, true, true);
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.add(new BooleanClause(applicationQuery, BooleanClause.Occur.MUST));
        queryBuilder.add(new BooleanClause(interfaceQuery, BooleanClause.Occur.MUST));
        queryBuilder.add(new BooleanClause(timeQuery, BooleanClause.Occur.FILTER));
        Sort groupSort = new Sort();
        SortField groupSortField = new SortField(DubboKeeperMonitorService.REMOTE_ADDRESS, SortField.Type.STRING);
        groupSort.setSort(groupSortField);
        try{
            TermFirstPassGroupingCollector firstPassCollector = new TermFirstPassGroupingCollector(DubboKeeperMonitorService.REMOTE_ADDRESS, groupSort, MAX_GROUP_SIZE);
            IndexSearcher searcher = generateSearcher(application);
            Query query = queryBuilder.build();
            searcher.search(query, firstPassCollector);
            Collection<SearchGroup<BytesRef>> topSearchGroups = firstPassCollector.getTopGroups(0, false);
            GroupDocs[] groupDocs = groupSearch(topSearchGroups, DubboKeeperMonitorService.REMOTE_ADDRESS, false, searcher, query, groupSort);
            return generateUsage(groupDocs,searcher);
        }catch (IOException e) {
            logger.error("failed to grouping search", e);
        }
        return new ArrayList<Usage>();
    }
    private List<Usage> generateUsage(GroupDocs[] groupDocs ,IndexSearcher searcher) throws IOException {
        List<Usage> usages = new ArrayList<Usage>();
        if(groupDocs==null||groupDocs.length<=0){
            return usages;
        }
        Set<String> fields = new HashSet<String>();
        fields.add(DubboKeeperMonitorService.FAILURE);
        fields.add(DubboKeeperMonitorService.SUCCESS);
        for(GroupDocs group:groupDocs){
            Usage usage = new Usage();
            usage.setRemoteAddress(((BytesRef)group.groupValue).utf8ToString());
            ScoreDoc[] docs = group.scoreDocs;
            long count=0;
            for(ScoreDoc doc :docs){
                Document document = searcher.doc(doc.doc, fields);
                count+=Long.parseLong(document.get(DubboKeeperMonitorService.FAILURE));
                count+=Long.parseLong(document.get(DubboKeeperMonitorService.SUCCESS));
            }
            usage.setCount(count);
            usages.add(usage);
        }
        return usages;
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
        String directoryType = ConfigUtils.getProperty("monitor.lucene.directory.type");
        String directory = ConfigUtils.getProperty("monitor.lucene.directory", System.getProperty("user.home") + "/monitor");
        LuceneDirectoryType type = LuceneDirectoryType.typeOf(directoryType);
        Directory luceneDirectory = null;
        Path path = null;
        if (!StringUtils.isEmpty(directory)) {
            path = Paths.get(directory + File.separator + application);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        }
        switch (type) {
            case MMAP:
                luceneDirectory = new MMapDirectory(path);
                break;
            case NIOFS:
                luceneDirectory = new NIOFSDirectory(path);
                break;
            case SIMPLE:
                luceneDirectory = new NIOFSDirectory(path);
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

}

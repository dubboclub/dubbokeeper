package com.dubboclub.monitor.storage.lucene;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.monitor.MonitorService;
import com.dubboclub.monitor.DubboKeeperMonitorService;
import com.dubboclub.monitor.model.Statistics;
import com.dubboclub.monitor.storage.StatisticsStorage;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import sun.security.krb5.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by bieber on 2015/9/25.
 */
public class LuceneStatisticsStorage implements StatisticsStorage {
    
    private static final ConcurrentHashMap<String,Directory> LUCENE_DIRECTORY_MAP = new ConcurrentHashMap<String,Directory>();
    
    private static Logger logger = LoggerFactory.getLogger(LuceneStatisticsStorage.class);
    
    @Override
    public void storeStatistics(Statistics statistics) {
        try{
            if(!LUCENE_DIRECTORY_MAP.containsKey(statistics.getApplication())){
                LUCENE_DIRECTORY_MAP.putIfAbsent(statistics.getHost(), generateLuceneDirectory(statistics.getHost()));
            }
            Directory directory = LUCENE_DIRECTORY_MAP.get(statistics.getHost());
            IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(directory,config);
            Document document = new Document();
            document.add(new StringField(DubboKeeperMonitorService.APPLICATION,statistics.getApplication(), Field.Store.YES));
            document.add(new StringField(DubboKeeperMonitorService.INTERFACE,statistics.getServiceInterface(),Field.Store.YES));
            document.add(new StringField(DubboKeeperMonitorService.METHOD,statistics.getMethod(), Field.Store.YES));
            document.add(new StringField(DubboKeeperMonitorService.REMOTE_ADDRESS,statistics.getRemoteAddress(), Field.Store.YES));
            document.add(new StringField(DubboKeeperMonitorService.REMOTE_TYPE,statistics.getRemoteType().toString(), Field.Store.YES));
            document.add(new StringField(DubboKeeperMonitorService.APPLICATION_TYPE,statistics.getType().toString(), Field.Store.YES));
            document.add(new StringField(DubboKeeperMonitorService.HOST_KEY,statistics.getHost(), Field.Store.YES));
            document.add(new IntField(DubboKeeperMonitorService.INVOKE_STAT,statistics.isInvokeStat()?1:0, Field.Store.YES));
            document.add(new LongField(DubboKeeperMonitorService.CONCURRENT,statistics.getConcurrent(),Field.Store.YES));
            document.add(new LongField(DubboKeeperMonitorService.ELAPSED,statistics.getElapsed(),Field.Store.YES));
            document.add(new LongField(DubboKeeperMonitorService.INPUT,statistics.getInput(),Field.Store.YES));
            document.add(new LongField(DubboKeeperMonitorService.OUTPUT,statistics.getOutput(),Field.Store.YES));
            document.add(new LongField(DubboKeeperMonitorService.TIMESTAMP, statistics.getOutput(), Field.Store.YES));
            indexWriter.addDocument(document);
            indexWriter.close();
        }catch (Exception e){
            logger.error("failed to store statistics info",e);
        }
    }

    @Override
    public List<Statistics> queryStatisticsByHost(String application, String host, long startTime, long endTime) {
        return null;
    }

    @Override
    public List<Statistics> queryStatisticsForInterface(String application, String serviceInterface, long startTime, long endTime) {
        return null;
    }

    @Override
    public List<Statistics> queryAllApplicationAbstractInfo() {
        return null;
    }


    private Directory generateLuceneDirectory(String application) throws IOException {
        String directoryType = ConfigUtils.getProperty("monitor.lucene.directory.type");
        String directory = ConfigUtils.getProperty("monitor.lucene.directory",System.getProperty("user.home")+"/monitor");
        LuceneDirectoryType type = LuceneDirectoryType.typeOf(directoryType);
        Directory luceneDirectory=null;
        if(StringUtils.isEmpty(directory)&&type!=LuceneDirectoryType.RAM){
            throw new IllegalStateException("current lucene directory type is "+type+" must config monitor.lucene.directory");
        }
        Path path=null;
        if(!StringUtils.isEmpty(directory)){
            path = Paths.get(directory+File.separator+application);
            if(!Files.exists(path)){
                Files.createDirectories(path);
            }
        }
        switch (type){
            case RAM:
                luceneDirectory = new RAMDirectory();
                break;
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
    
}

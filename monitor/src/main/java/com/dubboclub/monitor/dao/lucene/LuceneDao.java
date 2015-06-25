package com.dubboclub.monitor.dao.lucene;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.dubboclub.monitor.util.LuceneUtil;
import com.dubboclub.monitor.util.LuceneUtil.LuceneRunnable;

public class LuceneDao {
	
    String APPLICATION = "application";
    
    String INTERFACE = "interface";

    String METHOD = "method";

    String GROUP = "group";

    String VERSION = "version";

    String CONSUMER = "consumer";

    String PROVIDER = "provider";
    
    String TIMESTAMP = "timestamp";

    static String SUCCESS = "success";

    static String FAILURE = "failure";
    
    String INPUT = Constants.INPUT_KEY;

    String OUTPUT = Constants.OUTPUT_KEY;

    static String ELAPSED = "elapsed";

    static String CONCURRENT = "concurrent";

    String MAX_INPUT = "max.input";

    String MAX_OUTPUT = "max.output";

    static String MAX_ELAPSED = "max.elapsed";

    static String MAX_CONCURRENT = "max.concurrent";

	public static final Directory IDX_DIR = new RAMDirectory();
	
	private static final String[] types = { SUCCESS, FAILURE, ELAPSED, CONCURRENT, MAX_ELAPSED, MAX_CONCURRENT };

    /**
     * Add document to the index.
     * 
     * @param statistics Statistics from DubboMonitor
     */
    public void createDocument(final URL statistics) {
        LuceneUtil.handle(new LuceneRunnable() {

			@Override
			public void run(IndexWriter indexWriter) throws Exception {
				 org.apache.lucene.document.Document luceneDocument = getDocumentFromSatistics(statistics);
	                indexWriter.addDocument(luceneDocument);
				
			}
        });
    }
    
    /**
     * Build Lucene document from statistics.
     * 
     * @param statistics Statistics from DubboMonitor
     * @return Document
     */
	private Document getDocumentFromSatistics(URL statistics) {
		String timestamp = statistics.getParameter(Constants.TIMESTAMP_KEY);
		Date now;

		if (timestamp == null || timestamp.length() == 0) {
			now = new Date();
		} else {
			try {
				now = new SimpleDateFormat("yyyyMMddHHmmss").parse(timestamp);
			} catch (ParseException e) {
				now = new Date(Long.parseLong(timestamp));
			}
		}

		String type;
		String consumer;
		String provider;
		if (statistics.hasParameter(PROVIDER)) {
			type = CONSUMER;
			consumer = statistics.getHost();
			provider = statistics.getParameter(PROVIDER);
			int i = provider.indexOf(':');
			if (i > 0) {
				provider = provider.substring(0, i);
			}
		} else {
			type = PROVIDER;
			consumer = statistics.getParameter(CONSUMER);
			int i = consumer.indexOf(':');
			if (i > 0) {
				consumer = consumer.substring(0, i);
			}
			provider = statistics.getHost();
		}

		Document document = new Document();
		document.add(new LongField("timestamp", now.getTime(), Field.Store.YES));
		document.add(new StringField("interface", statistics.getServiceInterface(), Field.Store.YES));
		document.add(new StringField("method", statistics.getParameter(METHOD), Field.Store.YES));
		document.add(new StringField("type", type, Field.Store.YES));
		document.add(new StringField("provider", provider, Field.Store.YES));
		document.add(new StringField("consumer", consumer, Field.Store.YES));
		for (String key : types) {
			document.add(new LongField("key", statistics.getParameter(key, 0), Field.Store.YES));
		}
		return document;
	}
}

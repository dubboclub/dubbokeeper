package com.dubboclub.monitor.dao.lucene;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.dubboclub.monitor.AppContext;
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

	private static final String[] keys = { SUCCESS, FAILURE, ELAPSED, CONCURRENT, MAX_ELAPSED, MAX_CONCURRENT };

	/**
	 * Add document to the index.
	 * 
	 * @param statistics
	 *            Statistics from DubboMonitor
	 */
	public void createDocument(final URL statistics) {
		LuceneUtil.handle(new LuceneRunnable() {

			@Override
			public void run(IndexWriter indexWriter) throws Exception {
				Document luceneDocument = getDocumentFromSatistics(statistics);
				indexWriter.addDocument(luceneDocument);

			}
		});
	}

	public List<Document> search(Query query, Filter filter, int n, Sort sort) throws Exception {
		DirectoryReader directoryReader = AppContext.getInstance().getDirectoryReader();
		List<Document> documentList = new LinkedList<Document>();
		if (directoryReader == null) {
			return documentList;
		}
		IndexSearcher searcher = new IndexSearcher(directoryReader);
		TopDocs topDocs = null;
		topDocs = searcher.search(query, filter, n, sort);
		ScoreDoc[] docs = topDocs.scoreDocs;

		for (int i = 0; i < docs.length; i++) {
			Document document = searcher.doc(docs[i].doc);
			documentList.add(document);
		}

		return documentList;

	}


	/**
	 * Build Lucene document from statistics.
	 * 
	 * @param statistics
	 *            Statistics from DubboMonitor
	 * @return Document
	 */
	private Document getDocumentFromSatistics(URL statistics) {
		String timestamp = statistics.getParameter(Constants.TIMESTAMP_KEY);
		Date now;

		if (timestamp == null || timestamp.length() == 0) {
			now = new Date();
		} else if (timestamp.length() != "yyyyMMddHHmmss".length()) {
			now = new Date(Long.parseLong(timestamp));
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
		} else {
			type = PROVIDER;
			consumer = statistics.getParameter(CONSUMER);
			provider = statistics.getHost();
		}

		Document document = new Document();
		document.add(new LongField("timestamp", now.getTime(), Field.Store.YES));

		document.add(new StringField("interface", statistics.getServiceInterface(), Field.Store.YES));
		document.add(new StringField("method", statistics.getParameter(METHOD), Field.Store.YES));

		document.add(new StringField("type", type, Field.Store.YES));
		document.add(new StringField("provider", provider, Field.Store.YES));
		document.add(new StringField("consumer", consumer, Field.Store.YES));
		for (String key : keys) {
			document.add(new LongField(key, statistics.getParameter(key, 0), Field.Store.YES));
		}
		return document;
	}
}

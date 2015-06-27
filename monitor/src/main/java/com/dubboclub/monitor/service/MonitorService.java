package com.dubboclub.monitor.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;

import com.dubboclub.monitor.dao.lucene.LuceneDao;
import com.dubboclub.monitor.model.Statistics;

public class MonitorService {

	public List<Statistics> listElapsedByService(String service, long lastTimestamp) {
		List<Statistics> data = new LinkedList<Statistics>();
		List<Document> docs = new LinkedList<Document>();
		LuceneDao dao = new LuceneDao();
		Filter filter = NumericRangeFilter.newLongRange("timestamp", lastTimestamp, null, false, false);
		try {
			docs = dao.search(new TermQuery(new Term("interface", service)), filter, 10, new Sort(new SortField("timestamp", Type.LONG, false)));
		} catch (Exception e) {

		}
		for (Document document : docs) {
			data.add(getStaticsFromDocument(document));
		}
		return data;

	}

	private Statistics getStaticsFromDocument(Document document) {
		Statistics statistics = new Statistics();
		statistics.setTimestamp(new Date(Long.valueOf(document.get("timestamp"))));
		statistics.setServiceInterface(document.get("interface"));
		statistics.setMethod(document.get("method"));

		statistics.setType(document.get("type"));
		statistics.setProvider(document.get("provider"));
		statistics.setConsumer(document.get("consumer"));

		statistics.setSuccess(Long.valueOf(document.get("success")));
		statistics.setFailure(Long.valueOf(document.get("failure")));
		statistics.setElapsed(Long.valueOf(document.get("elapsed")));
		statistics.setConcurrent(Long.valueOf(document.get("concurrent")));
		statistics.setMaxConcurrent(Long.valueOf(document.get("max.concurrent")));
		statistics.setMaxElapsed(Long.valueOf(document.get("max.elapsed")));
		// statistics.setMaxInput(Long.valueOf(document.get("max.input")));
		// statistics.setMaxOutput(Long.valueOf(document.get("max.output")));

		return statistics;
	}
}

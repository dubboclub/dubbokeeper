package com.dubboclub.monitor;

import java.util.List;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.monitor.MonitorService;
import com.dubboclub.monitor.dao.lucene.LuceneDao;

/**
 * Created by bieber on 2015/6/1.
 * 
 * @author bieber
 * @author shevawen
 * 
 */
public class DubboKeeperMonitorService implements MonitorService {

	public static final Directory IDX_DIR = new RAMDirectory();
	private static final String POISON_PROTOCOL = "poison";
	
	LuceneDao dao = new LuceneDao();

	public DubboKeeperMonitorService() {

	}

	@Override
	public void collect(URL statistics) {
		if (POISON_PROTOCOL.equals(statistics.getProtocol())) {
			return;
		}
		dao.createDocument(statistics);
	}

	@Override
	public List<URL> lookup(URL url) {
		return null;
	}
}

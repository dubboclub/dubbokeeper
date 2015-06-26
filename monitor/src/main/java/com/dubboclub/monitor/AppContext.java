package com.dubboclub.monitor;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.dubboclub.monitor.constant.Constants;

public class AppContext {

	/**
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(AppContext.class);

	/**
	 * Singleton instance.
	 */
	private static AppContext instance;

	/**
	 * Lucene directory.
	 */
	private Directory directory;

	/**
	 * Directory reader.
	 */
	private DirectoryReader directoryReader;

	/**
	 * Private constructor.
	 */
	private AppContext() {
		// TODO read from config file
		String luceneStorageConfig = "RAM";
		// RAM directory storage by default
		if (luceneStorageConfig == null || luceneStorageConfig.equals(Constants.LUCENE_DIRECTORY_STORAGE_RAM)) {
			directory = new RAMDirectory();
			log.info("Using RAM Lucene storage");
		} else if (luceneStorageConfig.equals(Constants.LUCENE_DIRECTORY_STORAGE_FILE)) {
			File luceneDirectory = new File("./indexing");
			try {
				directory = new SimpleFSDirectory(luceneDirectory);
			} catch (IOException e) {
				log.error("Failed while set file as Lucene storage");
			}
			log.info("Using file Lucene storage");
		}
	}

	/**
	 * Returns a single instance of the application context.
	 * 
	 * @return Application context
	 */
	public static AppContext getInstance() {
		if (instance == null) {
			instance = new AppContext();
		}
		return instance;
	}

	/**
	 * Getter of indexing directory.
	 *
	 * @return the directory
	 */
	public Directory getDirectory() {
		return directory;
	}

	/**
	 * Returns a valid directory reader. Take care of reopening the reader if
	 * the index has changed and closing the previous one.
	 *
	 * @return the directoryReader
	 */
	public DirectoryReader getDirectoryReader() {
		if (directoryReader == null) {
			if (!DirectoryReader.indexExists(directory)) {
				return null;
			}
			try {
				directoryReader = DirectoryReader.open(directory);
			} catch (IOException e) {
				log.error("Error creating the directory reader", e);
			}
		} else {
			try {
				DirectoryReader newReader = DirectoryReader.openIfChanged(directoryReader);
				if (newReader != null) {
					directoryReader.close();
					directoryReader = newReader;
				}
			} catch (IOException e) {
				log.error("Error while reopening the directory reader", e);
			}
		}
		return directoryReader;
	}

}

package com.kv.crawler;

import java.net.URL;

/**
 * Thread class to fetch contents of a page and do required processing on it
 * 
 * @author hadoopninja
 * 
 */
public abstract class ProcessUrlThread extends Thread {
	/** The URL to be processed */
	protected URL _url;
	protected IThreadStateReceiver _stateListener;
	protected int _myId;
	protected UrlQueue _urlQueue;

	public ProcessUrlThread(URL url, IThreadStateReceiver stateListener,
			int threadId, UrlQueue urlQueue) {
		_url = url;
		_stateListener = stateListener;
		_myId = threadId;
		_urlQueue = urlQueue;
	}

	public void run() {
		processTask();
	}

	abstract protected void processTask();
}

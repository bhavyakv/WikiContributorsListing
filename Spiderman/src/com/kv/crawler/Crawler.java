package com.kv.crawler;

import java.net.MalformedURLException;
import java.net.URL;

import com.kv.wiki.crawler.WikiUrlProcessThread;

/**
 * Base implementation for a web crawler
 * 
 * @author hadoopninja
 * 
 */
public class Crawler implements IThreadStateReceiver {
	private int _maxThreadsRunning;
	private UrlQueue _urlQueue;
	private int _noOfThreads = 0;
	private int _threadsCounter = 0;
	private int _noOfUrlsPerPage;

	public static void main(String[] args) {
		if (5 != args.length) {
			System.out
					.print("Usage java Crawler <seed url> <max_depth_to_visit> <max_urls_to_collect_per_page> <max_threads_running_at_once> <output_dir_name>");
			return;
		}
		String seedUrl = args[0];

		int maxDepth = 0;
		maxDepth = Integer.parseInt(args[1]);

		int noOfUrlsPerPage = 0;
		noOfUrlsPerPage = Integer.parseInt(args[2]);

		int maxThreads = Integer.parseInt(args[3]);

		String outputDirName = args[4];
		Crawler spider = new Crawler(seedUrl, maxDepth, noOfUrlsPerPage,
				maxThreads, outputDirName);
		spider.processUrlQueue();

	}

	public Crawler(String seedUrl, int maxDepth, int noOfUrlsPerPage,
			int maxThreadsRunning, String outputDirName) {
		try {
			_urlQueue = new UrlQueue(new URL(seedUrl), maxDepth);
			_noOfUrlsPerPage = noOfUrlsPerPage;
			_maxThreadsRunning = maxThreadsRunning;
			SavePage.setRootDirectory(outputDirName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks current state of threads and URL queue and schedules processing of
	 * URLs
	 */
	private synchronized void processUrlQueue() {
		System.out.println("startCrawling ++");
		int threadsRemaining = _maxThreadsRunning - _noOfThreads;
		System.out.println("startCrawling() threadsRemaining:"
				+ threadsRemaining);
		if (_threadsCounter == 0) {
			System.out.println("Start time:" + System.currentTimeMillis());
		}
		if (true == _urlQueue.areAllLinksVisited() && 0 == _noOfThreads) {
			_urlQueue.incrementLevel();
		}
		while (!_urlQueue.areAllLinksVisited() && 0 < threadsRemaining) {
			visitNextLink();
			threadsRemaining--;
		}
		System.out.println("startCrawling --");

	}

	/**
	 * Spawns a new thread to visit next URL at the front of the queue
	 */
	private void visitNextLink() {
		URL siteUrl = _urlQueue.popUrlToCrawl();

		ProcessUrlThread urlProcessingThread = new WikiUrlProcessThread(
				siteUrl, this, _threadsCounter++, _urlQueue, _noOfUrlsPerPage);
		urlProcessingThread.start();
		_noOfThreads++;

	}

	@Override
	public void executingTask(Object task, int threadId) {
		System.out.println("Thread:" + threadId + " Current task:"
				+ (String) task);

	}

	@Override
	public void finished(int threadId) {
		System.out.println("Thread " + threadId + " finished execution");
		_noOfThreads--;
		processUrlQueue();
		System.out.println("current time:" + System.currentTimeMillis());
	}
}

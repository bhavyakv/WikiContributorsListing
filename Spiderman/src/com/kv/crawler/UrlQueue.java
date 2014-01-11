package com.kv.crawler;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Queue implementation to hold list of URLs visited and gathered by the crawler
 * 
 * @author hadoopninja
 * 
 */
public class UrlQueue {
	private LinkedList<URL> _currentDepthCrawlList = null;
	private LinkedList<URL> _nextDepthCrawlList = null;

	// Using a set as it will take care of duplicates(already visited URLs)
	private Set<URL> _gatheredLinks = null;
	private int _maxDepth = 0;
	private int _currentCrawlingLevel = 0;

	public UrlQueue(URL seedUrl, int maxDepth) {
		_currentDepthCrawlList = new LinkedList<URL>();
		_currentDepthCrawlList.add(seedUrl);
		_nextDepthCrawlList = new LinkedList<URL>();
		// Add to "seen" URLs list so that root URL is also marked as already
		// gathered
		_gatheredLinks = new HashSet<URL>();
		_gatheredLinks.add(seedUrl);
		_maxDepth = maxDepth;
	}

	/**
	 * Return next URL to be visited
	 * 
	 * @return
	 */
	public URL popUrlToCrawl() {
		return _currentDepthCrawlList.remove();
	}

	/**
	 * Add a new URL into the queue to be visited if it meets the required
	 * criteria
	 * 
	 * @param newUrl
	 * @return
	 */
	public boolean push(URL newUrl) {

		boolean bUrlExamined = false;
		String urlPath = newUrl.getPath();
		if (urlPath.contains(".pdf") || urlPath.contains("@")
				|| urlPath.contains(":80") || urlPath.contains(".jpg")
				|| urlPath.contains(".php")) {
			// do not add, we're interested in only HTML content
		} else {
			synchronized (_gatheredLinks) {
				if (true == _gatheredLinks.add(newUrl)) {
					System.out.println("Added URL:" + newUrl);
					_nextDepthCrawlList.addLast(newUrl);
					bUrlExamined = true;
				} else {
					System.out.println("duplicate URl:" + newUrl);
				}
			}
		}
		return bUrlExamined;
	}

	/**
	 * Checks if program has completed visiting all URLs in the current queue
	 * 
	 * @return
	 */
	public boolean areAllLinksVisited() {
		return _currentDepthCrawlList.size() > 0 ? false : true;
	}

	/**
	 * Returns the number of URLs added to be visited in the next level
	 * 
	 * @return
	 */
	public int getGatheredLinksSize() {
		synchronized (_gatheredLinks) {
			return _gatheredLinks.size();
		}
	}

	/**
	 * Moves to the next level in the breadth-first crawling approach
	 */
	public void incrementLevel() {
		_currentCrawlingLevel++;
		_currentDepthCrawlList = _nextDepthCrawlList;
		_nextDepthCrawlList = new LinkedList<URL>();
	}

	/**
	 * Returns current crawling depth according to the URL tree
	 * 
	 * @return
	 */
	public int getCurrentCrawlingDepth() {
		return _currentCrawlingLevel;
	}

	/**
	 * Checks if max depth for crawling has been reached
	 * 
	 * @return
	 */
	public boolean hasMaxDepthReached() {
		return (_currentCrawlingLevel == _maxDepth) ? true : false;
	}

}

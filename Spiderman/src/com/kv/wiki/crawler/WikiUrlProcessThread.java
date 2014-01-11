package com.kv.wiki.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kv.crawler.IThreadStateReceiver;
import com.kv.crawler.ProcessUrlThread;
import com.kv.crawler.SavePage;
import com.kv.crawler.UrlQueue;

public class WikiUrlProcessThread extends ProcessUrlThread {

	private int _noOfUrlsPerPage;

	public WikiUrlProcessThread(URL url, IThreadStateReceiver stateListener,
			int threadId, UrlQueue urlQueue, int noOfUrlsPerPage) {
		super(url, stateListener, threadId, urlQueue);
		_noOfUrlsPerPage = noOfUrlsPerPage;
	}

	/**
	 * Processes a Wiki page extracting relevant info
	 */
	@Override
	protected void processTask() {
		String visitingUrl = _url.toString();
		Document page = null;
		try {
			// Wikipedia's robots.txt advises a crawl-delay of atleast 1 sec
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			page = Jsoup.connect(visitingUrl).get();

			getContributorsForPage(page, _url);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.print("Couldn't make request to URL " + visitingUrl);
		}

		if (null != page) {
			_stateListener.executingTask("Parsing for links, page at URL:"
					+ visitingUrl, _myId);
			Elements paragraphs = page.select("p");
			Elements linkedArticles = paragraphs.select("a[href]");
			int noOfLinksAdded = 0;
			if (false == _urlQueue.hasMaxDepthReached()) {
				for (Element link : linkedArticles) {
					if (noOfLinksAdded < _noOfUrlsPerPage) {
						String pageUrl = link.attr("href");
						try {
							// urls might be relative to current page
							URL completeUrl = new URL(_url, pageUrl);
							if (true == _urlQueue.push(completeUrl)) {
								// Increment count only if link was added,
								// don't increment if it was duplicate/invalid URL
								noOfLinksAdded++;
							}
						} catch (MalformedURLException e) {
							System.err.print("MalformedUrl: foundurl:"
									+ pageUrl);
							// e.printStackTrace();
						}
					} else {
						System.out.println(_noOfUrlsPerPage + " visited!");
						break;
					}

				}
			}

		}
		_stateListener.finished(_myId);

	}

	/**
	 * Gets the contributors info and saves it into a file
	 * 
	 * @param page
	 * @param url
	 * @throws IOException
	 */
	private void getContributorsForPage(Document page, URL url)
			throws IOException {
		// Going to the "History" page
		Elements historyTabs = page.select("a:contains(View History)");
		if (historyTabs.size() == 1) {
			// there should be only one History tab
			Element historyTab = historyTabs.get(0);
			String historyPageUrl = historyTab.attr("href");

			URL newUrl = new URL(url, historyPageUrl);
			Document historyPage = Jsoup.connect(newUrl.toString()).get();

			// Going to the "Contributors" page
			Elements contributorsTabs = historyPage
					.select("a:contains(Contributors)");
			if (contributorsTabs.size() == 1) {
				Element contributorsTab = contributorsTabs.get(0);
				String contributorsPageUrl = contributorsTab.attr("href");
				char[] dstString = new char[contributorsPageUrl.length()];
				contributorsPageUrl.getChars(2, contributorsPageUrl.length(),
						dstString, 0);

				URL pageUrl = new URL(new String("http://"
						+ new String(dstString)));

				Document contributorsPage = Jsoup.connect(pageUrl.toString())
						.get();
				Elements queryResult = contributorsPage
						.select("table.query-result");
				String filename = url.getPath();
				filename = filename.replace('/', '-');
				_stateListener.executingTask("Saving contributors info in "
						+ filename, _myId);
				SavePage.savePageContentWithUrl(queryResult.toString(),
						filename, url);
			}
		}

	}

}

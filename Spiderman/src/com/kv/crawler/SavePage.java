package com.kv.crawler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 
 * @author hadoopninja
 * 
 */
public class SavePage {
	private static String _rootDir;

	/**
	 * Save contents of a complete page to a file
	 * 
	 * @param url
	 * @param filename
	 */
	public static void saveUrlToFile(URL url, String filename) {
		InputStream is = null;
		BufferedInputStream in = new BufferedInputStream(is);
		FileOutputStream fos = null;
		try {
			is = url.openStream();
			fos = new FileOutputStream(new File(filename));
			byte[] buf = new byte[1048576];

			int n = in.read(buf);
			while (-1 != n) {
				fos.write(buf);
				n = in.read(buf);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Handles file copying in a new thread/current thread
	 * 
	 * @param pageContent
	 * @param filename
	 * @param bSpawnNewThread
	 */
	public static void savePageContentToFile(final String pageContent,
			final String filename, boolean bSpawnNewThread) {
		if (true == bSpawnNewThread) {
			new Thread() {
				public void run() {
					copyContentToFile(pageContent, filename);
				}
			}.start();
		} else {
			copyContentToFile(pageContent, filename);
		}
	}

	/**
	 * Copies the content passed into a file
	 * 
	 * @param pageContent
	 * @param filename
	 */
	private static void copyContentToFile(final String pageContent,
			final String filename) {
		FileOutputStream fos = null;
		try {
			String path = _rootDir + File.separator + filename + ".html";
			File f = new File(path);
			f.getParentFile().mkdirs();
			// f.createNewFile();
			fos = new FileOutputStream(f);
			fos.write(pageContent.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Sets the root directory for this project in which all files are to be
	 * created
	 * 
	 * @param rootDir
	 */
	public static void setRootDirectory(String rootDir) {
		_rootDir = rootDir;

	}

	/**
	 * Wrapper method to prepend the source page URL to the page content being
	 * written to a file
	 * 
	 * @param string
	 * @param filename
	 * @param sourceUrl
	 */
	public static void savePageContentWithUrl(String string, String filename,
			URL sourceUrl) {
		// Put source URl as first line in file
		String pageContent = "Source page URL:" + sourceUrl.toString() + "\n";
		pageContent += string;
		copyContentToFile(pageContent, filename);
	}
}

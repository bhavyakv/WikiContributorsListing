package com.wiki.contributors.mr;

import java.io.IOException;
import java.util.Locale;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Produces a mapping of contributors name and URL of page to which he/she has contributed
 * @author hadoopninja
 *
 */
public class WikiContributorsMapper extends
		Mapper<LongWritable, Text, Text, Text> {
	String _contributingPageUrl = null;

	public void map(LongWritable key, Text inputValue, Context context)
			throws IOException, InterruptedException {
		String htmlLine = inputValue.toString();
		String toLowerCaseHtml = htmlLine.toLowerCase(Locale.US);

		// Get the source URL for this page
		if (null == _contributingPageUrl
				&& true == toLowerCaseHtml.contains("source page url")) {
			int urlStartIndex = toLowerCaseHtml.indexOf(':') + 1;
			_contributingPageUrl = toLowerCaseHtml.substring(urlStartIndex);
		}
		// End of a page
		else if (true == toLowerCaseHtml.contains("</table>")) {
			_contributingPageUrl = null;
		}
		// Get contributors' name and write to map output
		else if ((null != _contributingPageUrl)
				&& (true == toLowerCaseHtml.contains("td"))
				&& (true == toLowerCaseHtml.contains("user"))) {
			Document htmlFormattedLine = Jsoup.parse(htmlLine);
			org.jsoup.select.Elements contributorInfoLine = htmlFormattedLine
					.select("a[href]");
			String contributor = null;
			if (contributorInfoLine.size() == 1) {
				contributor = contributorInfoLine.get(0).text();
			} else {
				System.err.println("Failed to extract contributor name");
			}

			context.write(new Text(contributor), new Text(_contributingPageUrl));
		}
	}

}

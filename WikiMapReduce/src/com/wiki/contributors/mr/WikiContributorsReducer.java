package com.wiki.contributors.mr;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Groups all pages that a person has contributed to
 * @author hadoopninja
 *
 */
public class WikiContributorsReducer extends Reducer<Text, Text, Text, Text> {
	String _prevContributorName = null;

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		Iterator<Text> valuesIterator = values.iterator();

		// Write the contributor name to output only at the beginning, followed
		// by all the pages he has contributed to
		Text outputKey = new Text(key);
		Text outputValue = new Text(valuesIterator.next());
		context.write(outputKey, outputValue);
		outputKey.set("");
		while (valuesIterator.hasNext()) {
			outputValue.set(valuesIterator.next());
			context.write(outputKey, outputValue);
		}

	}

}

package com.wiki.contributors.mr;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Driver class for the MapReduce job to list contributors and the pages they've contributed to
 * @author hadoopninja
 *
 */
public class DriverWithToolRunner extends Configured implements Tool {

	private static final String JOB_NAME = "Wiki contributors job";

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();

		Job mrJob = new Job(conf, JOB_NAME);
		mrJob.setJarByClass(getClass());

		Path inputFilePath = new Path(args[0]);
		Path outputFilePath = new Path(args[1]);
		FileSystem fs = FileSystem.get(URI.create(args[1]), getConf());
		if (true == fs.exists(outputFilePath)) {
			fs.delete(outputFilePath, true);
		}
		FileInputFormat.addInputPath(mrJob, inputFilePath);
		FileOutputFormat.setOutputPath(mrJob, outputFilePath);

		mrJob.setMapperClass(WikiContributorsMapper.class);
		mrJob.setReducerClass(WikiContributorsReducer.class);

		mrJob.setOutputKeyClass(Text.class);
		mrJob.setOutputValueClass(Text.class);

		return mrJob.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) {
		try {
			ToolRunner.run(new DriverWithToolRunner(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

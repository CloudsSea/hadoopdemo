package com.hadoop.demo;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;

public class WordCount extends Configured implements Tool{
	public static class Map extends Mapper<LongWritable, Text, Text,IntWritable>{
		
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		
		public void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);
			while (tokenizer.hasMoreElements()) {
				word.set(tokenizer.nextToken());
				context.write(word, one);
			}
		}
	}
	
	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable>{

		public void reduce(Text key, Iterator<IntWritable> values, Context context) throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			int sum = 0;
			while(values.hasNext()) {
				sum += values.next().get();
			}
			context.write(key, new IntWritable(sum));
			
		}
		
	}
	
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Job job = new Job(getConf());
		job.setJarByClass(WordCount.class);
		job.setJobName("wordcount");
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		boolean success = job.waitForCompletion(true);
		
		return success ? 0:1;
	}
	
	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new WordCount(), args);
		System.exit(ret);
		
	}
	
}

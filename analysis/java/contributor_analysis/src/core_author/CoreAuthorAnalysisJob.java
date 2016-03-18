package core_author;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import util.HdfsFileUtil;

public class CoreAuthorAnalysisJob {
	public static void main(String[] args) throws Exception {
		HdfsFileUtil.deletePath(args[1]);
		HdfsFileUtil.deletePath(args[2]);
		first_mr(args[0], args[1], args[3]);
		second_mr(args[1], args[2]);
	}
	
	public static boolean first_mr(String input, String middle, String extra) throws Exception{
		Configuration conf = new Configuration();
		// must set conf before create job
		conf.set("textinputformat.record.delimiter", "\ncommit ");
	    
	    Job job = Job.getInstance(conf, "core_author_analysis");
	    
	    job.setJarByClass(CoreAuthorAnalysisJob.class);
	    job.setMapperClass(FirstMaper.class);
	    job.setCombinerClass(SumReducer.class);
	    job.setReducerClass(SumReducer.class);
	    
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(IntWritable.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    
		job.setNumReduceTasks(1);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(input));
	    FileOutputFormat.setOutputPath(job, new Path(middle));

	    boolean res = job.waitForCompletion(true) ? true : false;
	    
	    HdfsFileUtil.writeCounterToFile(job, "org.apache.hadoop.mapred.Task$Counter", "MAP_OUTPUT_RECORDS", extra, "author_number");
	    
	    return res;
	}
	
	public static boolean second_mr(String middle, String output) throws Exception{
		Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "core_author_analysis_second");
	    
	    job.setJarByClass(CoreAuthorAnalysisJob.class);
	    job.setMapperClass(SortMapper.class);
	    job.setReducerClass(SortReducer.class);
	    
	    job.setMapOutputKeyClass(IntWritable.class);
	    job.setMapOutputValueClass(Text.class);
	    job.setOutputKeyClass(IntWritable.class);
	    job.setOutputValueClass(Text.class);
	    job.setSortComparatorClass(util.ReverseIntComparator.class);
	    
		job.setNumReduceTasks(1);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(middle));
	    FileOutputFormat.setOutputPath(job, new Path(output));

	    boolean res = job.waitForCompletion(true) ? true : false;
	    
	    return res;
	}
}

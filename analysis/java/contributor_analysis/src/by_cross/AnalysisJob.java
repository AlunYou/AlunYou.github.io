package by_cross;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import reusable.IntTextDefaultReducer;
import reusable.TextIntLineSortMapper;
import util.HdfsFileUtil;

public class AnalysisJob {
	public static void main(String[] args) throws Exception {
		HdfsFileUtil.deletePath(args[1]);
		HdfsFileUtil.deletePath(args[2]);
		HdfsFileUtil.deletePath(args[3]);
		first_mr(args[0], args[1] + "/1");
		second_mr(args[1] + "/1", args[1] + "/2", args[3]);
		third_mr(args[1] + "/2", args[2]);
	}
	
	public static boolean first_mr(String input, String middle) throws Exception{
		Configuration conf = new Configuration();
		// must set conf before create job
		conf.set("textinputformat.record.delimiter", "\ncommit ");
	    
	    Job job = Job.getInstance(conf, "cross_analysis");
	    
	    job.setJarByClass(AnalysisJob.class);
	    job.setMapperClass(FirstMaper.class);
	    //Must not use combiner here, it's not about efficiency, it's about correctness. use combiner will output text,text, but the reducer needs text, int
	    //job.setCombinerClass(FirstReducer.class);
	    job.setReducerClass(FirstReducer.class);
	    
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(IntWritable.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    
		job.setNumReduceTasks(1);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(input));
	    FileOutputFormat.setOutputPath(job, new Path(middle));

	    boolean res = job.waitForCompletion(true) ? true : false;
	    
	    return res;
	}
	
	public static boolean second_mr(String middle, String output, String extra) throws Exception{
		Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "cross_analysis_second");
	    
	    job.setJarByClass(AnalysisJob.class);
	    job.setMapperClass(SecondMapper.class);
	    job.setReducerClass(SecondReducer.class);
	    
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(Text.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    
		job.setNumReduceTasks(1);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(middle));
	    FileOutputFormat.setOutputPath(job, new Path(output));

	    boolean res = job.waitForCompletion(true) ? true : false;
	    
	    HdfsFileUtil.writeCounterToFile(job, "org.apache.hadoop.mapred.Task$Counter", "REDUCE_INPUT_GROUPS", extra, "author_number");
	    HdfsFileUtil.writeCounterToFile(job, "org.apache.hadoop.mapred.Task$Counter", "REDUCE_OUTPUT_RECORDS", extra, "cross_author_number");
	    
	    return res;
	}
	
	public static boolean third_mr(String middle, String output) throws Exception{
		Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "cross_analysis_third");
	    
	    job.setJarByClass(AnalysisJob.class);
	    job.setMapperClass(TextIntLineSortMapper.class);
	    job.setReducerClass(IntTextDefaultReducer.class);
	    
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

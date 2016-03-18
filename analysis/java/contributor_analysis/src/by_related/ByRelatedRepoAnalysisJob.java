package by_related;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import core_author.SortMapper;
import core_author.SortReducer;
import util.HdfsFileUtil;

public class ByRelatedRepoAnalysisJob {
	public static void main(String[] args) throws Exception {
		HdfsFileUtil.deletePath(args[1]);
		HdfsFileUtil.deletePath(args[2]);
		HdfsFileUtil.deletePath(args[3]);
		HdfsFileUtil.deletePath(args[4]);
		first_mr(args[0], args[1]);
		second_mr(args[1], args[2]);
		third_mr(args[2], args[3]);
		fourth_mr(args[3], args[4]);
	}
	
	public static boolean first_mr(String input, String middle) throws Exception{
		Configuration conf = new Configuration();
		// must set conf before create job
		conf.set("textinputformat.record.delimiter", "\ncommit ");
	    
	    Job job = Job.getInstance(conf, "by_related_analysis");
	    
	    job.setJarByClass(ByRelatedRepoAnalysisJob.class);
	    job.setMapperClass(by_cross.FirstMaper.class);
	    //Must not use combiner here, it's not about efficiency, it's about correctness. use combiner will output text,text, but the reducer needs text, int
	    //job.setCombinerClass(FirstReducer.class);
	    job.setReducerClass(by_cross.FirstReducer.class);
	    
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
	
	public static boolean second_mr(String middle, String output) throws Exception{
		Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "by_related_analysis_second");
	    
	    job.setJarByClass(ByRelatedRepoAnalysisJob.class);
	    job.setMapperClass(by_cross.SecondMapper.class);
	    job.setReducerClass(SecondReducer.class);
	    
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(Text.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    
		job.setNumReduceTasks(1);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(middle));
	    FileOutputFormat.setOutputPath(job, new Path(output));

	    boolean res = job.waitForCompletion(true) ? true : false;
	    
	    return res;
	}
	
	public static boolean third_mr(String middle, String output) throws Exception{
		Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "by_related_analysis_third");
	    
	    job.setJarByClass(ByRelatedRepoAnalysisJob.class);
	    job.setMapperClass(LineAsKeyMapper.class);
	    job.setReducerClass(SumRelatedReducer.class);
	    
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(IntWritable.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    //job.setSortComparatorClass(util.ReverseIntComparator.class);
	    
		job.setNumReduceTasks(1);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(middle));
	    FileOutputFormat.setOutputPath(job, new Path(output));

	    boolean res = job.waitForCompletion(true) ? true : false;
	    
	    return res;
	}
	
	public static boolean fourth_mr(String middle, String output) throws Exception{
		Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "by_related_analysis_fourth");
	    
	    job.setJarByClass(ByRelatedRepoAnalysisJob.class);
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

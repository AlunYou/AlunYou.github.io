package by_author;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import util.HdfsFileUtil;

public class AnalysisJob {
	public static void main(String[] args) throws Exception {
		HdfsFileUtil.deletePath(args[1]);
		HdfsFileUtil.deletePath(args[2]);
		HdfsFileUtil.deletePath(args[3]);
		first_mr(args[0], args[2], args[3]);
	}
	
	public static boolean first_mr(String input, String output, String extra) throws Exception{
		Configuration conf = new Configuration();
		// must set conf before create job
		conf.set("textinputformat.record.delimiter", "\ncommit ");
	    
	    Job job = Job.getInstance(conf, "by_author_analysis");
	    
	    job.setJarByClass(AnalysisJob.class);
	    job.setMapperClass(FirstMaper.class);
	    job.setCombinerClass(FirstReducer.class);
	    job.setReducerClass(FirstReducer.class);
	    
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(IntWritable.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    
		job.setNumReduceTasks(1);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(input));
	    FileOutputFormat.setOutputPath(job, new Path(output));

	    boolean res = job.waitForCompletion(true) ? true : false;
	    
	    HdfsFileUtil.writeCounterToFile(job, AUTHOR_COUNTER.FULL_TIME, extra, "full_time_author_number");
	    HdfsFileUtil.writeCounterToFile(job, AUTHOR_COUNTER.PART_TIME, extra, "part_time_author_number");
	    
	    return res;
	}
}

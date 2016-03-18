package by_zone;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
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
		first_mr(args[0], args[1] + "/1");
		second_mr(args[1] + "/1", args[2]);
	}
	
	public static boolean first_mr(String input, String output) throws Exception{
		Configuration conf = new Configuration();
		// must set conf before create job
		conf.set("textinputformat.record.delimiter", "\ncommit ");
	    
	    Job job = Job.getInstance(conf, "contributor_analysis_first");
	    
	    job.setJarByClass(AnalysisJob.class);
	    job.setMapperClass(FirstMaper.class);
	    job.setCombinerClass(FirstReducer.class);
	    job.setReducerClass(FirstReducer.class);
	    job.setPartitionerClass(TimezonePartitioner.class);
	    
	    job.setMapOutputKeyClass(TripleKey.class);
	    job.setMapOutputValueClass(LongWritable.class);
	    job.setOutputKeyClass(TripleKey.class);
	    job.setOutputValueClass(LongWritable.class);
	    
		
		job.setNumReduceTasks(24 * 2);//24 time zones, and each time zone we have a reduce about overtime or non-overtime
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(input));
	    FileOutputFormat.setOutputPath(job, new Path(output));

	    return job.waitForCompletion(true) ? true : false;
	}
	
	public static boolean second_mr(String input, String output) throws Exception{
		Configuration conf = new Configuration();
		// must set conf before create job
		//conf.set("textinputformat.record.delimiter", "\ncommit ");
	    
	    Job job = Job.getInstance(conf, "contributor_analysis_second");
	    
	    job.setJarByClass(AnalysisJob.class);
	    job.setMapperClass(SecondMaper.class);
	    //job.setCombinerClass(SecondReducer.class);
	    job.setReducerClass(SecondReducer.class);
	    //job.setPartitionerClass(TimezonePartitioner.class);
	    
	    job.setMapOutputKeyClass(TripleKey.class);
	    job.setMapOutputValueClass(LongWritable.class);
	    job.setOutputKeyClass(PairKey.class);
	    job.setOutputValueClass(PairWritable.class);
	    job.setGroupingComparatorClass(TripleKey.FirstTwoOnlyComparator.class);
	    
		
		job.setNumReduceTasks(1);//want to get a result file
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(input));
	    FileOutputFormat.setOutputPath(job, new Path(output));

	    return job.waitForCompletion(true) ? true : false;
	}
}

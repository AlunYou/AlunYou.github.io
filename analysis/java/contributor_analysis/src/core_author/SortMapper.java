package core_author;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

public class SortMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
	private IntWritable commit_num = new IntWritable();
	private Text author = new Text();
	private String[] splits = null;

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
	   String line = value.toString();
	   splits = line.split("\\s+");
	   if(splits.length >= 2){
		   int num = Integer.parseInt(splits[splits.length - 1]);
		   String email = splits[0];
		   commit_num.set(num);
		   author.set(email);
		   context.write(commit_num, author);
	   }
	}
}
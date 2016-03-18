package by_core_author;

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
		   StringBuilder sb = new StringBuilder();
		   for(int i=0; i<splits.length-1; i++){
			   sb.append(splits[i]);
			   sb.append(" ");
		   }
		   String email = sb.toString();
		   commit_num.set(num);
		   author.set(email);
		   context.write(commit_num, author);
	   }
	}
}
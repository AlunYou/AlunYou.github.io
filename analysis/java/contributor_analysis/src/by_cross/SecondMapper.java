package by_cross;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

public class SecondMapper extends Mapper<LongWritable, Text, Text, Text> {
	private Text result_key = new Text();
	private Text result_value = new Text();
	private String[] splits = null;

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
	   String line = value.toString();
	   splits = line.split("\\s+");
	   if(splits.length >= 2){
		   String email = splits[0];
		   String repo = splits[1];
		   result_key.set(email);
		   result_value.set(repo);
		   context.write(result_key, result_value);
	   }
	}
}
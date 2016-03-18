package by_related;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

public class SplitKeyValueMapper extends Mapper<LongWritable, Text, Text, Text> {
	private Text result_key = new Text();
	private Text result_value = new Text();
	private String[] splits = null;
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
	   String line = value.toString();
	   splits = line.split("\\+s");
	   result_key.set(splits[0]);
	   result_value.set(splits[1]);
	   context.write(result_key, result_value);
	}
}
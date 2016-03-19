package reusable;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

public class IntIntLineSortMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
	private IntWritable result_key = new IntWritable();
	private IntWritable result_value = new IntWritable();
	private String[] splits = null;

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
	   String line = value.toString();
	   splits = line.split("\\s+");
	   if(splits.length == 2){
		   result_key.set(Integer.parseInt(splits[0]));
		   result_value.set(Integer.parseInt(splits[1]));
		   context.write(result_value, result_key);
	   }
	}
}


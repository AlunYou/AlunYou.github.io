package by_related;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

public class LineAsKeyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	private IntWritable result_value = new IntWritable();

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
	   result_value.set(1);
	   context.write(value, result_value);
	}
}
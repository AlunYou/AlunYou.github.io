package by_related;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

public class SumRelatedReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	private IntWritable result_value = new IntWritable();
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable val : values) {
			sum += val.get();
		}
		
		result_value.set(sum);
		context.write(key, result_value);
	}
}

package by_cross;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

public class SecondReducer extends Reducer<Text, Text, Text, IntWritable> {
	private IntWritable result = new IntWritable();

	@SuppressWarnings("unused")
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		int total = 0;
		for (Text val : values) {
			total += 1;
		}
		
		if(total > 1){
			result.set(total);
			context.write(key, result);
		}
	}
}

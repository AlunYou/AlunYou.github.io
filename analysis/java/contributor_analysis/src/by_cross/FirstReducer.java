package by_cross;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

public class FirstReducer extends Reducer<Text, IntWritable, Text, Text> {
	private Text result_key = new Text();
	private Text result_value = new Text();
	private String[] splits = null;

	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		
		//System.out.println("###############################" + key);
		
		splits = key.toString().split(",");
		if(splits.length >= 2){
			String email = splits[0];
			String repo = splits[1];
			
			result_key.set(email);
			result_value.set(repo);

			context.write(result_key, result_value);
		}
		
	}
}

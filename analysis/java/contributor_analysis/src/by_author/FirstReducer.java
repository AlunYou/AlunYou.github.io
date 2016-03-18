package by_author;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

public class FirstReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	private IntWritable result = new IntWritable();

	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		long sum_worktime = 0;
		long sum_overtime = 0;
		for (IntWritable val : values) {
			if(val.get() == 1){
				sum_overtime += 1;
			}
			else{
				sum_worktime += 1;
			}
		}
		long sum_commit = sum_worktime + sum_overtime;
		if(sum_worktime >= sum_commit * 0.6){
			result.set(0);
			context.getCounter(AUTHOR_COUNTER.FULL_TIME).increment(1);
		}
		else{
			result.set(1);
			context.getCounter(AUTHOR_COUNTER.PART_TIME).increment(1);
		}

		context.write(key, result);
	}
}

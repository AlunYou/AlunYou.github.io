package contributor_analysis;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class FirstReducer extends Reducer<TripleKey, LongWritable, TripleKey, LongWritable> {
	private LongWritable result = new LongWritable();

	public void reduce(TripleKey key, Iterable<LongWritable> values, Context context)
			throws IOException, InterruptedException {
		long sum = 0;
		for (LongWritable val : values) {
			sum += val.get();
		}
		result.set(sum);
		context.write(key, result);
	}
}

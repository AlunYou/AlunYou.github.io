package contributor_analysis;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class SecondReducer extends Reducer<TripleKey, LongWritable, PairKey, PairWritable> {
	private PairKey result_key = new PairKey();
	private PairWritable result = new PairWritable();

	public void reduce(TripleKey key, Iterable<LongWritable> values, Context context)
			throws IOException, InterruptedException {
		long sum = 0;
		long size = 0;
		for (LongWritable val : values) {
			sum += val.get();
			size += 1;
		}
		result.setCommitNumber(sum);
		result.setAuthorNumber(size);
		
		result_key.setTimezone(key.getTimezone());
		result_key.setOvertime(key.getOvertime());
		
		context.write(result_key, result);
	}
}

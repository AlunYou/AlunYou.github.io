package contributor_analysis;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Partitioner;

public class TimezonePartitioner extends Partitioner<TripleKey, LongWritable>{

	@Override
	public int getPartition(TripleKey key, LongWritable value, int numReduceTasks) {
		if(key.getOvertime()){
			return (key.getTimezone() + 12) * 2;
		}
		else{
			return (key.getTimezone() + 12) * 2 + 1;
		}
	}
}

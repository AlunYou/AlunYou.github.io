package contributor_analysis;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;


public class SecondMaper extends  Mapper<LongWritable, Text, TripleKey, LongWritable> {
	
	private TripleKey combo_key = new TripleKey();
	private LongWritable map_value = new LongWritable();
	
	private String val = null;
	private String[] splits = null;
	private String[] tri_splits = null;
	
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		val = value.toString();
		if (val != null && !val.isEmpty()) {
			splits = val.split("\\s+");
			if(splits.length == 2){
				tri_splits = splits[0].split(":");
				if(tri_splits.length == 3){
					combo_key.setEmail(tri_splits[2]);
					combo_key.setOvertime(Integer.parseInt(tri_splits[1]) == 1 ? true : false);
					combo_key.setTimezone(Integer.parseInt(tri_splits[0]));
					map_value.set(Integer.parseInt(splits[1]));
					context.write(combo_key, map_value);
				}
			}
		}
	}

}

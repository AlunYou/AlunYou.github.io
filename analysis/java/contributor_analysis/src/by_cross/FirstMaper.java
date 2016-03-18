package by_cross;

import java.io.IOException;
//import java.time.DayOfWeek;
//import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;


public class FirstMaper extends  Mapper<LongWritable, Text, Text, IntWritable> {
	private Text map_key = new Text();
	private IntWritable map_value = new IntWritable();
	
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd kk:mm:ss yyyy Z");
	private ZonedDateTime zoned_datetime;
	
	private String val = null;
	private String line = null;
	private String[] splits = null;
	private String email = null;
	
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		val = value.toString();
		if (val != null && !val.isEmpty()) {
			splits = val.split("\n");
			for(int i=0; i<splits.length; i++){
				line = splits[i];
				if(line.startsWith("Author:")){
					int index_begin = line.indexOf('<');
					int index_end = line.indexOf('>', index_begin);
					if(index_begin > 0 && index_begin < line.length() && index_end > 0 && index_end < line.length()){
						email = line.substring(index_begin+1, index_end);
						email.trim();
					}
				}
				if(line.startsWith("Date:")){
					line = line.replace("Date:   ", "");
		            try{
		            	zoned_datetime = ZonedDateTime.parse(line, formatter);
		            }
		            catch(DateTimeParseException e){
		            	
		            }
				}
			}
			if(email != null && email.length() != 0 && zoned_datetime != null){
				
				String fileName = ((FileSplit) context.getInputSplit()).getPath().getName(); 
				map_key.set(email + "," + fileName);
				map_value.set(1);
				//System.out.println(map_key);
				context.write(map_key, map_value);
			}
			
		}
	}

}

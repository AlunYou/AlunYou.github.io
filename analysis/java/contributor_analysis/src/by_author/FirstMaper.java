package by_author;

import java.io.IOException;
import java.time.DayOfWeek;
//import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;


public class FirstMaper extends  Mapper<LongWritable, Text, Text, IntWritable> {
	private Text map_key = new Text();
	private IntWritable map_value = new IntWritable();
	
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d kk:mm:ss yyyy Z");
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
						email = email.replaceAll("\\s+", "+");
					}
				}
				if(line.startsWith("Date:")){
					line = line.replace("Date:   ", "");
		            try{
		            	zoned_datetime = ZonedDateTime.parse(line, formatter);
		            }
		            catch(DateTimeParseException e){
		            	String error = e.getMessage();
						System.err.println(error);
		            }
				}
			}
			if(email != null && email.length() != 0 && zoned_datetime != null){
				//ZoneOffset new_zone_offset = zoned_datetime.getOffset();
            	//int timezone = new_zone_offset.getTotalSeconds() / 3600;
				// 8:00 -> 19:00 is the regular work time.
				int hour = zoned_datetime.getHour();
				DayOfWeek day = zoned_datetime.getDayOfWeek();
				int overtime = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY || (hour > 18 || hour < 8)) ? 1 : 0;
				
				map_key.set(email);
				map_value.set(overtime);
				context.write(map_key, map_value);
			}
			
		}
	}

}

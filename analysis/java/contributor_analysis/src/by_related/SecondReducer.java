package by_related;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

public class SecondReducer extends Reducer<Text, Text, Text, Text> {
	private ArrayList<String> list = new ArrayList<String>();
	private Text result_key = new Text();
	private Text result_value = new Text();
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		list.clear();
		for (Text val : values) {
			list.add(val.toString());
		}
		if(list.size() > 1){
			for (int i=0; i<list.size(); i++) {
				String val1 = list.get(i);
				for (int j=i+1; j<list.size(); j++) {
					String val2 = list.get(j);
					int compare = val1.compareTo(val2);
					if(compare < 0){
						result_key.set(val1);
						result_value.set(val2);
						context.write(result_key, result_value);
					}
					else if(compare > 0){
						result_key.set(val2);
						result_value.set(val1);
						context.write(result_key, result_value);
					}
				}
			}
		}
	}
}

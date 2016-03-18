package util;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.*;

public class ReverseIntComparator extends WritableComparator {
    protected ReverseIntComparator() {
        super(IntWritable.class, true);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public int compare(WritableComparable w1, WritableComparable w2) {
        //LongWritable key1 = (LongWritable) w1;
        //LongWritable key2 = (LongWritable) w2;     
    	int v = w1.compareTo(w2);
        return -1 * w1.compareTo(w2);
    }
} 
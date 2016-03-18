package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Progressable;

public class HdfsFileUtil {
	
	public static boolean deletePath(String path) throws IOException{
		Configuration configuration = new Configuration();
	    
		boolean result = true;
		FileSystem hdfs = null;
	    try{
	    	hdfs = FileSystem.get(configuration);
	    	result = hdfs.delete(new Path(path), true);
	    }
	    catch(Exception e){
	    	result = false;
	    }
	    finally{
	    	if(hdfs != null){
	    		hdfs.close();
	    	}
	    }
	    return result;
	}

	public static boolean writeFile(String path, String content) throws IOException{
		Configuration configuration = new Configuration();
	    
		boolean result = true;
		FileSystem hdfs = null;
	    try{
	    	hdfs = FileSystem.get(configuration);
	    	Path file = new Path(path);
		    if (hdfs.exists(file)) { 
		    	hdfs.delete(file, true); 
		    } 
		    OutputStream os = hdfs.create(file,
		        new Progressable() {
		            public void progress() {
		                System.out.println(".");
		            }
		    });
		    BufferedWriter br = null;
		    try{
			    br = new BufferedWriter( new OutputStreamWriter( os, "UTF-8" ) );
			    br.write(content);
		    }
		    catch(Exception e){
		    	result = false;
		    }
		    finally{
		    	if(br != null){
		    		br.close();
		    	}
		    }
	    }
	    catch(Exception e){
	    	result = false;
	    }
	    finally{
	    	if(hdfs != null){
	    		hdfs.close();
	    	}
	    }
	    return result;
	}
	
	public static boolean writeCounterToFile(Job job, Enum<?> key, String output_dir, String fileName) throws IOException{
		Counters counters = job.getCounters();
	    Counter counter = counters.findCounter(key);
	    System.out.println(counter.getDisplayName() + ":" + counter.getValue());
	    
	    StringBuilder content = new StringBuilder();
	    content.append(counter.getValue());
	    String path = output_dir + "/" + fileName;
	    return HdfsFileUtil.writeFile(path, content.toString());
	}
	
	public static boolean writeCounterToFile(Job job, String package_name, String counter_name, String output_dir, String fileName) throws IOException{
		Counters counters = job.getCounters();
	    Counter counter = counters.findCounter(package_name, counter_name);
	    System.out.println(counter.getDisplayName() + ":" + counter.getValue());
	    
	    StringBuilder content = new StringBuilder();
	    content.append(counter.getValue());
	    String path = output_dir + "/" + fileName;
	    return HdfsFileUtil.writeFile(path, content.toString());
	}
}

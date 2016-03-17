package contributor_analysis;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;  
import org.apache.hadoop.io.*;  
 
public class PairKey implements WritableComparable<PairKey> {  

	private int timezone;
	private boolean overtime;
	
	@Override
	public void readFields(DataInput in) throws IOException {
		timezone = in.readInt();
		overtime = in.readBoolean();
	}
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(timezone);
		out.writeBoolean(overtime);
	}
	@Override
	public int compareTo(PairKey o) {
		int compare1 = Integer.compare(timezone, o.getTimezone());
		if(compare1 != 0){
			return compare1;
		}
		return Boolean.compare(overtime, o.getOvertime());
	}
	
	public int hashCode() { 
	    return timezone ^ (overtime ? 1 : 0);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(timezone);
		builder.append(":");
		builder.append(overtime ? 1 : 0);
		return builder.toString();
	}
	
	public int getTimezone() {
		return timezone;
	}
	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}
	public boolean getOvertime() {
		return overtime;
	}
	public void setOvertime(boolean overtime) {
		this.overtime = overtime;
	}

}
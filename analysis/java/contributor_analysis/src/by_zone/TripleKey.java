package by_zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;  
import org.apache.hadoop.io.*;  
 
public class TripleKey implements WritableComparable<TripleKey> {  

	private int timezone;
	private boolean overtime;
	private String email;
	
	
	@Override
	public void readFields(DataInput in) throws IOException {
		timezone = in.readInt();
		overtime = in.readBoolean();
		email = in.readUTF();
	}
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(timezone);
		out.writeBoolean(overtime);
		out.writeUTF(email);
	}
	@Override
	public int compareTo(TripleKey o) {
		int compare1 = Integer.compare(timezone, o.getTimezone());
		if(compare1 != 0){
			return compare1;
		}
		int compare2 = Boolean.compare(overtime, o.getOvertime());
		if(compare2 != 0){
			return compare2;
		}
		return email.compareTo(o.getEmail());
	}
	
	public int hashCode() { 
	    return timezone ^ (overtime ? 1 : 0) ^ email.hashCode();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(timezone);
		builder.append(":");
		builder.append(overtime ? 1 : 0);
		builder.append(":");
		builder.append(email);
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}  
	
	public static class FirstTwoOnlyComparator extends WritableComparator {
	    public FirstTwoOnlyComparator() {
	        super(TripleKey.class);
	    }
	     
	    @Override
	    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
	        int i1 = readInt(b1, s1);
	        int i2 = readInt(b2, s2);
	         
	        int compare1 = Integer.compare(i1, i2);
			if(compare1 != 0){
				return compare1;
			}
	         
	        boolean first1 = b1[s1+4] == 1;
	        boolean first2 = b2[s2+4] == 1;
	        return Boolean.compare(first1, first2);
	    }
	}

}
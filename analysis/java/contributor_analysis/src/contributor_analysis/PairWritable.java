package contributor_analysis;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class PairWritable implements Writable{
	
	private long authorNumber;
	private long commitNumber;

	@Override
	public void readFields(DataInput in) throws IOException {
		authorNumber = in.readLong();
		commitNumber = in.readLong();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(authorNumber);
		out.writeLong(commitNumber);
	}

	public long getCommitNumber() {
		return commitNumber;
	}

	public void setCommitNumber(long commit_number) {
		this.commitNumber = commit_number;
	}

	public long getAuthorNumber() {
		return authorNumber;
	}

	public void setAuthorNumber(long author_number) {
		this.authorNumber = author_number;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(authorNumber);
		builder.append(":");
		builder.append(commitNumber);
		return builder.toString();
	}
}

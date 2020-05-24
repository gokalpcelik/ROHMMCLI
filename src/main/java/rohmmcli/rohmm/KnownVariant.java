package rohmmcli.rohmm;

import htsjdk.tribble.Feature;

public interface KnownVariant {
		
	boolean hasNext();
	
	int getNextPos();
	
	void closeIterator();
	
	void close();
	
	void createIterator(String contig, int start, int end);


}

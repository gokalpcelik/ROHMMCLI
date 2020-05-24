package rohmmcli.rohmm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.tribble.bed.BEDFeature;

public class BEDTypeKnownVariant implements KnownVariant {
	
	protected BEDReader bedrdr = null;
	protected CloseableIterator<BEDFeature> iter = null;
	
	public BEDTypeKnownVariant(File BED) {
		// TODO Auto-generated constructor stub
		try {
			bedrdr = new BEDReader(BED);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return iter.hasNext();
	}

	@Override
	public int getNextPos() {
		// TODO Auto-generated method stub
		return iter.next().getStart();
	}

	@Override
	public void closeIterator() {
		// TODO Auto-generated method stub
		iter.close();
		iter = null;

	}

	@Override
	public void createIterator(String contig, int start, int end) {
		// TODO Auto-generated method stub
		try {
			iter = bedrdr.getReader().query(contig, start, end);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		closeIterator();
		bedrdr.closeBEDReader();
		bedrdr = null;
		
	}

}

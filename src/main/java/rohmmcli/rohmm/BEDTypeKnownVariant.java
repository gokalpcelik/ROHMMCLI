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
			this.bedrdr = new BEDReader(BED);
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return this.iter.hasNext();
	}

	@Override
	public int getNextPos() {
		// TODO Auto-generated method stub
		return this.iter.next().getStart();
	}

	@Override
	public void closeIterator() {
		// TODO Auto-generated method stub
		if (this.iter != null) {
			this.iter.close();
		}
		this.iter = null;

	}

	@Override
	public void createIterator(String contig, int start, int end) {
		// TODO Auto-generated method stub
		try {
			this.iter = this.bedrdr.getReader().query(contig, start, end);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		this.closeIterator();
		this.bedrdr.closeBEDReader();
		this.bedrdr = null;

	}
}

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

		try {
			this.bedrdr = new BEDReader(BED);
		} catch (final FileNotFoundException e) {

			e.printStackTrace();
		}
	}

	@Override
	public boolean hasNext() {
		return this.iter.hasNext();
	}

	@Override
	public int getNextPos() {
		return this.iter.next().getStart();
	}

	@Override
	public void closeIterator() {
		if (this.iter != null) {
			this.iter.close();
		}
		this.iter = null;

	}

	@Override
	public void createIterator(String contig, int start, int end) {
		try {
			this.iter = this.bedrdr.getReader().query(contig, start, end);
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
		this.closeIterator();
		this.bedrdr.closeBEDReader();
		this.bedrdr = null;

	}
}

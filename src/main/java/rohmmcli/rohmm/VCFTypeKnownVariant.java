package rohmmcli.rohmm;

import java.io.File;
import java.io.FileNotFoundException;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;

public class VCFTypeKnownVariant implements KnownVariant {

	protected VCFReader vcfrdr = null;
	protected CloseableIterator<VariantContext> iter = null;

	public VCFTypeKnownVariant(File vcffile) {
		try {
			this.vcfrdr = new VCFReader(vcffile);
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
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
		this.iter = this.vcfrdr.getReader().query(contig, start, end);
	}

	@Override
	public void close() {
		this.closeIterator();
		this.vcfrdr.closeVCFReader();
		this.vcfrdr = null;
	}

}

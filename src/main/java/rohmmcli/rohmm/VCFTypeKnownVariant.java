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
			vcfrdr = new VCFReader(vcffile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public int getNextPos() {
		return iter.next().getStart();
	}

	@Override
	public void closeIterator() {
		iter.close();
		iter = null;

	}

	@Override
	public void createIterator(String contig, int start, int end) {
		iter = vcfrdr.getReader().query(contig, start, end);
	}

	@Override
	public void close() {
		closeIterator();
		vcfrdr.closeVCFReader();
		vcfrdr = null;
	}

}

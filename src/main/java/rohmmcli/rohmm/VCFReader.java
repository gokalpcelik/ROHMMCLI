package rohmmcli.rohmm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.FileExtensions;
import htsjdk.tribble.exception.UnsortedFileException;
import htsjdk.tribble.index.Index;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.tribble.index.IndexFactory.IndexBalanceApproach;
import htsjdk.tribble.index.IndexFactory.IndexType;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

public class VCFReader {

	protected File VCFFile;
	protected File VCFIndex;
	protected VCFFileReader vcfReader;
	protected boolean hasPLTag = false;
	protected boolean hasADTag = false;
	public VCFReader(File VCF) throws FileNotFoundException {
		VCFFile = VCF;

		if (!vcfIndexExists()) {
			VCFIndex = new File(createIndex());
		}
		createReader();

	}

	protected void closeVCFReader() {
		vcfReader.close();
		vcfReader = null;
	}
	


	private boolean vcfIndexExists() {
		if (VCFFile.getAbsolutePath().endsWith(FileExtensions.VCF)) {
			VCFIndex = new File(VCFFile.getAbsolutePath() + FileExtensions.TRIBBLE_INDEX);
		} else if (VCFFile.getAbsolutePath().endsWith(FileExtensions.COMPRESSED_VCF)) {
			VCFIndex = new File(VCFFile.getAbsolutePath() + FileExtensions.TABIX_INDEX);
		}
		return VCFIndex.exists();
	}

	private String createIndex() {
		String idxpath = "";

		OverSeer.log(getClass().getSimpleName(), "File index not found. Creating one...", OverSeer.INFO);

		VCFCodec codec = new VCFCodec();

		if (VCFFile.getAbsolutePath().endsWith(FileExtensions.VCF)) {
			idxpath = VCFFile.getAbsolutePath() + FileExtensions.TRIBBLE_INDEX;

			try {
				Index idx = IndexFactory.createDynamicIndex(VCFFile, codec, IndexBalanceApproach.FOR_SEEK_TIME);
				idx.write(new File(idxpath));
			} catch (Exception e) {
				if (e instanceof UnsortedFileException) {
					OverSeer.log(getClass().getSimpleName(),
							"VCF File is not sorted. Sorting function is currently not implemented. Please sort your vcf using a proper tool.",
							OverSeer.ERROR);
				} else
					e.printStackTrace();

			}

		} else if (VCFFile.getAbsolutePath().endsWith(FileExtensions.COMPRESSED_VCF)) {
			idxpath = VCFFile.getAbsolutePath() + FileExtensions.TABIX_INDEX;

			try {
				Index idx = IndexFactory.createIndex(VCFFile, codec, IndexType.TABIX);
				idx.write(new File(idxpath));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		OverSeer.log(getClass().getSimpleName(), "Successfully created " + idxpath, OverSeer.INFO);

		return idxpath;
	}

	@SuppressWarnings("unused")
	private String sortVCF() {
		String sortedVCFpath = "";
		// implement if needed...
		return sortedVCFpath;
	}
	
	protected VCFHeader getHeader() {
	
		if(vcfReader != null)
			return vcfReader.getFileHeader();
		else 
			return null;
	}
	
	protected VCFFileReader getReader() {
		return vcfReader;
	}
	
	private void createReader() {
		vcfReader =  new VCFFileReader(VCFFile, VCFIndex);
	}

	protected String getVCFFileName() {
		return VCFFile.getName();
	}
	
	protected List<String> getAvailableContigsList() {

		ArrayList<String> availableContigs = new ArrayList<String>();

		

		List<SAMSequenceRecord> lists = vcfReader.getFileHeader().getSequenceDictionary().getSequences();

		for (SAMSequenceRecord record : lists) {

			String sequencename = record.getSequenceName();
			CloseableIterator<VariantContext> iter = vcfReader.query(sequencename, 1, Integer.MAX_VALUE);
			if (iter.hasNext()) {
				
				VariantContext temp = iter.next();
				
				if(temp.getGenotype(0).hasLikelihoods())
					hasPLTag = true;
				
				if(temp.getGenotype(0).hasAD())
					hasADTag = true;
				
				availableContigs.add(sequencename);
			}

			iter.close();

		}
		if (availableContigs.size() > 0)
			return availableContigs;

		return null;

	}

	protected List<String> getVCFSampleList() {
		return getHeader().getSampleNamesInOrder();
	}

}

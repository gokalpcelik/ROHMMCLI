package rohmmcli.rohmm;

import java.io.File;
import java.io.FileNotFoundException;

import htsjdk.samtools.util.FileExtensions;
import htsjdk.tribble.index.Index;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.tribble.index.IndexFactory.IndexBalanceApproach;
import htsjdk.tribble.index.IndexFactory.IndexType;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFFileReader;

public class VCFReader {

	protected File VCFFile;
	protected File VCFIndex;

	public VCFReader(String vcfpath) throws FileNotFoundException {
		VCFFile = new File(vcfpath);

		if (!vcfIndexExists()) {
			VCFIndex = new File(createIndex());
		}

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

		Utility.log(getClass().getSimpleName(), "File index not found. Creating one...", Utility.INFO);

		VCFCodec codec = new VCFCodec();

		if (VCFFile.getAbsolutePath().endsWith(FileExtensions.VCF)) {
			idxpath = VCFFile.getAbsolutePath() + FileExtensions.TRIBBLE_INDEX;

			try {
				Index idx = IndexFactory.createDynamicIndex(VCFFile, codec, IndexBalanceApproach.FOR_SEEK_TIME);
				idx.write(new File(idxpath));
			} catch (Exception e) {
				e.printStackTrace();
				// will implement this part if needed.
				/*
				 * if(e instanceof UnsortedFileException) { sortVCF(); }
				 */
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

		Utility.log(getClass().getSimpleName(), "Successfully created " + idxpath, Utility.INFO);

		return idxpath;
	}

	@SuppressWarnings("unused")
	private String sortVCF() {
		String sortedVCFpath = "";
		// implement if needed...
		return sortedVCFpath;
	}

	protected VCFFileReader createReader() {
		return new VCFFileReader(VCFFile, VCFIndex);
	}

	protected String getVCFFileName() {
		return VCFFile.getName();
	}

}

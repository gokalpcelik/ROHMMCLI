/*
 * Author : Gokalp Celik
 *
 * Date : May 26, 2020
 *
 */
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
		this.VCFFile = VCF;

		try {
			if (!this.vcfIndexExists()) {
				this.VCFIndex = new File(this.createIndex());
			}
			this.createReader();
		} catch (final Exception e) {
			// TODO: handle exception
		}

	}

	protected void closeVCFReader() {
		this.vcfReader.close();
		this.vcfReader = null;
	}

	private boolean vcfIndexExists() throws Exception {
		try {
			if (this.VCFFile.getAbsolutePath().endsWith(FileExtensions.VCF)) {
				this.VCFIndex = new File(this.VCFFile.getAbsolutePath() + FileExtensions.TRIBBLE_INDEX);
			} else if (this.VCFFile.getAbsolutePath().endsWith(FileExtensions.COMPRESSED_VCF)) {
				this.VCFIndex = new File(this.VCFFile.getAbsolutePath() + FileExtensions.TABIX_INDEX);
			}
			return this.VCFIndex.exists();
		} catch (final Exception e) {
			// TODO: handle exception
			OverSeer.log(this.getClass().getSimpleName(), "Problematic VCF File or not a VCF File", OverSeer.ERROR);
			throw new Exception("Improper file format");
		}
	}

	private String createIndex() {
		String idxpath = "";

		OverSeer.log(this.getClass().getSimpleName(), "File index not found. Creating one...", OverSeer.INFO);

		final VCFCodec codec = new VCFCodec();

		if (this.VCFFile.getAbsolutePath().endsWith(FileExtensions.VCF)) {
			idxpath = this.VCFFile.getAbsolutePath() + FileExtensions.TRIBBLE_INDEX;

			try {
				final Index idx = IndexFactory.createDynamicIndex(this.VCFFile, codec,
						IndexBalanceApproach.FOR_SEEK_TIME);
				idx.write(new File(idxpath));
			} catch (final Exception e) {
				if (e instanceof UnsortedFileException) {
					OverSeer.log(this.getClass().getSimpleName(),
							"VCF File is not sorted. Sorting function is currently not implemented. Please sort your vcf using a proper tool.",
							OverSeer.ERROR);
				} else {
					e.printStackTrace();
				}

			}

		} else if (this.VCFFile.getAbsolutePath().endsWith(FileExtensions.COMPRESSED_VCF)) {
			idxpath = this.VCFFile.getAbsolutePath() + FileExtensions.TABIX_INDEX;

			try {
				final Index idx = IndexFactory.createIndex(this.VCFFile, codec, IndexType.TABIX);
				idx.write(new File(idxpath));
			} catch (final Exception e) {
				e.printStackTrace();
			}

		}

		OverSeer.log(this.getClass().getSimpleName(), "Successfully created " + idxpath, OverSeer.INFO);

		return idxpath;
	}

	@SuppressWarnings("unused")
	private String sortVCF() {
		final String sortedVCFpath = "";
		// implement if needed...
		return sortedVCFpath;
	}

	protected VCFHeader getHeader() {

		if (this.vcfReader != null) {
			return this.vcfReader.getFileHeader();
		} else {
			return null;
		}
	}

	protected VCFFileReader getReader() {
		return this.vcfReader;
	}

	private void createReader() {
		this.vcfReader = new VCFFileReader(this.VCFFile, this.VCFIndex);
	}

	protected String getVCFFileName() {
		return this.VCFFile.getName();
	}

	protected List<String> getAvailableContigsList() {
		try {
			final ArrayList<String> availableContigs = new ArrayList<>();

			final List<SAMSequenceRecord> lists = this.vcfReader.getFileHeader().getSequenceDictionary().getSequences();

			for (final SAMSequenceRecord record : lists) {

				final String sequencename = record.getSequenceName();
				final CloseableIterator<VariantContext> iter = this.vcfReader.query(sequencename, 1, Integer.MAX_VALUE);
				if (iter.hasNext()) {

					final VariantContext temp = iter.next();

					if (temp.getGenotype(0).hasLikelihoods()) {
						this.hasPLTag = true;
					}

					if (temp.getGenotype(0).hasAD()) {
						this.hasADTag = true;
					}

					availableContigs.add(sequencename);
				}

				iter.close();

			}
			if (availableContigs.size() > 0) {
				return availableContigs;
			}
		} catch (final Exception e) {
			// TODO: handle exception
			return null;
		}
		return null;

	}

	protected List<String> getVCFSampleList() {
		try {
			return this.getHeader().getSampleNamesInOrder();
		} catch (final Exception e) {
			// TODO: handle exception
			return null;
		}
	}

}

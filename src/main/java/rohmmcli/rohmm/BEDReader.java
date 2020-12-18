package rohmmcli.rohmm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import htsjdk.samtools.util.FileExtensions;
import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.TabixFeatureReader;
import htsjdk.tribble.TribbleIndexedFeatureReader;
import htsjdk.tribble.bed.BEDCodec;
import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.exception.UnsortedFileException;
import htsjdk.tribble.index.Index;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.tribble.index.IndexFactory.IndexBalanceApproach;
import htsjdk.tribble.index.IndexFactory.IndexType;
import htsjdk.tribble.readers.LineIterator;

public class BEDReader {

	protected File BEDFile;
	protected File BEDIndex;
	protected boolean tribbletype = false;
	protected AbstractFeatureReader<BEDFeature, LineIterator> bedReader;

	public BEDReader(File BED) throws FileNotFoundException {
		this.BEDFile = BED;
		if (!this.bedIndexExists()) {
			this.BEDIndex = new File(this.createIndex());
		}
		this.createReader();
	}

	protected void closeBEDReader() {
		try {
			this.bedReader.close();
			this.bedReader = null;
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean bedIndexExists() {
		if (this.BEDFile.getAbsolutePath().endsWith(FileExtensions.BED)) {
			this.tribbletype = true;
			this.BEDIndex = new File(this.BEDFile.getAbsolutePath() + FileExtensions.TRIBBLE_INDEX);
		} else if (this.BEDFile.getAbsolutePath().endsWith("bed.gz")) {
			this.BEDIndex = new File(this.BEDFile.getAbsolutePath() + FileExtensions.TABIX_INDEX);
		}
		return this.BEDIndex.exists();
	}

	private String createIndex() {
		String idxpath = "";

		OverSeer.log(this.getClass().getSimpleName(), "File index not found. Creating one...", OverSeer.INFO);

		final BEDCodec codec = new BEDCodec();

		if (this.tribbletype) {
			idxpath = this.BEDFile.getAbsolutePath() + FileExtensions.TRIBBLE_INDEX;

			try {
				final Index idx = IndexFactory.createDynamicIndex(this.BEDFile, codec,
						IndexBalanceApproach.FOR_SEEK_TIME);
				idx.write(new File(idxpath));
			} catch (final Exception e) {
				if (e instanceof UnsortedFileException) {
					OverSeer.log(this.getClass().getSimpleName(),
							"BED File is not sorted. Sorting function is currently not implemented. Please sort your vcf using a proper tool.",
							OverSeer.ERROR);
				} else {
					e.printStackTrace();
				}

			}

		} else {
			idxpath = this.BEDFile.getAbsolutePath() + FileExtensions.TABIX_INDEX;

			try {
				final Index idx = IndexFactory.createIndex(this.BEDFile, codec, IndexType.TABIX);
				idx.write(new File(idxpath));
			} catch (final Exception e) {
				if (e instanceof UnsortedFileException) {
					OverSeer.log(this.getClass().getSimpleName(),
							"BED File is not sorted. Sorting function is currently not implemented. Please sort your vcf using a proper tool.",
							OverSeer.ERROR);
				} else {
					e.printStackTrace();
				}
			}

		}

		OverSeer.log(this.getClass().getSimpleName(), "Successfully created " + idxpath, OverSeer.INFO);

		return idxpath;
	}

	protected AbstractFeatureReader<BEDFeature, LineIterator> getReader() {

		try {
			return this.bedReader;
		} catch (final Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	private void createReader() {
		if (this.tribbletype) {
			try {
				this.bedReader = new TribbleIndexedFeatureReader<>(
						this.BEDFile.getAbsolutePath(), this.BEDIndex.getAbsolutePath(), new BEDCodec(), true);
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				this.bedReader = new TabixFeatureReader<>(this.BEDFile.getAbsolutePath(),
						this.BEDIndex.getAbsolutePath(), new BEDCodec());
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
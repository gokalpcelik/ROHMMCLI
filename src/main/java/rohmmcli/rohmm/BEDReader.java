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
		BEDFile = BED;
		if(!bedIndexExists()) {
			BEDIndex = new File(createIndex());
		}
		createReader();
	}

	protected void closeBEDReader() {
		try {
			bedReader.close();
			bedReader = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	private boolean bedIndexExists() {
		if(BEDFile.getAbsolutePath().endsWith(FileExtensions.BED)) {
			tribbletype = true;
			BEDIndex = new File(BEDFile.getAbsolutePath() + FileExtensions.TRIBBLE_INDEX);
		}	
		else if(BEDFile.getAbsolutePath().endsWith("bed.gz")) {
			BEDIndex = new File(BEDFile.getAbsolutePath() + FileExtensions.TABIX_INDEX);
		}
		return BEDIndex.exists();
	}

	private String createIndex() {
		String idxpath = "";

		OverSeer.log(getClass().getSimpleName(), "File index not found. Creating one...", OverSeer.INFO);

		BEDCodec codec = new BEDCodec();

		if (tribbletype) {
			idxpath = BEDFile.getAbsolutePath() + FileExtensions.TRIBBLE_INDEX;

			try {
				Index idx = IndexFactory.createDynamicIndex(BEDFile, codec, IndexBalanceApproach.FOR_SEEK_TIME);
				idx.write(new File(idxpath));
			} catch (Exception e) {
				if (e instanceof UnsortedFileException) {
					OverSeer.log(getClass().getSimpleName(),
							"BED File is not sorted. Sorting function is currently not implemented. Please sort your vcf using a proper tool.",
							OverSeer.ERROR);
				} else
					e.printStackTrace();

			}

		} else {
			idxpath = BEDFile.getAbsolutePath() + FileExtensions.TABIX_INDEX;

			try {
				Index idx = IndexFactory.createIndex(BEDFile, codec, IndexType.TABIX);
				idx.write(new File(idxpath));
			} catch (Exception e) {
				if (e instanceof UnsortedFileException) {
					OverSeer.log(getClass().getSimpleName(),
							"BED File is not sorted. Sorting function is currently not implemented. Please sort your vcf using a proper tool.",
							OverSeer.ERROR);
				} else
				e.printStackTrace();
			}

		}

		OverSeer.log(getClass().getSimpleName(), "Successfully created " + idxpath, OverSeer.INFO);

		return idxpath;
	}
	
	protected AbstractFeatureReader<BEDFeature, LineIterator> getReader() {
		
		try {
			return bedReader;
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	private void createReader() {
		if(tribbletype) {
			try {
				bedReader = new TribbleIndexedFeatureReader<BEDFeature, LineIterator>(BEDFile.getAbsolutePath(), BEDIndex.getAbsolutePath(), new BEDCodec(), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				bedReader = new TabixFeatureReader<BEDFeature, LineIterator>(BEDFile.getAbsolutePath(), BEDIndex.getAbsolutePath(), new BEDCodec());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
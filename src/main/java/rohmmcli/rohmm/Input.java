package rohmmcli.rohmm;

import java.io.File;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.tribble.readers.TabixReader;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class Input {
	// Check for all variables to see if a setter and a getter is present. Fix if
	// not present.
	protected TreeMap<Integer, String> inputdata;
	protected TreeMap<Integer, VariantInfo> inputdatanew;
	protected boolean HWenabled;
	protected boolean Distenabled = false;
	protected double minAFfilter;
	protected double maxAFfilter;
	protected int HomCount;
	protected String contigname;
	protected String gnomadpath;
	protected String vcfpath;
	protected String AFtag;
	protected double defaultMAF = 0.4;
	protected boolean skipindels = false;
	protected int fillfactor = 1;
	protected boolean usePLs = true;
	protected boolean useADs = false; // maybe deprecated??
	protected boolean legacywPL = false;
	protected boolean useGTs = false; // legacy
	protected boolean useUserPLs = false;
	protected int userPL = 30;
	protected boolean useFiller = false;
	protected int oldsampleidx = 0;
	protected double minisculeformissing = 0.000001;
	protected boolean forceCalculateAF = true;
	protected String[] samplenamearr;
	protected boolean skipzeroaf = false;
	protected HashSet<String> sampleset;
	

	// ROHMMCLI v 0.9g 03/08/2019 Gokalp Celik...
	public Input(boolean hw, double minaf, double maxaf, int homcount) throws Exception {
		HWenabled = hw;
		maxAFfilter = maxaf;
		minAFfilter = minaf;
		HomCount = homcount;

	}

//	Old codepath will be removed in the next release. This code path has served well however due to excessive disk access it is overtly slow and cannot be used anymore. 

	@Deprecated
	public void generateInput() throws Exception {
		inputdata = new TreeMap<>();

		if (useFiller) {
			System.err.println("Input with Gnomad");
			TabixReader gnomadrdr = new TabixReader(gnomadpath, gnomadpath + ".tbi");
			// System.err.println("Generating the input map - GNOMAD phase");
			TabixReader.Iterator gnomaditer = gnomadrdr.query(contigname.replaceAll("chr", ""));
			String gnomaditem;

			String info = "0,255,255" + (HWenabled ? "#" + minisculeformissing : "");
			String info2 = "0" + (HWenabled ? "#" + minisculeformissing : "");
			String info3 = "0," + userPL + "," + userPL + (HWenabled ? "#" + minisculeformissing : "");
			int counter = 1;
			while ((gnomaditem = gnomaditer.next()) != null) {
				if (counter % fillfactor == 0) {
					String[] arr = gnomaditem.split("\t");
					if (usePLs || legacywPL)
						inputdata.put(Integer.parseInt(arr[1]), info); // use alternate
					// 0,30,30
					else if (useUserPLs)
						inputdata.put(Integer.parseInt(arr[1]), info3); // added
					else
						inputdata.put(Integer.parseInt(arr[1]), info2);
					counter++;
				}

			}

			gnomadrdr.close();
		}

		// System.err.println("Generating the input map - VCF phase");
		VCFFileReader vcfrdr = new VCFFileReader(new File(vcfpath), new File(vcfpath + ".tbi"));
		CloseableIterator<VariantContext> vcfiter = queryWholeContig(vcfrdr, contigname);
		// int homcounter = 0;
		// vcfreading

		while (vcfiter.hasNext()) {
			VariantContext temp = vcfiter.next();

			double MAF = defaultMAF;

			try {
				MAF = temp.getAttributeAsDouble(AFtag, defaultMAF);
			} catch (Exception e) {
				// MAF = defaultMAF;
			}

			// insertgenotypecheck and classificationcode here when working with real
			// samples not from 1000G
			if (temp.isBiallelic() && temp.isNotFiltered() && (skipindels ? !temp.isIndel() : true)) { // MAF > 0.0
																										// olayini
																										// kaldirdik
																										// luzumsuz bir
																										// durumdu...

				if (usePLs && temp.getGenotype(oldsampleidx).hasPL()) {

					int[] PLs = temp.getGenotype(oldsampleidx).getPL();

					inputdata.put(temp.getStart(), PLs[0] + "," + PLs[1] + "," + PLs[2] + (HWenabled ? "#" + MAF : ""));
				} else if (useUserPLs) {
					if (temp.getGenotype(oldsampleidx).isHet()) {
						inputdata.put(temp.getStart(), userPL + ",0," + userPL + (HWenabled ? "#" + MAF : ""));
					} else if (temp.getGenotype(oldsampleidx).isHomRef()) {
						inputdata.put(temp.getStart(), "0," + userPL + "," + userPL + (HWenabled ? "#" + MAF : ""));
					} else if (temp.getGenotype(oldsampleidx).isHomVar()) {
						inputdata.put(temp.getStart(), userPL + "," + userPL + ",0" + (HWenabled ? "#" + MAF : ""));
					}

				}
				// not yet implemented do something
				else if (useADs && temp.getGenotype(oldsampleidx).hasAD())
					;
				// not yet implemented do something

				// this part is legacy now. May be removed completely in the final version. Keep
				// in mind. This part will be modified and merged to useADs completely.
				else if (useGTs) {

					/*
					 * boolean isHomVar = false; boolean isHomRef = false;
					 * 
					 * if (temp.getGenotype(sampleindex).isHomVar() && MAF > 0.5) { isHomRef = true;
					 * } else if (temp.getGenotype(sampleindex).isHomRef() && MAF > 0.5) { isHomVar
					 * = true; } else if (temp.getGenotype(sampleindex).isHom() && MAF == 0.5)
					 * isHomVar = true;
					 * 
					 * if (temp.getGenotype(sampleindex).isHet()) { BAFmap.put(temp.getStart(),
					 * "1"); } else if (isHomRef) { BAFmap.put(temp.getStart(), "0"); } else if
					 * (isHomVar) { BAFmap.put(temp.getStart(), "2");
					 * 
					 * }
					 */
					if (temp.getGenotype(oldsampleidx).isHet())
						inputdata.put(temp.getStart(), "1");
					else if (temp.getGenotype(oldsampleidx).isHomVar())
						inputdata.put(temp.getStart(), "2");
					else
						inputdata.put(temp.getStart(), "0");

				}

				else if (legacywPL) {

					int[] PLs = new int[3];
					if (temp.getGenotype(oldsampleidx).hasPL()) {

						PLs = temp.getGenotype(oldsampleidx).getPL();
					}

					if (temp.getGenotype(oldsampleidx).isHomVar() && MAF > 0.5) {

						if (useUserPLs) {
							PLs[0] = 0;
							PLs[1] = userPL;
							PLs[2] = userPL;
						} else {
							PLs[0] ^= PLs[2];
							PLs[2] ^= PLs[0];
							PLs[0] ^= PLs[2];
						}

					} else if (temp.getGenotype(oldsampleidx).isHomRef() && MAF < 0.5) {
						if (useUserPLs) {
							PLs[0] = 0;
							PLs[1] = userPL;
							PLs[2] = userPL;
						}
					} else if (temp.getGenotype(oldsampleidx).isHomRef() && MAF > 0.5) {
						if (useUserPLs) {
							PLs[0] = userPL;
							PLs[1] = userPL;
							PLs[2] = 0;
						} else {
							PLs[0] ^= PLs[2];
							PLs[2] ^= PLs[0];
							PLs[0] ^= PLs[2];
						}

					} else if (temp.getGenotype(oldsampleidx).isHomVar() && MAF < 0.5) {

						if (useUserPLs) {
							PLs[0] = userPL;
							PLs[1] = userPL;
							PLs[2] = 0;
						}

					} else if (temp.getGenotype(oldsampleidx).isHom() && MAF == 0.5)

						inputdata.put(temp.getStart(), PLs[0] + "," + PLs[1] + "," + PLs[2]);

				}

				/*
				 * else if (useGTs) {
				 * 
				 * if (temp.getGenotype(sampleindex).isHet()) { BAFmap.put(temp.getStart(), "1"
				 * + (HWenabled ? "#" + MAF : "")); homcounter = 0; } else if
				 * (temp.getGenotype(sampleindex).isHomRef()) { // do something extra here if
				 * needed. May // need to check // this thing out....
				 * BAFmap.put(temp.getStart(), "0" + (HWenabled ? "#" + MAF : "")); } else if
				 * (temp.getGenotype(sampleindex).isHomVar()) {
				 * 
				 * if (MAF <= minAFfilter) BAFmap.put(temp.getStart(), "2" + (HWenabled ? "#" +
				 * MAF : "")); else if (MAF > minAFfilter && MAF <= maxAFfilter && homcounter <
				 * HomCount) { BAFmap.put(temp.getStart(), "0" + (HWenabled ? "#" + MAF : ""));
				 * homcounter++; } else if (MAF > minAFfilter && MAF <= maxAFfilter &&
				 * homcounter >= HomCount) { BAFmap.put(temp.getStart(), "2" + (HWenabled ? "#"
				 * + MAF : "")); homcounter++; } else if (MAF > maxAFfilter) {
				 * BAFmap.put(temp.getStart(), "0" + (HWenabled ? "#" + MAF : "")); } } }
				 */

			}
		}

		vcfiter.close();
		vcfrdr.close();
		// System.err.println("Input map generated...");

	}

	public void generateInputNew() throws Exception {

		inputdatanew = new TreeMap<>();

		ImputeVariantInfo ivi = new ImputeVariantInfo();

		if (useFiller) {
			System.err.println("Input with Gnomad");
			TabixReader gnomadrdr = new TabixReader(gnomadpath, gnomadpath + ".tbi");
			// System.err.println("Generating the input map - GNOMAD phase");
			TabixReader.Iterator gnomaditer = gnomadrdr.query(contigname.replaceAll("chr", ""));
			String gnomaditem;
			int counter = 1;
			while ((gnomaditem = gnomaditer.next()) != null) {
				if (counter % fillfactor == 0) {
					String[] arr = gnomaditem.split("\t");
					//if (usePLs || legacywPL || useUserPLs) //temizlenecek kodlar arasinda....
						inputdatanew.put(Integer.parseInt(arr[1]), ivi);
					counter++;
				}

			}

			gnomadrdr.close();
		}

		// System.err.println("Generating the input map - VCF phase");
		VCFFileReader vcfrdr = new VCFFileReader(new File(vcfpath), new File(vcfpath + ".tbi"));
		CloseableIterator<VariantContext> vcfiter = queryWholeContig(vcfrdr, contigname);
		// int homcounter = 0;
		// vcfreading

		while (vcfiter.hasNext()) {
			VariantContext temp = vcfiter.next();

			// insertgenotypecheck and classificationcode here when working with real
			// samples not from 1000G
			if ((skipindels ? temp.isSNP() : true) && temp.isBiallelic() && temp.isNotFiltered()) { // MAF > 0.0 olayini
																									// kaldirdik

				int dAF = temp.getCalledChrCount(temp.getAlternateAllele(0), sampleset); // durumdu...

				if (dAF > 0) {
					SampleVariantInfo svi = new SampleVariantInfo(samplenamearr.length, useUserPLs, userPL);

					for (int spos = 0; spos < samplenamearr.length; spos++) {
						Genotype tempg = temp.getGenotype(samplenamearr[spos]);

						if (tempg.isCalled()) {

							if (tempg.hasLikelihoods())
								svi.addPL(tempg.getLikelihoods().getAsPLs(), spos); // this change makes it compatible
																					// with
																					// freebayes like callers that emit
																					// GL
																					// instead of PL

							if (tempg.isHet())
								svi.addGenotype(1, spos);
							else if (tempg.isHomVar())
								svi.addGenotype(2, spos);
							else
								svi.addGenotype(0, spos);
						} else {
							svi.addDefaultPL(spos);
							svi.addGenotype(0, spos);
						}
					}

					if (forceCalculateAF)
						svi.forceCalculateAF();
					else
						svi.addAF(temp.getAttributeAsDouble(AFtag, defaultMAF));

					inputdatanew.put(temp.getStart(), svi);

				} else if (!skipzeroaf)
					inputdatanew.put(temp.getStart(), ivi);
			}

		}
		vcfiter.close();
		vcfrdr.close();
		// System.err.println("Input map generated...");

	}

	// needed for fast whole contig query.
	public CloseableIterator<VariantContext> queryWholeContig(VCFFileReader vcfrdr, String contig) {
		return vcfrdr.query(contig, 1, Integer.MAX_VALUE);
	}

	public int countVariants(VCFFileReader vcfrdr) {
		int count = 0;

		CloseableIterator<VariantContext> iter = vcfrdr.iterator();

		while (iter.hasNext())
			count++;

		iter.close();

		vcfrdr.close();
		return count;

	}

	public Input setVCFPath(String path) {
		vcfpath = path;

		return this;
	}

	/*
	 * private int[] normalizePL(int[] PL) { int max = Math.max(PL[0],
	 * Math.max(PL[1], PL[2]));
	 * 
	 * if (max > 255) { PL[0] = (int) (PL[0] * (255 / (double) max)); PL[1] = (int)
	 * (PL[1] * (255 / (double) max)); PL[2] = (int) (PL[2] * (255 / (double) max));
	 * } return PL; }
	 */

	public Input setGNOMADPath(String path) {
		gnomadpath = path;

		return this;
	}

	public Input setDefaultMAF(double af) {
		defaultMAF = af;

		return this;
	}

	public Input setAFTag(String tag) {
		AFtag = tag;

		return this;
	}

	public void setContig(String ctg) {
		contigname = ctg;

	}

	@Deprecated
	public int[] getObservationSet() {
		int[] obs = new int[inputdata.size()];
		int count = 0;
		for (Entry<?, ?> e : inputdata.entrySet()) {
			obs[count] = Integer.parseInt(e.getValue().toString().split("#")[0]);
			count++;
		}

		return obs;
	}

	@Deprecated
	public int[] getObservationSetNew(int sampleindex) {
		int[] obs = new int[inputdatanew.size()];
		int count = 0;
		for (Entry<Integer, VariantInfo> e : inputdatanew.entrySet()) {
			obs[count] = e.getValue().getGenotype(sampleindex);
			count++;
		}

		return obs;
	}

	@Deprecated
	public int[][] getObservationSetPLs() {
		int[][] obs = new int[inputdata.size()][3];
		int count = 0;
		for (Entry<?, ?> e : inputdata.entrySet()) {
			obs[count][0] = Integer.parseInt(e.getValue().toString().split("#")[0].split(",")[0]);
			obs[count][1] = Integer.parseInt(e.getValue().toString().split("#")[0].split(",")[1]);
			obs[count][2] = Integer.parseInt(e.getValue().toString().split("#")[0].split(",")[2]);

			count++;
		}

		return obs;
	}

	@Deprecated
	public int[][] getObservationSetPLsNew(int sampleindex) {
		int[][] obs = new int[inputdatanew.size()][3];
		int count = 0;
		for (Entry<Integer, VariantInfo> e : inputdatanew.entrySet()) {
			// obs[count][0] = e.getValue().getPL(sampleindex)[0];//to make things
			// simpler...
			// obs[count][1] = e.getValue().getPL(sampleindex)[1];
			// obs[count][2] = e.getValue().getPL(sampleindex)[2];
			obs[count] = e.getValue().getPL(sampleindex);
			count++;
		}

		return obs;
	}

	public void setObsAndPLs(HMM hmm, int sampleindex) {
		int[] obsgt = new int[inputdatanew.size()];
		int[][] obspl = new int[inputdatanew.size()][3];

		int count = 0;
		for (Entry<Integer, VariantInfo> e : inputdatanew.entrySet()) {
			obsgt[count] = !(usePLs || useUserPLs || legacywPL) ? e.getValue().getGenotype(sampleindex) : 0;
			obspl[count] = (usePLs || useUserPLs || legacywPL) ? e.getValue().getPL(sampleindex) : null;
			count++;
		}

		hmm.GTs = !(usePLs || useUserPLs || legacywPL) ? obsgt : null;
		hmm.PLmatrix = (usePLs || useUserPLs || legacywPL) ? obspl : null;
	}

	@Deprecated
	public double[] getMAFSet() {
		double[] maf = new double[inputdata.size()];
		int count = 0;
		for (Entry<?, ?> e : inputdata.entrySet()) {
			maf[count] = Double.parseDouble(e.getValue().toString().split("#")[1]);
			count++;
		}

		return maf;
	}

	@Deprecated
	public double[] getMAFSetNew() {
		double[] maf = new double[inputdatanew.size()];
		int count = 0;
		for (Entry<Integer, VariantInfo> e : inputdatanew.entrySet()) {
			maf[count] = e.getValue().getAF();
			count++;
		}

		return maf;
	}

	@Deprecated
	public int[] getDistanceSet() {
		int[] distmap = new int[inputdata.size()];
		int count = 0;
		int DIST = 0;
		for (Integer k : inputdata.keySet()) {
			distmap[count] = k - DIST;
			DIST = k;
			count++;
		}

		return distmap;
	}

	@Deprecated
	public int[] getDistanceSetNew() {
		int[] distmap = new int[inputdatanew.size()];
		int count = 0;
		int DIST = 0;
		for (Integer k : inputdatanew.keySet()) {
			distmap[count] = k - DIST;
			DIST = k;
			count++;
		}

		return distmap;
	}

	public void setMAFAndDist(HMM hmm) {

		if (Distenabled || HWenabled) {
			int count = 0;
			int DIST = 0;

			int[] distmap = new int[inputdatanew.size()];

			double[] maf = new double[inputdatanew.size()];

			for (Entry<Integer, VariantInfo> e : inputdatanew.entrySet()) {
				int k = e.getKey();
				distmap[count] = Distenabled ? k - DIST : 0;
				DIST = k;
				maf[count] = HWenabled ? e.getValue().getAF() : 0.0;
				count++;
			}

			hmm.Dists = Distenabled ? distmap : null;
			hmm.MAFs = HWenabled ? maf : null;
		}
	}

	public void setDistEnabled() {
		Distenabled = true;
	}

	public boolean getHWmode() {
		return HWenabled;
	}

	@Deprecated
	public TreeMap<Integer, String> getInputData() {
		return inputdata;
	}

	public TreeMap<Integer, VariantInfo> getInputDataNew() {
		return inputdatanew;
	}

	public void killTreeMap() {

		inputdata = null;
		inputdatanew = null;

	}

	public void setSampleSet() {
		sampleset = new HashSet<>();
		for(String s : samplenamearr)
		{
			sampleset.add(s);
		}
	}

	public void scanVCF() {

	}

}

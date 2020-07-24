package rohmmcli.rohmm;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class Input {
	// Check for all variables to see if a setter and a getter is present. Fix if
	// not present.
	// protected TreeMap<Integer, String> inputdata;
	protected TreeMap<Integer, VariantInfo> inputData;
	protected boolean HWenabled = false;
	protected boolean Distenabled = false;
	protected String contigname;
	protected String gnomadpath;
	protected String vcfpath;
	protected String AFtag = null;
	protected double defaultMAF;
	protected boolean skipindels = false;
	protected int fillfactor = 1;
	protected boolean usePLs = true;
	protected boolean useADs = false;
	protected double ADThreshold = 0.2;
	protected int depthThreshold = 10;
	protected boolean legacywPL = false;
	@Deprecated
	protected boolean useGTs = false; // legacy
	protected boolean useUserPLs = false;
	protected int userPL = 30;
	protected boolean spikeIn = false;

	// protected int oldsampleidx = 0;
	protected double minisculeformissing = 0.000001;
	protected String[] samplenamearr;
	protected boolean skipzeroaf = false;
	protected HashSet<String> sampleset;

	// ROHMMCLI v 0.9g 03/08/2019 Gokalp Celik...
	public Input() {

	}
//	Old codepath will be removed in the next release. This code path has served well however due to excessive disk access it is overtly slow and cannot be used anymore.

	// @Deprecated
	/*
	 * public void generateInput() throws Exception { inputdata = new TreeMap<>();
	 *
	 * if (useFiller) { OverSeer.log(this.getClass().getSimpleName(),
	 * "Fill with GNOMAD", OverSeer.INFO); TabixReader gnomadrdr = new
	 * TabixReader(gnomadpath, gnomadpath + ".tbi"); //
	 * System.err.println("Generating the input map - GNOMAD phase");
	 * TabixReader.Iterator gnomaditer =
	 * gnomadrdr.query(contigname.replaceAll("chr", "")); String gnomaditem;
	 *
	 * String info = "0,255,255" + (HWenabled ? "#" + minisculeformissing : "");
	 * String info2 = "0" + (HWenabled ? "#" + minisculeformissing : ""); String
	 * info3 = "0," + userPL + "," + userPL + (HWenabled ? "#" + minisculeformissing
	 * : ""); int counter = 1; while ((gnomaditem = gnomaditer.next()) != null) { if
	 * (counter % fillfactor == 0) { String[] arr = gnomaditem.split("\t"); if
	 * (usePLs || legacywPL) inputdata.put(Integer.parseInt(arr[1]), info); // use
	 * alternate // 0,30,30 else if (useUserPLs)
	 * inputdata.put(Integer.parseInt(arr[1]), info3); // added else
	 * inputdata.put(Integer.parseInt(arr[1]), info2); counter++; }
	 *
	 * }
	 *
	 * gnomadrdr.close(); }
	 *
	 * // System.err.println("Generating the input map - VCF phase"); VCFFileReader
	 * vcfrdr = new VCFFileReader(new File(vcfpath), new File(vcfpath + ".tbi"));
	 * CloseableIterator<VariantContext> vcfiter = queryWholeContig(vcfrdr,
	 * contigname); // int homcounter = 0; // vcfreading
	 *
	 * while (vcfiter.hasNext()) { VariantContext temp = vcfiter.next();
	 *
	 * double MAF = defaultMAF;
	 *
	 * try { MAF = temp.getAttributeAsDouble(AFtag, defaultMAF); } catch (Exception
	 * e) { // MAF = defaultMAF; }
	 *
	 * // insertgenotypecheck and classificationcode here when working with real //
	 * samples not from 1000G if (temp.isBiallelic() && temp.isNotFiltered() &&
	 * (skipindels ? !temp.isIndel() : true)) { // MAF > 0.0 // olayini // kaldirdik
	 * // luzumsuz bir // durumdu...
	 *
	 * if (usePLs && temp.getGenotype(oldsampleidx).hasPL()) {
	 *
	 * int[] PLs = temp.getGenotype(oldsampleidx).getPL();
	 *
	 * inputdata.put(temp.getStart(), PLs[0] + "," + PLs[1] + "," + PLs[2] +
	 * (HWenabled ? "#" + MAF : "")); } else if (useUserPLs) { if
	 * (temp.getGenotype(oldsampleidx).isHet()) { inputdata.put(temp.getStart(),
	 * userPL + ",0," + userPL + (HWenabled ? "#" + MAF : "")); } else if
	 * (temp.getGenotype(oldsampleidx).isHomRef()) { inputdata.put(temp.getStart(),
	 * "0," + userPL + "," + userPL + (HWenabled ? "#" + MAF : "")); } else if
	 * (temp.getGenotype(oldsampleidx).isHomVar()) { inputdata.put(temp.getStart(),
	 * userPL + "," + userPL + ",0" + (HWenabled ? "#" + MAF : "")); }
	 *
	 * } // not yet implemented do something else if (useADs &&
	 * temp.getGenotype(oldsampleidx).hasAD()) ; // not yet implemented do something
	 *
	 * // this part is legacy now. May be removed completely in the final version.
	 * Keep // in mind. This part will be modified and merged to useADs completely.
	 * else if (useGTs) {
	 *
	 *
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
	 *
	 * if (temp.getGenotype(oldsampleidx).isHet()) inputdata.put(temp.getStart(),
	 * "1"); else if (temp.getGenotype(oldsampleidx).isHomVar())
	 * inputdata.put(temp.getStart(), "2"); else inputdata.put(temp.getStart(),
	 * "0");
	 *
	 * }
	 *
	 * else if (legacywPL) {
	 *
	 * int[] PLs = new int[3]; if (temp.getGenotype(oldsampleidx).hasPL()) {
	 *
	 * PLs = temp.getGenotype(oldsampleidx).getPL(); }
	 *
	 * if (temp.getGenotype(oldsampleidx).isHomVar() && MAF > 0.5) {
	 *
	 * if (useUserPLs) { PLs[0] = 0; PLs[1] = userPL; PLs[2] = userPL; } else {
	 * PLs[0] ^= PLs[2]; PLs[2] ^= PLs[0]; PLs[0] ^= PLs[2]; }
	 *
	 * } else if (temp.getGenotype(oldsampleidx).isHomRef() && MAF < 0.5) { if
	 * (useUserPLs) { PLs[0] = 0; PLs[1] = userPL; PLs[2] = userPL; } } else if
	 * (temp.getGenotype(oldsampleidx).isHomRef() && MAF > 0.5) { if (useUserPLs) {
	 * PLs[0] = userPL; PLs[1] = userPL; PLs[2] = 0; } else { PLs[0] ^= PLs[2];
	 * PLs[2] ^= PLs[0]; PLs[0] ^= PLs[2]; }
	 *
	 * } else if (temp.getGenotype(oldsampleidx).isHomVar() && MAF < 0.5) {
	 *
	 * if (useUserPLs) { PLs[0] = userPL; PLs[1] = userPL; PLs[2] = 0; }
	 *
	 * } else if (temp.getGenotype(oldsampleidx).isHom() && MAF == 0.5)
	 *
	 * inputdata.put(temp.getStart(), PLs[0] + "," + PLs[1] + "," + PLs[2]);
	 *
	 * }
	 *
	 *
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
	 *
	 *
	 * } }
	 *
	 * vcfiter.close(); // vcfrdr.close(); //
	 * System.err.println("Input map generated...");
	 *
	 * }
	 */

	public void generateInput() throws Exception {

		this.inputData = new TreeMap<>();
		TreeMap<Integer, String> nonSpikedFilter = new TreeMap<>();

		final ImputeVariantInfo ivi = new ImputeVariantInfo();

		/*
		 * if (spikeIn) { OverSeer.log(this.getClass().getSimpleName(),
		 * "Fill with GNOMAD", OverSeer.DEBUG); TabixReader gnomadrdr = new
		 * TabixReader(gnomadpath, gnomadpath + ".tbi");
		 * OverSeer.log(this.getClass().getSimpleName(),
		 * "Generating the input map - GNOMAD phase", OverSeer.DEBUG);
		 * TabixReader.Iterator gnomaditer =
		 * gnomadrdr.query(contigname.replaceAll("chr", "")); String gnomaditem; int
		 * counter = 1; while ((gnomaditem = gnomaditer.next()) != null) { if (counter %
		 * fillfactor == 0) { String[] arr = gnomaditem.split("\t"); // if (usePLs ||
		 * legacywPL || useUserPLs) //temizlenecek kodlar arasinda....
		 * inputdatanew.put(Integer.parseInt(arr[1]), ivi); counter++; }
		 *
		 * }
		 *
		 * gnomadrdr.close(); }
		 */

		if (OverSeer.knownVariant != null) {
			if (this.spikeIn) {
				OverSeer.knownVariant.createIterator(this.contigname, 1, Integer.MAX_VALUE);
				while (OverSeer.knownVariant.hasNext()) {
					this.inputData.put(OverSeer.knownVariant.getNextPos(), ivi);
				}
				OverSeer.knownVariant.closeIterator();
			} else {
				OverSeer.knownVariant.createIterator(this.contigname, 1, Integer.MAX_VALUE);
				while (OverSeer.knownVariant.hasNext()) {
					nonSpikedFilter.put(OverSeer.knownVariant.getNextPos(), null);
				}
				OverSeer.knownVariant.closeIterator();
			}
		}

		OverSeer.log(this.getClass().getSimpleName(), "Generating the input map - VCF phase", OverSeer.DEBUG);

		// this fixes the problem with unindexed and uncompressed vcf files. // BCF
		// support coming soon.
		final VCFFileReader vcfrdr = OverSeer.getVCFFileReader();
		final CloseableIterator<VariantContext> vcfiter = this.queryWholeContig(vcfrdr, this.contigname);
		// int homcounter = 0;
		// vcfreading
		final int sizecheck = this.inputData.size() == 0 ? nonSpikedFilter.size() : this.inputData.size();
		// System.err.println(inputdatanew.size());

		while (vcfiter.hasNext()) {
			final VariantContext temp = vcfiter.next();

			// insertgenotypecheck and classificationcode here when working with real
			// samples not from 1000G
			final Integer tempstart = temp.getStart();

			// System.err.println(tempstart + " " + inputdatanew.containsKey(tempstart) + "
			// " + inputdatanew.keySet().contains(tempstart));

			if ((this.skipindels ? temp.isSNP() : true) && temp.isBiallelic() && temp.isNotFiltered()
					&& (sizecheck != 0
							? OverSeer.filterUnknowns
									? this.spikeIn ? this.inputData.containsKey(tempstart)
											: nonSpikedFilter.containsKey(tempstart)
									: true
							: true)) { // MAF > 0.0 olayini
				// kaldirdik

				final int dAF = temp.getCalledChrCount(temp.getAlternateAllele(0), this.sampleset); // durumdu...

				if (dAF > 0) {
					final SampleVariantInfo svi = new SampleVariantInfo(this.samplenamearr.length, this.useUserPLs,
							this.userPL);

					for (int spos = 0; spos < this.samplenamearr.length; spos++) {
						final Genotype tempg = temp.getGenotype(this.samplenamearr[spos]);

						if (tempg.isCalled()) {
							if (tempg.hasLikelihoods() && !svi.FakePL) {
								svi.addPL(tempg.getLikelihoods().getAsPLs(), spos);
							}
							if (tempg.isHet()) {
								if (this.useADs) {
									if (this.isBalanced(tempg)) {
										svi.addGenotype(1, spos);
									} else if (this.isRefBiased(tempg)) {
										svi.addGenotype(0, spos);
										svi.addBalancedHomRefPL(spos);
									} else {
										svi.addGenotype(2, spos);
										svi.addBalancedHomVarPL(spos);
									}
								} else {
									svi.addGenotype(1, spos);
								}

							} else if (tempg.isHomVar()) {
								svi.addGenotype(2, spos);
							} else {
								svi.addGenotype(0, spos);
							}
						} else {
							svi.addDefaultPL(spos);
							svi.addGenotype(0, spos);
						}
					}

					if (this.AFtag == null) {
						if (!OverSeer.DMAF && this.sampleset.size() >= 30) {
							svi.forceCalculateAF();
						} else {
							svi.addAF(this.defaultMAF);
						}
					} else {
						svi.addAF(temp.getAttributeAsDouble(this.AFtag, this.defaultMAF));
					}

					this.inputData.put(temp.getStart(), svi);

				} else if (!this.skipzeroaf) {
					this.inputData.put(temp.getStart(), ivi);
				}
			}

		}
		nonSpikedFilter = null;
		vcfiter.close();
		// vcfrdr.close();
		OverSeer.log(this.getClass().getSimpleName(), "Input map generated...", OverSeer.DEBUG);

	}

	// needed for fast whole contig query.
	public CloseableIterator<VariantContext> queryWholeContig(VCFFileReader vcfrdr, String contig) {
		return vcfrdr.query(contig, 1, Integer.MAX_VALUE);
	}

	public Input setVCFPath(String path) {
		this.vcfpath = path;

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
		this.gnomadpath = path;

		return this;
	}

	public Input setDefaultMAF(double af) {
		this.defaultMAF = af;

		return this;
	}

	public Input setAFTag(String tag) {
		this.AFtag = tag;

		return this;
	}

	public void setContig(String ctg) {
		this.contigname = ctg;

	}

	/*
	 * public int[] getObservationSet() { int[] obs = new int[inputdata.size()]; int
	 * count = 0; for (Entry<?, ?> e : inputdata.entrySet()) { obs[count] =
	 * Integer.parseInt(e.getValue().toString().split("#")[0]); count++; }
	 *
	 * return obs; }
	 */

	/*
	 * public int[] getObservationSetNew(int sampleindex) { int[] obs = new
	 * int[inputdatanew.size()]; int count = 0; for (Entry<Integer, VariantInfo> e :
	 * inputdatanew.entrySet()) { obs[count] =
	 * e.getValue().getGenotype(sampleindex); count++; }
	 *
	 * return obs; }
	 */

	/*
	 * public int[][] getObservationSetPLs() { int[][] obs = new
	 * int[inputdata.size()][3]; int count = 0; for (Entry<?, ?> e :
	 * inputdata.entrySet()) { obs[count][0] =
	 * Integer.parseInt(e.getValue().toString().split("#")[0].split(",")[0]);
	 * obs[count][1] =
	 * Integer.parseInt(e.getValue().toString().split("#")[0].split(",")[1]);
	 * obs[count][2] =
	 * Integer.parseInt(e.getValue().toString().split("#")[0].split(",")[2]);
	 *
	 * count++; }
	 *
	 * return obs; }
	 */

	/*
	 * public int[][] getObservationSetPLsNew(int sampleindex) { int[][] obs = new
	 * int[inputdatanew.size()][3]; int count = 0; for (Entry<Integer, VariantInfo>
	 * e : inputdatanew.entrySet()) { // obs[count][0] =
	 * e.getValue().getPL(sampleindex)[0];//to make things // simpler... //
	 * obs[count][1] = e.getValue().getPL(sampleindex)[1]; // obs[count][2] =
	 * e.getValue().getPL(sampleindex)[2]; obs[count] =
	 * e.getValue().getPL(sampleindex); count++; }
	 *
	 * return obs; }
	 */

	public void setObsAndPLs(HMM hmm, int sampleindex) {
		final int[] obsgt = new int[this.inputData.size()];
		final int[][] obspl = new int[this.inputData.size()][3];

		int count = 0;
		for (final Entry<Integer, VariantInfo> e : this.inputData.entrySet()) {
			obsgt[count] = !this.usePLs && !this.useUserPLs && !this.legacywPL ? e.getValue().getGenotype(sampleindex)
					: 0;
			obspl[count] = this.usePLs || this.useUserPLs || this.legacywPL ? e.getValue().getPL(sampleindex) : null;
			count++;
		}

		hmm.GTs = !this.usePLs && !this.useUserPLs && !this.legacywPL ? obsgt : null;
		hmm.PLmatrix = this.usePLs || this.useUserPLs || this.legacywPL ? obspl : null;
	}

	/*
	 * public double[] getMAFSet() { double[] maf = new double[inputdata.size()];
	 * int count = 0; for (Entry<?, ?> e : inputdata.entrySet()) { maf[count] =
	 * Double.parseDouble(e.getValue().toString().split("#")[1]); count++; }
	 *
	 * return maf; }
	 */

	/*
	 * public double[] getMAFSetNew() { double[] maf = new
	 * double[inputdatanew.size()]; int count = 0; for (Entry<Integer, VariantInfo>
	 * e : inputdatanew.entrySet()) { maf[count] = e.getValue().getAF(); count++; }
	 *
	 * return maf; }
	 */

	/*
	 * public int[] getDistanceSet() { int[] distmap = new int[inputdata.size()];
	 * int count = 0; int DIST = 0; for (Integer k : inputdata.keySet()) {
	 * distmap[count] = k - DIST; DIST = k; count++; }
	 *
	 * return distmap; }
	 */

	/*
	 * public int[] getDistanceSetNew() { int[] distmap = new
	 * int[inputdatanew.size()]; int count = 0; int DIST = 0; for (Integer k :
	 * inputdatanew.keySet()) { distmap[count] = k - DIST; DIST = k; count++; }
	 *
	 * return distmap; }
	 */
	public void setMAFAndDist(HMM hmm) {

		if (this.Distenabled || this.HWenabled) {
			int count = 0;
			int DIST = 0;

			final int[] distmap = new int[this.inputData.size()];

			final double[] maf = new double[this.inputData.size()];

			for (final Entry<Integer, VariantInfo> e : this.inputData.entrySet()) {
				final int k = e.getKey();
				distmap[count] = this.Distenabled ? k - DIST : 0;
				DIST = k;
				maf[count] = this.HWenabled ? e.getValue().getAF() : 0.0;
				count++;
			}

			hmm.Dists = this.Distenabled ? distmap : null;
			hmm.MAFs = this.HWenabled ? maf : null;
		}
	}

	public void setDistEnabled() {
		this.Distenabled = true;
	}

	public boolean getHWmode() {
		return this.HWenabled;
	}

	/*
	 * public TreeMap<Integer, String> getInputData() { return inputdata; }
	 */

	public TreeMap<Integer, VariantInfo> getInputDataNew() {
		return this.inputData;
	}

	public void killTreeMap() {

		// inputdata = null;
		this.inputData = null;

	}

	private boolean isBalanced(Genotype gt) {

		if (gt.hasAD()) {
			try {
				final int ref = gt.getAD()[0];
				final int alt = gt.getAD()[1];
				final double refratio = (double) ref / ref + alt;
				final double altratio = (double) alt / ref + alt;
				if (Math.min(refratio, altratio) < this.ADThreshold) {
					return false;
				}
			} catch (final Exception e) {
				return true;
			}
		}
		return true;
	}

	private boolean isRefBiased(Genotype gt) {
		if (gt.hasAD()) {
			try {
				final int ref = gt.getAD()[0];
				final int alt = gt.getAD()[1];
				if (ref > alt) {
					return true;
				}
			} catch (final Exception e) {
				return true;
			}
		}
		return false;
	}

	public void setSampleSet() {
		this.sampleset = new HashSet<>();
		for (final String s : this.samplenamearr) {
			this.sampleset.add(s);
		}
	}
}

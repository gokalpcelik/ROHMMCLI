package rohmmcli.rohmm;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class Input {

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

	protected double minisculeformissing = 0.000001;
	protected String[] samplenamearr;
	protected boolean skipzeroaf = false;
	protected HashSet<String> sampleset;

	public Input() {

	}

	public void generateInput() throws Exception {

		this.inputData = new TreeMap<>();
		TreeMap<Integer, String> nonSpikedFilter = new TreeMap<>();

		final ImputeVariantInfo ivi = new ImputeVariantInfo();

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

		final VCFFileReader vcfrdr = OverSeer.getVCFFileReader();
		final CloseableIterator<VariantContext> vcfiter = this.queryWholeContig(vcfrdr, this.contigname);

		final int sizecheck = this.inputData.size() == 0 ? nonSpikedFilter.size() : this.inputData.size();

		while (vcfiter.hasNext()) {
			final VariantContext temp = vcfiter.next();

			final Integer tempstart = temp.getStart();

			if ((this.skipindels ? temp.isSNP() : true) && temp.isBiallelic() && temp.isNotFiltered()
					&& (sizecheck != 0
							? OverSeer.filterUnknowns
									? this.spikeIn ? this.inputData.containsKey(tempstart)
											: nonSpikedFilter.containsKey(tempstart)
									: true
							: true)) {
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

	public TreeMap<Integer, VariantInfo> getInputDataNew() {
		return this.inputData;
	}

	public void killTreeMap() {

		this.inputData = null;
	}

	private boolean isBalanced(Genotype gt) {

		if (gt.hasAD()) {
			try {
				final int ref = gt.getAD()[0];
				final int alt = gt.getAD()[1];
				final double refratio = (double) ref / (ref + alt);
				final double altratio = (double) alt / (ref + alt);
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

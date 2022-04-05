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

	protected String contigname;
	protected String vcfpath;
	protected String AFtag = null;
	protected double defaultMAF;
	protected boolean skipindels = false;
	protected boolean useADs = false;
	protected boolean useDT = false;
	protected double ADThreshold = 0.0;
	protected int DepthThreshold = 0;
	protected boolean useUserPLs = false;
	protected boolean spikeIn = false;
	protected String[] samplenamearr;
	protected boolean skipzeroaf = false;
	protected HashSet<String> sampleset;

	public Input() {

	}

	public void generateInput() throws Exception {

		this.inputData = new TreeMap<>();
		TreeMap<Integer, String> nonSpikedFilter = new TreeMap<>();

		if (OverSeer.knownVariant != null) {
			if (this.spikeIn) {
				OverSeer.knownVariant.createIterator(this.contigname, 1, Integer.MAX_VALUE);
				while (OverSeer.knownVariant.hasNext()) {
					this.inputData.put(OverSeer.knownVariant.getNextPos(), OverSeer.IVI);
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
				final int dAF = temp.getCalledChrCount(temp.getAlternateAllele(0), this.sampleset);

				if (dAF > 0) {
					final SampleVariantInfo svi = new SampleVariantInfo(this.samplenamearr.length);

					for (int spos = 0; spos < this.samplenamearr.length; spos++) {
						final Genotype tempg = temp.getGenotype(this.samplenamearr[spos]);

						if (tempg.isCalled() && (useDT ? getDepth(tempg) >= DepthThreshold : true)) {

							if (tempg.isHet()) {
								if (this.useADs) {
									if (this.isBalanced(tempg)) {
										svi.addGenotype(1, spos);
										if (this.useUserPLs || !tempg.hasLikelihoods())
											svi.addBalancedHetPL(spos);
										else if (tempg.hasLikelihoods() && !this.useUserPLs)
											svi.addPL(tempg.getLikelihoods().getAsPLs(), spos);
									} else if (this.isBalanced(tempg) && this.isRefBiased(tempg)) {
										svi.addGenotype(0, spos);
										svi.addBalancedHomRefPL(spos);
									} else {
										svi.addGenotype(2, spos);
										svi.addBalancedHomVarPL(spos);
									}
								} else {
									svi.addGenotype(1, spos);
									if (this.useUserPLs || !tempg.hasLikelihoods())
										svi.addBalancedHetPL(spos);
									else if (tempg.hasLikelihoods() && !this.useUserPLs)
										svi.addPL(tempg.getLikelihoods().getAsPLs(), spos);
								}

							} else if (tempg.isHomVar()) {
								svi.addGenotype(2, spos);
								if (this.useUserPLs || !tempg.hasLikelihoods())
									svi.addBalancedHomVarPL(spos);
								else if (tempg.hasLikelihoods() && !this.useUserPLs)
									svi.addPL(tempg.getLikelihoods().getAsPLs(), spos);
							} else {
								svi.addGenotype(0, spos);
								if (this.useUserPLs || !tempg.hasLikelihoods())
									svi.addBalancedHomRefPL(spos);
								else if (tempg.hasLikelihoods() && !this.useUserPLs)
									svi.addPL(tempg.getLikelihoods().getAsPLs(), spos);
							}
						} else {
							svi.addDefaultPL(spos);
							svi.addGenotype(0, spos);
						}
					}

					if (Model.hwmode) {
						if (this.AFtag == null) {
							if (!OverSeer.DMAF && this.sampleset.size() >= 5) {
								svi.forceCalculateAF();
							} else {
								svi.addAF(this.defaultMAF);
							}
						} else {
							svi.addAF(temp.getAttributeAsDouble(this.AFtag, this.defaultMAF));
						}
					}

					this.inputData.put(temp.getStart(), svi);

				} else if (!this.skipzeroaf) {
					this.inputData.put(temp.getStart(), OverSeer.IVI);
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

	public void setHMMInputs(HMM hmm) {

		int count = 0;
		int DIST = 0;

		OverSeer.hmm.Dists = new int[this.inputData.size()];

		OverSeer.hmm.VIs = new VariantInfo[this.inputData.size()];

		for (final Entry<Integer, VariantInfo> e : this.inputData.entrySet()) {
			final int k = e.getKey();
			OverSeer.hmm.Dists[count] = k - DIST;
			DIST = k;
			OverSeer.hmm.VIs[count] = e.getValue();
			count++;
		}

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

	private int getDepth(Genotype gt) {
		try {
			if (gt.hasDP())
				return gt.getDP();
			else if (gt.hasAD())
				return gt.getAD()[0] + gt.getAD()[1];
		} catch (final Exception e) {
			return 0;
		}
		return 0;
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

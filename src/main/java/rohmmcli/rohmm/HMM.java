package rohmmcli.rohmm;

public class HMM {

	private double[][] emprobs;
	private double[][] transprobs;
	private final double[] initprobs;
	private double Normfact;
	private double defaulttransitionprob;
	protected int[][] PLmatrix = null;
	protected double[] MAFs = null;
	protected int[] GTs = null;
	protected int[] Dists = null;
	protected double[] preCalcPL = null;

	// 0 for ROH 1 for NORM
	// 0 1 2 for states
	// ROHMMCLI v 0.9g 03/08/2019 Gokalp Celik...
	public HMM(double[][] emissionprobs, double[][] transitionprobs, double[] initialprobs) {
		this.emprobs = emissionprobs;
		this.transprobs = transitionprobs;
		this.initprobs = initialprobs;
		this.initPLs();
	}

	public HMM(double[][] emissionprobs, double defaultprob, double normfact, double[] initialprobs) {
		this.emprobs = emissionprobs;
		this.transprobs = null;
		this.initprobs = initialprobs;
		this.Normfact = normfact;
		this.defaulttransitionprob = defaultprob;
		this.initPLs();
	}

	public HMM(double defaultprob, double normfact, double[] initialprobs) {
		this.emprobs = null;
		this.transprobs = null;
		this.initprobs = initialprobs;
		this.Normfact = normfact;
		this.defaulttransitionprob = defaultprob;
		this.initPLs();
	}

	// Precalculate all genotype likelyhoods so that further calculation is not
	// necessary. Similar to bcftools. Numbers greater than 255 are usually
	// meaningless in terms of probability.
	// position 256 is reserved for zero probability of a genotype under any
	// condition therefore helps user to set their own proabilities for free.
	private void initPLs() {
		this.preCalcPL = new double[257];

		for (int i = 0; i < 256; i++) {
			this.preCalcPL[i] = Math.pow(10, -1 * i / 10.0);
		}

		this.preCalcPL[256] = 0;
	}

	// similar to bcftools PL criteria. More than 255 is usually meaningless also
	// may end up floating point precision problems.
	private int maxPL(int PL) {
		if (PL > 255) {
			return 255;
		} else if (PL < 0) {
			return 256;
		}
		return PL;
	}

	private double PhredProbability(int PL) {
		return this.preCalcPL[PL];
	}

	public double getEmissionProb(int state, int pos) {

		if (this.PLmatrix != null) {
			try {
				return this.emprobs[state][0] * this.PhredProbability(this.maxPL(this.PLmatrix[pos][0]))
						+ this.emprobs[state][1] * this.PhredProbability(this.maxPL(this.PLmatrix[pos][1]))
						+ this.emprobs[state][2] * this.PhredProbability(this.maxPL(this.PLmatrix[pos][2]));
			} catch (final Exception e) {
				System.out.println(pos);
				e.printStackTrace();
			}
		} else if (this.GTs != null) {
			return this.emprobs[state][this.GTs[pos]];
		}

		return 0.0;

	}

	public int getPathLength() {
		if (this.PLmatrix != null) {
			return this.PLmatrix.length;
		} else if (this.GTs != null) {
			return this.GTs.length;
		}
		return 0;
	}

	// Burada bire hata yapmismiyiz?
	public double getTransitionProb(int prevstate, int curstate) {
		return this.transprobs[prevstate][curstate];
	}

	public double[] getInitial() {
		return this.initprobs;
	}

	public void setNormalizationFactor(double factor) {
		this.Normfact = factor;
	}

	public void setEmissionMatrix(double[][] emmatrix) {
		this.emprobs = emmatrix;
	}

	public void setTransitionMatrix(double[][] transmatrix) {
		this.transprobs = transmatrix;
	}

	public void generateEMMatrixHW(double MAF) {
		final double[][] EMmatrix = new double[2][3];

		EMmatrix[0][0] = 1.0 - MAF;
		EMmatrix[0][1] = 0.0;
		EMmatrix[0][2] = MAF;
		EMmatrix[1][0] = (1.0 - MAF) * (1.0 - MAF);
		EMmatrix[1][1] = (1.0 - MAF) * MAF * 2;
		EMmatrix[1][2] = MAF * MAF;

		this.setEmissionMatrix(EMmatrix);
	}

	public void genereateTRMatrixDist(int distance) {
		final double[][] TRmatrix = new double[2][2];

		TRmatrix[0][1] = this.defaulttransitionprob * (1 - Math.exp(-1 * distance / this.Normfact));
		TRmatrix[1][0] = this.defaulttransitionprob * (1 - Math.exp(-1 * distance / this.Normfact));
		TRmatrix[0][0] = 1 - TRmatrix[0][1];
		TRmatrix[1][1] = 1 - TRmatrix[1][0];

		this.setTransitionMatrix(TRmatrix);
	}

}

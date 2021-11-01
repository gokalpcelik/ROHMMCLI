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
	protected int SampleIndex;

	protected VariantInfo[] VIs;

	// 0 for ROH 1 for NORM
	// 0 1 2 for gts
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

	private void initPLs() {
		this.preCalcPL = new double[257];

		for (int i = 0; i < 256; i++) {
			this.preCalcPL[i] = Math.pow(10, -1 * i / 10.0);
		}

		this.preCalcPL[256] = 0;
	}

	// similar to bcftools PL criteria. More than 255 is usually meaningless also
	// may end up floating point precision problems.

	private double PhredProbability(int PL) {
		return this.preCalcPL[PL];
	}

	public double getEmissionProb(int state, int pos) {

		return this.emprobs[state][0] * this.PhredProbability(this.VIs[pos].getPL(SampleIndex)[0])
				+ this.emprobs[state][1] * this.PhredProbability(this.VIs[pos].getPL(SampleIndex)[1])
				+ this.emprobs[state][2] * this.PhredProbability(this.VIs[pos].getPL(SampleIndex)[2]);

	}

	public int getPathLength() {
		return VIs.length;
	}

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

	public void setSampleIndex(int index) {
		SampleIndex = index;
	}

}

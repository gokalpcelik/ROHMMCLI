package rohmmcli.rohmm;

public class HMM {

	private double[][] emprobs;
	private double[][] transprobs;
	private double[] initprobs;
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
		emprobs = emissionprobs;
		transprobs = transitionprobs;
		initprobs = initialprobs;
		initPLs();
	}

	public HMM(double[][] emissionprobs, double defaultprob, double normfact, double[] initialprobs) {
		emprobs = emissionprobs;
		transprobs = null;
		initprobs = initialprobs;
		Normfact = normfact;
		defaulttransitionprob = defaultprob;
		initPLs();
	}

	public HMM(double defaultprob, double normfact, double[] initialprobs) {
		emprobs = null;
		transprobs = null;
		initprobs = initialprobs;
		Normfact = normfact;
		defaulttransitionprob = defaultprob;
		initPLs();
	}

	// Precalculate all genotype likelyhoods so that further calculation is not
	// necessary
	private void initPLs() {
		preCalcPL = new double[256];

		for (int i = 0; i < 256; i++)
			preCalcPL[i] = Math.pow(10, (-1 * i / 10.0));
	}

	// similar to bcftools PL criteria. More than 255 is usually meaningless also
	// may end up floating point precision problems.
	private int maxPL(int PL) {
		if (PL > 255)
			return 255;
		return PL;
	}

	private double PhredProbability(int PL) {
		return preCalcPL[PL];
	}

	public double getEmissionProb(int state, int pos) {

		// System.err.println(pos);

		if (PLmatrix != null) {
			try {
				return emprobs[state][0] * PhredProbability(maxPL(PLmatrix[pos][0]))
						+ emprobs[state][1] * PhredProbability(maxPL(PLmatrix[pos][1]))
						+ emprobs[state][2] * PhredProbability(maxPL(PLmatrix[pos][2]));
			} catch (Exception e) {
				System.out.println(pos);
				e.printStackTrace();
			}
		} else if (GTs != null) {
			return emprobs[state][GTs[pos]];
		}

		return 0.0;

	}

	public int getPathLength() {
		if (PLmatrix != null)
			return PLmatrix.length;
		else if (GTs != null)
			return GTs.length;
		return 0;
	}

	// Burada bire hata yapmismiyiz?
	public double getTransitionProb(int prevstate, int curstate) {
		return transprobs[prevstate][curstate];
	}

	public double[] getInitial() {
		return initprobs;
	}

	public void setNormalizationFactor(double factor) {
		Normfact = factor;
	}

	public void setEmissionMatrix(double[][] emmatrix) {
		emprobs = emmatrix;
	}

	public void setTransitionMatrix(double[][] transmatrix) {
		transprobs = transmatrix;
	}

	public void generateEMMatrixHW(double MAF) {
		double[][] EMmatrix = new double[2][3];

		EMmatrix[0][0] = (1.0 - MAF);
		EMmatrix[0][1] = (0.0);
		EMmatrix[0][2] = (MAF);
		EMmatrix[1][0] = (1.0 - MAF) * (1.0 - MAF);
		EMmatrix[1][1] = (1.0 - MAF) * MAF * 2;
		EMmatrix[1][2] = MAF * MAF;

		this.setEmissionMatrix(EMmatrix);
	}

	public void genereateTRMatrixDist(int distance) {
		double[][] TRmatrix = new double[2][2];

		TRmatrix[0][1] = defaulttransitionprob * (1 - Math.exp(-1 * distance / Normfact));
		TRmatrix[1][0] = defaulttransitionprob * (1 - Math.exp(-1 * distance / Normfact));
		TRmatrix[0][0] = 1 - TRmatrix[0][1];
		TRmatrix[1][1] = 1 - TRmatrix[1][0];

		this.setTransitionMatrix(TRmatrix);
	}

}

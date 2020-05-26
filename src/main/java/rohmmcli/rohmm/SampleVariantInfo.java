package rohmmcli.rohmm;

public class SampleVariantInfo implements VariantInfo {

	protected int[][] PLArray;
	protected int[] GenotypeArray;
	protected boolean FakePL;
	protected int FPLV;
	protected double AF;
	protected double sum = 0.0;

	public SampleVariantInfo(int samplesize, boolean FPL, int fplv) {
		this.FakePL = FPL;
		this.FPLV = fplv;
		this.PLArray = new int[samplesize][3];
		this.GenotypeArray = new int[samplesize];
	}

	public void addPL(int[] PL, int sampleindex) {
		this.PLArray[sampleindex][0] = PL[0];
		this.PLArray[sampleindex][1] = PL[1];
		this.PLArray[sampleindex][2] = PL[2];
	}

	public void addGenotype(int genotype, int sampleindex) {
		this.sum = this.sum + genotype;
		this.GenotypeArray[sampleindex] = genotype;
	}

	@Override
	public int[] getPL(int sampleindex) {
		if (this.FakePL) {
			final int genotype = this.getGenotype(sampleindex);
			int[] PL = null;

			switch (genotype) {
			case VariantInfo.HOMREF:
				PL = new int[] { 0, this.FPLV, this.FPLV };
				break;
			case VariantInfo.HET:
				PL = new int[] { this.FPLV, 0, this.FPLV };
				break;
			case VariantInfo.HOMVAR:
				PL = new int[] { this.FPLV, this.FPLV, 0 };
				break;
			}
			return PL;
		} else {
			return this.PLArray[sampleindex];
		}
	}

	@Override
	public int getGenotype(int sampleindex) {
		return this.GenotypeArray[sampleindex];
	}

	@Override
	public double getAF() {
		return this.AF;
	}

	public void addAF(double allelefreq) {
		this.AF = allelefreq;
	}

	public void forceCalculateAF() {
		this.AF = this.sum / (this.GenotypeArray.length * 2);
	}

	public void addDefaultPL(int sampleindex) {
		this.PLArray[sampleindex][0] = 0;
		this.PLArray[sampleindex][1] = 255;
		this.PLArray[sampleindex][2] = 255;
	}

	public void addBalancedHomRefPL(int sampleindex) {
		this.PLArray[sampleindex][0] = 0;
		this.PLArray[sampleindex][1] = this.FPLV;
		this.PLArray[sampleindex][2] = this.FPLV;
	}

	public void addBalancedHetPL(int sampleindex) {
		this.PLArray[sampleindex][0] = this.FPLV;
		this.PLArray[sampleindex][1] = 0;
		this.PLArray[sampleindex][2] = this.FPLV;
	}

	public void addBalancedHomVarPL(int sampleindex) {
		this.PLArray[sampleindex][0] = this.FPLV;
		this.PLArray[sampleindex][1] = this.FPLV;
		this.PLArray[sampleindex][2] = 0;
	}

}

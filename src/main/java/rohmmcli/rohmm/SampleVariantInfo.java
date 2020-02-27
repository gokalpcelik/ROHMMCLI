package rohmmcli.rohmm;

public class SampleVariantInfo implements VariantInfo {

	protected int[][] PLArray;
	protected int[] GenotypeArray;
	protected boolean FakePL;
	protected int FPLV;
	protected double AF;
	protected double sum = 0.0;
	

	
	public SampleVariantInfo(int samplesize, boolean FPL, int fplv) {
		FakePL = FPL;
		FPLV = fplv;
		PLArray = new int[samplesize][3];
		GenotypeArray = new int[samplesize];
	}

	public void addPL(int[] PL, int sampleindex) {
		PLArray[sampleindex][0] = PL[0];
		PLArray[sampleindex][1] = PL[1];
		PLArray[sampleindex][2] = PL[2];
	}

	public void addGenotype(int genotype, int sampleindex) {
		sum = sum + genotype;
		GenotypeArray[sampleindex] = genotype;
	}

	@Override
	public int[] getPL(int sampleindex) {
		if (FakePL) {
			int genotype = this.getGenotype(sampleindex);
			int[] PL = null;

			switch (genotype) {
			case VariantInfo.HOMREF:
				PL = new int[] { 0, FPLV, FPLV };
				break;
			case VariantInfo.HET:
				PL = new int[] { FPLV, 0, FPLV };
				break;
			case VariantInfo.HOMVAR:
				PL = new int[] { FPLV, FPLV, 0 };
				break;
			}
			return PL;
		} else
			return PLArray[sampleindex];
	}

	@Override
	public int getGenotype(int sampleindex) {
		return GenotypeArray[sampleindex];
	}

	@Override
	public double getAF() {
		return AF;
	}

	public void addAF(double AF) {
		this.AF = AF;
	}

	public void forceCalculateAF() {
		AF = sum / (GenotypeArray.length * 2);
	}

	public void addDefaultPL(int sampleindex) {
		PLArray[sampleindex][0] = 0;
		PLArray[sampleindex][1] = 255;
		PLArray[sampleindex][2] = 255;
	}

}

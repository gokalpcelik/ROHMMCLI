package rohmmcli.rohmm;

public class SampleVariantInfo implements VariantInfo {

	protected int[][] PLArray;
	protected int[] GenotypeArray;
	protected double AF;
	protected double sum = 0.0;

	public SampleVariantInfo(int samplesize) {
		this.PLArray = new int[samplesize][3];
		this.GenotypeArray = new int[samplesize];
	}

	public void addPL(int[] PL, int sampleindex) {
		this.PLArray[sampleindex][0] = Math.min(255, PL[0]);
		this.PLArray[sampleindex][1] = Math.min(255, PL[1]);
		this.PLArray[sampleindex][2] = Math.min(255, PL[2]);
	}

	public void addGenotype(int genotype, int sampleindex) {
		this.sum = this.sum + genotype;
		this.GenotypeArray[sampleindex] = genotype;
	}

	@Override
	public int[] getPL(int sampleindex) {
		return this.PLArray[sampleindex];

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
		this.PLArray[sampleindex] = OverSeer.DefaultPL;

	}

	public void addBalancedHomRefPL(int sampleindex) {
		this.PLArray[sampleindex] = OverSeer.FHRPLArray;
	}

	public void addBalancedHetPL(int sampleindex) {
		this.PLArray[sampleindex] = OverSeer.FHPLArray;
	}

	public void addBalancedHomVarPL(int sampleindex) {
		this.PLArray[sampleindex] = OverSeer.FHAPLArray;
	}

}

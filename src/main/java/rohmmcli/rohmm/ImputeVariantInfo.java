package rohmmcli.rohmm;

public class ImputeVariantInfo implements VariantInfo {

	public static final int[] PL = { 0, 255, 255 };
	public static final int GENOTYPE = 0;
	public static final double AF = 0.0;

	public ImputeVariantInfo() {
	}

	@Override
	public int[] getPL(int sampleindex) {
		return PL;
	}

	@Override
	public int getGenotype(int sampleindex) {
		return GENOTYPE;
	}

	@Override
	public double getAF() {
		return AF;
	}

}

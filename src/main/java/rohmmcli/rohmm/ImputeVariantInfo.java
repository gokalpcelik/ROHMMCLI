package rohmmcli.rohmm;

public class ImputeVariantInfo implements VariantInfo {

	public static final double AF = 0.0;

	public ImputeVariantInfo() {
	}

	@Override
	public int[] getPL(int sampleindex) {
		return OverSeer.DefaultPL;
	}

	@Override
	public int getGenotype(int sampleindex) {
		return HOMREF;
	}

	@Override
	public double getAF() {
		return AF;
	}

}

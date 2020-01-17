package rohmmcli.rohmm;

public class ImputeVariantInfo implements VariantInfo {

	public ImputeVariantInfo() {
	}

	@Override
	public int[] getPL(int sampleindex) {
		int[] PL = { 0, 255, 255 };
		return PL;
	}

	@Override
	public int getGenotype(int sampleindex) {
		return 0;
	}

	@Override
	public double getAF() {
		return 0.0;
	}

}

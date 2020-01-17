package rohmmcli.rohmm;

public interface VariantInfo {

	public final int HOMREF = 0;
	public final int HET = 1;
	public final int HOMVAR = 2;

	public int[] getPL(int sampleindex);

	public int getGenotype(int sampleindex);

	public double getAF();

}

package rohmmcli.rohmm;

public interface VariantInfo {

	static final int HOMREF = 0;
	static final int HET = 1;
	static final int HOMVAR = 2;

	int[] getPL(int sampleindex);

	int getGenotype(int sampleindex);

	double getAF();

}
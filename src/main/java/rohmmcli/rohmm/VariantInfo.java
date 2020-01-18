package rohmmcli.rohmm;

public interface VariantInfo {

	int HOMREF = 0;
	int HET = 1;
	int HOMVAR = 2;

	int[] getPL(int sampleindex);

	int getGenotype(int sampleindex);

	double getAF();

}

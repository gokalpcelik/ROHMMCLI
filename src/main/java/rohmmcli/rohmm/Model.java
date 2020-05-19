package rohmmcli.rohmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import htsjdk.samtools.example.ExampleSamUsage;

@SuppressWarnings("unused")
public class Model {
	
	protected static boolean hwmode;
	protected static boolean distmode;
	protected static HMM hmm;
	protected static final String HWMODEL = "MODELHW";
	protected static final String HWDISTMODEL = "MODELHWDIST";
	protected static final String XMODEL = "MODELX";
	protected static final String XDISTMODEL = "MODELXDIST";
	
	
	public static HMM hmmModel(String model) throws Exception{

		switch(model)
		{
			case HWMODEL:
				getDefaultAlleleFrequencyModel(false);
				break;
			case HWDISTMODEL:
				getDefaultAlleleFrequencyModel(true);
				break;
			case XMODEL:
				getDefaultAlleleDistributionModel(false);
				break;
			case XDISTMODEL:
				getDefaultAlleleDistributionModel(true);
				break;
			default:
				if (new File(model).exists())
					hmmModelParser(new File(model));
				break;
		}
			
		return hmm;
	}
	
	private static void getDefaultAlleleDistributionModel(boolean distenabled) {
		
		double[][] emmatrix = {{0.990666,0.0,0.009334},{0.986219,0.007916,0.005865}};
		double[] start = {0.5,0.5};
		double[][] transmatrix = {{0.999991,0.000009},{0.000004,0.999996}};
		double defprob = 0.1;
		double normfact = 100000;
		
		if(!distenabled)
			hmm = new HMM(emmatrix, transmatrix, start);
		else
			hmm = new HMM(emmatrix, defprob, normfact, start);
	}
	
	private static void getDefaultAlleleFrequencyModel(boolean distenabled) {
		double[] start = {0.5,0.5};
		double[][] transmatrix = {{0.999991,0.000009},{0.000004,0.999996}};
		double defprob = 0.1;
		double normfact = 100000;
		
		if(!distenabled)
			hmm = new HMM(null, transmatrix, start);
		else
			hmm = new HMM(defprob, normfact, start);
	}
	
	

	private static void hmmModelParser(File modelfile) {

		hwmode = false;
		distmode = false;
		double[][] emmatrix = new double[2][3];
		double[] start = new double[2];
		double[][] transmatrix = new double[2][2];
		@Deprecated
		double minAF = Double.NEGATIVE_INFINITY;
		@Deprecated
		double maxAF = Double.POSITIVE_INFINITY;
		@Deprecated
		int homcount = Integer.MIN_VALUE;
		double defprob = Double.POSITIVE_INFINITY;
		double normfact = Double.NEGATIVE_INFINITY;
		try {

			FileReader fr = new FileReader(modelfile);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			if (!line.equalsIgnoreCase("Model-File")) {
				ExceptionHandler.IncorrectModelFormat();
			}

			while ((line = br.readLine()) != null) {
				String[] argses = line.split("\t");

				String temp = argses[0];
				switch (temp) {
					case "HW":
						hwmode = argses[1].equalsIgnoreCase("TRUE") ? true : false;
						break;
					case "START":
						start[0] = Double.parseDouble(argses[1]);
						start[1] = Double.parseDouble(argses[2]);
						break;
					case "EMROH":
						emmatrix[0][0] = Double.parseDouble(argses[1]);
						emmatrix[0][1] = Double.parseDouble(argses[2]);
						emmatrix[0][2] = Double.parseDouble(argses[3]);
						break;
					case "EMNORM":
						emmatrix[1][0] = Double.parseDouble(argses[1]);
						emmatrix[1][1] = Double.parseDouble(argses[2]);
						emmatrix[1][2] = Double.parseDouble(argses[3]);
						break;
					case "TRANSROH":
						transmatrix[0][0] = Double.parseDouble(argses[1]);
						transmatrix[0][1] = Double.parseDouble(argses[2]);
						break;
					case "TRANSNORM":
						transmatrix[1][0] = Double.parseDouble(argses[1]);
						transmatrix[1][1] = Double.parseDouble(argses[2]);
						break;
					case "MINAF":
						minAF = Double.parseDouble(argses[1]);
						break;
					case "MAXAF":
						maxAF = Double.parseDouble(argses[1]);
						break;
					case "HOMCOUNT":
						homcount = Integer.parseInt(argses[1]);
						break;
					case "DEFAULTPROB":
						defprob = Double.parseDouble(argses[1]);
						break;
					case "DIST":
						distmode = argses[1].equalsIgnoreCase("TRUE") ? true : false;
						break;
					case "NORMFACT":
						normfact = Double.parseDouble(argses[1]);
						break;

				}

			}

			br.close();
			fr.close();

			/*
			 * System.err.println(Arrays.toString(emmatrix[0]));
			 * System.err.println(Arrays.toString(emmatrix[1]));
			 * System.err.println(Arrays.toString(transmatrix[0]));
			 * System.err.println(Arrays.toString(transmatrix[1]));
			 * System.err.println(Arrays.toString(start)); System.err.println(minAF);
			 * System.err.println(maxAF); System.err.println(homcount);
			 */

			// do something
			hmm = new HMM(emmatrix, transmatrix, start);
			if (distmode) {
				if (!hwmode)
					hmm = new HMM(emmatrix, defprob, normfact, start);
				else
					hmm = new HMM(defprob, normfact, start);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}

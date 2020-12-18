/*
 * Author : Gokalp Celik
 * Year : 2020
 */
package rohmmcli.rohmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Model {

	public static boolean hwmode = false;
	public static boolean distmode = false;
	protected static HMM hmm = null;
	protected static final String HWMODEL = "MODELHW";
	protected static final String HWDISTMODEL = "MODELHWDIST";
	protected static final String XMODEL = "MODELX";
	protected static final String XDISTMODEL = "MODELXDIST";
	public static String customModel = "";

	public static HMM hmmModel(String model) throws Exception {

		switch (model) {
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
		case "CUSTOM":
			hmmModelMakerGUI();
			break;
		default:
			if (new File(model).exists()) {
				hmmModelParser(new File(model));
			}
			break;
		}

		return hmm;
	}

	private static void getDefaultAlleleDistributionModel(boolean distenabled) {

		final double[][] emmatrix = { { 0.990666, 0.0, 0.009334 }, { 0.986219, 0.007916, 0.005865 } };
		final double[] start = { 0.5, 0.5 };
		final double[][] transmatrix = { { 0.999991, 0.000009 }, { 0.000004, 0.999996 } };
		final double defprob = 0.1;
		final double normfact = 100000;
		distmode = distenabled;
		if (!distmode) {
			hmm = new HMM(emmatrix, transmatrix, start);
		} else {
			hmm = new HMM(emmatrix, defprob, normfact, start);
		}
	}

	private static void getDefaultAlleleFrequencyModel(boolean distenabled) {
		final double[] start = { 0.5, 0.5 };
		final double[][] transmatrix = { { 0.999991, 0.000009 }, { 0.000004, 0.999996 } };
		final double defprob = 0.1;
		final double normfact = 100000;
		hwmode = true;
		distmode = distenabled;
		if (!distmode) {
			hmm = new HMM(null, transmatrix, start);
		} else {
			hmm = new HMM(defprob, normfact, start);
		}
	}

	private static void hmmModelMakerGUI() {
		final double[][] emmatrix = new double[2][3];
		// final double[] start = new double[2];
		final double[] start = { 0.5, 0.5 };
		final double[][] transmatrix = new double[2][2];
		double defprob = Double.POSITIVE_INFINITY;
		double normfact = Double.NEGATIVE_INFINITY;
		try {

			final String[] modelparams = customModel.split("\n");

			for (final String line : modelparams) {
				final String[] argses = line.split("\t");

				final String temp = argses[0];
				switch (temp) {
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
				case "DEFAULTPROB":
					defprob = Double.parseDouble(argses[1]);
					break;
				case "NORMFACT":
					normfact = Double.parseDouble(argses[1]);
					break;

				}

			}
			hmm = new HMM(emmatrix, transmatrix, start);
			if (distmode) {
				if (!hwmode) {
					hmm = new HMM(emmatrix, defprob, normfact, start);
				} else {
					hmm = new HMM(defprob, normfact, start);
				}
			}
		} catch (final Exception e) {
			System.err.println(e.getMessage());
		}

	}

	private static void hmmModelParser(File modelfile) {

		hwmode = false;
		distmode = false;
		final double[][] emmatrix = new double[2][3];
		final double[] start = new double[2];
		final double[][] transmatrix = new double[2][2];
		double defprob = Double.POSITIVE_INFINITY;
		double normfact = Double.NEGATIVE_INFINITY;
		try {

			final FileReader fr = new FileReader(modelfile);
			final BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			if (!line.equalsIgnoreCase("Model-File")) {
				ExceptionHandler.IncorrectModelFormat();
			}

			while ((line = br.readLine()) != null) {
				final String[] argses = line.split("\t");

				final String temp = argses[0];
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

			hmm = new HMM(emmatrix, transmatrix, start);
			if (distmode) {
				if (!hwmode) {
					hmm = new HMM(emmatrix, defprob, normfact, start);
				} else {
					hmm = new HMM(defprob, normfact, start);
				}
			}
		} catch (final Exception e) {
			System.err.println(e.getMessage());
		}
	}

}

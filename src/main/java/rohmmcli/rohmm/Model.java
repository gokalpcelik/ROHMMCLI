package rohmmcli.rohmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Model {
	
	protected static boolean hwmode;
	protected static boolean distmode;
	protected static HMM hmm;
	
	
	@SuppressWarnings("unused")
	
	
	
	public static HMM hmmModelParser(String modelfile) {

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
			File model = new File(modelfile);
			if (!model.exists())
				throw new FileNotFoundException("Model file not found...");
			FileReader fr = new FileReader(model);
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

			/*System.err.println(Arrays.toString(emmatrix[0]));
			System.err.println(Arrays.toString(emmatrix[1]));
			System.err.println(Arrays.toString(transmatrix[0]));
			System.err.println(Arrays.toString(transmatrix[1]));
			System.err.println(Arrays.toString(start));
			System.err.println(minAF);
			System.err.println(maxAF);
			System.err.println(homcount);*/

			// do something
			hmm = new HMM(emmatrix, transmatrix, start);
			if (distmode) {
				if (!hwmode)
					hmm = new HMM(emmatrix, defprob, normfact, start);
				else
					hmm = new HMM(defprob, normfact, start);
			}
			
			return hmm;

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

}

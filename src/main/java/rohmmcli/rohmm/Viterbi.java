package rohmmcli.rohmm;

//ROHMMCLI v 0.9g 03/08/2019 Gokalp Celik...
public class Viterbi {

	private static double[][] forward;

	private static double[][] backward;

	public static int[] getViterbiPath(HMM hmm) {
		int[] viterbipath = new int[hmm.getPathLength()];

		double[][] viterbicalc = new double[2][viterbipath.length];
		forward = new double[2][hmm.getPathLength()];
		backward = new double[2][hmm.getPathLength()];

		if (hmm.MAFs != null)
			hmm.generateEMMatrixHW(hmm.MAFs[0]);

		for (int i = 0; i < 2; i++) {

			viterbicalc[i][0] = Math.log(hmm.getInitial()[i]) + Math.log(hmm.getEmissionProb(i, 0));
		}

		for (int currentpos = 1; currentpos < viterbipath.length; currentpos++) {

			if (hmm.MAFs != null)
				hmm.generateEMMatrixHW(hmm.MAFs[currentpos]);

			if (hmm.Dists != null)
				hmm.genereateTRMatrixDist(hmm.Dists[currentpos]);

			double emissionprob = Math.log(hmm.getEmissionProb(0, currentpos));
			// forward algorithm part 1
			double logsum = Double.NEGATIVE_INFINITY;

			double ftemp1 = forward[0][currentpos - 1] + Math.log(hmm.getTransitionProb(0, 0));

			if (ftemp1 > Double.NEGATIVE_INFINITY) {
				logsum = ftemp1 + Math.log(1 + Math.exp(logsum - ftemp1));
			}

			double ftemp2 = forward[1][currentpos - 1] + Math.log(hmm.getTransitionProb(1, 0));

			if (ftemp2 > Double.NEGATIVE_INFINITY) {
				logsum = ftemp2 + Math.log(1 + Math.exp(logsum - ftemp2));
			}

			forward[0][currentpos] = emissionprob + logsum;

			// viterbi forward part 1
			double temp1 = viterbicalc[0][currentpos - 1] + Math.log(hmm.getTransitionProb(0, 0)) + emissionprob;
			double temp2 = viterbicalc[1][currentpos - 1] + Math.log(hmm.getTransitionProb(1, 0)) + emissionprob;

			viterbicalc[0][currentpos] = Math.max(temp1, temp2);

			emissionprob = Math.log(hmm.getEmissionProb(1, currentpos));

			// forward algorithm part 2
			logsum = Double.NEGATIVE_INFINITY;

			double ftemp3 = forward[0][currentpos - 1] + Math.log(hmm.getTransitionProb(0, 1));

			if (ftemp3 > Double.NEGATIVE_INFINITY) {
				logsum = ftemp3 + Math.log(1 + Math.exp(logsum - ftemp3));
			}

			double ftemp4 = forward[1][currentpos - 1] + Math.log(hmm.getTransitionProb(1, 1));

			if (ftemp4 > Double.NEGATIVE_INFINITY) {
				logsum = ftemp4 + Math.log(1 + Math.exp(logsum - ftemp4));
			}

			forward[1][currentpos] = emissionprob + logsum;

			// viterbi forward part 2
			double temp3 = viterbicalc[0][currentpos - 1] + Math.log(hmm.getTransitionProb(0, 1)) + emissionprob;
			double temp4 = viterbicalc[1][currentpos - 1] + Math.log(hmm.getTransitionProb(1, 1)) + emissionprob;

			viterbicalc[1][currentpos] = Math.max(temp3, temp4);

		}

		double temp1 = viterbicalc[0][viterbipath.length - 1];
		double temp2 = viterbicalc[1][viterbipath.length - 1];
		double max = Math.max(temp1, temp2);

		if (max == temp1) {
			viterbipath[viterbipath.length - 1] = 0;
		} else {
			viterbipath[viterbipath.length - 1] = 1;
		}

		// backward part 0
		for (int i = 0; i < 2; i++) {
			backward[i][backward[1].length - 1] = Math.log(1);
		}

		for (int i = viterbipath.length - 2; i >= 0; i--) {

			if (hmm.MAFs != null)
				hmm.generateEMMatrixHW(hmm.MAFs[i + 1]);

			if (hmm.Dists != null)
				hmm.genereateTRMatrixDist(hmm.Dists[i + 1]);

			// viterbi traceback
			if (viterbicalc[viterbipath[i + 1]][i + 1] == viterbicalc[0][i]
					+ Math.log(hmm.getTransitionProb(0, viterbipath[i + 1]))
					+ Math.log(hmm.getEmissionProb(viterbipath[i + 1], i + 1))) {
				viterbipath[i] = 0;
			} else {
				viterbipath[i] = 1;
			}

			// backward algorithm
			double logsum = Double.NEGATIVE_INFINITY;
			double emissionprob = hmm.getEmissionProb(0, i + 1);
			double emissionprob2 = hmm.getEmissionProb(1, i + 1);
			double btemp1 = backward[0][i + 1] + Math.log(hmm.getTransitionProb(0, 0) * emissionprob);

			if (btemp1 > Double.NEGATIVE_INFINITY) {
				logsum = btemp1 + Math.log(1 + Math.exp(logsum - btemp1));
			}

			double btemp2 = backward[1][i + 1] + Math.log(hmm.getTransitionProb(0, 1) * emissionprob2);

			if (btemp2 > Double.NEGATIVE_INFINITY) {
				logsum = btemp2 + Math.log(1 + Math.exp(logsum - btemp2));
			}

			backward[0][i] = logsum;

			logsum = Double.NEGATIVE_INFINITY;

			double btemp3 = backward[0][i + 1] + Math.log(hmm.getTransitionProb(1, 0) * emissionprob);

			if (btemp3 > Double.NEGATIVE_INFINITY) {
				logsum = btemp3 + Math.log(1 + Math.exp(logsum - btemp3));
			}

			double btemp4 = backward[1][i + 1] + Math.log(hmm.getTransitionProb(1, 1) * emissionprob2);

			if (btemp4 > Double.NEGATIVE_INFINITY) {
				logsum = btemp4 + Math.log(1 + Math.exp(logsum - btemp4));
			}

			backward[1][i] = logsum;

		}

		return viterbipath;

	}

	public static double[][] posterior(HMM hmm) {
		double[][] posterior = new double[2][hmm.getPathLength()];

		double[][] f = forward;
		double[][] b = backward;

		double probObs = f[0][posterior[1].length - 1];
		double j = f[1][posterior[1].length - 1];
		if (j > Double.NEGATIVE_INFINITY) {
			probObs = j + Math.log(1 + Math.exp(probObs - j));
		}

		for (int i = 0; i < posterior[1].length; i++) {
			posterior[0][i] = Math.exp(f[0][i] + b[0][i] - probObs);
			posterior[1][i] = Math.exp(f[1][i] + b[1][i] - probObs);

			// System.out.println(posterior[0][i] + "\t" + posterior[0][i]);
		}

		return posterior;
	}

}

//Legacy seperate implementation for forward and backward algorithms. Now they are within viterbipath for a faster result. Less loops faster results. 
//	public static double[][] forward(HMM hmm) {
//		double[][] forward = new double[2][hmm.getPathLength()];
//
//		if (hmm.MAFs != null)
//			hmm.generateEMMatrixHW(hmm.MAFs[0]);
//
//		for (int i = 0; i < 2; i++) {
//
//			forward[i][0] = Math.log(hmm.getInitial()[i] * hmm.getEmissionProb(i, 0));
//
//		}
//
//		// System.out.println(forward[0][0] + "\t" + forward[1][0]);
//
//		for (int currentpos = 1; currentpos < forward[1].length; currentpos++) {
//
//			if (hmm.MAFs != null)
//				hmm.generateEMMatrixHW(hmm.MAFs[currentpos]);
//
//			if (hmm.Dists != null)
//				hmm.genereateTRMatrixDist(hmm.Dists[currentpos]);
//
//			double logsum = Double.NEGATIVE_INFINITY;
//
//			double temp1 = forward[0][currentpos - 1] + Math.log(hmm.getTransitionProb(0, 0));
//
//			if (temp1 > Double.NEGATIVE_INFINITY) {
//				logsum = temp1 + Math.log(1 + Math.exp(logsum - temp1));
//			}
//
//			double temp2 = forward[1][currentpos - 1] + Math.log(hmm.getTransitionProb(1, 0));
//
//			if (temp2 > Double.NEGATIVE_INFINITY) {
//				logsum = temp2 + Math.log(1 + Math.exp(logsum - temp2));
//			}
//
//			forward[0][currentpos] = Math.log(hmm.getEmissionProb(0, currentpos)) + logsum;
//
//			logsum = Double.NEGATIVE_INFINITY;
//
//			double temp3 = forward[0][currentpos - 1] + Math.log(hmm.getTransitionProb(0, 1));
//
//			if (temp3 > Double.NEGATIVE_INFINITY) {
//				logsum = temp3 + Math.log(1 + Math.exp(logsum - temp3));
//			}
//
//			double temp4 = forward[1][currentpos - 1] + Math.log(hmm.getTransitionProb(1, 1));
//
//			if (temp4 > Double.NEGATIVE_INFINITY) {
//				logsum = temp4 + Math.log(1 + Math.exp(logsum - temp4));
//			}
//
//			forward[1][currentpos] = Math.log(hmm.getEmissionProb(1, currentpos)) + logsum;
//
//			// System.out.println(forward[0][currentpos] + "\t" + forward[1][currentpos]);
//		}
//
//		return forward;
//	}
//
//	public static double[][] backward(HMM hmm) {
//		double[][] backward = new double[2][hmm.getPathLength()];
//
//		for (int i = 0; i < 2; i++) {
//			backward[i][backward[1].length - 1] = Math.log(1);
//		}
//
//		// System.out.println(backward[0][backward[1].length - 1] + "\t" +
//		// backward[1][backward[1].length - 1]);
//
//		for (int currentpos = backward[1].length - 2; currentpos >= 0; currentpos--) {
//
//			if (hmm.MAFs != null)
//				hmm.generateEMMatrixHW(hmm.MAFs[currentpos + 1]);
//
//			if (hmm.Dists != null)
//				hmm.genereateTRMatrixDist(hmm.Dists[currentpos + 1]);
//
//			double logsum = Double.NEGATIVE_INFINITY;
//			double emissionprob = hmm.getEmissionProb(0, currentpos + 1);
//			double emissionprob2 = hmm.getEmissionProb(1, currentpos + 1);
//			double temp1 = backward[0][currentpos + 1] + Math.log(hmm.getTransitionProb(0, 0) * emissionprob);
//
//			if (temp1 > Double.NEGATIVE_INFINITY) {
//				logsum = temp1 + Math.log(1 + Math.exp(logsum - temp1));
//			}
//
//			double temp2 = backward[1][currentpos + 1] + Math.log(hmm.getTransitionProb(0, 1) * emissionprob2);
//
//			if (temp2 > Double.NEGATIVE_INFINITY) {
//				logsum = temp2 + Math.log(1 + Math.exp(logsum - temp2));
//			}
//
//			backward[0][currentpos] = logsum;
//
//			logsum = Double.NEGATIVE_INFINITY;
//
//			double temp3 = backward[0][currentpos + 1] + Math.log(hmm.getTransitionProb(1, 0) * emissionprob);
//
//			if (temp3 > Double.NEGATIVE_INFINITY) {
//				logsum = temp3 + Math.log(1 + Math.exp(logsum - temp3));
//			}
//
//			double temp4 = backward[1][currentpos + 1] + Math.log(hmm.getTransitionProb(1, 1) * emissionprob2);
//
//			if (temp4 > Double.NEGATIVE_INFINITY) {
//				logsum = temp4 + Math.log(1 + Math.exp(logsum - temp4));
//			}
//
//			backward[1][currentpos] = logsum;
//			// System.out.println(backward[0][currentpos] + "\t" + backward[1][currentpos]);
//		}
//		return backward;
//	}
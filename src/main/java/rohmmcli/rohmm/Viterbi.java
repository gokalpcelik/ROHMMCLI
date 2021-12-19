/*
 * Author : Gokalp Celik
 * 
 * Date : May 26, 2020
 * 
 */
package rohmmcli.rohmm;

//ROHMMCLI v 0.9g 03/08/2019 Gokalp Celik...
public class Viterbi {

	private static double[][] forward;

	private static double[][] backward;

	public static int[] getViterbiPath(HMM hmm) {
		final int[] viterbipath = new int[hmm.getPathLength()];

		final double[][] viterbicalc = new double[2][viterbipath.length];
		forward = new double[2][hmm.getPathLength()];
		backward = new double[2][hmm.getPathLength()];

		if (Model.hwmode) {
			hmm.generateEMMatrixHW(hmm.VIs[0].getAF());
		}

		for (int i = 0; i < 2; i++) {

			viterbicalc[i][0] = Math.log(hmm.getInitial()[i]) + Math.log(hmm.getEmissionProb(i, 0));
		}

		for (int currentpos = 1; currentpos < viterbipath.length; currentpos++) {

			if (Model.hwmode) {
				hmm.generateEMMatrixHW(hmm.VIs[currentpos].getAF());
			}

			if (Model.distmode) {
				hmm.genereateTRMatrixDist(hmm.Dists[currentpos]);
			}

			double emissionprob = Math.log(hmm.getEmissionProb(0, currentpos));
			// forward algorithm part 1
			double logsum = Double.NEGATIVE_INFINITY;

			final double ftemp1 = forward[0][currentpos - 1] + Math.log(hmm.getTransitionProb(0, 0));

			if (ftemp1 > Double.NEGATIVE_INFINITY) {
				logsum = ftemp1 + Math.log(1 + Math.exp(logsum - ftemp1));
			}

			final double ftemp2 = forward[1][currentpos - 1] + Math.log(hmm.getTransitionProb(1, 0));

			if (ftemp2 > Double.NEGATIVE_INFINITY) {
				logsum = ftemp2 + Math.log(1 + Math.exp(logsum - ftemp2));
			}

			forward[0][currentpos] = emissionprob + logsum;

			// viterbi forward part 1
			final double temp1 = viterbicalc[0][currentpos - 1] + Math.log(hmm.getTransitionProb(0, 0)) + emissionprob;
			final double temp2 = viterbicalc[1][currentpos - 1] + Math.log(hmm.getTransitionProb(1, 0)) + emissionprob;

			viterbicalc[0][currentpos] = Math.max(temp1, temp2);

			emissionprob = Math.log(hmm.getEmissionProb(1, currentpos));

			// forward algorithm part 2
			logsum = Double.NEGATIVE_INFINITY;

			final double ftemp3 = forward[0][currentpos - 1] + Math.log(hmm.getTransitionProb(0, 1));

			if (ftemp3 > Double.NEGATIVE_INFINITY) {
				logsum = ftemp3 + Math.log(1 + Math.exp(logsum - ftemp3));
			}

			final double ftemp4 = forward[1][currentpos - 1] + Math.log(hmm.getTransitionProb(1, 1));

			if (ftemp4 > Double.NEGATIVE_INFINITY) {
				logsum = ftemp4 + Math.log(1 + Math.exp(logsum - ftemp4));
			}

			forward[1][currentpos] = emissionprob + logsum;

			// viterbi forward part 2
			final double temp3 = viterbicalc[0][currentpos - 1] + Math.log(hmm.getTransitionProb(0, 1)) + emissionprob;
			final double temp4 = viterbicalc[1][currentpos - 1] + Math.log(hmm.getTransitionProb(1, 1)) + emissionprob;

			viterbicalc[1][currentpos] = Math.max(temp3, temp4);

		}

		final double temp1 = viterbicalc[0][viterbipath.length - 1];
		final double temp2 = viterbicalc[1][viterbipath.length - 1];
		final double max = Math.max(temp1, temp2);

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

			if (Model.hwmode) {
				hmm.generateEMMatrixHW(hmm.VIs[i + 1].getAF());
			}

			if (Model.distmode) {
				hmm.genereateTRMatrixDist(hmm.Dists[i + 1]);
			}

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
			final double emissionprob = hmm.getEmissionProb(0, i + 1);
			final double emissionprob2 = hmm.getEmissionProb(1, i + 1);
			final double btemp1 = backward[0][i + 1] + Math.log(hmm.getTransitionProb(0, 0) * emissionprob);

			if (btemp1 > Double.NEGATIVE_INFINITY) {
				logsum = btemp1 + Math.log(1 + Math.exp(logsum - btemp1));
			}

			final double btemp2 = backward[1][i + 1] + Math.log(hmm.getTransitionProb(0, 1) * emissionprob2);

			if (btemp2 > Double.NEGATIVE_INFINITY) {
				logsum = btemp2 + Math.log(1 + Math.exp(logsum - btemp2));
			}

			backward[0][i] = logsum;

			logsum = Double.NEGATIVE_INFINITY;

			final double btemp3 = backward[0][i + 1] + Math.log(hmm.getTransitionProb(1, 0) * emissionprob);

			if (btemp3 > Double.NEGATIVE_INFINITY) {
				logsum = btemp3 + Math.log(1 + Math.exp(logsum - btemp3));
			}

			final double btemp4 = backward[1][i + 1] + Math.log(hmm.getTransitionProb(1, 1) * emissionprob2);

			if (btemp4 > Double.NEGATIVE_INFINITY) {
				logsum = btemp4 + Math.log(1 + Math.exp(logsum - btemp4));
			}

			backward[1][i] = logsum;

		}

		return viterbipath;

	}

	public static double[][] posterior(HMM hmm) {
		final double[][] posterior = new double[2][hmm.getPathLength()];

		final double[][] f = forward;
		final double[][] b = backward;

		double probObs = f[0][posterior[1].length - 1];
		final double j = f[1][posterior[1].length - 1];
		if (j > Double.NEGATIVE_INFINITY) {
			probObs = j + Math.log(1 + Math.exp(probObs - j));
		}

		for (int i = 0; i < posterior[1].length; i++) {
			posterior[0][i] = Math.exp(f[0][i] + b[0][i] - probObs);
			posterior[1][i] = Math.exp(f[1][i] + b[1][i] - probObs);

		}

		return posterior;
	}

}

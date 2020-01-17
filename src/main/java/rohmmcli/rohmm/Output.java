package rohmmcli.rohmm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map.Entry;

//ROHMMCLI v 0.9g 03/08/2019 Gokalp Celik...
public class Output {

	@Deprecated
	public static void GenerateOutput(String contig, Input in, int[] viterbipath, String outputprefix,
			double[][] posterior, boolean combine, int ROHLEN, int ROHCOUNT) throws Exception {

		throw new Exception("This method is deprecated already... Please use the new method instead");
		

		
//		FileWriter fr = new FileWriter(outputprefix + (combine ? "" : ("-" + contig)) + "_ROH.bed", combine);
//		BufferedWriter br = new BufferedWriter(fr);
//
//		// br.write("#" + cmdline + "\n");
//
//		// System.err.println("Generating outputs for contig " + contig);
//
//		int[] positions = new int[in.getInputData().size()];
//		int count = 0;
//		for (Entry<?, ?> e : in.getInputData().entrySet()) {
//			positions[count] = (Integer) e.getKey();
//			count++;
//		}
//
//		int start = positions[0];
//		int end = start;
//		int status = viterbipath[0];
//		double posteriorprob = 0;
//		double posteriorpr = 0;
//		int rohcount = 0;
//		if (status == 0) {
//			posteriorprob = posteriorprob + posterior[0][0];
//			posteriorpr = posteriorpr + (posterior[1][0]);
//			rohcount++;
//		}
//
//		for (int i = 1; i < positions.length; i++) {
//
//			if (viterbipath[i] == 0) {
//				posteriorprob = posteriorprob + posterior[0][i];
//				posteriorpr = posteriorpr + posterior[1][i];
//				rohcount++;
//			}
//
//			if (status == viterbipath[i]) {
//				end = positions[i];
//			} else {
//				if (status == 0 && (end - start >= ROHLEN) && rohcount >= ROHCOUNT) {
//					br.write(contig + "\t" + start + "\t" + end + "\tROH\t" + (posteriorprob / rohcount) + "\t"
//							+ rohcount + "\n");
//					posteriorprob = 0;
//					rohcount = 0;
//				}
//				start = positions[i];
//				end = start;
//				status = viterbipath[i];
//			}
//		}
//		if (status == 0)
//			br.write(contig + "\t" + start + "\t" + end + "\tROH\t" + rohcount + "\t" + (posteriorprob / rohcount)
//					+ "\n");
//
//		br.close();
//		fr.close();
	}

	public static void GenerateOutputNew(String contig, Input in, int[] viterbipath, String outputprefix,
			double[][] posterior, boolean combine, int ROHLEN, int ROHCOUNT, double ROHQUAL) throws Exception {

		FileWriter fr = new FileWriter(outputprefix + (combine ? "" : ("-" + contig)) + "_ROH.bed", combine);
		BufferedWriter br = new BufferedWriter(fr);

		// br.write("#" + cmdline + "\n");

		// System.err.println("Generating outputs for contig " + contig);

		int[] positions = new int[in.getInputDataNew().size()];
		int count = 0;
		for (Entry<?, ?> e : in.getInputDataNew().entrySet()) {
			positions[count] = (Integer) e.getKey();
			count++;
		}

		int start = positions[0];
		int end = start;
		int status = viterbipath[0];
		double posteriorprob = 0;
		double posteriorpr = 0;
		int rohcount = 0;
		if (status == 0) {
			posteriorprob = posteriorprob + posterior[0][0];
			posteriorpr = posteriorpr + (posterior[1][0]);
			rohcount++;
		}

		for (int i = 1; i < positions.length; i++) {

			if (viterbipath[i] == 0) {
				posteriorprob = posteriorprob + posterior[0][i];
				posteriorpr = posteriorpr + posterior[1][i];
				rohcount++;
			}

			if (status == viterbipath[i]) {
				end = positions[i];
			} else {
				if (status == 0 && (end - start >= ROHLEN) && rohcount >= ROHCOUNT && posteriorprob >= ROHQUAL) {
					br.write(contig + "\t" + start + "\t" + end + "\tROH\t" + (posteriorprob / rohcount) + "\t"
							+ rohcount + "\n");
					posteriorprob = 0;
					rohcount = 0;
				}
				start = positions[i];
				end = start;
				status = viterbipath[i];
			}
		}
		if (status == 0)
			br.write(contig + "\t" + start + "\t" + end + "\tROH\t" + (posteriorprob / rohcount) + "\t"    //burada yapilan salak bir hata yuzunden olayi mahvediyorduk nerdeyese...
					+ rohcount + "\n");

		br.close();
		fr.close();
	}

}

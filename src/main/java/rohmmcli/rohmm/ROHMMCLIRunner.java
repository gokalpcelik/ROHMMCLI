/*
 * Author : Gokalp Celik
 * 
 * Date : May 27, 2020
 * 
 */
package rohmmcli.rohmm;

import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;

import htsjdk.variant.vcf.VCFFileReader;
import rohmmcli.gui.ROHMMMain;

@SuppressWarnings("unused")
public class ROHMMCLIRunner {
	public static void main(String[] args) throws Exception {
		System.err.println(" ______     ______     __  __     __    __     __    __   ");
		System.err.println("/\\  == \\   /\\  __ \\   /\\ \\_\\ \\   /\\ \"-./  \\   /\\ \"-./  \\  ");
		System.err.println("\\ \\  __<   \\ \\ \\/\\ \\  \\ \\  __ \\  \\ \\ \\-./\\ \\  \\ \\ \\-./\\ \\ ");
		System.err.println(" \\ \\_\\ \\_\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\ \\ \\_\\  \\ \\_\\ \\ \\_\\");
		System.err.println("  \\/_/ /_/   \\/_____/   \\/_/\\/_/   \\/_/  \\/_/   \\/_/  \\/_/");
		OverSeer.START = System.currentTimeMillis();
		OverSeer.log(ROHMMCLIRunner.class.getSimpleName(), "ROHMMCLI v" + OverSeer.VERSION + " Gokalp Celik...",
				OverSeer.INFO);
		OverSeer.getOS();

		if (args.length == 0) {
			OverSeer.log(ROHMMCLIRunner.class.getSimpleName(), "Running ROHMMGUI", OverSeer.INFO);
			if (OverSeer.isMac() && OverSeer.isMacDarkMode()) {
				UIManager.setLookAndFeel(new FlatDarculaLaf());
			} else {
				UIManager.setLookAndFeel(new FlatIntelliJLaf());
			}
			OverSeer.resetOptionsGUI();
			ROHMMMain.RunGUI();
		} else {
			OverSeer.parseCommands(args);
			OverSeer.setHMMParams();
			OverSeer.setInputParams();

			// Utility.logInput(cmd);
			Runner(OverSeer.cmd);
			OverSeer.closeAllReaders();
			OverSeer.endTimer();
			System.exit(0);

		}

	}

	public static void Runner(CommandLine cmd) {

		VCFFileReader vcfrdr = null;
		String[] contigs = null;
		try {
			contigs = OverSeer.setContigList();
		} catch (final Exception e1) {
			OverSeer.log(ROHMMCLIRunner.class.getName(), "Cannot set required parameter CONTIG. Check your VCF File",
					OverSeer.ERROR);
		}

		try {

			vcfrdr = OverSeer.getVCFFileReader();
			if (vcfrdr != null) {
				final String[] samples = OverSeer.setSampleNameList();
				OverSeer.input.samplenamearr = samples;
				OverSeer.input.setSampleSet();

				final int count = 1;

				if (contigs != null) {
					for (final String contig : contigs) {

						OverSeer.input.setContig(contig);
						OverSeer.input.generateInput();
						OverSeer.input.setMAFAndDist(OverSeer.hmm);

						OverSeer.log(ROHMMCLIRunner.class.getSimpleName(),
								"Size of the input dataset " + OverSeer.input.getInputDataNew().size(), OverSeer.INFO);

						int sampleindex = 0;
						for (final String sample : samples) {
							int[] states = null;
							double[][] posterior = null;

							/*
							 * if (input.usePLs || input.useUserPLs || input.legacywPL) hmm.PLmatrix =
							 * input.getObservationSetPLsNew(sampleindex); else hmm.GTs =
							 * input.getObservationSetNew(sampleindex);
							 */

							OverSeer.input.setObsAndPLs(OverSeer.hmm, sampleindex);

							states = Viterbi.getViterbiPath(OverSeer.hmm);

							posterior = Viterbi.posterior(OverSeer.hmm);

							int rohlen = 0;
							if (cmd.hasOption("MRL")) {
								rohlen = Integer.parseInt(cmd.getOptionValue("MRL"));
							}

							int rohcount = 0;
							if (cmd.hasOption("MSC")) {
								rohcount = Integer.parseInt(cmd.getOptionValue("MSC"));
							}

							double qual = 0.0;
							if (cmd.hasOption("Q")) {
								qual = Double.parseDouble(cmd.getOptionValue("Q"));
							}

							Output.generateOutput(contig, OverSeer.input, states,
									cmd.getOptionValue("O") + "_" + sample, posterior, OverSeer.combineOutput(), rohlen,
									rohcount, qual);

							sampleindex++;

						}

						OverSeer.input.killTreeMap();
					}
				} else {
					System.err.println("Contigs are null");
				}
			} else {
				System.err.println("vcfrdr is null");
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}
}

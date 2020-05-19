package rohmmcli.rohmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import rohmmcli.gui.*;

import com.formdev.flatlaf.FlatIntelliJLaf;


@SuppressWarnings("unused")
public class ROHMMCLIRunner {

	public static void main(String[] args) throws Exception {
		OverSeer.START = System.currentTimeMillis();
		OverSeer.getOS();
		OverSeer.log(ROHMMCLIRunner.class.getSimpleName(), "ROHMMCLI v" + OverSeer.VERSION + " Gokalp Celik...",
				OverSeer.INFO);

		if (args.length == 0) {
			OverSeer.log(ROHMMCLIRunner.class.getSimpleName(), "Running ROHMMGUI", OverSeer.INFO);
			UIManager.setLookAndFeel(new FlatIntelliJLaf());
			ROHMMMain.RunGUI();
		} else {
			OverSeer.parseCommands(args);
			OverSeer.setInputParams();
			OverSeer.setHMMParams();

			// Utility.logInput(cmd);
			Runner(OverSeer.cmd);

			OverSeer.endTimer();
			System.exit(0);

		}

	}

	// Depends on Utility Class that provides all the parameters.
	public static void newRunner() {

	}

	@SuppressWarnings("deprecation")
	public static void Runner(CommandLine cmd) {

		VCFFileReader vcfrdr = null;
		/*
		 * int[] observations = null; int[][] observationPLs = null; double[] mafs =
		 * null; int[] dists = null;
		 */

		String[] contigs = null;

		String contigparam = cmd.getOptionValue("C");

		switch (contigparam) {
		case "GRCh37":
			contigs = OverSeer.GRCH37NoXY;
			break;
		case "hg19":
			contigs = OverSeer.HG1938NoXY;
			break;
		case "hg38":
			contigs = OverSeer.HG1938NoXY;
			break;
		case "GRCh38":
			contigs = OverSeer.HG1938NoXY;
			break;
		default:
			contigs = cmd.getOptionValue("C").split(",");
			break;
		}

		/*
		 * if (cmd.hasOption("F")) {
		 * 
		 * input.fillfactor = 1; }
		 */

		try {
			
			vcfrdr = OverSeer.getVCFFileReader();

			/*
			 * if (cmd.hasOption("F")) { CloseableIterator<VariantContext> counter =
			 * vcfrdr.iterator();
			 * 
			 * int sitecount = 0;
			 * 
			 * while (counter.hasNext()) { sitecount++; counter.next();
			 * 
			 * }
			 * 
			 * input.fillfactor = (12000000 / sitecount) + 1;
			 * 
			 * System.err.println("Fill factor: " + input.fillfactor); }
			 */

			ArrayList<String> alsample = vcfrdr.getFileHeader().getSampleNamesInOrder();

			vcfrdr.close();

			String[] samples;
			ArrayList<String> omsamples = new ArrayList<>();
			if (cmd.hasOption("SN"))
				samples = cmd.getOptionValue("SN").split(",");
			else if (cmd.hasOption("SL")) {

				FileReader fr = new FileReader(new File(cmd.getOptionValue("SL")));
				BufferedReader br = new BufferedReader(fr);

				String line;

				ArrayList<String> templist = new ArrayList<>();

				while ((line = br.readLine()) != null) {
					if (alsample.contains(line))
						templist.add(line);
					else
						omsamples.add(line);
				}

				br.close();
				fr.close();

				templist.trimToSize();
				omsamples.trimToSize();
				samples = templist.toArray(new String[templist.size()]);

			} else
				samples = alsample.toArray(new String[alsample.size()]);

			OverSeer.input.samplenamearr = samples;
			OverSeer.input.setSampleSet();

			System.err.println("Total number of selected samples " + samples.length);
			System.err.println("Total number of omitted samples " + omsamples.size());

			int count = 1;

			if (cmd.hasOption("exome"))
				System.err.println("Exome Sample");

			if (cmd.hasOption("OLDCODE")) {

				// Single Sample all contig code path old and slow. May not show much difference
				// for a single sample but very slow for multi sample analysis

				for (String sample : samples) {

					System.err.println("Working on sample number " + count + " of " + samples.length);

					OverSeer.input.oldsampleidx = alsample.indexOf(sample);

					for (String contig : contigs) {

						int[] states = null;
						double[][] posterior = null;
						OverSeer.input.setContig(contig);
						if (cmd.hasOption("G")) {
							File gnomadfile = new File(
									cmd.getOptionValue("G") + "/Gnomad_hg19_" + contig.replaceAll("chr", "")
											+ (cmd.hasOption("exome") ? "_exome.bed.gz" : ".bed.gz"));
							OverSeer.input.setGNOMADPath(gnomadfile.getPath());

						}

						OverSeer.input.generateInput();

						if (OverSeer.input.usePLs || OverSeer.input.useUserPLs || OverSeer.input.legacywPL)
							OverSeer.hmm.PLmatrix = OverSeer.input.getObservationSetPLs();
						else
							OverSeer.hmm.GTs = OverSeer.input.getObservationSet();

						if (OverSeer.input.getHWmode()) {
							OverSeer.hmm.MAFs = OverSeer.input.getMAFSet();
						}

						if (OverSeer.input.Distenabled) {
							OverSeer.hmm.Dists = OverSeer.input.getDistanceSet();
						}

						states = Viterbi.getViterbiPath(OverSeer.hmm);

						posterior = Viterbi.posterior(OverSeer.hmm);

						int rohlen = 0;
						if (cmd.hasOption("MRL"))
							rohlen = Integer.parseInt(cmd.getOptionValue("MRL"));

						int rohcount = 0;
						if (cmd.hasOption("MSC"))
							rohcount = Integer.parseInt(cmd.getOptionValue("MSC"));

						Output.GenerateOutput(contig, OverSeer.input, states, (cmd.getOptionValue("O") + "_" + sample),
								posterior, OverSeer.combineOutput(), rohlen, rohcount);

						OverSeer.input.killTreeMap();

					}

					count++;
				}
			} else {
				// New Multisample code path with more optimizations.

				for (String contig : contigs) {

					OverSeer.input.setContig(contig);
					if (cmd.hasOption("G")) {
						File gnomadfile = new File(
								cmd.getOptionValue("G") + "/Gnomad_hg19_" + contig.replaceAll("chr", "")
										+ (cmd.hasOption("exome") ? "_exome.bed.gz" : ".bed.gz"));
						OverSeer.input.setGNOMADPath(gnomadfile.getPath());

					}

					OverSeer.input.generateInputNew();

					/*
					 * if (input.getHWmode()) { hmm.MAFs = input.getMAFSetNew(); }
					 * 
					 * if (input.Distenabled) { hmm.Dists = input.getDistanceSetNew(); }
					 */

					OverSeer.input.setMAFAndDist(OverSeer.hmm);

					System.err.println("Size of the input dataset " + OverSeer.input.getInputDataNew().size());

					int sampleindex = 0;
					for (String sample : samples) {
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
						if (cmd.hasOption("MRL"))
							rohlen = Integer.parseInt(cmd.getOptionValue("MRL"));

						int rohcount = 0;
						if (cmd.hasOption("MSC"))
							rohcount = Integer.parseInt(cmd.getOptionValue("MSC"));

						double qual = 0.0;
						if (cmd.hasOption("Q"))
							qual = Double.parseDouble(cmd.getOptionValue("Q"));

						Output.GenerateOutputNew(contig, OverSeer.input, states,
								(cmd.getOptionValue("O") + "_" + sample), posterior, OverSeer.combineOutput(), rohlen,
								rohcount, qual);

						sampleindex++;

					}

					OverSeer.input.killTreeMap();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

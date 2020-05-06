package rohmmcli.rohmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

import rohmmcli.gui.*;
@SuppressWarnings("unused")
public class ROHMMCLIRunner {

	public static void main(String[] args) throws Exception {
		Utility.START = System.currentTimeMillis();
		Utility.log(ROHMMCLIRunner.class.getSimpleName(),"ROHMMCLI v"+ Utility.VERSION +" Gokalp Celik...", Utility.INFO);
		
		if(args.length == 0)
		{
			Utility.log(ROHMMCLIRunner.class.getSimpleName(), "Running ROHMMGUI", Utility.INFO);
			ROHMMMain.RunGUI();
		}
		else {
			Utility.parseCommands(args);
			Utility.setInputParams();
			Utility.setHMMParams();
			
			//Utility.logInput(cmd);
			Runner(Utility.cmd);
			
			Utility.ENDTIMER();
			System.exit(0);

		}
		
		
		
		
		
		
		
	}
	//Depends on Utility Class that provides all the parameters. 
	public static void newRunner()
	{
		
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
			contigs = Utility.GRCH37NoXY;
			break;
		case "hg19":
			contigs = Utility.HG1938NoXY;
			break;
		case "hg38":
			contigs = Utility.HG1938NoXY;
			break;
		case "GRCh38":
			contigs = Utility.HG1938NoXY;
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
			VCFReader vcffile = new VCFReader(Utility.input.vcfpath);
			vcfrdr = vcffile.createReader();

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

			Utility.input.samplenamearr = samples;
			Utility.input.setSampleSet();

			System.err.println("Total number of selected samples " + samples.length);
			System.err.println("Total number of omitted samples " + omsamples.size());

			int count = 1;
			
			if(cmd.hasOption("exome"))
				System.err.println("Exome Sample");
			
			if (cmd.hasOption("OLDCODE")) {

				// Single Sample all contig code path old and slow. May not show much difference
				// for a single sample but very slow for multi sample analysis

				for (String sample : samples) {

					System.err.println("Working on sample number " + count + " of " + samples.length);

					Utility.input.oldsampleidx = alsample.indexOf(sample);

					for (String contig : contigs) {

						int[] states = null;
						double[][] posterior = null;
						Utility.input.setContig(contig);
						if (cmd.hasOption("G")) {
							File gnomadfile = new File(cmd.getOptionValue("G") + "/Gnomad_hg19_"
									+ contig.replaceAll("chr", "") + (cmd.hasOption("exome") ? "_exome.bed.gz" : ".bed.gz"));
							Utility.input.setGNOMADPath(gnomadfile.getPath());

						}

						Utility.input.generateInput();

						if (Utility.input.usePLs || Utility.input.useUserPLs || Utility.input.legacywPL)
							Utility.hmm.PLmatrix = Utility.input.getObservationSetPLs();
						else
							Utility.hmm.GTs = Utility.input.getObservationSet();

						if (Utility.input.getHWmode()) {
							Utility.hmm.MAFs = Utility.input.getMAFSet();
						}

						if (Utility.input.Distenabled) {
							Utility.hmm.Dists = Utility.input.getDistanceSet();
						}

						states = Viterbi.getViterbiPath(Utility.hmm);

						posterior = Viterbi.posterior(Utility.hmm);

						int rohlen = 0;
						if (cmd.hasOption("MRL"))
							rohlen = Integer.parseInt(cmd.getOptionValue("MRL"));

						int rohcount = 0;
						if (cmd.hasOption("MSC"))
							rohcount = Integer.parseInt(cmd.getOptionValue("MSC"));

						Output.GenerateOutput(contig, Utility.input, states, (cmd.getOptionValue("O") + "_" + sample),
								posterior, Utility.combineOutput(), rohlen, rohcount);

						Utility.input.killTreeMap();

					}

					count++;
				}
			} else {
				// New Multisample code path with more optimizations.

				for (String contig : contigs) {

					Utility.input.setContig(contig);
					if (cmd.hasOption("G")) {
						File gnomadfile = new File(
								cmd.getOptionValue("G") + "/Gnomad_hg19_" + contig.replaceAll("chr", "") + (cmd.hasOption("exome") ? "_exome.bed.gz" : ".bed.gz"));
						Utility.input.setGNOMADPath(gnomadfile.getPath());

					}

					Utility.input.generateInputNew();

					/*
					 * if (input.getHWmode()) { hmm.MAFs = input.getMAFSetNew(); }
					 * 
					 * if (input.Distenabled) { hmm.Dists = input.getDistanceSetNew(); }
					 */

					Utility.input.setMAFAndDist(Utility.hmm);

					System.err.println("Size of the input dataset " + Utility.input.getInputDataNew().size());

					int sampleindex = 0;
					for (String sample : samples) {
						int[] states = null;
						double[][] posterior = null;

						/*
						 * if (input.usePLs || input.useUserPLs || input.legacywPL) hmm.PLmatrix =
						 * input.getObservationSetPLsNew(sampleindex); else hmm.GTs =
						 * input.getObservationSetNew(sampleindex);
						 */

						Utility.input.setObsAndPLs(Utility.hmm, sampleindex);

						states = Viterbi.getViterbiPath(Utility.hmm);

						posterior = Viterbi.posterior(Utility.hmm);

						int rohlen = 0;
						if (cmd.hasOption("MRL"))
							rohlen = Integer.parseInt(cmd.getOptionValue("MRL"));

						int rohcount = 0;
						if (cmd.hasOption("MSC"))
							rohcount = Integer.parseInt(cmd.getOptionValue("MSC"));
						
						double qual = 0.0; 
						if(cmd.hasOption("Q"))	
							qual = Double.parseDouble(cmd.getOptionValue("Q"));
						
						Output.GenerateOutputNew(contig, Utility.input, states, (cmd.getOptionValue("O") + "_" + sample),
								posterior, Utility.combineOutput(), rohlen, rohcount, qual);

						sampleindex++;

					}

					Utility.input.killTreeMap();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

package rohmmcli.rohmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

@SuppressWarnings("unused")
public class ROHMMCLIRunner {

	private static HMM hmm = null;
	private static Input input = null;
	private static boolean combine = false;

	public static void main(String[] args) throws Exception {
		Utility.START = System.currentTimeMillis();

		Utility.log(ROHMMCLIRunner.class.getSimpleName(),"ROHMMCLI v"+ Utility.VERSION +" Gokalp Celik...", Utility.INFO);
		CommandLine cmd = Utility.parseCommands(args);
		Model model = new Model();
		input = new Input();
		hmm = Model.hmmModelParser(cmd.getOptionValue("hmm"));

		input.Distenabled = Model.distmode;
		input.HWenabled = Model.hwmode;
		input.AFtag = cmd.hasOption("AF") ? cmd.getOptionValue("AF") : null;
		input.skipindels = cmd.hasOption("S") ? true : false;
		input.defaultMAF = cmd.hasOption("D") ? Double.parseDouble(cmd.getOptionValue("D")) : 0.4;
		input.skipzeroaf = cmd.hasOption("SZ") ? true : false;
		input.setVCFPath(cmd.getOptionValue("V"));

		/*
		 * if (cmd.hasOption("FF")) input.fillfactor =
		 * Integer.parseInt(cmd.getOptionValue("FF"));
		 */

		if (cmd.hasOption("GT")) {
			input.usePLs = false;
			input.useUserPLs = true;
			input.userPL = Integer.parseInt(cmd.getOptionValue("GT"));
		} /*
			 * else if (cmd.hasOption("AD")) { input.usePLs = false; input.useADs = true; }
			 */ else if (cmd.hasOption("legacy")) {
			input.usePLs = false;
			input.useGTs = true;
		} else if (cmd.hasOption("Custom")) {
			input.usePLs = false;
			input.useGTs = false;
			input.legacywPL = true;
		}

		if (cmd.hasOption("MFM"))
			input.minisculeformissing = Double.parseDouble(cmd.getOptionValue("MFM"));

		if (cmd.hasOption("F"))
			input.useFiller = true;

		if (cmd.hasOption("combine"))
			combine = true;

		input.setDefaultMAF(Double.parseDouble(cmd.getOptionValue("D", "0.4")));
		Utility.logInput(cmd);
		Runner(cmd);

		/*
		 * System.out.println(input.defaultMAF); System.out.println(input.AFtag);
		 * System.out.println(input.minAFfilter); System.out.println(input.maxAFfilter);
		 * System.out.println(input.gnomadpath); System.out.println(input.Distenabled);
		 * System.out.println(input.getHWmode());
		 */
		
		
		Utility.ENDTIMER();
		System.exit(0);
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

		if (cmd.hasOption("S"))
			input.skipindels = true;

		try {
			VCFReader vcffile = new VCFReader(input.vcfpath);
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

			input.samplenamearr = samples;
			input.setSampleSet();

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

					input.oldsampleidx = alsample.indexOf(sample);

					for (String contig : contigs) {

						int[] states = null;
						double[][] posterior = null;
						input.setContig(contig);
						if (cmd.hasOption("G")) {
							File gnomadfile = new File(cmd.getOptionValue("G") + "/Gnomad_hg19_"
									+ contig.replaceAll("chr", "") + (cmd.hasOption("exome") ? "_exome.bed.gz" : ".bed.gz"));
							input.setGNOMADPath(gnomadfile.getPath());

						}

						input.generateInput();

						if (input.usePLs || input.useUserPLs || input.legacywPL)
							hmm.PLmatrix = input.getObservationSetPLs();
						else
							hmm.GTs = input.getObservationSet();

						if (input.getHWmode()) {
							hmm.MAFs = input.getMAFSet();
						}

						if (input.Distenabled) {
							hmm.Dists = input.getDistanceSet();
						}

						states = Viterbi.getViterbiPath(hmm);

						posterior = Viterbi.posterior(hmm);

						int rohlen = 0;
						if (cmd.hasOption("MRL"))
							rohlen = Integer.parseInt(cmd.getOptionValue("MRL"));

						int rohcount = 0;
						if (cmd.hasOption("MSC"))
							rohcount = Integer.parseInt(cmd.getOptionValue("MSC"));

						Output.GenerateOutput(contig, input, states, (cmd.getOptionValue("O") + "_" + sample),
								posterior, combine, rohlen, rohcount);

						input.killTreeMap();

					}

					count++;
				}
			} else {
				// New Multisample code path with more optimizations.

				for (String contig : contigs) {

					input.setContig(contig);
					if (cmd.hasOption("G")) {
						File gnomadfile = new File(
								cmd.getOptionValue("G") + "/Gnomad_hg19_" + contig.replaceAll("chr", "") + (cmd.hasOption("exome") ? "_exome.bed.gz" : ".bed.gz"));
						input.setGNOMADPath(gnomadfile.getPath());

					}

					input.generateInputNew();

					/*
					 * if (input.getHWmode()) { hmm.MAFs = input.getMAFSetNew(); }
					 * 
					 * if (input.Distenabled) { hmm.Dists = input.getDistanceSetNew(); }
					 */

					input.setMAFAndDist(hmm);

					System.err.println("Size of the input dataset " + input.getInputDataNew().size());

					int sampleindex = 0;
					for (String sample : samples) {
						int[] states = null;
						double[][] posterior = null;

						/*
						 * if (input.usePLs || input.useUserPLs || input.legacywPL) hmm.PLmatrix =
						 * input.getObservationSetPLsNew(sampleindex); else hmm.GTs =
						 * input.getObservationSetNew(sampleindex);
						 */

						input.setObsAndPLs(hmm, sampleindex);

						states = Viterbi.getViterbiPath(hmm);

						posterior = Viterbi.posterior(hmm);

						int rohlen = 0;
						if (cmd.hasOption("MRL"))
							rohlen = Integer.parseInt(cmd.getOptionValue("MRL"));

						int rohcount = 0;
						if (cmd.hasOption("MSC"))
							rohcount = Integer.parseInt(cmd.getOptionValue("MSC"));
						
						double qual = 0.0; 
						if(cmd.hasOption("Q"))	
							qual = Double.parseDouble(cmd.getOptionValue("Q"));
						
						Output.GenerateOutputNew(contig, input, states, (cmd.getOptionValue("O") + "_" + sample),
								posterior, combine, rohlen, rohcount, qual);

						sampleindex++;

					}

					input.killTreeMap();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

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
		long start = System.currentTimeMillis();

		System.err.println("ROHMMCLI v 0.9n 01/11/2019 Gokalp Celik...");
		System.err.println("Java Version: " + System.getProperty("java.runtime.version"));
		Options opts = new Options();
		HelpFormatter fmtr = new HelpFormatter();

		CommandLine cmd = null;
		opts.addRequiredOption("hmm", "hmm-file", true,
				"HMM parameters file. See help file for file format descriptors. REQUIRED");

		opts.addRequiredOption("V", "Variant-File", true, "Variant file input for analysis. REQUIRED");

		opts.addOption("G", "Gnomad-Path", true, "Path to gnomad filler bed files. NOT REQUIRED");

		opts.addRequiredOption("O", "Output-File-Prefix", true, "Output file prefix for bed files. REQUIRED");

		opts.addRequiredOption("C", "Contig-String", true,
				"Comma-seperated contig selection string such as chr1,chr2,chr3...");

		opts.addOption("AF", "AF-Tag", true,
				"Allele Frequency tag for site filtering. If not declared AF will be calculated from the genotype counts of the samples.");

		opts.addOption("help", false, "Display this text...");

		opts.addOption("combine", false, "Combine Bed file outputs into single file..");

		opts.addOption("D", true, "Default MAF for sites missing MAF. Default 0.4");

		opts.addOption("F", "Use-GNOMAD-Filler", false, "Use GNOMAD SNP sites as filler.");

		opts.addOption("S", "skip-indels", false, "Skip indels and use SNPs only");

		opts.addOption("legacy", false, "Use Legacy genotyping mode. Deprecated");

		opts.addOption("GT", true, "Use GTs only with the given uncertainity level. 30 is recommended (equals 1e-3).");

		/*
		 * opts.addOption("AD", true,
		 * "Use Allelic Depths to decide genotype using binomial test with a given probability. 0.2 is recommended."
		 * );
		 */

		opts.addOption("SN", "sample-name", true, "Comma seperated list of sample names from the vcf file");

		opts.addOption("SL", "sample-list", true, "File that contains the names of the samples one sample per line");

		opts.addOption("MRL", "minimum-roh-length", true, "Minimum length to report a region as ROH. Default 0");

		opts.addOption("MSC", "minimum-site-count", true,
				"Minimum number of sites to report a region as ROH. Default 0");

		opts.addOption("Custom", false,
				"Use MAFs to determine proper homozygosity signals and use PLs for uncertainity");

		opts.addOption("MFM", "miniscule-for-missing", true, "Delta for missing data AF probability (experimental)");

		opts.addOption("OLDCODE", false, "Use old single sample calculation code path. Old but proven");

		opts.addOption("SZ", "skip-zeroaf", false,
				"Skip markers with zero allele frequency within the selected sample population. This may have different consequences using HW versus static emission parameters...");

		opts.addOption("EAF", "external-file-af", true,
				"Define an external vcf file for the population allele frequencies");	// Adding this option will enable
																						// users to define an external
																						// population vcf to set allele
																						// frequencies if hw is used.
		// opts.addOption("FF", "fill-factor", true, "Factor to reduce the filled
		// positions from GnomAD reference. Use 100 for exomes and 4 for low coverage
		// WGS. Changes the number of filled sites by the factor given here. Use higher
		// numbers if your total variant sites are lower than 50000");

		// opts.addRequiredOption("sn", "sample-names",true, "List of sample names
		// seperated by comma such as sample1,sample2...");
		
		opts.addOption("Q","min-qual",true,"Minimum ROH quality to emit");
		
		opts.addOption("exome",false,"Activate if the sample is a whole exome analysis");
		
		opts.addOption("LOG","log-file",true,"Define a log file to keep record of the run");	 //bunu yapmak lazım yoksa kalırız ortada ilerideki işlerde. 

		try {
			CommandLineParser parser = new DefaultParser();
			cmd = parser.parse(opts, args);

		} catch (Exception e) {

			PrintWriter pw = new PrintWriter(System.err);
			fmtr.printUsage(pw, 80, "java -jar ROHMMCLI.jar <params>");
			fmtr.printOptions(pw, 80, opts, 0, 10);
			pw.flush();

			long end = System.currentTimeMillis();
			System.err.println("Total time: " + (double) (end - start) / 1000 + " seconds.");
			pw.close();
			System.exit(1);

		}

		hmmModelParser(cmd.getOptionValue("hmm"));

		if (cmd.hasOption("AF")) {
			input.setAFTag(cmd.getOptionValue("AF"));
			input.forceCalculateAF = false;
		}
		if (cmd.hasOption("S"))
			input.skipindels = true;

		if (cmd.hasOption("D"))
			input.defaultMAF = Double.parseDouble(cmd.getOptionValue("D"));

		if (cmd.hasOption("SZ"))
			input.skipzeroaf = true;

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
		System.err.println("VCF file: " + cmd.getOptionValue("V"));
		System.err.println("GNOMAD path: " + cmd.getOptionValue("G"));
		System.err.println("Output prefix: " + cmd.getOptionValue("O"));
		System.err.println("Select Contigs: " + cmd.getOptionValue("C"));
		System.err.println("AFTAG: " + cmd.getOptionValue("AF", "Force Calculate from sample list"));
		Runner(cmd);

		/*
		 * System.out.println(input.defaultMAF); System.out.println(input.AFtag);
		 * System.out.println(input.minAFfilter); System.out.println(input.maxAFfilter);
		 * System.out.println(input.gnomadpath); System.out.println(input.Distenabled);
		 * System.out.println(input.getHWmode());
		 */
		long end = System.currentTimeMillis();
		System.err.println("Total time: " + (double) (end - start) / 1000 + " seconds.");
		System.exit(0);
	}

	public static void hmmModelParser(String modelfile) {

		boolean hwmode = false;
		boolean distmode = false;
		double[][] emmatrix = new double[2][3];
		double[] start = new double[2];
		double[][] transmatrix = new double[2][2];
		double minAF = Double.NEGATIVE_INFINITY;
		double maxAF = Double.POSITIVE_INFINITY;
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
				IncorrectModelFormat();
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

			System.err.println(Arrays.toString(emmatrix[0]));
			System.err.println(Arrays.toString(emmatrix[1]));
			System.err.println(Arrays.toString(transmatrix[0]));
			System.err.println(Arrays.toString(transmatrix[1]));
			System.err.println(Arrays.toString(start));
			System.err.println(minAF);
			System.err.println(maxAF);
			System.err.println(homcount);

			// do something
			hmm = new HMM(emmatrix, transmatrix, start);
			input = new Input(hwmode, minAF, maxAF, homcount);
			if (distmode) {
				input.setDistEnabled();
				if (!input.getHWmode())
					hmm = new HMM(emmatrix, defprob, normfact, start);
				else
					hmm = new HMM(defprob, normfact, start);
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private static void IncorrectModelFormat() throws Exception {
		throw new Exception("Model file format is not correct...");
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
			contigs = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
					"16", "17", "18", "19", "20", "21", "22" };
			break;
		case "hg19":
			contigs = new String[] { "chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10",
					"chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21",
					"chr22" };
			break;
		case "hg38":
			contigs = new String[] { "chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10",
					"chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21",
					"chr22" };
			break;
		case "GRCh38":
			contigs = new String[] { "chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7", "chr8", "chr9", "chr10",
					"chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21",
					"chr22" };
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
			vcfrdr = new VCFFileReader(new File(input.vcfpath));

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

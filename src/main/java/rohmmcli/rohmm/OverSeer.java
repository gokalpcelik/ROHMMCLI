/*
 * Author : Gokalp Celik
 *
 * Date : May 30, 2020
 *
 */
package rohmmcli.rohmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import htsjdk.samtools.util.FileExtensions;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import rohmmcli.gui.GUIOptionStandards;

//OverSeer.class organizes all the input and output functions as well as coordinates GUI and CMD interactions.
//Consult OverSeer.class whenever a parameter needs to be set or changed.
//Other classes should not be used freely...
public class OverSeer {

	public static final int ERROR = 0;
	public static final int WARNING = 1;
	public static final int INFO = 2;
	public static final int DEBUG = 3;
	protected static CommandLine cmd = null;
	protected static HMM hmm = null;
	protected static Input input = null;
	protected static boolean filterUnknowns = true;
	protected static boolean DMAF = false;
	protected static int LOGLEVEL = 3; // for development purposes. Will set to 0 upon release.
	protected static long START;
	protected static long END;
	protected static String VCFPath = null;
	protected static VCFReader vcfrdr = null;
	protected static KnownVariant knownVariant = null;
	protected static final String[] GRCH37NoXY = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
			"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22" };
	protected static final String[] HG1938NoXY = new String[] { "chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7",
			"chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19",
			"chr20", "chr21", "chr22" };
	protected static final String[] GRCH37Full = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
			"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y" };
	protected static final String[] HG1938Full = new String[] { "chr1", "chr2", "chr3", "chr4", "chr5", "chr6", "chr7",
			"chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19",
			"chr20", "chr21", "chr22", "chrX", "chrY" };

	protected static String OSNAME = null;

	protected static List<String> CONTIGLIST = null;

	protected static HashMap<String, String> optionMap = new HashMap<>();

	public static final String VERSION = "0.9t-GUI 26/05/2020";

	public static void log(String COMPONENT, String Message, int Level) {

		if (Level <= LOGLEVEL) {
			switch (Level) {
			case INFO:
				System.err.println("[INFO] [" + COMPONENT + "]: " + Message);
				break;
			case WARNING:
				System.err.println("[WARNING] [" + COMPONENT + "]: " + Message);
				break;
			case ERROR:
				System.err.println("[ERROR] [" + COMPONENT + "]: " + Message);
				break;
			case DEBUG:
				System.err.println("[DEBUG] [" + COMPONENT + "]: " + Message);
				break;
			}
		}

	}

	public static CommandLine getGUICMD() {
		return cmd;
	}

	public static void clearOptionMap() {
		optionMap.clear();
	}

	public static void setOption(String key, String value) {
		optionMap.put(key, value);
	}

	public static void removeOption(String key) {
		optionMap.remove(key);
	}

	public static void setGUICMD() {

		final ArrayList<String> guiCMD = new ArrayList<>();

		for (final String key : optionMap.keySet()) {

			guiCMD.add(key);
			final String value = optionMap.get(key);
			guiCMD.add(value != null ? value : "");
		}

		System.out.println(guiCMD);
		String[] arr = new String[0];
		arr = guiCMD.toArray(arr);
		System.out.println(arr.length);
		parseCommands(guiCMD.toArray(new String[0]));

	}

	public static void endTimer() {
		END = System.currentTimeMillis();
		log("SYSTEM", "Total time: " + (double) (END - START) / 1000 + " seconds.", INFO);
	}

	public static void setVCFPath(File vcffile) throws Exception {
		try {
			CONTIGLIST = null;
			VCFPath = vcffile.getAbsolutePath();
			vcfrdr = new VCFReader(vcffile);
		} catch (final Exception e) {
			// TODO: handle exception
			throw new Exception("Cannot set VCF File path due to problematic file");
		}
	}

	public static VCFHeader getVCFHeader() {
		return vcfrdr != null ? vcfrdr.getHeader() : null;
	}

	public static VCFFileReader getVCFFileReader() {
		return vcfrdr != null ? vcfrdr.getReader() : null;
	}

	public static List<String> getAvailableContigsList() {
		try {
			CONTIGLIST = CONTIGLIST == null ? vcfrdr.getAvailableContigsList() : CONTIGLIST;
			return CONTIGLIST != null ? CONTIGLIST : vcfrdr != null ? vcfrdr.getAvailableContigsList() : null;
		} catch (final Exception e) {
			return null;
		}
	}

	public static String[] setContigList() throws Exception {

		String[] contigs = null;

		if (cmd.hasOption("C")) {

			final String contigparam = cmd.getOptionValue("C");

			switch (contigparam) {
			case "GRCh37":
				contigs = GRCH37NoXY;
				break;
			case "hg19":
				contigs = HG1938NoXY;
				break;
			case "hg38":
				contigs = HG1938NoXY;
				break;
			case "GRCh38":
				contigs = HG1938NoXY;
				break;
			default:
				contigs = cmd.getOptionValue("C").split(",");
				break;
			}
		} else {
			contigs = getAvailableContigsList().toArray(new String[0]);
		}
		return contigs;
	}

	public static List<String> getSampleNameList() {
		return vcfrdr != null ? vcfrdr.getVCFSampleList() : null;
	}

	public static HashMap<String, String> getOptionMap() {
		return optionMap;
	}

	public static String[] setSampleNameList() throws IOException {

		String[] samples = new String[0];
		final ArrayList<String> omsamples = new ArrayList<>();
		final ArrayList<String> alsample = (ArrayList<String>) getSampleNameList();
		if (cmd.hasOption("SL")) {
			final File f = new File(cmd.getOptionValue("SL"));
			try {
				final FileReader fr = new FileReader(f);
				final BufferedReader br = new BufferedReader(fr);

				String line;

				final ArrayList<String> templist = new ArrayList<>();

				while ((line = br.readLine()) != null) {
					if (alsample.contains(line)) {
						templist.add(line);
					} else {
						omsamples.add(line);
					}
				}

				br.close();
				fr.close();

				templist.trimToSize();
				omsamples.trimToSize();
				samples = templist.toArray(new String[0]);
			} catch (final FileNotFoundException e) {
				log("SYSTEM", "Sample list file not found. Selecting all available samples", OverSeer.WARNING);
				samples = alsample.toArray(new String[0]);
				return samples;

			}
		} else if (cmd.hasOption("SN")) {
			samples = cmd.getOptionValue("SN").split(",");
		} else {
			samples = alsample.toArray(new String[0]);
		}

		return samples;
	}

	public static void setInputParams() {
		input = new Input();

		input.Distenabled = Model.distmode;
		input.HWenabled = Model.hwmode;
		input.AFtag = cmd.hasOption("AF") ? cmd.getOptionValue("AF") : null;
		input.skipindels = cmd.hasOption("S");
		input.defaultMAF = cmd.hasOption("D") ? Double.parseDouble(cmd.getOptionValue("D")) : 0.4;
		input.skipzeroaf = cmd.hasOption("SZ");
		VCFPath = cmd.getOptionValue("V");
		try {
			setVCFPath(new File(VCFPath));
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			System.err.println("VCF Path is not set...");
		}

		input.useADs = cmd.hasOption("AD");
		input.ADThreshold = input.useADs ? Double.parseDouble(cmd.getOptionValue("AD")) : 0.2;
		/*
		 * if (cmd.hasOption("FF")) input.fillfactor =
		 * Integer.parseInt(cmd.getOptionValue("FF"));
		 */
		input.userPL = cmd.hasOption("ER") ? Integer.parseInt(cmd.getOptionValue("ER")) : 30;

		if (cmd.hasOption("GT")) {
			input.usePLs = false;
			input.useUserPLs = true;
			input.userPL = Integer.parseInt(cmd.getOptionValue("GT"));
		}
		/*
		 * else if (cmd.hasOption("legacy")) { input.usePLs = false; input.useGTs =
		 * true; } else if (cmd.hasOption("Custom")) { input.usePLs = false;
		 * input.useGTs = false; input.legacywPL = true; }
		 */

		// if (cmd.hasOption("MFM"))
		// input.minisculeformissing = Double.parseDouble(cmd.getOptionValue("MFM"));

		if (cmd.hasOption("F")) {
			input.spikeIn = true;
		}

		if (cmd.hasOption("G")) {
			setKnownVariant();
		}

		DMAF = cmd.hasOption("DefaultMAF");

		filterUnknowns = !cmd.hasOption("IncludeUnknowns");

		input.setDefaultMAF(Double.parseDouble(cmd.getOptionValue("D", "0.4")));

	}

	public static void setHMMParams() {

		try {
			hmm = Model.hmmModel(cmd.getOptionValue("hmm"));
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void setKnownVariant() {
		final File knownVariantFile = new File(cmd.getOptionValue("G"));
		if (knownVariantFile.exists()) {
			if (knownVariantFile.getAbsolutePath().endsWith(FileExtensions.BED)
					|| knownVariantFile.getAbsolutePath().endsWith("bed.gz")) {
				knownVariant = new BEDTypeKnownVariant(knownVariantFile);
			} else {
				knownVariant = new VCFTypeKnownVariant(knownVariantFile);
			}
		}
	}

	public static Boolean combineOutput() {
		if (cmd.hasOption("split")) {
			return false;
		}
		return true;
	}

	public static void closeAllReaders() {
		log("SYSTEM", "Closing all IO..", OverSeer.INFO);
		try {
			if (vcfrdr != null) {
				vcfrdr.closeVCFReader();
			}
			if (knownVariant != null) {
				knownVariant.close();
			}
		} catch (final Exception e) {
			// TODO: handle exception
			log("SYSTEM", "Already Closed...", OverSeer.WARNING);
		}
	}

	public static void parseCommands(String[] args) {
		final Options opts = new Options();
		final HelpFormatter fmtr = new HelpFormatter();

		opts.addRequiredOption("hmm", "hmm-file", true,
				"HMM parameters file. See help file for file format descriptors. REQUIRED");

		opts.addOption("V", "Variant-File", true, "Variant file input for analysis. REQUIRED");

		opts.addOption("G", "Gnomad-Path", true, "Path to gnomad filler bed files. NOT REQUIRED");

		opts.addRequiredOption("O", "Output-File-Prefix", true, "Output file prefix for bed files. REQUIRED");

		opts.addOption("C", "Contig-String", true, "Comma-seperated contig selection string such as chr1,chr2,chr3...");

		opts.addOption("AF", "AF-Tag", true,
				"Allele Frequency tag for site filtering. If not declared AF will be calculated from the genotype counts of the samples.");

		opts.addOption("DefaultMAF", false,
				"Disable AF calculation and use a fixed default value for MAF in HW models");

		opts.addOption("help", false, "Display this text...");

		opts.addOption("split", false, "Split Bed file per contig..");

		opts.addOption("D", true, "Default MAF for sites missing MAF. Default 0.4");

		opts.addOption("F", "spike-in", false, "Spike in known sites as HOMREF even if they are not called.");

		opts.addOption("S", "skip-indels", false, "Skip indels and use SNPs only");

		// opts.addOption("legacy", false, "Use Legacy genotyping mode. Deprecated");

		opts.addOption("GT", true, "Use Empirical error rate only for all sites. 30 is recommended (equals 1e-3).");

		opts.addOption("ER", "error-rate", true,
				"Empirical error rate for misgenotyped alleles. Phred scaled. 30 is recommended (equals 1e-3).");

		opts.addOption("AD", true, "Use Allelic Balance Threshold to decide genotype. 0.2 is recommended.");

		opts.addOption("SN", "sample-name", true, "Comma seperated list of sample names from the vcf file");

		opts.addOption("SL", "sample-list", true, "File that contains the names of the samples one sample per line");

		opts.addOption("MRL", "minimum-roh-length", true, "Minimum length to report a region as ROH. Default 0");

		opts.addOption("MSC", "minimum-site-count", true,
				"Minimum number of sites to report a region as ROH. Default 0");

		opts.addOption("IncludeUnknowns", false,
				"Include high quality unknownsites under known sites option active...");

		// opts.addOption("MFM", "miniscule-for-missing", true, "Delta for missing data
		// AF probability (experimental)");

		// opts.addOption("OLDCODE", false, "Use old single sample calculation code
		// path. Deprecated");

		opts.addOption("SZ", "skip-zeroaf", false,
				"Skip markers with zero allele frequency within the selected sample population. This may have different consequences using HW versus static emission parameters...");

		opts.addOption("EAF", "external-file-af", true,
				"Define an external vcf file for the population allele frequencies"); // Adding this option will enable
																						// users to define an external
																						// population vcf to set allele
																						// frequencies if hw is used.
		// opts.addOption("FF", "fill-factor", true, "Factor to reduce the filled
		// positions from GnomAD reference. Use 100 for exomes and 4 for low coverage
		// WGS. Changes the number of filled sites by the factor given here. Use higher
		// numbers if your total variant sites are lower than 50000");

		// opts.addRequiredOption("sn", "sample-names",true, "List of sample names
		// seperated by comma such as sample1,sample2...");

		opts.addOption("Q", "min-qual", true, "Minimum ROH quality to emit");

		opts.addOption("exome", false, "Activate if the sample is a whole exome analysis");

		opts.addOption("LL", "log-level", true, "Log level: ERROR,WARNING or INFO. Default INFO"); // bunu yapmak lazım
																									// yoksa kalırız
																									// ortada //
																									// ilerideki
																									// işlerde.

		try {
			final CommandLineParser parser = new DefaultParser();
			cmd = parser.parse(opts, args);
			// LOGLEVEL = Integer.parseInt(cmd.getOptionValue("LL", "0"));

		} catch (final Exception e) {

			e.printStackTrace();
			final PrintWriter pw = new PrintWriter(System.err, true);
			fmtr.printUsage(pw, 80, "java -jar ROHMMCLI.jar <params>");
			fmtr.printOptions(pw, 80, opts, 0, 10);
			endTimer();
			// pw.close();
			// System.exit(1);
		}

	}

	public static void resetOptionsGUI() {
		optionMap.clear();
		optionMap.put(GUIOptionStandards.ALLELICBALANCETHRESHOLD, "0.2");
		optionMap.put(GUIOptionStandards.USERDEFINEDGTERROR, "30");
		optionMap.put(GUIOptionStandards.HMMMODELFILE, Model.XDISTMODEL);
		optionMap.put(GUIOptionStandards.MINIMUMROHLENGTH, "1");
		optionMap.put(GUIOptionStandards.MINIMUMROHQUAL, "0.0");
	}

	public static void getOS() {
		OSNAME = System.getProperty("os.name").toLowerCase();
		log("SYSTEM", "Running on " + OSNAME, OverSeer.INFO);
	}

	public static boolean isMac() {
		if (OSNAME.contains("mac")) {
			return true;
		}
		return false;
	}

	protected static boolean isMacDarkMode() {
		try {
			final Process themechecker = Runtime.getRuntime().exec("defaults read -g AppleInterfaceStyle");
			themechecker.waitFor(100, TimeUnit.MILLISECONDS);
			return themechecker.exitValue() == 0;
		} catch (final Exception e) {
			return false;
		}
	}

	public static boolean isWindows() {
		if (OSNAME.contains("win")) {
			return true;
		}
		return false;
	}

	public static boolean isUnix() {
		if (OSNAME.contains("nux") || OSNAME.contains("nix") || OSNAME.contains("aix") || OSNAME.contains("bsd")) {
			return true;
		}
		return false;
	}

}
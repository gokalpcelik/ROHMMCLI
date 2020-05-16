package rohmmcli.rohmm;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

public class Utility {

	public static final int ERROR = 0;
	public static final int WARNING = 1;
	public static final int INFO = 2;
	public static final int DEBUG = 3;
	protected static CommandLine cmd = null;
	protected static HMM hmm = null;
	protected static Input input = null;
	protected static boolean combine = false;
	protected static int LOGLEVEL = 3; // for development purposes. Will set to 0 upon release.
	protected static long START;
	protected static long END;
	protected static String VCFPath = null;
	protected static VCFReader vcfrdr = null;
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

	protected static HashMap<String, String> optionMap = new HashMap<String, String>();

	protected static final String VERSION = "0.9r-GUI 03/05/2020";

	public static void log(String COMPONENT, String Message, int Level) {

		if (Level <= LOGLEVEL) {
			switch (Level) {
			case INFO:
				System.err.println("[INFO] " + COMPONENT + ": " + Message);
				break;
			case WARNING:
				System.err.println("[WARNING] " + COMPONENT + ": " + Message);
				break;
			case ERROR:
				System.err.println("[ERROR] " + COMPONENT + ": " + Message);
				break;
			case DEBUG:
				System.err.println("[DEBUG] " + COMPONENT + ": " + Message);
				break;
			}
		}

	}

	public static void clearOptionMap() {
		optionMap.clear();
	}

	public static void setOption(String key, String value) {
		optionMap.put(key, value);
	}

	public static void setGUICMD() {

		ArrayList<String> guiCMD = new ArrayList<>();

		for (String key : optionMap.keySet()) {

			guiCMD.add(key);
			guiCMD.add(optionMap.get(key));
		}

		parseCommands(guiCMD.toArray(new String[0]));

	}

	public static void endTimer() {
		END = System.currentTimeMillis();
		log("[SYSTEM]", "Total time: " + (double) (END - START) / 1000 + " seconds.", INFO);
	}

	public static void setVCFPath(String path) {
		VCFPath = path;
	}

	public static VCFHeader getVCFHeader() {
		VCFHeader header = null;
		try {
			VCFReader vcfReader = new VCFReader(VCFPath);
			VCFFileReader vcfrdr = vcfReader.createReader();
			header = vcfrdr.getFileHeader();
			vcfrdr.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return header;
	}

	public static void setInputParams() {
		input = new Input();

		input.Distenabled = Model.distmode;
		input.HWenabled = Model.hwmode;
		input.AFtag = cmd.hasOption("AF") ? cmd.getOptionValue("AF") : null;
		input.skipindels = cmd.hasOption("S") ? true : false;
		input.defaultMAF = cmd.hasOption("D") ? Double.parseDouble(cmd.getOptionValue("D")) : 0.4;
		input.skipzeroaf = cmd.hasOption("SZ") ? true : false;
		input.setVCFPath(VCFPath == null ? cmd.getOptionValue("V") : VCFPath);

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

	}

	public static void setHMMParams() {

		try {
			hmm = Model.hmmModel(cmd.getOptionValue("hmm"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Boolean combineOutput() {
		if (cmd.hasOption("combine"))
			return true;
		return false;
	}

	public static void parseCommands(String[] args) {
		Options opts = new Options();
		HelpFormatter fmtr = new HelpFormatter();

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

		opts.addOption("OLDCODE", false, "Use old single sample calculation code path. Deprecated");

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
			CommandLineParser parser = new DefaultParser();
			cmd = parser.parse(opts, args);
			// LOGLEVEL = Integer.parseInt(cmd.getOptionValue("LL", "0"));

		} catch (Exception e) {

			PrintWriter pw = new PrintWriter(System.err, true);
			fmtr.printUsage(pw, 80, "java -jar ROHMMCLI.jar <params>");
			fmtr.printOptions(pw, 80, opts, 0, 10);
			endTimer();
			pw.close();
			System.exit(1);
		}

	}

	public static void setOptionsGUI(String optionname, String value) {
		if (optionMap == null)
			optionMap = new HashMap<String, String>();

		optionMap.put(optionname, value);
	}

	public static void getOS() {
		OSNAME = System.getProperty("os.name").toLowerCase();
	}

	public static boolean isMac() {
		if (OSNAME.contains("mac"))
			return true;
		return false;
	}

	public static boolean isWindows() {
		if (OSNAME.contains("win"))
			return true;
		return false;
	}

	public static boolean isUnix() {
		if (OSNAME.contains("nux") || OSNAME.contains("nix") || OSNAME.contains("aix") || OSNAME.contains("bsd"))
			return true;
		return false;
	}

}

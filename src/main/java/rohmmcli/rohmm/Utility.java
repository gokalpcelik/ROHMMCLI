package rohmmcli.rohmm;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class Utility {
	
	public static void logSysInfo()
	{
		System.err.println("ROHMMCLI v 0.9n 01/11/2019 Gokalp Celik...");
		System.err.println("Java Version: " + System.getProperty("java.runtime.version"));
	}
	
	public static CommandLine parseCommands(String[] args)
	{
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
			pw.close();
			System.exit(1);
		}
		
		return cmd;
	}
	
	public static void logInput(CommandLine cmd)
	{
		System.err.println("VCF file: " + cmd.getOptionValue("V"));
		System.err.println("GNOMAD path: " + cmd.getOptionValue("G"));
		System.err.println("Output prefix: " + cmd.getOptionValue("O"));
		System.err.println("Select Contigs: " + cmd.getOptionValue("C"));
		System.err.println("AFTAG: " + cmd.getOptionValue("AF", "Force Calculate from sample list"));
	}

}

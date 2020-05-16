package rohmmcli.gui;

import java.io.File;

import javax.swing.JFileChooser;

public class FileSelectorUtil {

	protected static String VCFEXTENSIONS = "*.vcf;*.vcf.gz";
	protected static String BEDEXTENSIONS = "*.bed;*.bed.gz";

	public static File openFile(String title, String extensions) {

		return null;
	}

	public static JFileChooser getFileDialog(String title, File currentdir) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(title);
		fileChooser.setCurrentDirectory(currentdir);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		return fileChooser;
	}

}

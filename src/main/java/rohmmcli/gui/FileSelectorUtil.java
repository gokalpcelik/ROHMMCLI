package rohmmcli.gui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

public class FileSelectorUtil {

	protected static int LOAD = FileDialog.LOAD;
	protected static int SAVE = FileDialog.SAVE;
	protected static String VCFEXTENSIONS = ".vcf;.vcf.gz";
	protected static String BEDEXTENSIONS = ".bed;.bed.gz";

	public static File openFile(Frame parent, String title, String extensions) {
		
		FileDialog fd = getFileDialog(parent, title, LOAD);
		fd.setVisible(true);
		String filepath = fd.getFile();
		String dirpath = fd.getDirectory();
		System.err.println(dirpath + filepath);
		if (filepath != null && validateFileExtension(filepath, extensions))
			return new File(dirpath, filepath);
		return null;
	}

	public static FileDialog getFileDialog(Frame parent, String title, int fdmode) {
		FileDialog filedialog = new FileDialog(parent, title);

		filedialog.setDirectory(null);
		filedialog.setFile(null);
		filedialog.setMode(fdmode);

		return filedialog;
	}

	// custom method to check if the selected file is valid or not
	public static boolean validateFileExtension(String filepath, String extensions) {
		String[] extarr = extensions.split(";");

		for (String s : extarr) {
			System.err.println(s);
			if (filepath.endsWith(s)) {
				return true;
			}
		}
		return false;
	}

}

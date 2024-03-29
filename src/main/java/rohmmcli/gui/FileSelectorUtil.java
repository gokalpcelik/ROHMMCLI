package rohmmcli.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

public class FileSelectorUtil {

	public static File openFile(JFrame parent, String title, FileFilter filter, File currentdir) {
		final JFileChooser fc = getFileDialog(title, currentdir, JFileChooser.FILES_ONLY);
		fc.addChoosableFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(true);
		final int fcresult = fc.showOpenDialog(parent);
		if (fcresult == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		}
		return null;
	}

	public static File selectDirectory(JFrame parent, String title, File currentdir) {
		final JFileChooser fc = getFileDialog(title, currentdir, JFileChooser.DIRECTORIES_ONLY);
		final int fcresult = fc.showOpenDialog(parent);
		if (fcresult == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		}
		return null;
	}

	public static JFileChooser getFileDialog(String title, File currentdir, int fileselectionmode) {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(title);
		fileChooser.setCurrentDirectory(currentdir);
		fileChooser.setFileSelectionMode(fileselectionmode);
		return fileChooser;
	}

}

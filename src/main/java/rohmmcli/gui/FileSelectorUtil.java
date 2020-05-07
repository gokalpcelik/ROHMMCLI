package rohmmcli.gui;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

public class FileSelectorUtil {
	
	protected static int LOAD = FileDialog.LOAD;
	protected static int SAVE = FileDialog.SAVE;
	
	
	public static File openFile(Frame parent, String title)
	{
		FileDialog fd = getFileDialog(parent, title, LOAD);
		fd.setVisible(true);
		
		String filepath = fd.getFile();
		
		return new File(filepath);
	}
	
	public static FileDialog getFileDialog(Frame parent, String title, int fdmode)
	{
		FileDialog filedialog = new FileDialog(parent, title);
		
		filedialog.setDirectory(null);
		filedialog.setFile(null);
		filedialog.setMode(fdmode);
		
		return filedialog;
	}
	
	//custom method to check if the selected file is valid or not
	public static boolean fileExtensionValidator(String filepath, String extensions)
	{
		String[] extarr = extensions.replaceAll("*", "").split(";");
		
		for(String s : extarr)
			if(filepath.endsWith(s)) {
				return true;
			}
		return false;
	}
	
}

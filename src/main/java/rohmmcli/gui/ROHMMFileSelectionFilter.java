package rohmmcli.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ROHMMFileSelectionFilter extends FileFilter {

	protected String Message = null;
	protected String[] Extensionlist = null;
	
	public ROHMMFileSelectionFilter(String arg, String ...strings) {
		// TODO Auto-generated constructor stub
		super();
		Message = arg;
		Extensionlist = strings;
		
	}
	@Override
	public boolean accept(File arg0) {
		if (arg0.isDirectory())
			return true;

		String filename = arg0.getName();
		for(String ext : Extensionlist)
			if(filename.endsWith(ext))
			return true;
		return false;
	}

	@Override
	public String getDescription() {
		return Message;
	}

}

package rohmmcli.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class BEDFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {
		if (arg0.isDirectory())
			return true;

		String filename = arg0.getName();
		if (filename.endsWith("bed.gz") || filename.endsWith("bed"))
			return true;
		return false;
	}

	@Override
	public String getDescription() {
		return "BED Files";
	}

}

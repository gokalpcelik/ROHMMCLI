package rohmmcli.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;
@Deprecated
public class VCFFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {

		if (arg0.isDirectory())
			return true;

		String filename = arg0.getName();
		if (filename.endsWith("vcf.gz") || filename.endsWith("vcf"))
			return true;
		return false;
	}

	@Override
	public String getDescription() {
		return "VCF Files";
	}

}

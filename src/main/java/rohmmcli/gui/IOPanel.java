/*
 * Author : Gokalp Celik
 * Year : 2020
 */
package rohmmcli.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import rohmmcli.rohmm.OverSeer;
import rohmmcli.rohmm.ROHMMCLIRunner;

@SuppressWarnings("serial")
public class IOPanel extends JPanel {
	protected JPanel panel_1, panel_0;
	protected OptionPanel AdvPanel;
	protected JLabel vcfLabel, MRSL, MRLL, QL;
	protected JPanel panel;
	protected JTextField vcfPathField;
	protected JTextField outputPrefixField;
	protected JTextField outputDirField;
	protected JTextField knownVariantField;
	protected JTextField ADThreshvalue;
	protected JTextField QualValue;
	protected JTextField ROHLength;
	protected JTextField ROHCount;
	protected JCheckBox knownVariantInclusivePolicy;
	protected JCheckBox knownVariantSpikeInPolicy;
	protected JCheckBox skipZeroAFPolicy;
	protected JRadioButton useDefaultAlleleDistributionPolicy;
	protected JRadioButton useDefaultAlleleFrequencyPolicy;
	protected JRadioButton useCustomModelPolicy;
	protected JLabel brandLabel;
	protected JButton outputDirSelectButton;
	protected JButton vcfSelectButton;
	protected JButton selectAllChrButton;
	protected JButton selectNoneChrButton;
	protected JButton selectAllSampleButton;
	protected JButton selectNoneSampleButton;
	protected JButton runInference;
	protected JButton stopInference;
	protected JButton invertSelectionSampleButton;
	protected JButton selectKnownVariantButton;
	protected JScrollPane scrollPane;
	protected JList<String> chrlist;
	protected JScrollPane scrollPane_1;
	protected JList<String> samplelist;
	protected JFrame parentFrame;
	protected JCheckBox skipIndels;
	protected JCheckBox filterUsingKnown;
	protected JCheckBox ADthresh;
	protected DefaultListModel<String> chrlistmodel = null;
	protected DefaultListModel<String> samplenamemodel = null;
	protected RunnerWorker worker = null;

	/**
	 * Create the panel.
	 */
	public IOPanel() {
		this.setLayout(null);
		this.brandLabel = new JLabel(OverSeer.VERSION);
		this.brandLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.brandLabel.setBounds(12, 508, 120, 25);
		this.add(this.brandLabel);
		this.chrlistmodel = new DefaultListModel<>();
		this.samplenamemodel = new DefaultListModel<>();
		// vcfLabel = new JLabel("Choose VCF File");
		// vcfLabel.setBounds(12, 9, 120, 24);
		// add(vcfLabel);
		this.panel = new JPanel();
		this.vcfPathField = new JTextField();
		this.vcfPathField.setEditable(false);
		// vcfpathfield.setBounds(132, 10, 415, 24);
		// add(vcfpathfield);

		final IOFileDialogButtonListener ioButtonListener = new IOFileDialogButtonListener();

		this.vcfSelectButton = new JButton("Select VCF");
		this.vcfSelectButton.setActionCommand("selectinputvcf");
		// vcfselectbutton.setBounds(550, 10, 120, 24);
		this.vcfSelectButton.addActionListener(ioButtonListener);
		// add(vcfselectbutton);
		this.panel_0 = new JPanel();
		this.panel_0.setBounds(12, 0, 775, 53);
		this.panel_0.setBorder(new TitledBorder("VCF Input"));
		this.panel_0.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.ipadx = 30;
		this.panel_0.add(this.vcfSelectButton, c);
		c.ipadx = 563;
		this.panel_0.add(this.vcfPathField, c);

		this.add(this.panel_0);
		this.panel.setBorder(new TitledBorder("Chromosomes"));
		this.panel.setBounds(12, 50, 120, 406);
		this.add(this.panel);
		this.panel.setLayout(new BorderLayout(0, 0));

		this.scrollPane = new JScrollPane();

		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		final ChrSampleSelectButtonListener selectbuttonlistener = new ChrSampleSelectButtonListener();
		this.chrlist = new JList<>(this.chrlistmodel);
		this.scrollPane.setViewportView(this.chrlist);
		this.chrlist.addListSelectionListener(new ChrListSelectionListener());
		this.selectAllChrButton = new JButton("Select All");
		this.selectAllChrButton.setActionCommand("allchr");
		this.selectAllChrButton.setBounds(12, 458, 120, 25);
		this.selectNoneChrButton = new JButton("Select None");
		this.selectNoneChrButton.setActionCommand("nonechr");
		this.selectNoneChrButton.setBounds(12, 483, 120, 25);
		this.selectAllChrButton.addActionListener(selectbuttonlistener);
		this.selectNoneChrButton.addActionListener(selectbuttonlistener);

		this.panel.add(this.scrollPane);
		this.add(this.selectAllChrButton);
		this.add(this.selectNoneChrButton);

		this.panel_1 = new JPanel();
		this.panel_1.setBorder(new TitledBorder("Samples"));
		this.panel_1.setBounds(132, 50, 163, 406);
		this.add(this.panel_1);
		this.panel_1.setLayout(new BorderLayout(0, 2));

		this.scrollPane_1 = new JScrollPane();
		this.panel_1.add(this.scrollPane_1);
		this.scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.samplelist = new JList<>(this.samplenamemodel);
		this.samplelist.addListSelectionListener(new SampleListActionListener());
		this.scrollPane_1.setViewportView(this.samplelist);
		this.selectAllSampleButton = new JButton("Select All");
		this.selectAllSampleButton.setActionCommand("allsample");
		this.selectAllSampleButton.addActionListener(selectbuttonlistener);
		this.selectAllSampleButton.setBounds(132, 458, 163, 25);
		this.selectNoneSampleButton = new JButton("Select None");
		this.selectNoneSampleButton.setActionCommand("nonesample");
		this.selectNoneSampleButton.addActionListener(selectbuttonlistener);
		this.selectNoneSampleButton.setBounds(132, 483, 163, 25);
		this.invertSelectionSampleButton = new JButton("Invert Selection");
		this.invertSelectionSampleButton.setActionCommand("invertsample");
		this.invertSelectionSampleButton.addActionListener(selectbuttonlistener);
		this.invertSelectionSampleButton.setBounds(132, 508, 163, 25);
		this.add(this.selectAllSampleButton);
		this.add(this.selectNoneSampleButton);
		this.add(this.invertSelectionSampleButton);

		final JPanel outPanel = new JPanel(new GridBagLayout());

		outPanel.setBorder(new TitledBorder("Output Options"));
		outPanel.setBounds(295, 50, 492, 150);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridy = 1;
		c.ipadx = 20;
		final JLabel prefixwarnlabel = new JLabel("Prefix for Output Files");
		this.outputPrefixField = new JTextField();
		outPanel.add(prefixwarnlabel, c);
		c.ipadx = 250;
		outPanel.add(this.outputPrefixField, c);

		this.outputDirField = new JTextField();
		this.outputDirField.setEditable(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 0;
		c.ipadx = 20;
		this.outputDirSelectButton = new JButton("Select Directory");
		this.outputDirSelectButton.setActionCommand("selectoutputdir");
		this.outputDirSelectButton.addActionListener(ioButtonListener);
		outPanel.add(this.outputDirSelectButton, c);
		c.ipadx = 250;
		outPanel.add(this.outputDirField, c);
		this.MRSL = new JLabel("Minimum Site Count");
		this.MRLL = new JLabel("Minimum ROH Length");
		this.QL = new JLabel("Minimum ROH Qual");
		this.QualValue = new JTextField("0");
		this.ROHCount = new JTextField("0");
		this.ROHLength = new JTextField("0");
		c.gridy = 2;
		outPanel.add(this.MRSL, c);
		outPanel.add(this.ROHCount, c);
		c.gridy = 3;
		outPanel.add(this.MRLL, c);
		outPanel.add(this.ROHLength, c);
		c.gridy = 4;
		outPanel.add(this.QL, c);
		outPanel.add(this.QualValue, c);
		this.add(outPanel);
		final JPanel filterPanel = new JPanel(new GridBagLayout());
		filterPanel.setBorder(new TitledBorder("Variant Filtering"));
		filterPanel.setBounds(295, 200, 492, 170);
		final VariantFilterCheckBoxListener vfcl = new VariantFilterCheckBoxListener();
		this.skipIndels = new JCheckBox("Skip Indels");
		this.skipIndels.setActionCommand("skipindels");
		this.skipIndels.addActionListener(vfcl);
		c.weightx = 0.5;
		c.gridy = 0;
		filterPanel.add(this.skipIndels, c);
		this.filterUsingKnown = new JCheckBox("Use known set of variants to filter");
		this.filterUsingKnown.setActionCommand("useknownvariants");
		this.filterUsingKnown.addActionListener(vfcl);
		c.gridy = 0;
		filterPanel.add(this.filterUsingKnown, c);
		this.selectKnownVariantButton = new JButton("Select Known Variants");
		this.selectKnownVariantButton.setActionCommand("selectknown");
		this.selectKnownVariantButton.addActionListener(ioButtonListener);
		this.selectKnownVariantButton.setEnabled(false);
		c.ipadx = 15;
		c.gridy = 1;
		filterPanel.add(this.selectKnownVariantButton, c);
		this.knownVariantField = new JTextField();
		this.knownVariantField.setEditable(false);
		c.gridy = 1;
		filterPanel.add(this.knownVariantField, c);
		this.knownVariantInclusivePolicy = new JCheckBox("Include high quality unknown variants");
		this.knownVariantInclusivePolicy.setActionCommand("includeunknown");
		this.knownVariantInclusivePolicy.addActionListener(vfcl);
		this.knownVariantInclusivePolicy.setEnabled(false);
		this.knownVariantSpikeInPolicy = new JCheckBox("Spike-in missing as HOMREF");
		this.knownVariantSpikeInPolicy.setActionCommand("spikein");
		this.knownVariantSpikeInPolicy.addActionListener(vfcl);
		this.knownVariantSpikeInPolicy.setEnabled(false);
		c.gridy = 2;
		filterPanel.add(this.knownVariantSpikeInPolicy, c);
		c.gridy = 2;
		filterPanel.add(this.knownVariantInclusivePolicy, c);
		this.ADthresh = new JCheckBox("AD Threshold");
		this.ADthresh.setToolTipText("Allelic Depth Ratio threshold to eliminate false heterozygous calls");
		this.ADThreshvalue = new JTextField("0.2");
		c.gridy = 3;
		filterPanel.add(this.ADthresh, c);
		c.gridx = 1;
		filterPanel.add(this.ADThreshvalue, c);
		this.skipZeroAFPolicy = new JCheckBox("Skip sites that are all HOMREF in all selected samples");
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		filterPanel.add(this.skipZeroAFPolicy, c);
		this.add(filterPanel);
		final JPanel hmmPanel = new JPanel(new GridBagLayout());
		hmmPanel.setBorder(new TitledBorder("Simple HMM Options"));
		hmmPanel.setBounds(295, 372, 492, 100);
		final HMMRadioButtonListener hmmRadioButtonListener = new HMMRadioButtonListener();
		this.useDefaultAlleleDistributionPolicy = new JRadioButton("Use Default Allele Distribution Model");
		this.useDefaultAlleleDistributionPolicy.setActionCommand("usedefaultalleledistribution");
		this.useDefaultAlleleDistributionPolicy.addActionListener(hmmRadioButtonListener);
		this.useDefaultAlleleDistributionPolicy.setSelected(true);
		this.useDefaultAlleleFrequencyPolicy = new JRadioButton("Use Default Allele Frequency Model");
		this.useDefaultAlleleFrequencyPolicy.setActionCommand("usedefaultallelefrequency");
		this.useDefaultAlleleFrequencyPolicy.addActionListener(hmmRadioButtonListener);
		this.useCustomModelPolicy = new JRadioButton("Use Custom HMM Model (Set options from 'Advanced Options')");
		this.useCustomModelPolicy.setActionCommand("usecustom");
		this.useCustomModelPolicy.addActionListener(hmmRadioButtonListener);
		final ButtonGroup hmmModelRadioGroup = new ButtonGroup();
		hmmModelRadioGroup.add(this.useDefaultAlleleDistributionPolicy);
		hmmModelRadioGroup.add(this.useDefaultAlleleFrequencyPolicy);
		hmmModelRadioGroup.add(this.useCustomModelPolicy);
		c.weightx = 0.5;
		c.gridy = 0;
		hmmPanel.add(this.useDefaultAlleleDistributionPolicy, c);
		c.gridy = 1;
		hmmPanel.add(this.useDefaultAlleleFrequencyPolicy, c);
		c.gridy = 2;
		hmmPanel.add(this.useCustomModelPolicy, c);
		this.add(hmmPanel);
		final ROHMMRunnerButtonListener runnerbuttonlistener = new ROHMMRunnerButtonListener();
		this.runInference = new JButton(">>> RUN ROHMM! <<<");
		this.runInference.setActionCommand("runinference");
		this.stopInference = new JButton("STOP ROHMM");
		this.stopInference.setActionCommand("stopinference");
		this.runInference.setBounds(295, 483, 350, 50);
		this.stopInference.setBounds(645, 483, 142, 50);
		this.runInference.addActionListener(runnerbuttonlistener);
		this.stopInference.addActionListener(runnerbuttonlistener);
		this.stopInference.setEnabled(false);
		this.add(this.runInference);
		this.add(this.stopInference);

	}

	protected JPanel getSelf() {
		return this;
	}

	protected void updateChromosomeList(List<String> availableContigList) {
		if (availableContigList != null) {
			for (final String contig : availableContigList) {
				this.chrlistmodel.addElement(contig);
			}
		}
	}

	protected void updateSampleNameList(List<String> sampleNameList) {
		if (sampleNameList != null) {
			for (final String s : sampleNameList) {
				this.samplenamemodel.addElement(s);
			}
		}
	}

	private static <T> void selectionInverter(JList<T> jlist) {

		final int size = jlist.getModel().getSize();
		final ArrayList<Integer> selectedindices = new ArrayList<>();
		for (int i = 0; i < size; i++) {

			if (!jlist.isSelectedIndex(i)) {
				selectedindices.add(i);
			}
		}

		jlist.setSelectedIndices(selectedindices.stream().mapToInt(i -> i).toArray());

	}

	protected void resetIOPanelOptions() {
		IOPanel.this.skipIndels.setSelected(false);
		IOPanel.this.filterUsingKnown.setSelected(false);
		IOPanel.this.vcfPathField.setText("");
		IOPanel.this.outputDirField.setText("");
		IOPanel.this.knownVariantField.setText("");
		IOPanel.this.selectKnownVariantButton.setEnabled(false);
		IOPanel.this.chrlistmodel.clear();
		IOPanel.this.samplenamemodel.clear();
		IOPanel.this.knownVariantInclusivePolicy.setEnabled(false);
		IOPanel.this.knownVariantSpikeInPolicy.setEnabled(false);
		IOPanel.this.AdvPanel.INFOTags.removeAllItems();
	}

	public class IOFileDialogButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			IOPanel.this.parentFrame = (JFrame) SwingUtilities.getWindowAncestor(IOPanel.this.getSelf());

			final String actionCommand = e.getActionCommand();

			switch (actionCommand) {
			case "selectoutputdir":
				this.OutputDirSelectButtonAction();
				break;
			case "selectinputvcf":
				this.VCFSelectButtonAction();
				break;
			case "selectknown":
				IOPanel.this.KnownSelectButtonAction();
				break;
			default:
				break;
			}

		}

		protected void OutputDirSelectButtonAction() {
			try {
				final File file = FileSelectorUtil.selectDirectory(IOPanel.this.parentFrame, "Select Output Directory",
						new File("."));
				if (file != null) {
					IOPanel.this.outputDirField.setText(file.getAbsolutePath() + (OverSeer.isWindows() ? "\\" : "/"));
				}
			} catch (final Exception exp) {
				exp.printStackTrace();
			}
		}

		protected void VCFSelectButtonAction() {
			try {
				final File file = FileSelectorUtil.openFile(IOPanel.this.parentFrame, "Select VCF File...",
						new ROHMMFileSelectionFilter("VCF Files", "vcf", "vcf.gz"), new File("."));
				if (file != null) {
					IOPanel.this.resetIOPanelOptions();
					IOPanel.this.vcfPathField.setText(file.getAbsolutePath());
					OverSeer.resetOptionsGUI();
					OverSeer.setVCFPath(file);
					IOPanel.this.outputDirField.setText(file.getParent() + (OverSeer.isWindows() ? "\\" : "/"));
					OverSeer.setOption(GUIOptionStandards.OUTPUTPREFIX, file.getParent()
							+ (OverSeer.isWindows() ? "\\" : "/") + IOPanel.this.outputPrefixField.getText());
					IOPanel.this.updateChromosomeList(OverSeer.getAvailableContigsList());
					IOPanel.this.updateSampleNameList(OverSeer.getSampleNameList());
					IOPanel.this.AdvPanel.setInfoTags(OverSeer.getInfoTags());
				}

			} catch (final Exception exp) {
				exp.printStackTrace();
			}
		}

	}

	protected void KnownSelectButtonAction() {
		try {
			final File file = FileSelectorUtil.openFile(this.parentFrame, "Select Known Variants File...",
					new ROHMMFileSelectionFilter("BED and VCF Files", "bed", "bed.gz", "vcf", "vcf.gz"), new File("."));
			if (file != null) {
				this.knownVariantField.setText("");
				OverSeer.setOption(GUIOptionStandards.KNOWNSNPPATH, file.getAbsolutePath());
				this.knownVariantField.setText(file.getAbsolutePath());
				this.knownVariantSpikeInPolicy.setEnabled(true);
			}

		} catch (final Exception exp) {
			exp.printStackTrace();
		}
	}

	public class HMMRadioButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getActionCommand()) {
			case "usedefaultalleledistribution":
				OverSeer.setOption(GUIOptionStandards.HMMMODELFILE, "MODELXDIST");
				break;
			case "usedefaultallelefrequency":
				OverSeer.setOption(GUIOptionStandards.HMMMODELFILE, "MODELHWDIST");
				break;
			case "usecustom":
				OverSeer.setOption(GUIOptionStandards.HMMMODELFILE, "CUSTOM");
				// IOPanel.this.AdvPanel.setAdvancedOptions();
				break;
			}

		}

	}

	public class ChrSampleSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			final String actionCommand = arg0.getActionCommand();
			switch (actionCommand) {
			case "allchr":
				IOPanel.this.chrlist.setSelectionInterval(0, IOPanel.this.chrlist.getModel().getSize() - 1);
				IOPanel.this.setChrList();
				break;
			case "nonechr":
				IOPanel.this.chrlist.clearSelection();
				OverSeer.removeOption(GUIOptionStandards.CONTIGS);
				break;
			case "allsample":
				IOPanel.this.samplelist.setSelectionInterval(0, IOPanel.this.samplelist.getModel().getSize() - 1);
				IOPanel.this.setSampleList();
				break;
			case "nonesample":
				IOPanel.this.samplelist.clearSelection();
				OverSeer.removeOption(GUIOptionStandards.SAMPLENAMELIST);
				break;
			case "invertsample":
				selectionInverter(IOPanel.this.samplelist);
				IOPanel.this.setSampleList();
				break;
			}
		}

	}

	protected void setSampleList() {
		String samplestring = "";
		for (final String s : IOPanel.this.samplelist.getSelectedValuesList()) {
			samplestring += s + ",";
		}

		samplestring = samplestring.substring(0, samplestring.length() - 1);

		System.err.println(samplestring);
		OverSeer.setOption(GUIOptionStandards.SAMPLENAMELIST, samplestring);
	}

	protected void setChrList() {
		String chrstring = "";
		for (final String s : IOPanel.this.chrlist.getSelectedValuesList()) {
			chrstring += s + ",";
		}
		chrstring = chrstring.substring(0, chrstring.length() - 1);
		System.err.println(chrstring);
		OverSeer.setOption(GUIOptionStandards.CONTIGS, chrstring);
	}

	public class ROHMMRunnerButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getActionCommand()) {
			case "runinference":
				OverSeer.setOption(GUIOptionStandards.OUTPUTPREFIX,
						IOPanel.this.outputDirField.getText() + IOPanel.this.outputPrefixField.getText());

				if (IOPanel.this.ADthresh.isSelected()) {
					OverSeer.setOption(GUIOptionStandards.ALLELICBALANCETHRESHOLD,
							IOPanel.this.ADThreshvalue.getText());
				} else {
					OverSeer.removeOption(GUIOptionStandards.ALLELICBALANCETHRESHOLD);
				}

				if (IOPanel.this.skipZeroAFPolicy.isSelected()) {
					OverSeer.setOption(GUIOptionStandards.SKIPZEROAF, null);
				}

				IOPanel.this.runInference.setEnabled(false);
				IOPanel.this.stopInference.setEnabled(true);
				IOPanel.this.worker = new RunnerWorker();
				IOPanel.this.worker.execute();
				break;
			case "stopinference":
				if (!IOPanel.this.worker.isDone()) {
					IOPanel.this.worker.cancel(true);
					IOPanel.this.resetIOPanelOptions();
					IOPanel.this.runInference.setEnabled(true);
					IOPanel.this.stopInference.setEnabled(false);
				}
				break;
			}

		}

	}

	protected class ChrListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			if (arg0.getValueIsAdjusting()) {
				IOPanel.this.setChrList();
			}
		}
	}

	protected class VariantFilterCheckBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getActionCommand()) {
			case "skipindels":
				if (IOPanel.this.skipIndels.isSelected()) {
					OverSeer.setOption(GUIOptionStandards.SKIPINDELS, null);
				} else {
					OverSeer.removeOption(GUIOptionStandards.SKIPINDELS);
				}
				break;
			case "useknownvariants":
				IOPanel.this.selectKnownVariantButton.setEnabled(!IOPanel.this.selectKnownVariantButton.isEnabled());
				if (!IOPanel.this.filterUsingKnown.isSelected()) {
					OverSeer.removeOption(GUIOptionStandards.KNOWNSNPPATH);
					OverSeer.removeOption(GUIOptionStandards.INCLUDEUNKNOWN);
					OverSeer.removeOption(GUIOptionStandards.SPIKEIN);
					IOPanel.this.knownVariantInclusivePolicy.setEnabled(false);
					IOPanel.this.knownVariantInclusivePolicy.setSelected(false);
					IOPanel.this.knownVariantSpikeInPolicy.setEnabled(false);
					IOPanel.this.knownVariantSpikeInPolicy.setSelected(false);
					IOPanel.this.knownVariantField.setText("");
				}
				break;
			case "includeunknown":
				if (IOPanel.this.knownVariantInclusivePolicy.isSelected()) {
					OverSeer.setOption(GUIOptionStandards.INCLUDEUNKNOWN, null);
				} else {
					OverSeer.removeOption(GUIOptionStandards.INCLUDEUNKNOWN);
				}
				break;
			case "spikein":
				if (OverSeer.getOptionMap().containsKey(GUIOptionStandards.KNOWNSNPPATH)) {
					if (IOPanel.this.knownVariantSpikeInPolicy.isSelected()) {
						IOPanel.this.knownVariantInclusivePolicy.setEnabled(true);
						OverSeer.setOption(GUIOptionStandards.SPIKEIN, null);
					} else {
						IOPanel.this.knownVariantInclusivePolicy.setSelected(false);
						IOPanel.this.knownVariantInclusivePolicy.setEnabled(false);
						OverSeer.removeOption(GUIOptionStandards.SPIKEIN);
						OverSeer.removeOption(GUIOptionStandards.INCLUDEUNKNOWN);
					}
				}
				break;
			}

		}

	}

	protected class SampleListActionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			// TODO Auto-generated method stub

			if (arg0.getValueIsAdjusting()) {
				IOPanel.this.setSampleList();
			}
		}

	}

	protected class RunnerWorker extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() {
			// TODO Auto-generated method stub
			OverSeer.setGUICMD();
			if (IOPanel.this.useCustomModelPolicy.isSelected()) {
				IOPanel.this.AdvPanel.setAdvancedOptions();
			}
			OverSeer.setHMMParams();
			OverSeer.setInputParams();
			OverSeer.log(IOPanel.class.getSimpleName(), OverSeer.getOptionMap().toString(), OverSeer.DEBUG);
			ROHMMCLIRunner.Runner(OverSeer.getGUICMD());
			return null;
		}

		@Override
		public void done() {
			IOPanel.this.runInference.setEnabled(true);
			IOPanel.this.stopInference.setEnabled(false);

		}

	}
}

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
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import rohmmcli.rohmm.OverSeer;

@SuppressWarnings("serial")
public class IOPanel extends JPanel {
	protected JPanel panel_1, panel_0;
	protected JLabel vcfLabel;
	protected JPanel panel;
	protected JTextField vcfPathField;
	protected JTextField outputPrefixField;
	protected JTextField outputDirField;
	protected JTextField knownVariantField;
	protected JRadioButton knownVariantInclusivePolicy;
	protected JRadioButton knownVariantExclusivePolicy;
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
	protected JButton invertSelectionSampleButton;
	protected JButton selectKnownVariantButton;
	protected JScrollPane scrollPane;
	protected JList<String> chrlist;
	protected JScrollPane scrollPane_1;
	protected JList<String> samplelist;
	protected JFrame parentFrame;
	protected JCheckBox skipIndels;
	protected JCheckBox filterUsingKnown;
	protected DefaultListModel<String> chrlistmodel = null;
	protected DefaultListModel<String> samplenamemodel = null;

	/**
	 * Create the panel.
	 */
	public IOPanel() {
		setLayout(null);
		brandLabel = new JLabel(OverSeer.VERSION);
		brandLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		brandLabel.setBounds(12, 508, 120, 25);
		add(brandLabel);
		chrlistmodel = new DefaultListModel<String>();
		samplenamemodel = new DefaultListModel<String>();
		// vcfLabel = new JLabel("Choose VCF File");
		// vcfLabel.setBounds(12, 9, 120, 24);
		// add(vcfLabel);
		panel = new JPanel();
		vcfPathField = new JTextField();
		vcfPathField.setEditable(false);
		// vcfpathfield.setBounds(132, 10, 415, 24);
		// add(vcfpathfield);

		IOFileDialogButtonListener ioButtonListener = new IOFileDialogButtonListener();

		vcfSelectButton = new JButton("Select VCF");
		vcfSelectButton.setActionCommand("selectinputvcf");
		// vcfselectbutton.setBounds(550, 10, 120, 24);
		vcfSelectButton.addActionListener(ioButtonListener);
		// add(vcfselectbutton);
		panel_0 = new JPanel();
		panel_0.setBounds(12, 0, 775, 53);
		panel_0.setBorder(new TitledBorder("VCF Input"));
		panel_0.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.ipadx = 30;
		panel_0.add(vcfSelectButton, c);
		c.ipadx = 563;
		panel_0.add(vcfPathField, c);

		add(panel_0);
		panel.setBorder(new TitledBorder("Chromosomes"));
		panel.setBounds(12, 50, 120, 406);
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();

		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		ListActionListener listaction = new ListActionListener();
		ChrSampleSelectButtonListener selectbuttonlistener = new ChrSampleSelectButtonListener();
		chrlist = new JList<String>(chrlistmodel);
		scrollPane.setViewportView(chrlist);
		chrlist.addListSelectionListener(listaction);
		selectAllChrButton = new JButton("Select All");
		selectAllChrButton.setActionCommand("allchr");
		selectAllChrButton.setBounds(12, 458, 120, 25);
		selectNoneChrButton = new JButton("Select None");
		selectNoneChrButton.setActionCommand("nonechr");
		selectNoneChrButton.setBounds(12, 483, 120, 25);
		selectAllChrButton.addActionListener(selectbuttonlistener);
		selectNoneChrButton.addActionListener(selectbuttonlistener);

		panel.add(scrollPane);
		add(selectAllChrButton);
		add(selectNoneChrButton);

		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder("Samples"));
		panel_1.setBounds(132, 50, 163, 406);
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 2));

		scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1);
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		samplelist = new JList<String>(samplenamemodel);
		scrollPane_1.setViewportView(samplelist);
		samplelist.addListSelectionListener(listaction);
		selectAllSampleButton = new JButton("Select All");
		selectAllSampleButton.setActionCommand("allsample");
		selectAllSampleButton.addActionListener(selectbuttonlistener);
		selectAllSampleButton.setBounds(132, 458, 163, 25);
		selectNoneSampleButton = new JButton("Select None");
		selectNoneSampleButton.setActionCommand("nonesample");
		selectNoneSampleButton.addActionListener(selectbuttonlistener);
		selectNoneSampleButton.setBounds(132, 483, 163, 25);
		invertSelectionSampleButton = new JButton("Invert Selection");
		invertSelectionSampleButton.setActionCommand("invertsample");
		invertSelectionSampleButton.addActionListener(selectbuttonlistener);
		invertSelectionSampleButton.setBounds(132, 508, 163, 25);
		add(selectAllSampleButton);
		add(selectNoneSampleButton);
		add(invertSelectionSampleButton);

		JPanel outPanel = new JPanel(new GridBagLayout());

		outPanel.setBorder(new TitledBorder("Output Options"));
		outPanel.setBounds(295, 50, 492, 80);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridy = 1;
		c.ipadx = 20;
		JLabel prefixwarnlabel = new JLabel("Prefix for Output Files");
		outputPrefixField = new JTextField();
		outPanel.add(prefixwarnlabel, c);
		c.ipadx = 250;
		outPanel.add(outputPrefixField, c);

		outputDirField = new JTextField();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 0;
		c.ipadx = 20;
		outputDirSelectButton = new JButton("Select Directory");
		outputDirSelectButton.setActionCommand("selectoutputdir");
		outputDirSelectButton.addActionListener(ioButtonListener);
		outPanel.add(outputDirSelectButton, c);
		c.ipadx = 250;
		outPanel.add(outputDirField, c);
		add(outPanel);
		JPanel filterPanel = new JPanel(new GridBagLayout());
		filterPanel.setBorder(new TitledBorder("Variant Filtering"));
		filterPanel.setBounds(295, 127, 492, 100);
		skipIndels = new JCheckBox("Skip Indels");
		c.weightx = 0.5;
		c.gridy = 0;
		filterPanel.add(skipIndels, c);
		filterUsingKnown = new JCheckBox("Use known set of variants to filter");
		c.gridy = 0;
		filterPanel.add(filterUsingKnown, c);
		selectKnownVariantButton = new JButton("Select Known Variants");
		selectKnownVariantButton.setActionCommand("selectknown");
		selectKnownVariantButton.addActionListener(selectbuttonlistener);
		c.ipadx = 15;
		c.gridy = 1;
		filterPanel.add(selectKnownVariantButton, c);
		knownVariantField = new JTextField();
		c.gridy = 1;
		filterPanel.add(knownVariantField, c);
		ButtonGroup knownVariantRadioGroup = new ButtonGroup();
		knownVariantInclusivePolicy = new JRadioButton("Include high quality unknown variants");
		knownVariantInclusivePolicy.setSelected(true);
		knownVariantExclusivePolicy = new JRadioButton("Exclude unknown variants");
		knownVariantRadioGroup.add(knownVariantInclusivePolicy);
		knownVariantRadioGroup.add(knownVariantExclusivePolicy);
		c.gridy = 2;
		filterPanel.add(knownVariantExclusivePolicy, c);
		c.gridy = 2;
		filterPanel.add(knownVariantInclusivePolicy, c);

		add(filterPanel);
		JPanel hmmPanel = new JPanel(new GridBagLayout());
		hmmPanel.setBorder(new TitledBorder("Simple HMM Options"));
		hmmPanel.setBounds(295, 225, 492, 100);
		HMMRadioButtonListener hmmRadioButtonListener = new HMMRadioButtonListener();
		useDefaultAlleleDistributionPolicy = new JRadioButton("Use Default Allele Distribution Model");
		useDefaultAlleleDistributionPolicy.setActionCommand("usedefaultalleledistribution");
		useDefaultAlleleDistributionPolicy.addActionListener(hmmRadioButtonListener);
		useDefaultAlleleDistributionPolicy.setSelected(true);
		useDefaultAlleleFrequencyPolicy = new JRadioButton("Use Default Allele Frequency Model");
		useDefaultAlleleFrequencyPolicy.setActionCommand("usedefaultallelefrequency");
		useDefaultAlleleFrequencyPolicy.addActionListener(hmmRadioButtonListener);
		useCustomModelPolicy = new JRadioButton("Use Custom HMM Model (Set options from 'Advanced Options')");
		useCustomModelPolicy.setActionCommand("usecustom");
		useCustomModelPolicy.addActionListener(hmmRadioButtonListener);
		ButtonGroup hmmModelRadioGroup = new ButtonGroup();
		hmmModelRadioGroup.add(useDefaultAlleleDistributionPolicy);
		hmmModelRadioGroup.add(useDefaultAlleleFrequencyPolicy);
		hmmModelRadioGroup.add(useCustomModelPolicy);
		c.weightx = 0.5;
		c.gridy = 0;
		hmmPanel.add(useDefaultAlleleDistributionPolicy, c);
		c.gridy = 1;
		hmmPanel.add(useDefaultAlleleFrequencyPolicy, c);
		c.gridy = 2;
		hmmPanel.add(useCustomModelPolicy, c);
		add(hmmPanel);
		runInference = new JButton(">>> Run ROHMM! <<<");
		runInference.setBounds(295, 483, 492, 50);
		runInference.addActionListener(new ROHMMRunnerButtonListener());
		add(runInference);

	}

	protected JPanel getSelf() {
		return this;
	}

	protected void updateChromosomeList(List<String> availableContigList) {
		for (String contig : availableContigList) {
			chrlistmodel.addElement(contig);
		}
	}

	protected void updateSampleNameList(List<String> sampleNameList) {
		for (String s : sampleNameList) {
			samplenamemodel.addElement(s);
		}
	}

	private static <T> void selectionInverter(JList<T> jlist) {

		int size = jlist.getModel().getSize();
		ArrayList<Integer> selectedindices = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {

			if (!jlist.isSelectedIndex(i))
				selectedindices.add(i);
		}

		jlist.setSelectedIndices(selectedindices.stream().mapToInt(i -> i).toArray());

	}

	public class IOFileDialogButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			parentFrame = (JFrame) SwingUtilities.getWindowAncestor(getSelf());

			String actionCommand = e.getActionCommand();

			switch (actionCommand) {
			case "selectoutputdir":
				OutputDirSelectButtonAction();
				break;
			case "selectinputvcf":
				VCFSelectButtonAction();
				break;
			case "selectknown":
				break;
			default:
				break;
			}

		}

		public void OutputDirSelectButtonAction() {
			try {
				File file = FileSelectorUtil.selectDirectory(parentFrame, "Select Output Directory", new File("."));
				if (file != null) {
					outputDirField.setText(file.getAbsolutePath());
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}

		public void VCFSelectButtonAction() {
			try {
				File file = FileSelectorUtil.openFile(parentFrame, "Select VCF File...", new VCFFilter(),
						new File("."));
				if (file != null) {
					vcfPathField.setText(file.getAbsolutePath());
					OverSeer.setVCFPath(file);
					chrlistmodel.clear();
					samplenamemodel.clear();
					updateChromosomeList(OverSeer.getAvailableContigsList());
					updateSampleNameList(OverSeer.getSampleNameList());
				}

			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}

	}
	
	public class HMMRadioButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public class ChrSampleSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String actionCommand = arg0.getActionCommand();
			switch (actionCommand) {
			case "allchr":
				chrlist.setSelectionInterval(0, chrlist.getModel().getSize() - 1);
				break;
			case "nonechr":
				chrlist.clearSelection();
				break;
			case "allsample":
				samplelist.setSelectionInterval(0, samplelist.getModel().getSize() - 1);
				break;
			case "nonesample":
				samplelist.clearSelection();
				break;
			case "invertsample":
				selectionInverter(samplelist);
				break;
			}
		}

	}
	
	public class ROHMMRunnerButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public class ListActionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			// TODO Auto-generated method stub

		}
	}

}
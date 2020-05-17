package rohmmcli.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.variant.vcf.VCFHeader;
import rohmmcli.rohmm.Utility;

@SuppressWarnings("serial")
public class IOPanel extends JPanel {
	protected JPanel panel_1, panel_0;
	protected JLabel vcfLabel;
	protected JPanel panel;
	protected JTextField vcfPathField;
	protected JTextField outputPrefixField;
	protected JTextField outputDirField;
	protected JLabel brandLabel;
	protected JButton outputDirSelectButton;
	protected JButton vcfSelectButton;
	protected JButton selectAllChrButton;
	protected JButton selectNoneChrButton;
	protected JButton selectAllSampleButton;
	protected JButton selectNoneSampleButton;
	protected JButton runInference;
	protected JButton invertSelectionSampleButton;
	protected JScrollPane scrollPane;
	protected JList<String> chrlist;
	protected JScrollPane scrollPane_1;
	protected JList<String> samplelist;
	protected JFrame parentFrame;
	protected DefaultListModel<String> chrlistmodel = null;
	protected DefaultListModel<String> samplenamemodel = null;

	/**
	 * Create the panel.
	 */
	public IOPanel() {
		setLayout(null);
		brandLabel = new JLabel(Utility.VERSION);
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
		// vcfpathfield.setBounds(132, 10, 415, 24);
		// add(vcfpathfield);
		vcfSelectButton = new JButton("Select VCF");
		// vcfselectbutton.setBounds(550, 10, 120, 24);
		vcfSelectButton.addActionListener(new VCFSelectButtonListener());
		// add(vcfselectbutton);
		panel_0 = new JPanel();
		panel_0.setBounds(12, 0, 775, 53);
		panel_0.setBorder(new TitledBorder("VCF Input"));
		panel_0.setLayout(new GridBagLayout());
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.ipadx = 30;
		panel_0.add(vcfSelectButton, constraint);
		constraint.ipadx = 563;
		panel_0.add(vcfPathField, constraint);
		
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
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridy = 1;
		constraint.ipadx = 20;
		JLabel prefixwarnlabel = new JLabel("Prefix for Output Files");
		outputPrefixField = new JTextField();
		outPanel.add(prefixwarnlabel, constraint);
		constraint.ipadx = 250;
		outPanel.add(outputPrefixField, constraint);
		
		outputDirField = new JTextField();
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridy = 0;
		constraint.ipadx = 20;
		outputDirSelectButton = new JButton("Select Directory");
		outputDirSelectButton.addActionListener(new OutputDirSelectButtonListener());
		outPanel.add(outputDirSelectButton, constraint);
		constraint.ipadx = 250;
		outPanel.add(outputDirField, constraint);
		add(outPanel);
		JPanel hmmPanel = new JPanel(new GridBagLayout());
		hmmPanel.setBorder(new TitledBorder("HMM Selection"));
		hmmPanel.setBounds(295,127,492,50);
		add(hmmPanel);
		runInference = new JButton("Run ROHMM!");
		runInference.setBounds(295,177,492,50);
		add(runInference);
		

		
		
		

	}

	protected JPanel getSelf() {
		return this;
	}

	protected void updateChromosomeList(VCFHeader header) {

		SAMSequenceDictionary dict = header.getSequenceDictionary();

		List<SAMSequenceRecord> lists = dict.getSequences();

		for (SAMSequenceRecord record : lists) {
			chrlistmodel.addElement(record.getSequenceName());
		}

	}

	protected void updateSampleNameList(VCFHeader header) {
		ArrayList<String> samplenamelists = header.getSampleNamesInOrder();

		for (String s : samplenamelists) {
			samplenamemodel.addElement(s);
		}
	}
	
	private static <T> void selectionInverter(JList<T> jlist) {
		
		int size = jlist.getModel().getSize();
		ArrayList<Integer> selectedindices = new ArrayList<Integer>();
		for(int i = 0; i < size; i++) {
			
			if(!jlist.isSelectedIndex(i))
				selectedindices.add(i);
		}
		
		jlist.setSelectedIndices(selectedindices.stream().mapToInt(i -> i).toArray());

	}
	
	public class OutputDirSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			parentFrame = (JFrame) SwingUtilities.getWindowAncestor(getSelf());
			try {
				File file = FileSelectorUtil.selectDirectory(parentFrame, "Select Output Directory", new File("."));
				if (file != null) {
					outputDirField.setText(file.getAbsolutePath());
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
		
	}

	public class ChrSampleSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String actionCommand = arg0.getActionCommand();
			switch(actionCommand) {
			case "allchr":
				chrlist.setSelectionInterval(0, chrlist.getModel().getSize()-1);
				break;
			case "nonechr":
				chrlist.clearSelection();
				break;
			case "allsample":
				samplelist.setSelectionInterval(0, samplelist.getModel().getSize()-1);
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
	
	public class VCFSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			parentFrame = (JFrame) SwingUtilities.getWindowAncestor(getSelf());
			try {
				File file = FileSelectorUtil.openFile(parentFrame, "Select VCF File...", new VCFFilter(), new File("."));
				if (file != null) {
					vcfPathField.setText(file.getAbsolutePath());
					Utility.setVCFPath(vcfPathField.getText());
					VCFHeader header = Utility.getVCFHeader();
					chrlistmodel.clear();
					samplenamemodel.clear();
					updateChromosomeList(header);
					updateSampleNameList(header);
				}

			} catch (Exception exp) {
				exp.printStackTrace();
			}

		}

	}
	
	
	
	public class ListActionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			// TODO Auto-generated method stub

			

		}
	}

}
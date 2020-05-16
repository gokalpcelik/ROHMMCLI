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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
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
	protected JButton outputDirSelectButton;
	protected JButton vcfSelectButton;
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
		panel_0.setBounds(12, 10, 775, 50);
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
		panel.setBounds(12, 60, 120, 406);
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		panel.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		ListActionListener listaction = new ListActionListener();
		chrlist = new JList<String>(chrlistmodel);
		scrollPane.setViewportView(chrlist);
		chrlist.addListSelectionListener(listaction);
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder("Samples"));
		panel_1.setBounds(132, 60, 163, 406);
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1);
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		samplelist = new JList<String>(samplenamemodel);
		scrollPane_1.setViewportView(samplelist);
		samplelist.addListSelectionListener(listaction);
		
		JPanel outpanel = new JPanel(new GridBagLayout());
		
		outpanel.setBorder(new TitledBorder("Output Options"));
		outpanel.setBounds(295, 60, 492, 80);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridy = 1;
		constraint.ipadx = 20;
		JLabel prefixwarnlabel = new JLabel("Prefix for Output Files");
		outputPrefixField = new JTextField();
		outpanel.add(prefixwarnlabel, constraint);
		constraint.ipadx = 250;
		outpanel.add(outputPrefixField, constraint);
		
		outputDirField = new JTextField();
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridy = 0;
		constraint.ipadx = 20;
		outputDirSelectButton = new JButton("Select Directory");
		outputDirSelectButton.addActionListener(new OutputDirSelectButtonListener());
		outpanel.add(outputDirSelectButton, constraint);
		constraint.ipadx = 250;
		outpanel.add(outputDirField, constraint);
		
		
		
		add(outpanel);

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
	
	public class OutputDirSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.err.println("outdiraction");
		}
		
	}

	public class VCFSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			parentFrame = (JFrame) SwingUtilities.getWindowAncestor(getSelf());
			try {
				File file = FileSelectorUtil.openFile(parentFrame, "Open VCF File", FileSelectorUtil.VCFEXTENSIONS);
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

			if (arg0.getSource() == chrlist)
				System.err.println(chrlist.getSelectedValuesList());
			else if (arg0.getSource() == samplelist)
				System.err.println(samplelist.getSelectedValuesList());

		}
	}

}
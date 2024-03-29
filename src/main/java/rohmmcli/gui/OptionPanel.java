/*
 * Author : Gokalp Celik
 *
 * Date : Jul 8, 2020
 *
 */
package rohmmcli.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import rohmmcli.rohmm.Model;
import rohmmcli.rohmm.OverSeer;

@SuppressWarnings("serial")
public class OptionPanel extends JPanel {

	JTextField R1, R2, R3, N1, N2, N3, AFD, ERD, ADT, ST;
	JLabel COLUMNS, ROW1, ROW2;
	JCheckBox getAFFromTag, forceER, useADs;
	JComboBox<String> INFOTags, LOGLevel;
	JTextField RT, NT, BF, NF;
	JLabel ROWT1, ROWT2, BASEFACT, NORMFACT, AFDEF, ERDEF, LOGDEF, MINROHDEF, MINSITEDEF, MINQUALDEF, STDEF;
	JRadioButton customHMMAlleleDistribution, customHMMAlleleFrequency, useFixedTransitionParams,
			useDistanceDecayFunction;

	public OptionPanel() {
		this.setLayout(null);
		this.ROW1 = new JLabel("ROH");
		this.ROW1.setHorizontalAlignment(JLabel.RIGHT);
		this.ROW2 = new JLabel("NonROH");
		this.ROW2.setHorizontalAlignment(JLabel.RIGHT);
		this.AFDEF = new JLabel("Default Allele Frequency");
		this.AFDEF.setHorizontalAlignment(JLabel.RIGHT);
		this.ERDEF = new JLabel("Empirical Phred Scaled Error Rate");
		this.ERDEF.setToolTipText("Set -1 to disable empirical error rate and use all sites as is");
		this.STDEF = new JLabel("Starting ROH Probability");
		this.COLUMNS = new JLabel(
				" HOMREF                                        HET                                                HOMVAR");
		this.R1 = new JTextField("0.990666");
		this.R2 = new JTextField("0.0");
		this.R3 = new JTextField("0.009334");
		this.N1 = new JTextField("0.986219");
		this.N2 = new JTextField("0.007916");
		this.N3 = new JTextField("0.005865");
		this.AFD = new JTextField("0.4");
		this.ERD = new JTextField("30");
		this.ST = new JTextField("0.5");
		final JPanel hmmsetup = new JPanel();
		hmmsetup.setBounds(7 + OverSeer.INDENTCONST, 0, 775, 240);
		hmmsetup.setBorder(new TitledBorder("Custom HMM Emission Parameters"));
		hmmsetup.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		this.customHMMAlleleDistribution = new JRadioButton("Use Allele Distribution");
		this.customHMMAlleleDistribution.setSelected(true);
		this.customHMMAlleleFrequency = new JRadioButton("Use Allele Frequency");
		final ButtonGroup hmmemission = new ButtonGroup();
		hmmemission.add(this.customHMMAlleleDistribution);
		hmmemission.add(this.customHMMAlleleFrequency);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.01;
		hmmsetup.add(this.ERDEF, c);
		c.weightx = 0.5;
		c.gridx = 1;
		hmmsetup.add(this.ERD, c);
		this.forceER = new JCheckBox("Use same error rate for all sites");
		this.forceER.setSelected(false);
		c.gridx = 2;
		c.gridwidth = 2;
		hmmsetup.add(this.forceER, c);
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.01;
		hmmsetup.add(this.STDEF, c);
		c.gridx = 1;
		c.weightx = 0.3;
		hmmsetup.add(this.ST, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 4;
		c.weightx = 0.01;
		hmmsetup.add(this.customHMMAlleleDistribution, c);
		c.weightx = 1;
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 3;
		hmmsetup.add(this.COLUMNS, c);
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 4;
		hmmsetup.add(this.ROW1, c);
		c.gridx = 1;
		hmmsetup.add(this.R1, c);
		c.gridx = 2;
		hmmsetup.add(this.R2, c);
		c.gridx = 3;
		hmmsetup.add(this.R3, c);
		c.gridx = 0;
		c.gridy = 5;
		hmmsetup.add(this.ROW2, c);
		c.gridx = 1;
		hmmsetup.add(this.N1, c);
		c.gridx = 2;
		hmmsetup.add(this.N2, c);
		c.gridx = 3;
		hmmsetup.add(this.N3, c);
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 0.01;
		hmmsetup.add(this.customHMMAlleleFrequency, c);
		this.getAFFromTag = new JCheckBox("Get AF from INFO tags");
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 7;
		hmmsetup.add(this.getAFFromTag, c);
		this.INFOTags = new JComboBox<>();
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 7;
		c.weightx = 0.3;
		hmmsetup.add(this.INFOTags, c);
		c.gridx = 0;
		c.gridy = 8;
		c.weightx = 0.01;
		hmmsetup.add(this.AFDEF, c);
		c.weightx = 0.3;
		c.gridx = 1;
		hmmsetup.add(this.AFD, c);

		this.add(hmmsetup);

		final JPanel hmmsetupt = new JPanel(new GridBagLayout());
		hmmsetupt.setBorder(new TitledBorder("Custom HMM Transition Parameters"));
		hmmsetupt.setBounds(7 + OverSeer.INDENTCONST, 240, 775, 170);
		final GridBagConstraints c2 = new GridBagConstraints();

		this.useFixedTransitionParams = new JRadioButton("Use fixed transition parameters");
		this.useFixedTransitionParams.setSelected(true);
		this.useDistanceDecayFunction = new JRadioButton("Use distance decay function");
		final ButtonGroup transitionRadio = new ButtonGroup();
		transitionRadio.add(this.useFixedTransitionParams);
		transitionRadio.add(this.useDistanceDecayFunction);
		c2.fill = GridBagConstraints.HORIZONTAL;
		c2.gridx = 0;
		c2.gridy = 0;
		c2.gridwidth = 3;
		hmmsetupt.add(this.useFixedTransitionParams, c2);
		this.ROWT1 = new JLabel("ROH-NonROH");
		this.ROWT2 = new JLabel("NonROH-ROH");
		this.BASEFACT = new JLabel("Base Transition Probability");
		this.NORMFACT = new JLabel("Distance Normalization Factor");
		this.ROWT1.setHorizontalAlignment(JLabel.RIGHT);
		this.ROWT2.setHorizontalAlignment(JLabel.RIGHT);
		this.BASEFACT.setHorizontalAlignment(JLabel.RIGHT);
		this.NORMFACT.setHorizontalAlignment(JLabel.RIGHT);
		this.RT = new JTextField("0.000009");
		this.NT = new JTextField("0.000004");
		this.BF = new JTextField("0.1");
		this.NF = new JTextField("100000");
		c2.gridx = 0;
		c2.gridy = 1;
		c2.weightx = 0.01;
		c2.gridwidth = 1;
		hmmsetupt.add(this.ROWT1, c2);
		c2.weightx = 0.25;
		c2.gridx = 1;
		hmmsetupt.add(this.RT, c2);
		c2.weightx = 0.01;
		c2.gridx = 0;
		c2.gridy = 2;
		hmmsetupt.add(this.ROWT2, c2);
		c2.weightx = 0.25;
		c2.gridx = 1;
		hmmsetupt.add(this.NT, c2);
		c2.gridwidth = 3;
		c2.gridx = 0;
		c2.gridy = 3;
		hmmsetupt.add(this.useDistanceDecayFunction, c2);
		c2.gridwidth = 1;
		c2.gridx = 0;
		c2.gridy = 4;
		c2.weightx = 0.01;
		hmmsetupt.add(this.BASEFACT, c2);
		c2.weightx = 0.25;
		c2.gridx = 1;
		hmmsetupt.add(this.BF, c2);
		c2.weightx = 0.01;
		c2.gridx = 0;
		c2.gridy = 5;
		hmmsetupt.add(this.NORMFACT, c2);
		c2.weightx = 0.25;
		c2.gridx = 1;
		hmmsetupt.add(this.NF, c2);
		this.add(hmmsetupt);
		final JPanel miscopts = new JPanel(new GridBagLayout());
		miscopts.setBorder(new TitledBorder("Miscellaneous Options"));
		miscopts.setBounds(7 + OverSeer.INDENTCONST, 410, 775, 115);
		final GridBagConstraints c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		this.LOGDEF = new JLabel("Log level");
		c3.gridx = 0;
		c3.gridy = 0;
		c3.weightx = 0.01;
		miscopts.add(this.LOGDEF, c3);
		this.LOGLevel = new JComboBox<>(new String[] { "ERROR", "WARNING", "INFO", "DEBUG" });
		this.LOGLevel.addActionListener(arg0 -> OverSeer.setOption(GUIOptionStandards.LOGLEVEL,
				OptionPanel.this.LOGLevel.getSelectedIndex() + ""));
		c3.gridx = 1;
		c3.weightx = 0.3;
		miscopts.add(this.LOGLevel, c3);
		this.add(miscopts);

	}

	// Work on this part of the options so that HMM is set properly.
	
	public void setAdvancedErrorOptions()
	{
		if (this.forceER.isSelected()) {
			OverSeer.removeOption(GUIOptionStandards.USERDEFINEDGTERROR);
			OverSeer.setOption(GUIOptionStandards.USERDEFINEDGTERRORALL, OptionPanel.this.ERD.getText());
		} else {
			OverSeer.removeOption(GUIOptionStandards.USERDEFINEDGTERRORALL);
			OverSeer.setOption(GUIOptionStandards.USERDEFINEDGTERROR, OptionPanel.this.ERD.getText());
		}
	}
	
	public void setAdvancedOptions() {

		String custommodelstring = "START\t" + this.ST.getText() + "\t" + (1 - Double.parseDouble(this.ST.getText()))
				+ "\n";

		if (this.customHMMAlleleDistribution.isSelected()) {
			Model.hwmode = false;
			custommodelstring += "EMROH\t" + this.R1.getText() + "\t" + this.R2.getText() + "\t" + this.R3.getText()
					+ "\n";
			custommodelstring += "EMNORM\t" + this.N1.getText() + "\t" + this.N2.getText() + "\t" + this.N3.getText()
					+ "\n";

		} else if (this.customHMMAlleleFrequency.isSelected()) {
			Model.hwmode = true;
			if (this.getAFFromTag.isSelected()) {
				OverSeer.setOption(GUIOptionStandards.AFTAG, (String) this.INFOTags.getSelectedItem());
			}

			OverSeer.setOption(GUIOptionStandards.DEFAULTAF, this.AFD.getText()); // check if this function works
																					// properly..
		}

		if (this.useFixedTransitionParams.isSelected()) {
			Model.distmode = false;
			custommodelstring += "TRANSROH\t" + (1 - Double.parseDouble(this.RT.getText())) + "\t" + this.RT.getText()
					+ "\n";
			custommodelstring += "TRANSNORM\t" + this.NT.getText() + "\t" + (1 - Double.parseDouble(this.NT.getText()))
					+ "\n";
		} else if (this.useDistanceDecayFunction.isSelected()) {
			Model.distmode = true;
			custommodelstring += "DEFAULTPROB\t" + this.BF.getText() + "\nNORMFACT\t" + this.NF.getText() + "\n";

		}
		OverSeer.log(OptionPanel.this.getClass().getSimpleName(), "Custom model string: " + custommodelstring,
				OverSeer.DEBUG);
		Model.customModel = custommodelstring;
	}

	public void clearAllOptions() {
		Model.customModel = "";
		OverSeer.removeOption(GUIOptionStandards.AFTAG);
		OverSeer.removeOption(GUIOptionStandards.HMMMODELFILE);
		OverSeer.removeOption(GUIOptionStandards.DEFAULTAF);

	}

	public void setInfoTags(String[] tags) {
		for (final String tag : tags) {
			this.INFOTags.addItem(tag);
		}
	}
	
}
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

@SuppressWarnings("serial")
public class OptionPanel extends JPanel {

	JTextField R1, R2, R3, N1, N2, N3;
	JLabel COLUMNS, ROW1, ROW2;
	JCheckBox getAFFromTag;
	JComboBox<String> INFOTags;
	JTextField RT, NT, BF, NF;
	JLabel ROWT1, ROWT2, BASEFACT, NORMFACT;

	public OptionPanel() {
		this.setLayout(null);
		final JPanel hmmsetup = new JPanel();
		hmmsetup.setBounds(12, 0, 775, 160);
		hmmsetup.setBorder(new TitledBorder("Custom HMM Emission Parameters"));
		hmmsetup.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		final JRadioButton customHMMAlleleDistribution = new JRadioButton(
				"Use Custom Allele Distribution Probabilities");
		final JRadioButton customHMMAlleleFrequency = new JRadioButton("Use Custom Allele Frequency Parameters");
		final ButtonGroup hmmemission = new ButtonGroup();
		hmmemission.add(customHMMAlleleDistribution);
		hmmemission.add(customHMMAlleleFrequency);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		c.weightx = 0.5;
		hmmsetup.add(customHMMAlleleDistribution, c);
		this.ROW1 = new JLabel("ROH");
		this.ROW1.setHorizontalAlignment(JLabel.RIGHT);
		this.ROW2 = new JLabel("NonROH");
		this.ROW2.setHorizontalAlignment(JLabel.RIGHT);
		this.COLUMNS = new JLabel(
				" HOMREF                                                         HET                                                                  HOMVAR");
		this.R1 = new JTextField();
		this.R2 = new JTextField();
		this.R3 = new JTextField();
		this.N1 = new JTextField();
		this.N2 = new JTextField();
		this.N3 = new JTextField();

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 3;
		hmmsetup.add(this.COLUMNS, c);
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		hmmsetup.add(this.ROW1, c);
		c.gridx = 1;
		hmmsetup.add(this.R1, c);
		c.gridx = 2;
		hmmsetup.add(this.R2, c);
		c.gridx = 3;
		hmmsetup.add(this.R3, c);
		c.gridx = 0;
		c.gridy = 3;
		hmmsetup.add(this.ROW2, c);
		c.gridx = 1;
		hmmsetup.add(this.N1, c);
		c.gridx = 2;
		hmmsetup.add(this.N2, c);
		c.gridx = 3;
		hmmsetup.add(this.N3, c);
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 4;
		hmmsetup.add(customHMMAlleleFrequency, c);
		this.getAFFromTag = new JCheckBox("Get allele frequencies from INFO tags");
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 5;
		hmmsetup.add(this.getAFFromTag, c);
		this.INFOTags = new JComboBox<>();
		c.gridwidth = 1;
		c.gridx = 3;
		c.gridy = 5;
		hmmsetup.add(this.INFOTags, c);
		this.add(hmmsetup);

		final JPanel hmmsetupt = new JPanel(new GridBagLayout());
		hmmsetupt.setBorder(new TitledBorder("Custom HMM Transition Parameters"));
		hmmsetupt.setBounds(12, 160, 775, 170);
		final GridBagConstraints c2 = new GridBagConstraints();
		this.add(hmmsetupt);
		final JRadioButton useFixedTransitionParams = new JRadioButton("Use fixed transition parameters");
		final JRadioButton useDistanceDecayFunction = new JRadioButton("Use distance decay function");
		final ButtonGroup transitionRadio = new ButtonGroup();
		transitionRadio.add(useFixedTransitionParams);
		transitionRadio.add(useDistanceDecayFunction);
		c2.fill = GridBagConstraints.HORIZONTAL;
		c2.gridx = 0;
		c2.gridy = 0;
		c2.gridwidth = 3;
		hmmsetupt.add(useFixedTransitionParams, c2);
		this.ROWT1 = new JLabel("ROH-NonROH");
		this.ROWT2 = new JLabel("NonROH-ROH");
		this.BASEFACT = new JLabel("Base Transition Probability");
		this.NORMFACT = new JLabel("Distance Normalization Factor");
		this.ROWT1.setHorizontalAlignment(JLabel.RIGHT);
		this.ROWT2.setHorizontalAlignment(JLabel.RIGHT);
		this.BASEFACT.setHorizontalAlignment(JLabel.RIGHT);
		this.NORMFACT.setHorizontalAlignment(JLabel.RIGHT);
		this.RT = new JTextField();
		this.NT = new JTextField();
		this.BF = new JTextField();
		this.NF = new JTextField();
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
		hmmsetupt.add(useDistanceDecayFunction, c2);
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
	}

}

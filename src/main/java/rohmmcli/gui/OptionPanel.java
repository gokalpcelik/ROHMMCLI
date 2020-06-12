/*
 * Author : Gokalp Celik
 * Year : 2020
 */
package rohmmcli.gui;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class OptionPanel extends JPanel {

	public OptionPanel() {
		this.setLayout(null);
		final JPanel hmmsetup = new JPanel();
		hmmsetup.setBounds(12, 0, 775, 150);
		hmmsetup.setBorder(new TitledBorder("Custom HMM Generator"));
		hmmsetup.setLayout(new GridBagLayout());
		this.add(hmmsetup);

	}

}

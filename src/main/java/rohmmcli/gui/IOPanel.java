package rohmmcli.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class IOPanel extends JPanel {
	protected JPanel panel_1;
	protected JLabel vcfLabel;
	protected JPanel panel;
	protected JTextField vcfpathfield;
	protected JButton vcfselectbutton;
	protected JScrollPane scrollPane;
	protected JList chrlist;
	protected JScrollPane scrollPane_1;
	protected JList samplelist;
	protected JFrame parentFrame;

	/**
	 * Create the panel.
	 */
	public IOPanel() {
		setLayout(null);
		vcfLabel = new JLabel("Choose VCF File");
		vcfLabel.setBounds(12, 9, 131, 24);
		add(vcfLabel);
		panel = new JPanel();
		vcfpathfield = new JTextField();
		vcfpathfield.setBounds(147, 10, 400, 24);
		add(vcfpathfield);
		vcfselectbutton = new JButton("Select VCF");
		vcfselectbutton.setBounds(559, 10, 100, 24);
		vcfselectbutton.addActionListener(new VCFSelectButtonListener());
		add(vcfselectbutton);
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Chromosomes", TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setBounds(12, 60, 120, 406);
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		panel.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		chrlist = new JList();
		scrollPane.setViewportView(chrlist);

		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Samples", TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_1.setBounds(144, 60, 163, 406);
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1);
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		samplelist = new JList();
		scrollPane_1.setViewportView(samplelist);

	}

	protected JPanel getSelf() {
		return this;
	}

	public class VCFSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			parentFrame = (JFrame) SwingUtilities.getWindowAncestor(getSelf());
			try {
				File file = FileSelectorUtil.openFile(parentFrame, "Open VCF File");
				vcfpathfield.setText(file.getAbsolutePath());
			} catch (Exception exp) {
				// TODO: handle exception
			}

		}

	}
}

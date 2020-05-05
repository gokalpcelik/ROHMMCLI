package rohmmcli.gui;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.TitledBorder;

public class IOPanel extends JPanel {
	private JPanel panel_1;

	/**
	 * Create the panel.
	 */
	public IOPanel() {
		setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Chromosomes", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setBounds(12, 46, 120, 406);
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		JList chrlist = new JList();
		scrollPane.setViewportView(chrlist);
		
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Samples", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_1.setBounds(144, 46, 163, 406);
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1);
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		JList samplelist = new JList();
		scrollPane_1.setViewportView(samplelist);


	}
}

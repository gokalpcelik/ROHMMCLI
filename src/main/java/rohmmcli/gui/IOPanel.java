package rohmmcli.gui;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class IOPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public IOPanel() {
		setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(12, 33, 100, 408);
		add(scrollPane);
		
		JList chrlist = new JList();
		scrollPane.setViewportView(chrlist);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(124, 33, 100, 408);
		add(scrollPane_1);
		
		JList samplelist = new JList();
		scrollPane_1.setViewportView(samplelist);


	}
}

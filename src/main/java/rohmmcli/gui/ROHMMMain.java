package rohmmcli.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import rohmmcli.rohmm.Utility;

public class ROHMMMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Launch the application.
	 */
	public static void RunGUI() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ROHMMMain gui = new ROHMMMain();
				gui.setVisible(true);
			}
			
		});
		
	}

	/**
	 * Create the frame.
	 */
	public ROHMMMain() {
		setMinimumSize(new Dimension(800, 600));
		setPreferredSize(new Dimension(800, 600));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Utility.ENDTIMER();
			}
		});
		setTitle("ROHMM - Flexible HMM Homozygosity Finder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		IOPanel iopane = new IOPanel();
		OptionPanel optpane = new OptionPanel();
		tabbedPane.add(iopane);
		tabbedPane.add(optpane);
		tabbedPane.setTitleAt(0, "Input/Output");
		tabbedPane.setTitleAt(1, "Options");
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		pack();
		
		
		
	}

}

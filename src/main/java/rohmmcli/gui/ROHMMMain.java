package rohmmcli.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import rohmmcli.rohmm.OverSeer;

public class ROHMMMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void RunGUI() {
		EventQueue.invokeLater(() -> {
			final ROHMMMain gui = new ROHMMMain();
			gui.setVisible(true);
		});

	}

	/**
	 * Create the frame.
	 */
	public ROHMMMain() {
		this.setMinimumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(800, 600));
		this.setName("Deneme");
		this.setTitle("ROHMM - Flexible HMM Homozygosity Finder");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.addWindowListener(new MainWindowAdapter());
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		final IOPanel iopane = new IOPanel();
		final OptionPanel optpane = new OptionPanel();
		tabbedPane.add(iopane);
		tabbedPane.add(optpane);
		tabbedPane.setTitleAt(0, "Input/Output");
		tabbedPane.setTitleAt(1, "Advanced Options");
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		this.pack();

	}

	public class MainWindowAdapter extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			OverSeer.closeAllReaders();
			OverSeer.endTimer();
		}
	}

}

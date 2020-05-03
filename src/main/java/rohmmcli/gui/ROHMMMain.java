package rohmmcli.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import rohmmcli.rohmm.Utility;
import java.awt.Dimension;

public class ROHMMMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

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
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

}

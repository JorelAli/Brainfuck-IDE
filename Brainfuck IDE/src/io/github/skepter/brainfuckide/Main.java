package io.github.skepter.brainfuckide;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * Brainfuck IDE designed to run and debug Brainfuck code Extra credit to Fabian
 * M for BrainfuckEngine.java https://github.com/fabianm
 * 
 * @author Skepter
 *
 */
public class Main {

	private JFrame mainFrame;
	private JTextPane workspace;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setTitle("Brainfuck IDE");
		mainFrame.setBounds(100, 100, 750, 550);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		mainPanel.add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(400);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		splitPane.setLeftComponent(scrollPane);
		workspace = new JTextPane();
		scrollPane.setViewportView(workspace);


		JPanel statusBar = new JPanel();
		mainFrame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		statusBar.setLayout(new BorderLayout(0, 0));

		JLabel label = new JLabel("New label");
		statusBar.add(label, BorderLayout.NORTH);

	}

}

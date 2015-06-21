package io.github.skepter.brainfuckide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

/**
 * Brainfuck IDE designed to run and debug Brainfuck code Extra credit to Fabian
 * M for BrainfuckEngine.java https://github.com/fabianm
 * 
 * @author Skepter
 *
 */
public class Main {

	private JFrame mainFrame;
	private JTextArea workspace;
	private JTextArea output;
	private JTextField cellCount;
	private static JLabel statusLabel;
	private final static int DEFAULT_MEMORY = 384;

	private static int memory = DEFAULT_MEMORY;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
		}

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

		/* Basic frame setup */

		mainFrame = new JFrame();
		mainFrame.setTitle("Brainfuck IDE");
		mainFrame.setBounds(100, 100, 1100, 700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/* Status bar (This comes first because it has info that gets updates) */

		JPanel statusBar = new JPanel();
		mainFrame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		statusBar.setLayout(new BorderLayout(0, 0));

		statusLabel = new JLabel("Memory: 384");
		statusBar.add(statusLabel, BorderLayout.NORTH);

		/* Main viewport */

		JPanel mainPanel = new JPanel();
		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		output = new JTextArea();
		output.setEditable(false);

		/* Split pane */
		{

			JSplitPane splitPane = new JSplitPane();
			mainPanel.add(splitPane, BorderLayout.CENTER);
			splitPane.setDividerLocation(700);

			JScrollPane leftScrollPane = new JScrollPane();
			leftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			splitPane.setLeftComponent(leftScrollPane);
			workspace = new JTextArea();
			workspace.setLineWrap(true);
			workspace.setText("Hello, World!\n++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.");
			leftScrollPane.setViewportView(workspace);

			{

				/*
				 * Second split pane which controls memory and stuff As well as
				 * reset buttons, and memory output
				 */

				JSplitPane secondSplitPane = new JSplitPane();
				secondSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
				secondSplitPane.setDividerLocation(100);
				splitPane.setRightComponent(secondSplitPane);

				JPanel devPanel = new JPanel();
				devPanel.setBackground(new Color(135, 206, 250));
				devPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Controls", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
				secondSplitPane.setLeftComponent(devPanel);

				JButton runButton = new JButton("Run!");
				runButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						new BrainfuckIntegrator(new BrainfuckEngine(memory), workspace.getText(), output);
					}
				});

				JButton resetButton = new JButton("Reset");
				resetButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						output.setText("");
					}
				});

				/* Set memory section */

				JLabel setMemoryLabel = new JLabel("Set memory (Cells)");
				cellCount = new JTextField();
				cellCount.addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent arg0) {
					}

					@Override
					public void keyReleased(KeyEvent arg0) {
						try {
							Integer.parseInt(cellCount.getText());
							memory = Integer.parseInt(cellCount.getText());
						} catch (Exception e) {
							memory = DEFAULT_MEMORY;
						}
						statusLabel.setText("Memory: " + memory);
					}

					@Override
					public void keyTyped(KeyEvent arg0) {
					}

				});
				cellCount.setColumns(10);

				/* Group layout for controls */

				GroupLayout gl_devPanel = new GroupLayout(devPanel);
				gl_devPanel.setHorizontalGroup(gl_devPanel.createParallelGroup(Alignment.LEADING).addGroup(
						gl_devPanel
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										gl_devPanel.createParallelGroup(Alignment.TRAILING, false).addComponent(runButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(resetButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGap(47).addComponent(setMemoryLabel).addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(cellCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap()));
				gl_devPanel.setVerticalGroup(gl_devPanel.createParallelGroup(Alignment.LEADING).addGroup(
						gl_devPanel
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										gl_devPanel.createParallelGroup(Alignment.BASELINE).addComponent(runButton).addComponent(cellCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(setMemoryLabel)).addPreferredGap(ComponentPlacement.RELATED).addComponent(resetButton).addContainerGap(13, Short.MAX_VALUE)));
				devPanel.setLayout(gl_devPanel);
				secondSplitPane.setRightComponent(output);

			}
		}

	}
	
	public static void setStatusLabel(int pointer) {
		statusLabel.setText("Memory: " + memory + ", Pointer: " + pointer);
	}
}

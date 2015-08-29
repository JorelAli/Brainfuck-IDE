package io.github.skepter.brainfuckide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

/**
 * Brainfuck IDE designed to run and debug Brainfuck code Extra credit to
 * FabianM for BrainfuckEngine.java https://github.com/fabianm
 * 
 * @author Skepter
 *
 */
public class Main {

	/* Components */
	private JFrame mainFrame;
	public static JTextArea workspace;
	private static JTextArea memoryOutput;
	private JTextField cellCount;
	private static JLabel statusLabel;
	public static JTextArea output;

	/* Input components */
	private JTextField inputField;
	private JTextFieldInputStream is;
	private JTextField charInput;

	private final static int DEFAULT_MEMORY = 384;
	private static int memory = DEFAULT_MEMORY;

	private boolean wrapping = true;
	private Thread runningThread;
	private static File file;

	private byte bits = 8;

	/*
	 * Debugging stuff Move this to a new class to handle all of the debugging
	 * Set the class to null in the step. Invoke the class in debug, and keep
	 * the value not null, until the debugger ends or is forced.
	 */
	private Debugger debugger;

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
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/io/github/skepter/brainfuckide/icons/appIcon.png")));
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

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		mainPanel.add(toolBar, BorderLayout.NORTH);

		JButton runButton = new JButton("Run!");
		toolBar.add(runButton);
		runButton.setIcon(new ImageIcon(Main.class.getResource("/io/github/skepter/brainfuckide/icons/bullet_go.png")));

		JButton stopButton = new JButton("Stop");
		toolBar.add(stopButton);
		stopButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				setStatusLabel(-1, false);
				runningThread.stop();
			}
		});
		stopButton.setIcon(new ImageIcon(Main.class.getResource("/io/github/skepter/brainfuckide/icons/stop.png")));

		JButton resetButton = new JButton("Reset");
		toolBar.add(resetButton);
		resetButton.setIcon(new ImageIcon(Main.class.getResource("/io/github/skepter/brainfuckide/icons/arrow_refresh.png")));

		JButton debugButton = new JButton("Debug");
		debugButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				debugger = new Debugger(workspace, new BrainfuckEngine(memory, is, wrapping, bits), memoryOutput);
			}
		});
		debugButton.setIcon(new ImageIcon(Main.class.getResource("/io/github/skepter/brainfuckide/icons/bug_go.png")));
		toolBar.add(debugButton);

		JButton stepButton = new JButton("Step");
		stepButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (debugger != null) {
					/* Returns true if it's finished (set debugger to null), otherwise, continue */
					debugger.step();
				}
			}
		});
		toolBar.add(stepButton);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStatusLabel(-1, false);
				memoryOutput.setText("");
				output.setText("");
			}
		});
		runButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				if (runningThread != null) {
					runningThread.stop();
				}
				runningThread = new Thread() {
					@Override
					public void run() {
						new BrainfuckIntegrator(new BrainfuckEngine(memory, is, wrapping, bits), workspace.getText(), memoryOutput);
					}
				};
				runningThread.start();
			}
		});

		memoryOutput = new JTextArea("");
		memoryOutput.setEditable(false);

		/* Split panes */
		{
			{
				JSplitPane splitPane = new JSplitPane();
				mainPanel.add(splitPane, BorderLayout.CENTER);
				splitPane.setDividerLocation(700);

				/* Left split pane - workspace + output */
				{
					JSplitPane workspaceOutputPane = new JSplitPane();
					JScrollPane leftScrollPane = new JScrollPane();
					leftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					splitPane.setLeftComponent(workspaceOutputPane);
					workspace = new JTextArea();
					final JTextArea lines = new JTextArea("1");
					lines.setBackground(Color.LIGHT_GRAY);
					lines.setEditable(false);
					workspace.getDocument().addDocumentListener(new DocumentListener() {
						public String getText() {
							int caretPosition = workspace.getDocument().getLength();
							Element root = workspace.getDocument().getDefaultRootElement();
							String text = "1" + System.getProperty("line.separator");
							for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
								text += i + System.getProperty("line.separator");
							}
							return text;
						}

						@Override
						public void changedUpdate(DocumentEvent de) {
							lines.setText(getText());
						}

						@Override
						public void insertUpdate(DocumentEvent de) {
							lines.setText(getText());
						}

						@Override
						public void removeUpdate(DocumentEvent de) {
							lines.setText(getText());
						}

					});
					workspace.setLineWrap(true);
					workspace.setText("Hello World!\n++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.");
					leftScrollPane.setViewportView(workspace);
					leftScrollPane.setRowHeaderView(lines);

					workspaceOutputPane.setLeftComponent(leftScrollPane);
					workspaceOutputPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

					workspaceOutputPane.setDividerLocation(400);
					JScrollPane outputPane = new JScrollPane();
					outputPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					output = new JTextArea();
					output.setEditable(false);
					outputPane.setViewportView(output);

					workspaceOutputPane.setRightComponent(outputPane);

				}

				/* Right split pane - controls + memory output */
				{

					JSplitPane secondSplitPane = new JSplitPane();
					secondSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
					secondSplitPane.setDividerLocation(230);
					splitPane.setRightComponent(secondSplitPane);

					JPanel devPanel = new JPanel();
					devPanel.setBackground(new Color(135, 206, 250));
					devPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Settings", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
					secondSplitPane.setLeftComponent(devPanel);

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

					inputField = new JTextField();
					inputField.setColumns(10);
					is = new JTextFieldInputStream(inputField);
					inputField.addActionListener(is);

					JLabel inputLabel = new JLabel("Input:");

					JButton formatButton = new JButton("Format");
					formatButton.setIcon(new ImageIcon(Main.class.getResource("/io/github/skepter/brainfuckide/icons/page_white_paintbrush.png")));
					formatButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							new BrainfuckFormatter().format();
						}
					});

					/* Character code */

					JLabel characterLabel = new JLabel("Enter a character:");
					final JLabel characterCodeLabel = new JLabel("Character code:");

					charInput = new JTextField();
					charInput.addKeyListener(new KeyListener() {

						@Override
						public void keyPressed(KeyEvent arg0) {
						}

						@Override
						public void keyReleased(KeyEvent arg0) {
							try {
								String out = "";
								for (byte b : charInput.getText().getBytes()) {
									out = out + b + ", ";
								}
								out = out.substring(0, out.length() - 2);
								characterCodeLabel.setText("Character code: " + out);
							} catch (Exception e) {
								characterCodeLabel.setText("Character code:");
							}
						}

						@Override
						public void keyTyped(KeyEvent arg0) {
						}

					});
					charInput.setColumns(10);

					JButton unformatButton = new JButton("Unformat");
					unformatButton.setIcon(new ImageIcon(Main.class.getResource("/io/github/skepter/brainfuckide/icons/page_white.png")));
					unformatButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							new BrainfuckFormatter().unformat();
						}
					});

					final JToggleButton wrappingButton = new JToggleButton("Wrapping mode enabled");
					wrappingButton.setSelected(true);
					wrappingButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (wrappingButton.isSelected()) {
								// turn it on
								wrapping = true;
								wrappingButton.setText("Wrapping mode enabled");
							} else {
								wrapping = false;
								// turn it off
								wrappingButton.setText("Non-Wrapping mode enabled");
							}
						}
					});

					/* Cell size (bits) */

					JLabel cellSizeLabel1 = new JLabel("Set cell size (Bits)");

					JRadioButton cellSize8 = new JRadioButton("8 Bits");
					cellSize8.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							bits = 8;
						}
					});
					cellSize8.setBackground(new Color(135, 206, 250));

					JRadioButton cellSize16 = new JRadioButton("16 Bits");
					cellSize16.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							bits = 16;
						}
					});
					cellSize16.setBackground(new Color(135, 206, 250));

					JRadioButton cellSize32 = new JRadioButton("32 Bits");
					cellSize32.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							bits = 32;
						}
					});
					cellSize32.setBackground(new Color(135, 206, 250));

					ButtonGroup group = new ButtonGroup();
					group.add(cellSize8);
					group.add(cellSize16);
					group.add(cellSize32);

					group.setSelected(cellSize8.getModel(), true);

					/* Group layout */

					GroupLayout gl_devPanel = new GroupLayout(devPanel);
					gl_devPanel.setHorizontalGroup(gl_devPanel.createParallelGroup(Alignment.TRAILING)
							.addGroup(
									gl_devPanel
											.createSequentialGroup()
											.addContainerGap()
											.addGroup(
													gl_devPanel
															.createParallelGroup(Alignment.LEADING)
															.addGroup(
																	gl_devPanel
																			.createSequentialGroup()
																			.addGroup(
																					gl_devPanel
																							.createParallelGroup(Alignment.LEADING)
																							.addGroup(gl_devPanel.createSequentialGroup().addGap(63).addComponent(cellSize16).addGap(18).addComponent(cellSize32))
																							.addGroup(
																									gl_devPanel
																											.createSequentialGroup()
																											.addGroup(
																													gl_devPanel.createParallelGroup(Alignment.TRAILING, false)
																															.addComponent(formatButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																															.addComponent(setMemoryLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
																											.addGroup(
																													gl_devPanel
																															.createParallelGroup(Alignment.TRAILING)
																															.addGroup(
																																	gl_devPanel.createSequentialGroup().addGap(18)
																																			.addComponent(cellCount, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE))
																															.addGroup(
																																	gl_devPanel.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED).addComponent(unformatButton)
																																			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																																			.addComponent(wrappingButton))))).addGap(11))
															.addGroup(
																	gl_devPanel
																			.createSequentialGroup()
																			.addGroup(
																					gl_devPanel
																							.createParallelGroup(Alignment.LEADING, false)
																							.addComponent(cellSizeLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																							.addGroup(
																									gl_devPanel.createSequentialGroup().addComponent(inputLabel, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE).addGap(18)
																											.addComponent(inputField, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE))).addContainerGap(11, Short.MAX_VALUE))
															.addGroup(gl_devPanel.createSequentialGroup().addComponent(cellSize8).addContainerGap(303, Short.MAX_VALUE))
															.addGroup(
																	gl_devPanel.createSequentialGroup().addComponent(characterLabel).addPreferredGap(ComponentPlacement.UNRELATED)
																			.addComponent(charInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(18).addComponent(characterCodeLabel)
																			.addContainerGap(74, Short.MAX_VALUE)))));
					gl_devPanel.setVerticalGroup(gl_devPanel.createParallelGroup(Alignment.LEADING).addGroup(
							gl_devPanel
									.createSequentialGroup()
									.addGap(42)
									.addGroup(gl_devPanel.createParallelGroup(Alignment.BASELINE).addComponent(formatButton).addComponent(wrappingButton).addComponent(unformatButton))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_devPanel.createParallelGroup(Alignment.BASELINE).addComponent(setMemoryLabel).addComponent(cellCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_devPanel.createParallelGroup(Alignment.BASELINE).addComponent(inputField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(inputLabel))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(cellSizeLabel1)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_devPanel.createParallelGroup(Alignment.BASELINE).addComponent(cellSize8).addComponent(cellSize16).addComponent(cellSize32))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(
											gl_devPanel.createParallelGroup(Alignment.BASELINE).addComponent(characterLabel).addComponent(charInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(characterCodeLabel)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
					devPanel.setLayout(gl_devPanel);

					/* Set output */
					JScrollPane memoryOutputScrollPane = new JScrollPane();
					memoryOutputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					memoryOutputScrollPane.setViewportView(memoryOutput);

					secondSplitPane.setRightComponent(memoryOutputScrollPane);

				}
			}
		}

		/* Menu bar */

		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem newItem = new JMenuItem("New file");
		newItem.setIcon(new ImageIcon(Main.class.getResource("/io/github/skepter/brainfuckide/icons/add.png")));
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Main.file != null) {
					String text = null;

					try {
						FileReader fr = new FileReader(file);
						BufferedReader br = new BufferedReader(fr);
						StringBuilder out = new StringBuilder();
						String s;
						while ((s = br.readLine()) != null) {
							out.append(s).append("\n");
						}
						text = out.toString();
						br.close();
						fr.close();
					} catch (Exception e1) {
					}

					if (!workspace.getText().equals(text)) {
						int option = JOptionPane.showConfirmDialog(mainFrame, "Your file is not saved! Do you want to continue?");
						switch (option) {
							case JOptionPane.CANCEL_OPTION:
							case JOptionPane.NO_OPTION:
								return;
							case JOptionPane.YES_OPTION:
								break;
						}
					}
				}

				Main.file = null;
				workspace.setText("");
			}
		});
		fileMenu.add(newItem);

		JMenuItem openItem = new JMenuItem("Open file");
		openItem.setIcon(new ImageIcon(Main.class.getResource("/io/github/skepter/brainfuckide/icons/folder_page_white.png")));
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("Brainfuck file", "bf");
				fileChooser.addChoosableFileFilter(filter);
				int option = fileChooser.showOpenDialog(mainFrame);
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					Main.file = file;
					try {
						FileReader fr = new FileReader(file);
						BufferedReader br = new BufferedReader(fr);
						StringBuilder out = new StringBuilder();
						String s;
						while ((s = br.readLine()) != null) {
							out.append(s).append("\n");
						}
						workspace.setText(out.toString());
						br.close();
						fr.close();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(mainFrame, "There was an error whilst reading the file!");
					}
				}
			}
		});
		fileMenu.add(openItem);

		JMenuItem saveItem = new JMenuItem("Save file");
		saveItem.setIcon(new ImageIcon(Main.class.getResource("/io/github/skepter/brainfuckide/icons/disk.png")));
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Main.file != null) {
					try {
						FileWriter fw = new FileWriter(Main.file + ".bf");
						BufferedWriter bw = new BufferedWriter(fw);
						for (int i = 0; i < workspace.getText().split("\n").length; i++) {
							bw.write(workspace.getText().split("\n")[i]);
							bw.newLine();
						}
						bw.close();
						fw.close();
						JOptionPane.showMessageDialog(mainFrame, Main.file.getName() + ".bf saved successfully");
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(mainFrame, "There was an error whilst saving the file!");
					}
				} else {
					JFileChooser fileChooser = new JFileChooser();
					FileFilter filter = new FileNameExtensionFilter("Brainfuck file", "bf");
					fileChooser.addChoosableFileFilter(filter);
					int option = fileChooser.showSaveDialog(mainFrame);
					if (option == JFileChooser.APPROVE_OPTION) {
						try {
							Main.file = fileChooser.getSelectedFile();
							FileWriter fw = new FileWriter(fileChooser.getSelectedFile() + ".bf");
							BufferedWriter bw = new BufferedWriter(fw);
							for (int i = 0; i < workspace.getText().split("\n").length; i++) {
								bw.write(workspace.getText().split("\n")[i]);
								bw.newLine();
							}
							bw.close();
							fw.close();
							JOptionPane.showMessageDialog(mainFrame, fileChooser.getSelectedFile().getName() + ".bf saved successfully");
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(mainFrame, "There was an error whilst saving the file!");
						}
					}
				}
			}
		});
		fileMenu.add(saveItem);

		JMenu convertersMenu = new JMenu("Convert");
		menuBar.add(convertersMenu);

		JMenuItem BrainfuckToOok = new JMenuItem("Brainfuck to Ook!");
		BrainfuckToOok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new BrainfuckFormatter().brainfuckToOok();
			}
		});
		convertersMenu.add(BrainfuckToOok);

		JMenuItem OokToBrainfuck = new JMenuItem("Ook! to Brainfuck");
		OokToBrainfuck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new BrainfuckFormatter().ookToBrainfuck();
			}
		});
		convertersMenu.add(OokToBrainfuck);

		JSeparator separator = new JSeparator();
		convertersMenu.add(separator);

		JMenuItem BrainfuckToTroll = new JMenuItem("Brainfuck to TrollScript");
		BrainfuckToTroll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new BrainfuckFormatter().brainfuckToTrollscript();
			}
		});
		convertersMenu.add(BrainfuckToTroll);

		JMenuItem TrollToBrainfuck = new JMenuItem("TrollScript to Brainfuck");
		convertersMenu.add(TrollToBrainfuck);

		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);

		JMenuItem helpItem = new JMenuItem("How to use");
		helpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				HelpMenu frame = new HelpMenu();
				frame.setVisible(true);
			}
		});
		helpMenu.add(helpItem);

	}

	public static void setStatusLabel(int pointer, boolean input) {
		if (pointer == -1) {
			if (input) {
				statusLabel.setText("Memory: " + memory + ", " + "Awaiting an input......");
			} else {
				statusLabel.setText("Memory: " + memory);
			}
		} else {
			if (input) {
				statusLabel.setText("Memory: " + memory + ", Pointer: " + pointer + ", Awaiting an input......");
			} else {
				statusLabel.setText("Memory: " + memory + ", Pointer: " + pointer);
			}
		}

	}

	public static void highlight(int pointer, int bits) {
		int index1 = 0;
		int index2 = 0;
		int bitSize = 0;
		switch (bits) {
			case 8:
				bitSize = 4;
				break;
			case 16:
				bitSize = 6;
				break;
			case 32:
				bitSize = 11;
				break;
		}

		index1 = pointer * bitSize + (int) (pointer / 12);
		index2 = (pointer * bitSize) + bitSize - 1 + (int) (pointer / 12);
		Highlighter highlighter = memoryOutput.getHighlighter();
		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);
		try {
			highlighter.addHighlight(index1, index2, painter);
		} catch (BadLocationException e2) {
			e2.printStackTrace();
		}
	}
}

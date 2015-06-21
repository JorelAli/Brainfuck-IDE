package io.github.skepter.brainfuckide;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

/**
 * Brainfuck IDE designed to run and debug Brainfuck code Extra credit to Fabian
 * M for BrainfuckEngine.java https://github.com/fabianm
 * 
 * @author Skepter
 *
 */
public class Main {

	private JFrame frame;
	private BrainfuckEngine engine;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
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
		engine = new BrainfuckEngine(500);
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			engine.interpretWithoutReset("++[>+<------]>+.");
			System.out.println("");
			// engine.interpretWithoutReset("-");
			// engine.interpretWithoutReset("++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.");
		} catch (Exception e) {
		}

		debugInfo();

	}

	private void debugInfo() {
		System.out.println("Pointer is at: " + engine.dataPointer);
		
		/* Formats it into a nice grid */
		StringBuilder builder = new StringBuilder();
		for (short s : engine.data) {
			builder.append(String.format("%03d", s)).append(" ");
		}
		for (String part : getParts(builder.toString(), 48)) {
			System.out.println(part);
		}
	}

	private List<String> getParts(String string, int partitionSize) {
		List<String> parts = new ArrayList<String>();
		int len = string.length();
		for (int i = 0; i < len; i += partitionSize) {
			parts.add(string.substring(i, Math.min(len, i + partitionSize)));
		}
		return parts;
	}

}

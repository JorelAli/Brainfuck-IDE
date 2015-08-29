package io.github.skepter.brainfuckide;

import io.github.skepter.brainfuckide.BrainfuckEngine.Token;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class Debugger {

	/* Engine */
	protected long[] data;
	protected int dataPointer = 0;
	protected int charPointer = 0;
	protected BufferedReader fileReader;
	protected InputStreamReader consoleReader;
	protected OutputStream outWriter;
	protected int lineCount = 0;
	protected int columnCount = 0;
	private boolean wrapping = true;
	public byte bits = 8;

	/* Debugger */

	private int index;
	private int maxIndex;
	private JTextArea workspace;
	private String tempWorkspace;

	public Debugger(JTextArea workspace, BrainfuckEngine engine, JTextArea memoryOutput) {
		this.workspace = workspace;

		/* Loads data from engine */
		data = engine.data;
		consoleReader = engine.consoleReader;
		bits = engine.bits;

		/*
		 * Temporarily stores all of the data Strips comments and formats code
		 */
		tempWorkspace = workspace.getText();
		workspace.setText(tempWorkspace.replaceAll("[^\\.\\[\\]\\+\\-\\,\\>\\<]", ""));
		maxIndex = workspace.getText().length();
		new BrainfuckFormatter().format();
		index = 0;

		Highlighter highlighter = workspace.getHighlighter();
		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);
		try {
			highlighter.addHighlight(index, index + 1, painter);
		} catch (BadLocationException e2) {
			e2.printStackTrace();
		}
	}

	public boolean step() {
		index++;
		if (index == maxIndex) {
			System.out.println("End of max index.");
			return true;
		}

		/* Skips whitespace */
		while (workspace.getText().charAt(index) == ' ' || workspace.getText().charAt(index) == '\n') {
			index++;
		}

		try {
			interpret(workspace.getText().charAt(index), workspace.getText().toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* Highlights the next character */
		Highlighter highlighter = workspace.getHighlighter();
		highlighter.removeAllHighlights();
		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);
		try {
			highlighter.addHighlight(index, index + 1, painter);
			return false;
		} catch (BadLocationException e2) {
			e2.printStackTrace();
		}
		return false;
	}

	/* Interpreter */
	protected void interpret(char c, char[] chars) throws Exception {
		switch (c) {
			case Token.NEXT:
				// Increment the data pointer (to point to the next cell to the
				// right).
				if ((dataPointer + 1) > data.length) {
					throw new Exception("Error on line " + lineCount + ", column " + columnCount + ":" + "data pointer (" + dataPointer + ") on postion " + charPointer + "" + " out of range.");
				}
				dataPointer++;
				break;
			case Token.PREVIOUS:
				// Decrement the data pointer (to point to the next cell to the
				// left).
				if ((dataPointer - 1) < 0) {
					throw new Exception("Error on line " + lineCount + ", column " + columnCount + ":" + "data pointer (" + dataPointer + ") on postion " + charPointer + " " + "negative.");
				}
				dataPointer--;
				break;
			case Token.PLUS:
				// Increment (increase by one) the short at the data pointer.
				/*
				 * if ((((int) data[dataPointer]) + 1) > Integer.MAX_VALUE) {
				 * throw new Exception("Error on line " + lineCount +
				 * ", column " + columnCount + ":" +
				 * "short value at data pointer (" + dataPointer + ") " +
				 * " on postion " + charPointer +
				 * " higher than short max value."); }
				 */

				if (data[dataPointer] == (long) Math.pow(2, bits) - 1) {
					if (wrapping) {
						data[dataPointer] = 0;
					} else {
						data[dataPointer] = (long) Math.pow(2, bits) - 1;
					}

				} else {
					data[dataPointer]++;
				}
				break;
			case Token.MINUS:
				// Decrement (decrease by one) the short at the data pointer.
				/*
				 * if ((data[dataPointer] - 1) < 0) { throw new
				 * Exception("Error on line " + lineCount + ", column " +
				 * columnCount + ":" + "at data pointer " + dataPointer +
				 * " on postion " + charPointer +
				 * ": Value can not be lower than zero."); }
				 */
				if (data[dataPointer] == 0) {
					if (wrapping) {
						data[dataPointer] = (long) Math.pow(2, bits) - 1;
					} else {
						data[dataPointer] = 0;
					}

				} else {
					data[dataPointer]--;
				}
				break;
			case Token.OUTPUT:
				// Output the short at the current index in a character.
				Main.output.setText(Main.output.getText() + (char) data[dataPointer]);
				break;
			case Token.INPUT:
				// Accept one short of input, storing its value in the short at
				// the
				// data pointer.
				Main.setStatusLabel(dataPointer, true);
				// int inputValue;
				// while ((inputValue = consoleReader.read()) != -1) {
				// data[dataPointer] = (short) inputValue;
				// }
				data[dataPointer] = (byte) consoleReader.read();
				break;
			/* Sort this out */
			case Token.BRACKET_LEFT:
				if (data[dataPointer] == 0) {
					int i = 1;
					while (i > 0) {
						char c2 = chars[++charPointer];
						if (c2 == Token.BRACKET_LEFT)
							i++;
						else if (c2 == Token.BRACKET_RIGHT)
							i--;
					}
				}
				break;
			case Token.BRACKET_RIGHT:
				int i = 1;
				while (i > 0) {
					char c2 = chars[--charPointer];
					if (c2 == Token.BRACKET_LEFT)
						i--;
					else if (c2 == Token.BRACKET_RIGHT)
						i++;
				}
				charPointer--;
				break;
		}
		columnCount++;
	}

}

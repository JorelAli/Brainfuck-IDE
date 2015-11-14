package io.github.skepter.brainfuckide;

import io.github.skepter.brainfuckide.BrainfuckEngine.Token;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
	private JTextArea memoryOutput;
	private char currentChar;

	/* Memory output */

	private int builderLength;
	private String format;

	public Debugger(JTextArea workspace, BrainfuckEngine engine, JTextArea memoryOutput) {
		this.workspace = workspace;
		this.memoryOutput = memoryOutput;
		workspace.setEditable(false);

		/* Loads data from engine */
		data = engine.data;
		consoleReader = engine.consoleReader;
		bits = engine.bits;

		switch (bits) {
			case 8:
				builderLength = 48;
				format = "%03d";
				break;
			case 16:
				builderLength = 72;
				format = "%05d";
				break;
			case 32:
				builderLength = 132;
				format = "%010d";
				break;
		}

		/*
		 * Temporarily stores all of the data Strips comments and formats code
		 */
		tempWorkspace = workspace.getText();
		workspace.setText(tempWorkspace.replaceAll("[^\\.\\[\\]\\+\\-\\,\\>\\<]", ""));
		maxIndex = workspace.getText().length();
		new BrainfuckFormatter().format();
		index = 0;
		currentChar = workspace.getText().charAt(index);
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
		if (index == maxIndex - 1) {
			System.out.println("End of max index.");
			return true;
		}

		/* Skips whitespace */
		while (workspace.getText().charAt(index) == ' ' || workspace.getText().charAt(index) == '\n') {
			index++;
		}
		currentChar = workspace.getText().charAt(index);

		try {
			interpret(currentChar, workspace.getText().toCharArray());
			debugInfo();
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
						/* Skips whitespace */
						int indexClone = index;
						while (workspace.getText().charAt(indexClone) == ' ' || workspace.getText().charAt(indexClone) == '\n') {
							indexClone++;
						}
						char c2 = chars[++indexClone];
						if (c2 == Token.BRACKET_LEFT)
							i++;
						else if (c2 == Token.BRACKET_RIGHT)
							i--;
					}
				}
				break;
			case Token.BRACKET_RIGHT:
				int i = 1;
				int indexClone = index;
				while (i > 0) {
					if (indexClone >= 0) {
						while (workspace.getText().charAt(indexClone) == ' ' || workspace.getText().charAt(indexClone) == '\n') {
							indexClone--;
						}

						char c2 = chars[--indexClone];
						if (c2 == Token.BRACKET_LEFT)
							i--;
						else if (c2 == Token.BRACKET_RIGHT)
							i++;
					}
				}
				charPointer--;
				break;
		}
		columnCount++;
	}

	private void debugInfo() {
		memoryOutput.setText("");
		Main.setStatusLabel(dataPointer, false);

		/* Formats it into a nice grid */
		StringBuilder builder = new StringBuilder();
		for (long l : data) {
			builder.append(String.format(format, l)).append(" ");
		}
		for (String part : getParts(builder.toString(), builderLength)) {
			if (!(memoryOutput.getText().equals("")))
				memoryOutput.setText(memoryOutput.getText() + "\n" + part);
			else
				memoryOutput.setText(part);
			memoryOutput.setCaretPosition(0);
		}

		Main.highlight(dataPointer, bits);
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

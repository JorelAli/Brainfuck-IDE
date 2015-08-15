package io.github.skepter.brainfuckide;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * The {@link BrainfuckEngine} class is an implementation of the original
 * brainfuck language.
 * 
 * @author Fabian M.
 */
public class BrainfuckEngine {

	/**
	 * The memory thats available for this brainfuck program.
	 */
	protected long[] data;

	/**
	 * The data pointer that points to the current index in the
	 * {@link BrainfuckEngine#data} memory array.
	 */
	protected int dataPointer = 0;

	/**
	 * The character pointer that points to the current index of the character
	 * array of value of its file or string.
	 */
	protected int charPointer = 0;

	/**
	 * The {@link BrainfuckEngine#fileReader} allows use to read from a file if
	 * one is specified.
	 */
	protected BufferedReader fileReader;

	/**
	 * The {@link BrainfuckEngine#consoleReader} allows us to read from the
	 * console for the ',' keyword.
	 */
	protected InputStreamReader consoleReader;

	/**
	 * The {@link BrainfuckEngine#outWriter} allows us to write to the console.
	 */
	protected OutputStream outWriter;

	/**
	 * The current line the engine is at.
	 */
	protected int lineCount = 0;

	/**
	 * The current column the engine is at.
	 */
	protected int columnCount = 0;

	private boolean wrapping = true;
	public byte bits = 8;

	/**
	 * The {@link Token} class contains tokens in brainfuck.
	 * 
	 * @author Fabian M.
	 */
	protected static class Token {
		public final static char NEXT = '>';
		public final static char PREVIOUS = '<';
		public final static char PLUS = '+';
		public final static char MINUS = '-';
		public final static char OUTPUT = '.';
		public final static char INPUT = ',';
		public final static char BRACKET_LEFT = '[';
		public final static char BRACKET_RIGHT = ']';
	}

	/**
	 * Constructs a new {@link BrainfuckEngine} instance.
	 * 
	 * @param cells
	 *            The amount of memory cells.
	 */
	public BrainfuckEngine(int cells) {
		this(cells, System.in, true);
	}

	/**
	 * Constructs a new {@link BrainfuckEngine} instance.
	 * 
	 * @param cells
	 *            The amount of memory cells.
	 * @param out
	 *            The printstream of this program.
	 * @param in
	 *            The outputstream of this program.
	 */
	public BrainfuckEngine(int cells, InputStream in) {
		this(cells, in, true);
	}

	public BrainfuckEngine(int cells, InputStream in, boolean wrapping) {
		this(cells, in, wrapping, (byte) 8);
	}

	public BrainfuckEngine(int cells, InputStream in, boolean wrapping, byte bits) {
		initiate(cells);
		consoleReader = new InputStreamReader(in);
		this.wrapping = wrapping;
		this.bits = bits;
	}

	/**
	 * Initiate this instance.
	 */
	protected void initiate(int cells) {
		data = new long[cells];
		dataPointer = 0;
		charPointer = 0;
	}

	public void reset() {
		Main.setStatusLabel(dataPointer, false);
		initiate(data.length);
	}

	/**
	 * Interprets the given string, without resetting any data
	 * 
	 * @param str
	 *            The string to interpret.
	 * @throws Exception
	 */
	public void interpretWithoutReset(String str) throws Exception {
		for (; charPointer < str.length(); charPointer++)
			interpret(str.charAt(charPointer), str.toCharArray());
	}

	/**
	 * Interprets the given char
	 * 
	 * @param c
	 *            The char to interpret.
	 * @throws Exception
	 */
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
//				int inputValue;
//				while ((inputValue = consoleReader.read()) != -1) {
//					data[dataPointer] = (short) inputValue;
//				}
				data[dataPointer] = (byte) consoleReader.read();
				break;
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
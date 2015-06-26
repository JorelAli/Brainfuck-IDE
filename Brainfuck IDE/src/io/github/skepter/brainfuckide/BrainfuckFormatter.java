package io.github.skepter.brainfuckide;

import java.util.ArrayList;
import java.util.List;

/**
 * BrainfuckFormatter designed to format brainfuck
 */
public class BrainfuckFormatter {

	/**
	 * The character pointer that points to the current index of the character
	 * array of value of its file or string.
	 */
	private StringBuilder formattedOutput;
	private int indentationLevel = 0;

	/**
	 * The {@link Token} class contains tokens in brainfuck.
	 * 
	 * @author Fabian M.
	 */
	private static class Token {
		public final static char NEXT = '>';
		public final static char PREVIOUS = '<';
		public final static char PLUS = '+';
		public final static char MINUS = '-';
		public final static char OUTPUT = '.';
		public final static char INPUT = ',';
		public final static char BRACKET_LEFT = '[';
		public final static char BRACKET_RIGHT = ']';
	}

	public void brainfuckToOok() {
		String str = Main.workspace.getText();
		String code = str.replaceAll("[^\\.\\[\\]\\+\\-\\,\\>\\<]", "");
		String comments = str.replaceAll("[\\.\\[\\]\\+\\-\\,\\>\\<]", "");
		comments = comments.trim();

		String finalCode = "";
		for (char c : code.toCharArray()) {
			switch (c) {
				case Token.NEXT:
					finalCode = finalCode + "Ook. Ook? ";
					break;
				case Token.PREVIOUS:
					finalCode = finalCode + "Ook? Ook. ";
					break;
				case Token.MINUS:
					finalCode = finalCode + "Ook! Ook! ";
					break;
				case Token.PLUS:
					finalCode = finalCode + "Ook. Ook. ";
					break;
				case Token.OUTPUT:
					finalCode = finalCode + "Ook! Ook. ";
					break;
				case Token.INPUT:
					finalCode = finalCode + "Ook. Ook! ";
					break;
				case Token.BRACKET_LEFT:
					finalCode = finalCode + "Ook! Ook? ";
					break;
				case Token.BRACKET_RIGHT:
					finalCode = finalCode + "Ook? Ook! ";
					break;
			}
		}
		code = finalCode;

		Main.workspace.setText(comments + "\n" + code);
		Main.workspace.setCaretPosition(0);
	}

	public void ookToBrainfuck() {
		String str = Main.workspace.getText();
		String comments = str.replaceAll("(Ook)[.!?]", "");
		comments = comments.trim();

		String finalCode = "";
		finalCode = str.replace(comments, "");
		finalCode = finalCode.replace("  ", "");
		String out = "";
		List<String> arr = getParts(finalCode, 10);

		for (String s : arr) {
			s = s.trim();
			if (s.equals("Ook! Ook?"))
				out = out + "[";
			if (s.equals("Ook? Ook!"))
				out = out + "]";
			if (s.equals("Ook! Ook."))
				out = out + ".";
			if (s.equals("Ook. Ook!"))
				out = out + ",";
			if (s.equals("Ook. Ook?"))
				out = out + ">";
			if (s.equals("Ook? Ook."))
				out = out + "<";
			if (s.equals("Ook! Ook!"))
				out = out + "-";
			if (s.equals("Ook. Ook."))
				out = out + "+";
		}

		Main.workspace.setText(comments + "\n" + out);
		Main.workspace.setCaretPosition(0);
	}

	public void brainfuckToTrollscript() {
		String str = Main.workspace.getText();
		String code = str.replaceAll("[^\\.\\[\\]\\+\\-\\,\\>\\<]", "");
		String comments = str.replaceAll("[\\.\\[\\]\\+\\-\\,\\>\\<]", "");
		comments = comments.trim();

		String finalCode = "";
		for (char c : code.toCharArray()) {
			switch (c) {
				case Token.NEXT:
					finalCode = finalCode + "ooo";
					break;
				case Token.PREVIOUS:
					finalCode = finalCode + "ool";
					break;
				case Token.MINUS:
					finalCode = finalCode + "oll";
					break;
				case Token.PLUS:
					finalCode = finalCode + "olo";
					break;
				case Token.OUTPUT:
					finalCode = finalCode + "loo";
					break;
				case Token.INPUT:
					finalCode = finalCode + "lol";
					break;
				case Token.BRACKET_LEFT:
					finalCode = finalCode + "llo";
					break;
				case Token.BRACKET_RIGHT:
					finalCode = finalCode + "ll";
					break;
			}
		}
		code = finalCode;

		Main.workspace.setText(comments + "\n" + "tro" + code + "ll");
		Main.workspace.setCaretPosition(0);
	}

	public void unformat() {
		String str = Main.workspace.getText();
		String code = str.replaceAll("[^\\.\\[\\]\\+\\-\\,\\>\\<]", "");
		String comments = str.replaceAll("[\\.\\[\\]\\+\\-\\,\\>\\<]", "");
		comments = comments.trim();
		Main.workspace.setText(comments + "\n" + code);
		Main.workspace.setCaretPosition(0);
	}

	public void format() {
		formattedOutput = new StringBuilder();
		String str = Main.workspace.getText();
		String code = str.replaceAll("[^\\.\\[\\]\\+\\-\\,\\>\\<]", "");
		String comments = str.replaceAll("[\\.\\[\\]\\+\\-\\,\\>\\<]", "");
		comments = comments.trim();
		for (int charPointer = 0; charPointer < code.length(); charPointer++) {

			/* Catches exceptions with SIOOB */
			char prev = '#';
			try {
				prev = code.charAt(charPointer - 1);
			} catch (Exception e) {
			}

			char next = '#';
			try {
				next = code.charAt(charPointer + 1);
			} catch (Exception e) {
			}

			format(code.charAt(charPointer), prev, next);
		}
		Main.workspace.setText(comments + "\n" + formattedOutput.toString());
		Main.workspace.setCaretPosition(0);
	}

	/**
	 * Formats the chars
	 */
	private void format(char c, char prevChar, char nextChar) {
		switch (c) {
			case Token.NEXT:
			case Token.PREVIOUS:
				switch (prevChar) {
					case Token.NEXT:
					case Token.PREVIOUS:
						formattedOutput.append(c);
						break;
					case '#':
						formattedOutput.append(indent()).append(c);
						break;
					default:
						formattedOutput.append("\n").append(indent()).append(c);
						break;

				}
				break;
			case Token.PLUS:
			case Token.MINUS:
				if (prevChar == Token.NEXT || prevChar == Token.PREVIOUS) {
					formattedOutput.append(" ").append(c);
				} else {
					formattedOutput.append(c);
				}
				break;
			case Token.OUTPUT:
				indent();
				formattedOutput.append(c);
				break;
			case Token.INPUT:
				indent();
				formattedOutput.append(c);
				break;
			case Token.BRACKET_LEFT:
				formattedOutput.append("\n").append(indent()).append(c);

				indentationLevel++;

				break;
			case Token.BRACKET_RIGHT:
				formattedOutput.append("\n");
				indentationLevel--;
				formattedOutput.append(indent()).append(c);
				break;
		}
	}

	private String indent() {
		String str = "";
		for (int i = 0; i < indentationLevel; i++)
			str = str + "  ";
		return str;
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
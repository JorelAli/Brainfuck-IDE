package io.github.skepter.brainfuckide;

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
}
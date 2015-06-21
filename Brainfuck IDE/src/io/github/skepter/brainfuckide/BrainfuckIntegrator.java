package io.github.skepter.brainfuckide;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

public class BrainfuckIntegrator {

	BrainfuckEngine engine;
	String code;
	JTextArea memoryOutput;

	public BrainfuckIntegrator(BrainfuckEngine engine, String code, JTextArea memoryOutput) {
		this.engine = engine;
		this.code = code;
		this.memoryOutput = memoryOutput;

		interpret();
		debugInfo();
	}

	public void interpret() {
		try {
			engine.interpretWithoutReset(code);
		} catch (Exception e) {
		}
	}

	private void debugInfo() {
		Main.setStatusLabel(engine.dataPointer, false);

		/* Formats it into a nice grid */
		StringBuilder builder = new StringBuilder();
		for (short s : engine.data) {
			builder.append(String.format("%03d", s)).append(" ");
		}
		for (String part : getParts(builder.toString(), 48)) {
			if (!(memoryOutput.getText().equals("")))
				memoryOutput.setText(memoryOutput.getText() + "\n" + part);
			else
				memoryOutput.setText(part);
			memoryOutput.setCaretPosition(0);
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

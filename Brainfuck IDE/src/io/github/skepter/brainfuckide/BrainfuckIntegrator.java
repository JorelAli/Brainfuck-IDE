package io.github.skepter.brainfuckide;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

public class BrainfuckIntegrator {

	BrainfuckEngine engine;
	String code;
	JTextArea output;

	public BrainfuckIntegrator(BrainfuckEngine engine, String code, JTextArea output) {
		this.engine = engine;
		this.code = code;
		this.output = output;
		
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
		Main.setStatusLabel(engine.dataPointer);

		/* Formats it into a nice grid */
		StringBuilder builder = new StringBuilder();
		for (short s : engine.data) {
			builder.append(String.format("%03d", s)).append(" ");
		}
		for (String part : getParts(builder.toString(), 48)) {
			System.out.println(part);
			output.setText(output.getText() + "\n" + part);
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

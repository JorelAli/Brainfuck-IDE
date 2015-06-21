package io.github.skepter.brainfuckide;

import java.util.ArrayList;
import java.util.List;

public class BrainfuckIntegrator {

	BrainfuckEngine engine;

	public BrainfuckIntegrator(BrainfuckEngine engine) {
		this.engine = engine;
	}

	@SuppressWarnings("unused")
	private void code() {
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

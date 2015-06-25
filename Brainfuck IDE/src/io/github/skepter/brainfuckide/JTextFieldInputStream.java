package io.github.skepter.brainfuckide;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;

import javax.swing.JTextField;

public class JTextFieldInputStream extends InputStream implements ActionListener {

	private JTextField textField;
	private String str = null;
	private int pos = 0;

	public JTextFieldInputStream(final JTextField text) {
		textField = text;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		str = textField.getText();
		pos = 0;
		textField.setText("");
		synchronized (this) {
			this.notifyAll();
		}
	}

	@Override
	public int read() throws IOException {
		if (str != null && pos == str.length()) {
			str = null;
			return StreamTokenizer.TT_EOF;
		}
		
		while (str == null || pos >= str.length()) {
			try {
				synchronized(this) {
					this.wait();
				}
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		char s = str.charAt(pos++);
		System.out.println("READ: " + s);
		return s;
	}

}
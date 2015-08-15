package io.github.skepter.brainfuckide;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

public class HelpMenu extends JFrame {

	private static final long serialVersionUID = 3855519567003531922L;
	private JPanel contentPane;
	private JLabel helpPane;

	/**
	 * Create the frame.
	 */
	public HelpMenu() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1000, 650);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(150);
		contentPane.add(splitPane, BorderLayout.CENTER);

		String[] topics = { "Getting to know the IDE", "" };

		final JList list = new JList(topics);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {

					// Double-click detected
					int index = list.locationToIndex(evt.getPoint());
					showMenu(index);
				}
			}
		});
		splitPane.setLeftComponent(list);

		helpPane = new JLabel();
		helpPane.setFont(new Font("Arial", Font.PLAIN, 12));
		splitPane.setRightComponent(helpPane);
	}

	private void showMenu(int index) {
		switch (index) {
			case 0:
				helpPane.setText(readFromFile("helpPage.txt"));
				break;
			case 1:
				break;
		}
	}

	private String readFromFile(String fileName) {
		try {
			StringBuilder builder = new StringBuilder();
			final URL url = this.getClass().getResource("/io/github/skepter/brainfuckide/helppages/" + fileName);
			final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				builder.append(inputLine);
			}
			in.close();
			return builder.toString();
		} catch (Exception e) {
		}
		return null;
	}
}

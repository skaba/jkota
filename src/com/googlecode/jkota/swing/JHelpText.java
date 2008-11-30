package com.googlecode.jkota.swing;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class JHelpText extends JPanel {

	private JTextField input;
	private JLabel helpButton;
	private static Color DEFAULT_COLOR;

	static {
		DEFAULT_COLOR=(new JLabel()).getForeground();
	}

	public JHelpText(String initialValue,final String helpURL,boolean isPassword) {
		super(new FlowLayout());
		if(isPassword)
			input=new JPasswordField(10);
		else
			input=new JTextField(10);
		input.setText(initialValue);
		add(input);
		helpButton=new JLabel("?");
		helpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		helpButton.addMouseListener
		(
				new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						Desktop desktop=Desktop.getDesktop();
						try {
							desktop.browse(new URI(helpURL));
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
					@Override
					public void mouseEntered(MouseEvent e) {
						setForeground(Color.BLUE);
					}
					@Override
					public void mouseExited(MouseEvent e) {
						setForeground(DEFAULT_COLOR);
					}
				}
		);
		add(helpButton);
	}

	public String getText() { return input.getText(); }
}

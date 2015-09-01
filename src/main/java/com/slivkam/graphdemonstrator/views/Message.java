package com.slivkam.graphdemonstrator.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.slivkam.graphdemonstrator.presenters.Presenter.View;

public class Message extends JDialog implements View {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblMessage;

	/**
	 * Create the dialog.
	 */
	public Message(String message) {
		initComponents();
		lblMessage.setText(message);
	}

	private void initComponents() {
		setBounds(100, 100, 247, 142);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblMessage = new JLabel("Message!");
			contentPanel.add(lblMessage);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						Message.this.dispose();
					}
					
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}

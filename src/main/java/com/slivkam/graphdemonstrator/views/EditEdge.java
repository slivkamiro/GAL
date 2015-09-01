package com.slivkam.graphdemonstrator.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import com.slivkam.graphdemonstrator.presenters.EditEdgePresenter;
import com.slivkam.graphdemonstrator.presenters.EditEdgePresenter.EdgeEditor;
import com.slivkam.graphdemonstrator.presenters.Presenter.View;

public class EditEdge extends JDialog implements EdgeEditor, View {

	private final JPanel contentPanel = new JPanel();
	private JSpinner spWeight;
	private EditEdgePresenter presenter;

	/**
	 * Create the dialog.
	 * @param presenter
	 */
	public EditEdge(EditEdgePresenter presenter) {
		super();
		this.presenter = presenter;
		initComponents();
	}

	private void initComponents() {
		setBounds(100, 100, 282, 187);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel lblWeight = new JLabel("Weight:");

		spWeight = new JSpinner();
		spWeight.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
				gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
						.addContainerGap(94, Short.MAX_VALUE)
						.addComponent(lblWeight)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(spWeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(89))
				);
		gl_contentPanel.setVerticalGroup(
				gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
						.addGap(45)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblWeight)
								.addComponent(spWeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addContainerGap(48, Short.MAX_VALUE))
				);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						presenter.setWeight(spWeight.getValue().toString());
						EditEdge.this.dispose();

					}

				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						presenter.resetWeight();
						EditEdge.this.dispose();

					}

				});
				buttonPane.add(cancelButton);
			}
		}
	}

	@Override
	public void showEdgeProp(Integer w) {
		spWeight.setValue(w);

	}
}

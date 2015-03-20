/**
 * 
 */
package it.silma.reverie.gui;

import it.silma.reverie.config.Constants;
import it.silma.reverie.config.Messages;
import it.silma.reverie.config.ResourceLoader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Main game frame.
 * 
 * @author a.putzu
 * 
 */
public class ReverieFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 3372014042793552814L;

	private final ReverieHistoryField historyField = new ReverieHistoryField();
	private final ReverieCommandField commandField = new ReverieCommandField();

	private static ReverieFrame instance;

	public ReverieFrame() {
		super(Messages.GUI_TITLE);

		init();

		ReverieFrame.instance = this;
	}

	private void init() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLocationByPlatform(true);

		final JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(600, 400));

		// Area storico
		final JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setPreferredSize(new Dimension(600, 160));

		centerPanel.add(historyField, BorderLayout.CENTER);

		// Area comando
		final JPanel bottomPanel = new JPanel(new BorderLayout());
		final JTextField promptField = new JTextField("> ");
		promptField.setEditable(false);
		bottomPanel.add(promptField, BorderLayout.WEST);

		bottomPanel.add(commandField, BorderLayout.CENTER);

		// Bordi
		final Border empty = BorderFactory.createEmptyBorder(4, 4, 4, 4);
		topPanel.setBorder(empty);
		centerPanel.setBorder(BorderFactory.createCompoundBorder(empty, BorderFactory.createTitledBorder("")));
		bottomPanel.setBorder(empty);
		promptField.setBorder(empty);

		// Root Content pane
		final JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBackground(new Color(245, 245, 255));

		contentPanel.add(topPanel, BorderLayout.NORTH);
		contentPanel.add(centerPanel, BorderLayout.CENTER);
		contentPanel.add(bottomPanel, BorderLayout.SOUTH);

		this.setIconImage(ResourceLoader.loadImage(Constants.REVERIE_ICON));
		this.setContentPane(contentPanel);
		this.pack();
	}

	public static ReverieFrame getInstance() {
		return instance;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
		case Constants.COMMAND_REFUSED:
			historyField.processCommandRefused(e);
			break;

		case Constants.COMMAND_ACCEPTED:
			historyField.processCommandAccepted(e);

		default:
			break;
		}

	}

}

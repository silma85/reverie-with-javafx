/**
 * 
 */
package it.silma.reverie.gui;

import it.silma.reverie.config.Constants;
import it.silma.reverie.config.Messages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTextField;

/**
 * JTextField che riceve i comandi utente.
 * 
 * @author a.putzu
 * 
 */
public class ReverieCommandField extends JTextField implements ActionListener {

	private static final long serialVersionUID = 7775721716247604076L;

	public ReverieCommandField() {
		super();

		this.setActionCommand(Constants.COMMAND_ACTION);
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals(Constants.COMMAND_ACTION)) {

			ActionEvent ae = null;

			if (isValidCommand(this.getText())) {
				ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, Constants.COMMAND_ACCEPTED);
			} else {
				ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, Constants.COMMAND_REFUSED);
			}

			ReverieFrame.getInstance().actionPerformed(ae);
		}
	}

	private boolean isValidCommand(String command) {

		command = command.trim();

		if (command.isEmpty())
			return false;

		String[] commands = Messages.GUI_COMMANDS.split(Constants.SEPARATOR_OPTION);
		List<String> commandsList = Arrays.asList(commands);

		String commandVerb = command.split(" ")[0];

		if (commandsList.contains(commandVerb))
			return true;

		return false;
	}

}

/**
 * 
 */
package it.silma.reverie.gui;

import it.silma.reverie.config.Messages;
import it.silma.reverie.game.Randomizer;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * Pannello con storico dei comandi e risultato azioni
 * 
 * @author a.putzu
 * 
 */
public class ReverieHistoryField extends JTextArea implements MouseListener {

	private static final long serialVersionUID = -801347511200697834L;

	public ReverieHistoryField() {
		super();

		this.setBorder(BorderFactory.createEmptyBorder());

		this.setEditable(false);
		// this.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.setText("Ye click'd mey!");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.setText("How y'doing?");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.setText("So long!");
	}

	public void processCommandRefused(ActionEvent e) {
		this.setText(Randomizer.rollString(Messages.GUI_COMMAND_REFUSED));
	}

	public void processCommandAccepted(ActionEvent e) {
		this.setText("I feel ya...");
	}
}

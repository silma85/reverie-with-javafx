/**
 * 
 */
package it.silma.reverie.main;

import it.silma.reverie.gui.ReverieFrame;

import java.io.IOException;

import javax.swing.JOptionPane;

/**
 * Reverie main file<br />
 * 
 * Here be lions.
 * 
 * @author a.putzu
 * @version 0.1
 * 
 */
public class Reverie {

	public static void onError(String message) {
		JOptionPane.showMessageDialog(null, "<html>Errore irreversibile: " + message
				+ ". <br />Il programma verr&agrave; terminato.</html>");
		System.exit(1);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (final ClassNotFoundException e) {
			onError(e.getMessage());
		} catch (final InstantiationException e) {
			onError(e.getMessage());
		} catch (final IllegalAccessException e) {
			onError(e.getMessage());
		} catch (final javax.swing.UnsupportedLookAndFeelException e) {
			onError(e.getMessage());
		}

		// Lancia il thread che si occupera' di mostrare la GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				showGUI();
				/* new BetaBox(null); */
			}
		});

	}

	public static void showGUI() {
		final ReverieFrame mainFrame = new ReverieFrame();
		mainFrame.setVisible(true);
	}
}

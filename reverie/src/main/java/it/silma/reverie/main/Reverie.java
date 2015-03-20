/**
 * 
 */
package it.silma.reverie.main;

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
		JOptionPane.showMessageDialog(null, "<html>Errore irreversibile: "
				+ message
				+ ". <br />Il programma verr&agrave; terminato.</html>");
		System.exit(1);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

	}

}

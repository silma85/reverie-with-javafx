package it.silma.reverie.config;

import it.silma.reverie.main.Reverie;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.swing.ImageIcon;

public class ResourceLoader extends URLStreamHandler {

	private final ClassLoader classLoader;

	private ResourceLoader() {
		this.classLoader = getClass().getClassLoader();
	}

	public static Image loadImage(String resource) {

		Image image = Toolkit.getDefaultToolkit().getImage(loadURL(resource));

		return image;
	}

	public static ImageIcon loadImageIcon(String resource) {

		try {
			InputStream in = ImageIcon.class.getResourceAsStream("/" + Constants.PATH_TO_RES + resource);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			int d;
			while ((d = in.read()) != -1) {
				out.write(d);
			}

			byte[] bytes = out.toByteArray();

			return new ImageIcon(bytes);

		} catch (IOException e) {
			Reverie.onError(e.getMessage());
		}

		return null;
	}

	public static URL loadURL(String resource) {

		try {
			return new URL(null, "classpath:" + Constants.PATH_TO_RES + resource, new ResourceLoader());
		} catch (MalformedURLException e) {
			Reverie.onError(e.getMessage());
		}

		return null;
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		final URL resourceUrl = classLoader.getResource(u.getPath());
		return resourceUrl.openConnection();
	}
}

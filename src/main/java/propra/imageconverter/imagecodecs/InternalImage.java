package propra.imageconverter.imagecodecs;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * Interne Repräsentation eines Bildes
 *
 * @author marvin
 *
 */
public class InternalImage {

	/**
	 * Interne Pixeldatendarstellung
	 */
	private BufferedImage pixelData;

	public InternalImage() {
	}

	public InternalImage(final BufferedImage pixelData) {
		this();

		this.setPixelData(pixelData);
	}

	/**
	 * Gibt die Pixeldaten als BufferedImage. Kann null sein.
	 *
	 * @return Die Pixeldaten oder null, wenn nicht vorhanden
	 */
	public BufferedImage getPixelData() {
		return this.pixelData;
	}

	/**
	 * Setzt die Pixeldaten
	 *
	 * @param pixelData Die neuen Pixeldaten
	 */
	public void setPixelData(final BufferedImage pixelData) {
		this.pixelData = pixelData;
	}

	/**
	 * Gibt die Maße des Bildes. Kann null sein.
	 *
	 * @return Die Dimension des Bilder oder null, wenn keine Bilddaten vorhanden
	 *         sind
	 */
	public Dimension getSize() {
		final BufferedImage data = this.getPixelData();
		if (data == null) {
			return null;
		}

		return new Dimension(data.getWidth(), data.getHeight());
	}

}

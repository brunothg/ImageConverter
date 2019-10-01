package propra.imageconverter.codecs;

import java.awt.image.BufferedImage;

/**
 * Interne Repräsentation eines bildes
 *
 * @author marvin
 *
 */
public class InternalImage {

	private BufferedImage pixelData;

	public InternalImage() {
	}

	public InternalImage(BufferedImage pixelData) {
		this();

		this.setPixelData(pixelData);
	}

	public BufferedImage getPixelData() {
		return this.pixelData;
	}

	public void setPixelData(BufferedImage pixelData) {
		this.pixelData = pixelData;
	}

}

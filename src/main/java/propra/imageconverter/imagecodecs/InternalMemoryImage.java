package propra.imageconverter.imagecodecs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class InternalMemoryImage implements InternalImage {
	/**
	 * Interne Pixeldatendarstellung
	 */
	private final BufferedImage pixelData;
	private final Dimension size;

	public InternalMemoryImage(final Dimension size) {
		this.size = Objects.requireNonNull(size, "size");
		this.pixelData = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
	}

	public InternalMemoryImage(final BufferedImage pixelData) {
		this.size = new Dimension(pixelData.getWidth(), pixelData.getHeight());
		this.pixelData = pixelData;
	}

	/**
	 * Gibt die Pixeldaten als BufferedImage. Kann null sein.
	 *
	 * @return Die Pixeldaten oder null, wenn nicht vorhanden
	 */
	@Override
	public BufferedImage getPixelData() {
		return this.pixelData;
	}

	/**
	 * Setzt einen Pixel
	 *
	 * @param p Koordinaten
	 * @param c Farbe
	 */
	@Override
	public void setPixel(final Point p, final Color c) {
		this.pixelData.setRGB(p.x, p.y, c.getRGB());
	}

	/**
	 * Gibt die Farbe enes Pixels
	 *
	 * @param p Koordinaten
	 * @return Die Fareb des Pixels
	 */
	@Override
	public Color getPixel(final Point p) {
		return new Color(this.pixelData.getRGB(p.x, p.y));
	}

	/**
	 * Gibt die Maße des Bildes.
	 *
	 * @return Die Dimension des Bildes
	 */
	@Override
	public Dimension getSize() {
		return this.size;
	}

}

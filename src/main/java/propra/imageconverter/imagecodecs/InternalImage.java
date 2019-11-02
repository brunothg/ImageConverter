package propra.imageconverter.imagecodecs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * Interne Repräsentation eines Bildes
 *
 * @author marvin
 *
 */
public interface InternalImage {

	/**
	 * Gibt die Pixeldaten als BufferedImage. Kann null sein.
	 *
	 * @return Die Pixeldaten oder null, wenn nicht vorhanden
	 */
	public BufferedImage getPixelData();

	/**
	 * Setzt einen Pixel
	 *
	 * @param p Koordinaten
	 * @param c Farbe
	 */
	public void setPixel(final Point p, final Color c);

	/**
	 * Gibt die Farbe enes Pixels
	 *
	 * @param p Koordinaten
	 * @return Die Fareb des Pixels
	 */
	public Color getPixel(final Point p);

	/**
	 * Gibt die Maße des Bildes.
	 *
	 * @return Die Dimension des Bildes
	 */
	public Dimension getSize();

}

package propra.imageconverter.imagecodecs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

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

	/**
	 * Erstellt ein {@link InternalImage}
	 *
	 * @param size Größe des Bildes
	 * @return Ein passendes {@link InternalImage}
	 */
	public static InternalImage createInternalImage(final Dimension size) {
		final long freeMemory = Runtime.getRuntime().freeMemory();
		final long requiredMemory = size.height * size.width * 3;

		if (Math.max(freeMemory * 0.6, freeMemory - (1024 * 1024 * 50)) < requiredMemory) {
			try {
				return new InternalFileImage(size);
			} catch (final IOException e) {
				return new InternalMemoryImage(size);
			}
		} else {
			return new InternalMemoryImage(size);
		}

	}

	/**
	 * Schließt das {@link InternalImage} und gibt ggf. die benutzten Resourcen frei
	 */
	public void close();
}

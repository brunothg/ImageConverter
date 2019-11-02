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
	 * Setzt mehrere Pixel ausgehend von einem Punkt (von links nach rechts; von
	 * oben nach unten)
	 *
	 * @param p  Startpunkt
	 * @param cs Farben
	 */
	default public void setPixels(final Point p, final Color... cs) {
		int x = p.x;
		int y = p.y;

		for (int i = 0; i < cs.length; i++) {
			this.setPixel(new Point(x, y), cs[i]);

			x++;
			final Dimension size = this.getSize();
			if (x >= size.width) {
				x = 0;
				y++;

				if (y >= size.height) {
					throw new RuntimeException("OutOfBound: " + new Point(x, y) + " - " + size);
				}
			}
		}
	}

	/**
	 * Gibt die Farbe enes Pixels
	 *
	 * @param p Koordinaten
	 * @return Die Fareb des Pixels
	 */
	public Color getPixel(final Point p);

	/**
	 * Gibt mehrere Pixel ausgehend von einem Punkt (von links nach rechts; von oben
	 * nach unten)
	 *
	 * @param p     Startpunkt
	 * @param count Anzahl Pixel
	 * @return Farben
	 */
	default public Color[] getPixels(final Point p, final int count) {
		final Color[] colors = new Color[count];

		int x = p.x;
		int y = p.y;
		for (int i = 0; i < colors.length; i++) {
			colors[i] = this.getPixel(new Point(x, y));

			x++;
			final Dimension size = this.getSize();
			if (x >= size.width) {
				x = 0;
				y++;

				if (y >= size.height) {
					throw new RuntimeException("OutOfBound: " + new Point(x, y) + " - " + size);
				}
			}
		}

		return colors;
	}

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

package propra.imageconverter.codecs.tga;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

import propra.imageconverter.codecs.ConversionException;

/**
 * Interface für verschiedene Kompressionsverfahren
 *
 * @author bruns_m
 *
 */
public abstract class TgaCompression {

	public TgaCompression() {
		// Erzwinge default Constructor
	}

	/**
	 * Dekompremiert die Pixeldaten
	 *
	 * @param values Parameter für die Dekomprimierung
	 * @return Die Ergebnisse
	 */
	public abstract PixelCompressionValues uncompressPixelData(PixelCompressionValues values)
			throws ConversionException;

	/**
	 * Kompremiert die Pixeldaten
	 *
	 * @param values Parameter für die Komprimierung
	 * @returnDie Ergebnisse
	 */
	public abstract PixelCompressionValues compressPixelData(PixelCompressionValues values) throws ConversionException;

	public static class PixelCompressionValues {
		public Dimension dimension;
		public int pixelResolution;
		public Point origin;
		public TgaImageAttributes imageAttributes;
		public byte[] compressedPixelData;
		public BufferedImage uncompressedPixelData;
	}
}

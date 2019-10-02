package propra.imageconverter.codecs.propra;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * Interface für verschiedene Kompressionsverfahren
 *
 * @author bruns_m
 *
 */
public abstract class Compression {

	public Compression() {
		// Erzwinge default Constructor
	}

	/**
	 * Dekompremiert die Pixeldaten
	 * 
	 * @param values Parameter für die Dekomprimierung
	 * @return Die Ergebnisse
	 */
	public abstract PixelCompressionValues uncompressPixelData(PixelCompressionValues values);

	/**
	 * Kompremiert die Pixeldaten
	 * 
	 * @param values Parameter für die Komprimierung
	 * @returnDie Ergebnisse
	 */
	public abstract PixelCompressionValues compressPixelData(PixelCompressionValues values);

	public static class PixelCompressionValues {
		public Dimension dimension;
		public int pixelResolution;
		public byte[] compressedPixelData;
		public BufferedImage uncompressedPixelData;
	}
}

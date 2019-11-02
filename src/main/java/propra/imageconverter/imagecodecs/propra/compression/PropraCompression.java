package propra.imageconverter.imagecodecs.propra.compression;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.imagecodecs.ConversionException;

/**
 * Interface für verschiedene Kompressionsverfahren
 *
 * @author marvin
 *
 */
public abstract class PropraCompression {

	public PropraCompression() {
		// Erzwinge default Constructor
	}

	/**
	 * Dekompremiert die Pixeldaten
	 *
	 * @param values Parameter für die Dekomprimierung
	 * @return Die Ergebnisse
	 */
	public abstract PropraPixelDecodeValues uncompressPixelData(PropraPixelDecodeValues values)
			throws ConversionException;

	/**
	 * Kompremiert die Pixeldaten
	 *
	 * @param values Parameter für die Komprimierung
	 * @returnDie Ergebnisse
	 */
	public abstract PropraPixelEncodeValues compressPixelData(PropraPixelEncodeValues values)
			throws ConversionException;

	public abstract static class PropraPixelCompressionValues {
		public Dimension dimension;
		public int pixelResolution;
		public BufferedImage uncompressedPixelData;
	}

	public static class PropraPixelDecodeValues extends PropraPixelCompressionValues {
		public InputStream compressedPixelData;
	}

	public static class PropraPixelEncodeValues extends PropraPixelCompressionValues {
		public OutputStream compressedPixelData;
	}
}

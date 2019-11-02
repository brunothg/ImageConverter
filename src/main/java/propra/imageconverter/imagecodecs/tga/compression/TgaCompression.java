package propra.imageconverter.imagecodecs.tga.compression;

import java.awt.Dimension;
import java.awt.Point;
import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.imagecodecs.tga.TgaImageAttributes;

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
	 * @return Die Ergebnisse (gleiches Objekt wie parameter)
	 */
	public abstract TgaPixelDecodeValues uncompressPixelData(TgaPixelDecodeValues values) throws ConversionException;

	/**
	 * Kompremiert die Pixeldaten
	 *
	 * @param values Parameter für die Komprimierung
	 * @return Die Ergebnisse (gleiches Objekt wie parameter)
	 */
	public abstract TgaPixelEncodeValues compressPixelData(TgaPixelEncodeValues values) throws ConversionException;

	public abstract static class TgaPixelCompressionValues {
		public Dimension dimension;
		public int pixelResolution;
		public Point origin;
		public TgaImageAttributes imageAttributes;
		public InternalImage uncompressedPixelData;
	}

	public static class TgaPixelDecodeValues extends TgaPixelCompressionValues {
		public InputStream compressedPixelData;
	}

	public static class TgaPixelEncodeValues extends TgaPixelCompressionValues {
		public OutputStream compressedPixelData;
	}
}

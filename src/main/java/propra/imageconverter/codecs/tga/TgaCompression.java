package propra.imageconverter.codecs.tga;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

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
     * @return Die Ergebnisse (gleiches Objekt wie parameter)
     */
    public abstract PixelDecodeValues uncompressPixelData(PixelDecodeValues values) throws ConversionException;

    /**
     * Kompremiert die Pixeldaten
     *
     * @param values Parameter für die Komprimierung
     * @return Die Ergebnisse (gleiches Objekt wie parameter)
     */
    public abstract PixelEncodeValues compressPixelData(PixelEncodeValues values) throws ConversionException;

    public abstract static class PixelCompressionValues {
	public Dimension dimension;
	public int pixelResolution;
	public Point origin;
	public TgaImageAttributes imageAttributes;
	public BufferedImage uncompressedPixelData;
    }

    public static class PixelDecodeValues extends PixelCompressionValues {
	public InputStream compressedPixelData;
    }

    public static class PixelEncodeValues extends PixelCompressionValues {
	public OutputStream compressedPixelData;
    }
}

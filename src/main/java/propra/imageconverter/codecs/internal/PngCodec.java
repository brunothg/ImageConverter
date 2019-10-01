package propra.imageconverter.codecs.internal;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.ImageCodec;

public class PngCodec implements ImageCodec {

	private static final String FILE_EXTENSION = "png";

	@Override
	public BufferedImage readImage(InputStream in) throws ConversionException {
		try {
			return ImageIO.read(in);
		} catch (final Exception e) {
			throw new ConversionException("Fehler beim Lesen des Bildes: " + e.getMessage(), e);
		}
	}

	@Override
	public void writeImage(BufferedImage image, OutputStream out) throws ConversionException {
		try {
			ImageIO.write(image, FILE_EXTENSION, out);
		} catch (final Exception e) {
			throw new ConversionException("Fehler beim Schreiben des Bildes: " + e.getMessage(), e);
		}
	}

	@Override
	public String getFileExtension() {
		return FILE_EXTENSION;
	}

}

package propra.imageconverter.codecs.internal;

import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.ImageCodec;
import propra.imageconverter.imagecodecs.InternalImage;

/**
 * Codec für PNGs
 * 
 * @author marvin
 *
 */
public class PngCodec implements ImageCodec {

	private static final String FILE_EXTENSION = "png";

	@Override
	public InternalImage readImage(InputStream in) throws ConversionException {
		try {
			return new InternalImage(ImageIO.read(in));
		} catch (final Exception e) {
			throw new ConversionException("Fehler beim Lesen des Bildes: " + e.getMessage(), e);
		}
	}

	@Override
	public void writeImage(InternalImage image, OutputStream out) throws ConversionException {
		try {
			ImageIO.write(image.getPixelData(), FILE_EXTENSION, out);
		} catch (final Exception e) {
			throw new ConversionException("Fehler beim Schreiben des Bildes: " + e.getMessage(), e);
		}
	}

	@Override
	public String getFileExtension() {
		return FILE_EXTENSION;
	}

}

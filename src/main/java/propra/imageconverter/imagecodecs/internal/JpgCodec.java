package propra.imageconverter.imagecodecs.internal;

import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.ImageCodec;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.imagecodecs.InternalMemoryImage;

/**
 * Codec für JPGs
 *
 * @author marvin
 *
 */
public class JpgCodec implements ImageCodec {

	private static final String FILE_EXTENSION = "jpg";

	@Override
	public InternalImage readImage(final InputStream in) throws ConversionException {
		try {
			return new InternalMemoryImage(ImageIO.read(in));
		} catch (final Exception e) {
			throw new ConversionException("Fehler beim Lesen des Bildes: " + e.getMessage(), e);
		}
	}

	@Override
	public void writeImage(final InternalImage image, final OutputStream out) throws ConversionException {
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

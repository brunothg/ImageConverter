package propra.imageconverter.codecs.tga;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.ImageCodec;

/**
 * Codec für Targa Bilder. Siehe
 * <a href="https://de.wikipedia.org/wiki/Targa_Image_File">Targa Image
 * File</a>.
 *
 * @author marvin
 *
 */
public class TgaCodec implements ImageCodec {

	public static final String FILE_EXTENSION = "tga";

	@Override
	public BufferedImage readImage(InputStream in) throws ConversionException {
		// TODO readImage
		return null;
	}

	@Override
	public void writeImage(BufferedImage image, OutputStream out) throws ConversionException {
		// TODO writeImage
	}

	@Override
	public String getFileExtension() {
		return FILE_EXTENSION;
	}

}

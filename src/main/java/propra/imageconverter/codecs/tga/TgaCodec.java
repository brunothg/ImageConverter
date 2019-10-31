package propra.imageconverter.codecs.tga;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.ImageCodec;
import propra.imageconverter.imagecodecs.InternalImage;

/**
 * Codec für Targa Bilder. Siehe
 * <a href="https://de.wikipedia.org/wiki/Targa_Image_File">Targa Image
 * File</a>.
 *
 * @author marvin
 *
 */
public class TgaCodec implements ImageCodec {

	public static final int[] PIXEL_RESOLUTIONS = new int[] { 24 };

	public static final String FILE_EXTENSION = "tga";

	@Override
	public InternalImage readImage(InputStream in) throws ConversionException {
		final TgaReader reader = new TgaReader(in);
		final InternalImage image = reader.readImage();
		try {
			reader.close();
		} catch (final IOException e) {
			// Close quietly
		}
		return image;
	}

	@Override
	public void writeImage(InternalImage image, OutputStream out) throws ConversionException {
		final TgaWriter writer = new TgaWriter(out);
		writer.writeImage(image);
		try {
			writer.close();
		} catch (final IOException e) {
			// Close quietly
		}
	}

	@Override
	public String getFileExtension() {
		return FILE_EXTENSION;
	}

}

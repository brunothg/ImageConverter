package propra.imageconverter.imagecodecs.tga;

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

	private TgaImageType imageType = TgaImageType.Rgb;

	@Override
	public InternalImage readImage(final InputStream in) throws ConversionException {
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
	public void writeImage(final InternalImage image, final OutputStream out) throws ConversionException {
		final TgaWriter writer = new TgaWriter(out);
		writer.setImageType(this.imageType);
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

	@Override
	public void setCodecProperty(final String name, final String value) {
		switch (name) {
		case ImageCodec.PROPERTY_COMPRESSION:
			this.imageType = TgaImageType.fromCliId(value);
			break;

		default:
			System.out.println("Eigenschaft '" + name + "' wird nicht unterstützt.");
			break;
		}
	}

}

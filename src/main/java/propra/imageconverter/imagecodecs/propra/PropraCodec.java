package propra.imageconverter.imagecodecs.propra;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.ImageCodec;
import propra.imageconverter.imagecodecs.InternalImage;

/**
 * Codec für Propra Bilder. Siehe <a href=
 * "https://moodle-wrm.fernuni-hagen.de/mod/page/view.php?id=40779">Propra
 * Format</a>.
 *
 * @author marvin
 *
 */
public class PropraCodec implements ImageCodec {

	public static final String FILE_IDENTIFIER = "ProPraWS19";

	/**
	 * Erlaubte Werte für die Pixelauflösung (Bits pro Bildpunkt)
	 */
	public static final int[] PIXEL_RESOLUTIONS = { 24 };

	private static final String FILE_EXTENSION = "propra";

	private PropraCompressionType compressionType = PropraCompressionType.None;

	@Override
	public InternalImage readImage(final InputStream in) throws ConversionException {
		final PropraReader reader = new PropraReader(in);
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
		final PropraWriter writer = new PropraWriter(out);
		writer.setCompressionType(this.compressionType);
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
			this.compressionType = PropraCompressionType.fromCliId(value);
			break;

		default:
			System.out.println("Eigenschaft '" + name + "' wird nicht unterstützt.");
			break;
		}
	}

}

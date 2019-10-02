package propra.imageconverter.codecs.propra;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.ImageCodec;
import propra.imageconverter.codecs.InternalImage;

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

	@Override
	public InternalImage readImage(InputStream in) throws ConversionException {
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
	public void writeImage(InternalImage image, OutputStream out) throws ConversionException {
		final PropraWriter writer = new PropraWriter(out);
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

package propra.imageconverter.codecs.tga;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.InternalImage;
import propra.imageconverter.utils.ByteInputStream;

/**
 * Klasse zum Lesen von Tga-Bildern
 *
 * @author marvin
 *
 */
public class TgaReader implements Closeable {

	private final ByteInputStream in;

	public TgaReader(InputStream in) {
		this.in = new ByteInputStream(Objects.requireNonNull(in, "in"));
	}

	/**
	 * Liest ein Bild. Ein zweiter Aufruf der Methode wird fehlschlagen.
	 *
	 * @return Das gelesene Bild
	 * @throws ConversionException Wenn beim Lesen des Bildes ein Fehler auftritt
	 */
	public InternalImage readImage() throws ConversionException {
		// TODO readImage
		return null;
	}

	@Override
	public void close() throws IOException {
		this.in.close();
	}

}

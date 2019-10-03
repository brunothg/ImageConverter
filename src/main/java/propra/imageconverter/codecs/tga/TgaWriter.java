package propra.imageconverter.codecs.tga;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.InternalImage;
import propra.imageconverter.utils.ByteOutputStream;

/**
 * Klasse zum Schreiben von Tga-Bildern
 *
 * @author marvin
 *
 */
public class TgaWriter implements Closeable {

	private final ByteOutputStream out;

	public TgaWriter(OutputStream out) {
		this.out = new ByteOutputStream(Objects.requireNonNull(out, "out"));
	}

	/**
	 * Schreibt ein Bild. Ein zweiter Aufruf der Methode wird zu unerwünschten
	 * Ergebnissen führen.
	 *
	 * @param image Das Bild zum Schreiben
	 * @throws ConversionException Wenn beim Schreiben des Bildes ein Fehler
	 *                             auftritt
	 */
	public void writeImage(InternalImage image) throws ConversionException {
		Objects.requireNonNull(image, "image");
		// TODO writeImage
	}

	@Override
	public void close() throws IOException {
		this.out.close();
	}

}

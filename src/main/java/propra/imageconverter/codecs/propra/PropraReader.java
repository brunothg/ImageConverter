package propra.imageconverter.codecs.propra;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.InternalImage;
import propra.imageconverter.utils.ByteInputStream;

/**
 * 
 * Klasse zum einlesen von Propra Bildern
 * 
 * @author marvin
 *
 */
public class PropraReader implements Closeable {

    private final ByteInputStream in;

    public PropraReader(InputStream in) {
	this.in = new ByteInputStream(Objects.requireNonNull(in, "in"));
    }

    /**
     * Liest ein Bild. Ein zweiter Aufruf der Methode wird fehlschlagen.
     * 
     * @return Das gelesene Bild
     * @throws ConversionException Wenn beim Lesen des Bildes ein Fehler auftritt
     */
    public InternalImage readImage() throws ConversionException {
	final String formatkennung = readFormatkennung();
	if (!formatkennung.equals(PropraCodec.FILE_IDENTIFIER)) {
	    throw new ConversionException("Formatkennung fehlerhaft: " + formatkennung);
	}

	return null;
    }

    private String readFormatkennung() throws ConversionException {
	this.in.setByteOrder(ByteOrder.BIG_ENDIAN);
	try {
	    return this.in.readOrderedString(10, StandardCharsets.UTF_8);
	} catch (final IOException e) {
	    throw new ConversionException("Die Formatkennung konnte nicht gelesen werden: " + e.getMessage(), e);
	}
    }

    @Override
    public void close() throws IOException {
	this.in.close();
    }
}

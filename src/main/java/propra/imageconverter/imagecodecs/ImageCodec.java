package propra.imageconverter.imagecodecs;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface für den einheitlichen Zugriff auf die verschiedenen Bildtypen
 *
 * @author marvin
 *
 */
public interface ImageCodec {
	public static final String PROPERTY_COMPRESSION = "compression";

	/**
	 * Setzt eine Eigenschaft. Es ist nicht garantiert, dass dies vom Codec
	 * unterstützt wird.
	 *
	 * @param name
	 * @param value
	 */
	default public void setCodecProperty(final String name, final String value) {
		System.out.println("Eigenschaft '" + name + "' wird nicht unterstützt.");
	}

	/**
	 * Liest ein Bild von einem {@link InputStream}
	 *
	 * @param in {@link InputStream} des Bildes
	 * @return Das eingelesene Bild
	 * @throws ConversionException wenn ein Konvertierungsfehler auftritt
	 */
	public InternalImage readImage(InputStream in) throws ConversionException;

	/**
	 * Schreibt ein Bild in einen {@link OutputStream}
	 *
	 * @param image Das Bild, das geschrieben werden soll
	 * @param out   {@link OutputStream} zum Schreiben des Bildes
	 * @throws ConversionException wenn ein Konvertierungsfehler auftritt
	 */
	public void writeImage(InternalImage image, OutputStream out) throws ConversionException;

	/**
	 * Gibt die Dateiendung des Codecs (z.B. tga oder propra)
	 *
	 * @return Die Dateieindung
	 */
	public String getFileExtension();
}

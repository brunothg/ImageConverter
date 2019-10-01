package propra.imageconverter.codecs;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface für den einheitlichen Zugriff auf die verschiedenen Bildtypen
 *
 * @author marvin
 *
 */
public interface ImageCodec {

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

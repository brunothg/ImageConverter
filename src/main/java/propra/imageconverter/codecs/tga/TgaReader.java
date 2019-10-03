package propra.imageconverter.codecs.tga;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
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

		final int imageIdLength = this.readImageIdLength();

		this.readColormapType();

		final ImageType imageType = this.readImageType();

		this.readColorMapStart();
		this.readColorMapLength();
		this.readColorMapEntrySize();

		return null;
	}

	private void readColorMapEntrySize() throws ConversionException {
		try {
			this.in.readUnsignedByte();
		} catch (final IOException e) {
			throw new ConversionException(
					"Die Farbpaletteneintragsgröße konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private void readColorMapLength() throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.in.readOrderedUnsignedShort();
		} catch (final IOException e) {
			throw new ConversionException("Die Farbpalettenlänge konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private void readColorMapStart() throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.in.readOrderedUnsignedShort();
		} catch (final IOException e) {
			throw new ConversionException("Der Farbpalettenbeginn konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private ImageType readImageType() throws ConversionException {
		try {
			final int imageTypeId = this.in.readUnsignedByte();
			final ImageType imageType = ImageType.fromId(imageTypeId);

			if (imageType == null) {
				throw new ConversionException("Der Bildtyp ist unbekannt: " + imageTypeId);
			}

			return imageType;
		} catch (final IOException e) {
			throw new ConversionException("Der Bildtyp konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private void readColormapType() throws ConversionException {
		try {
			this.in.readUnsignedByte();
		} catch (final IOException e) {
			throw new ConversionException("Der Farbpalettentyp konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private int readImageIdLength() throws ConversionException {
		try {
			return this.in.readUnsignedByte();
		} catch (final IOException e) {
			throw new ConversionException("Die BildId-Länge konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	@Override
	public void close() throws IOException {
		this.in.close();
	}

}

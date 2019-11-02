package propra.imageconverter.imagecodecs.propra;

import java.awt.Dimension;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.imagecodecs.propra.PropraChecksum.PropraChecksumInputStream;
import propra.imageconverter.imagecodecs.propra.PropraCompression.PropraPixelDecodeValues;
import propra.imageconverter.utils.ByteInputStream;
import propra.imageconverter.utils.LimitInputStream;

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
		final String formatkennung = this.readFormatkennung();
		if (!formatkennung.equals(PropraCodec.FILE_IDENTIFIER)) {
			throw new ConversionException("Formatkennung fehlerhaft: " + formatkennung);
		}

		final Dimension dimension = this.readDimension();
		if ((dimension.height <= 0) || (dimension.width <= 0)) {
			throw new ConversionException("Nullgröße nicht erlaubt: " + dimension.toString());
		}

		final int pixelResolution = this.readPixelResolution();
		if (!Arrays.stream(PropraCodec.PIXEL_RESOLUTIONS).anyMatch(Integer.valueOf(pixelResolution)::equals)) {
			throw new ConversionException("Pixelauflösung nicht unterstützt: " + pixelResolution);
		}

		final PropraCompressionType compressionType = this.readCompressionType();

		final BigInteger pixelDataSize = this.readPixelDataSize();

		final long checksum = this.readChecksum();

		PropraChecksumInputStream pixelDataInputStream = new PropraChecksum.PropraChecksumInputStream(getPixelDataInputStream(pixelDataSize));
		final PropraCompression compression = compressionType.createCompressionInstance();
		PropraPixelDecodeValues compressionValues = new PropraPixelDecodeValues();
		compressionValues.dimension = dimension;
		compressionValues.pixelResolution = pixelResolution;
		compressionValues.compressedPixelData = pixelDataInputStream;
		compressionValues = compression.uncompressPixelData(compressionValues);
		try {
			pixelDataInputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (checksum != pixelDataInputStream.getActualChecksum()) {
			throw new ConversionException("Prüfsumme stimmt nicht überein");
		}

		// Es dürften keine weiteren Bytes mehr vorhanden sein
		if (!this.checkEndOfStream()) {
			throw new ConversionException("Weitere Daten nach dem Bild gefunden");
		}

		final InternalImage internalImage = new InternalImage();
		internalImage.setPixelData(compressionValues.uncompressedPixelData);
		return internalImage;
	}

	/**
	 * Überprüft, ob das Ende des Streams erreicht wurde. Vorsicht - hierfür wird
	 * versucht ein Byte zu lesen - sollte nicht benutzt werden, wenn weitere bytes
	 * gelesen werden sollen (Schluckt ggf. ein byte).
	 *
	 * @return true, wenn das Ende erreicht wurde und keine weiteren Bytes verfügbar
	 *         sind.
	 * @throws ConversionException
	 */
	private boolean checkEndOfStream() throws ConversionException {
		try {
			return this.in.read() < 0;
		} catch (final IOException e) {
			throw new ConversionException("EOF konnte nicht geprüft werden: " + e.getMessage(), e);
		}
	}

	private LimitInputStream getPixelDataInputStream(final BigInteger pixelDataSize) {
		this.in.setByteOrder(ByteOrder.BIG_ENDIAN);
		return new LimitInputStream(this.in, pixelDataSize);
	}
	
	private long readChecksum() throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			final long checksum = this.in.readOrderedUnsignedInt();
			return checksum;
		} catch (final IOException e) {
			throw new ConversionException("Die Prüfsumme konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private BigInteger readPixelDataSize() throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			final BigInteger pixelDataSize = this.in.readOrderedUnsignedNumber(8);
			return pixelDataSize;
		} catch (final IOException e) {
			throw new ConversionException("Die Pixeldatengröße konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private PropraCompressionType readCompressionType() throws ConversionException {
		try {
			final int compressionId = this.in.readUnsignedByte();
			final PropraCompressionType compressionType = PropraCompressionType.fromId(compressionId);

			if (compressionType == null) {
				throw new ConversionException("Das Kompremierungsverfahren ist unbekannt: " + compressionId);
			}

			return compressionType;
		} catch (final IOException e) {
			throw new ConversionException("Das Kompremierungsverfahren konnte nicht gelesen werden: " + e.getMessage(),
					e);
		}
	}

	private int readPixelResolution() throws ConversionException {
		try {
			return this.in.readUnsignedByte();
		} catch (final IOException e) {
			throw new ConversionException("Die Pixelauflösung konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private Dimension readDimension() throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);

		int width;
		int height;
		try {
			width = this.in.readOrderedUnsignedShort();
			height = this.in.readOrderedUnsignedShort();
		} catch (final IOException e) {
			throw new ConversionException("Die Bildabmessung konnte nicht gelesen werden: " + e.getMessage(), e);
		}

		return new Dimension(width, height);
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

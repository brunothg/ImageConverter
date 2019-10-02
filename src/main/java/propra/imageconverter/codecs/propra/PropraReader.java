package propra.imageconverter.codecs.propra;

import java.awt.Dimension;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.InternalImage;
import propra.imageconverter.codecs.propra.Compression.PixelCompressionValues;
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

		final CompressionType compressionType = this.readCompressionType();

		final BigInteger pixelDataSize = this.readPixelDataSize();

		final long checksum = this.readChecksum();

		final byte[] compressedPixelData = this.readCompressedPixelData(pixelDataSize);
		if (!this.checkChecksum(checksum, compressedPixelData)) {
			throw new ConversionException("Prüfsumme stimmt nicht überein");
		}

		// Es dürften keine weiteren Bytes mehr vorhanden sein
		if (!this.checkEndOfStream()) {
			throw new ConversionException("Weitere Daten nach dem Bild gefunden");
		}

		final Compression compression = compressionType.createCompressionInstance();
		PixelCompressionValues compressionValues = new PixelCompressionValues();
		compressionValues.dimension = dimension;
		compressionValues.pixelResolution = pixelResolution;
		compressionValues.compressedPixelData = compressedPixelData;
		compressionValues = compression.uncompressPixelData(compressionValues);

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

	private boolean checkChecksum(long checksum, byte[] compressedPixelData) {
		final long calculatedChecksum = PropraChecksum.calculateChecksum(compressedPixelData);

		return checksum == calculatedChecksum;
	}

	private byte[] readCompressedPixelData(BigInteger pixelDataSize) throws ConversionException {
		int pixelDataLength;
		try {
			pixelDataLength = pixelDataSize.intValueExact();
		} catch (final ArithmeticException e) {
			throw new ConversionException("Die Bildgröße ist zu groß: " + e.getMessage(), e);
		}

		final byte[] buffer = new byte[pixelDataLength];
		try {
			final int read = this.in.read(buffer);
			if (read != pixelDataLength) {
				throw new ConversionException("Pixeldaten nicht vollständig");
			}
		} catch (final IOException e) {
			throw new ConversionException("Die Pixeldaten konnten nicht gelesen werden: " + e.getMessage(), e);
		}

		return buffer;
	}

	private long readChecksum() throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			final long checksum = this.in.readOrderedUnsinedInt();
			return checksum;
		} catch (final IOException e) {
			throw new ConversionException("Die Prüfsumme konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private BigInteger readPixelDataSize() throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			final BigInteger pixelDataSize = this.in.readOrderedUnsinedNumber(8);
			return pixelDataSize;
		} catch (final IOException e) {
			throw new ConversionException("Die Pixeldatengröße konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private CompressionType readCompressionType() throws ConversionException {
		try {
			final int compressionId = this.in.readUnsignedByte();
			final CompressionType compressionType = CompressionType.fromId(compressionId);

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
			width = this.in.readOrderedUnsinedShort();
			height = this.in.readOrderedUnsinedShort();
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

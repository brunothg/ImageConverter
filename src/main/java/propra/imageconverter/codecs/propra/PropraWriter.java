package propra.imageconverter.codecs.propra;

import java.awt.Dimension;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.InternalImage;
import propra.imageconverter.codecs.propra.Compression.PixelCompressionValues;
import propra.imageconverter.utils.ByteOutputStream;

/**
 *
 * Klasse zum schreiben von Propra Bildern
 *
 * @author marvin
 *
 */
public class PropraWriter implements Closeable {

	private final ByteOutputStream out;

	public PropraWriter(OutputStream out) {
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

		this.writeFormatkennung();

		final Dimension dimension = image.getSize();
		this.writeDimension(dimension);

		final int pixelResolution = 24;
		this.writePixelResolution(pixelResolution);

		final CompressionType compressionType = CompressionType.None;
		this.writeCompressionType(compressionType);

		final BigInteger pixelDataSize = BigInteger.valueOf((pixelResolution / 8) * dimension.height * dimension.width);
		this.writePixelDataSize(pixelDataSize);

		final Compression createCompressionInstance = compressionType.createCompressionInstance();
		PixelCompressionValues compressionValues = new PixelCompressionValues();
		compressionValues.uncompressedPixelData = image.getPixelData();
		compressionValues.pixelResolution = pixelResolution;
		compressionValues.dimension = dimension;
		compressionValues = createCompressionInstance.compressPixelData(compressionValues);

		final long checksum = PropraChecksum.calculateChecksum(compressionValues.compressedPixelData);
		this.writeChecksum(checksum);

		this.writePixelData(compressionValues.compressedPixelData);
	}

	private void writePixelData(byte[] compressedPixelData) throws ConversionException {
		this.out.setByteOrder(ByteOrder.BIG_ENDIAN);
		try {
			this.out.writeOrderedBytes(compressedPixelData);
		} catch (final IOException e) {
			throw new ConversionException("Pixeldaten konnten nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeChecksum(long checksum) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsinedInt(checksum);
		} catch (final IOException e) {
			throw new ConversionException("Prüfsumme konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writePixelDataSize(BigInteger pixelDataSize) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsinedNumber(pixelDataSize, 8);
		} catch (final IOException e) {
			throw new ConversionException("Pixeldatengröße konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeCompressionType(CompressionType compressionType) throws ConversionException {
		try {
			this.out.writeUnsignedByte(compressionType.getId());
		} catch (final IOException e) {
			throw new ConversionException("Kompremierungsverfahren konnte nicht geschrieben werden: " + e.getMessage(),
					e);
		}
	}

	private void writePixelResolution(int resolutionBits) throws ConversionException {
		try {
			this.out.writeUnsignedByte(resolutionBits);
		} catch (final IOException e) {
			throw new ConversionException("Pixelauflösung konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeDimension(Dimension size) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsinedShort(size.width);
			this.out.writeOrderedUnsinedShort(size.height);
		} catch (final IOException e) {
			throw new ConversionException("Dimension konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeFormatkennung() throws ConversionException {
		this.out.setByteOrder(ByteOrder.BIG_ENDIAN);
		try {
			this.out.writeOrderedString(PropraCodec.FILE_IDENTIFIER, StandardCharsets.UTF_8, 10);
		} catch (final IOException e) {
			throw new ConversionException("Formatkennung konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	@Override
	public void close() throws IOException {
		this.out.close();
	}
}

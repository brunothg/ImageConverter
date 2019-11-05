package propra.imageconverter.imagecodecs.propra;

import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.imagecodecs.propra.PropraChecksum.PropraChecksumOutputStream;
import propra.imageconverter.imagecodecs.propra.compression.PropraCompression;
import propra.imageconverter.imagecodecs.propra.compression.PropraCompression.PropraPixelEncodeValues;
import propra.imageconverter.utils.ByteOutputStream;
import propra.imageconverter.utils.CounterOutputStream;

/**
 *
 * Klasse zum schreiben von Propra Bildern
 *
 * @author marvin
 *
 */
public class PropraWriter implements Closeable {

	private final ByteOutputStream out;

	PropraCompressionType compressionType = PropraCompressionType.None;

	public PropraWriter(final OutputStream out) {
		this.out = new ByteOutputStream(new BufferedOutputStream(Objects.requireNonNull(out, "out"), 1024));
	}

	/**
	 * Schreibt ein Bild. Ein zweiter Aufruf der Methode wird zu unerwünschten
	 * Ergebnissen führen.
	 *
	 * @param image Das Bild zum Schreiben
	 * @throws ConversionException Wenn beim Schreiben des Bildes ein Fehler
	 *                             auftritt
	 */
	public void writeImage(final InternalImage image) throws ConversionException {
		Objects.requireNonNull(image, "image");

		this.writeFormatkennung();

		final Dimension dimension = image.getSize();
		this.writeDimension(dimension);

		final int pixelResolution = 24;
		this.writePixelResolution(pixelResolution);

		this.writeCompressionType(this.compressionType);

		try {
			// Versuche Pixeldaten in temp Datei zwischenzuspeichern, die Checksumme zu
			// berechnen/schreiben und dann die Pixeldaten aus der temp Datei zu kopieren
			final Path pixelDataTempFile = Files.createTempFile("propra", "imagedata");
			if ((pixelDataTempFile == null) || !Files.isReadable(pixelDataTempFile)
					|| !Files.isWritable(pixelDataTempFile)) {
				Files.deleteIfExists(pixelDataTempFile);
				throw new IOException("Not readable and writeable");
			}

			try {
				final CounterOutputStream counterOutputStream = new CounterOutputStream(
						Files.newOutputStream(pixelDataTempFile));
				final PropraChecksumOutputStream propraChecksumOutputStream = new PropraChecksum.PropraChecksumOutputStream(
						counterOutputStream);
				final OutputStream pixelDataOutputStream = propraChecksumOutputStream;

				final PropraCompression createCompressionInstance = this.compressionType.createCompressionInstance();
				PropraPixelEncodeValues compressionValues = new PropraPixelEncodeValues();
				compressionValues.uncompressedPixelData = image;
				compressionValues.pixelResolution = pixelResolution;
				compressionValues.dimension = dimension;
				compressionValues.compressedPixelData = pixelDataOutputStream;
				compressionValues = createCompressionInstance.compressPixelData(compressionValues);
				pixelDataOutputStream.close();

				final BigInteger pixelDataSize = counterOutputStream.getActualCounter();
				this.writePixelDataSize(pixelDataSize);

				final long checksum = propraChecksumOutputStream.getActualChecksum();
				this.writeChecksum(checksum);

				final InputStream pixelDataInputStream = Files.newInputStream(pixelDataTempFile);
				this.writePixelData(pixelDataInputStream);
				pixelDataInputStream.close();

				Files.deleteIfExists(pixelDataTempFile);
			} catch (final Exception e) {
				throw new ConversionException("Dateisystem-Fehler: " + e.getMessage(), e);
			}
		} catch (final IOException e) {
			// Fallback auf alte in memory Methode
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			final CounterOutputStream counterOutputStream = new CounterOutputStream(byteArrayOutputStream);
			final PropraChecksumOutputStream propraChecksumOutputStream = new PropraChecksum.PropraChecksumOutputStream(
					counterOutputStream);
			final OutputStream pixelDataOutputStream = propraChecksumOutputStream;

			final PropraCompression createCompressionInstance = this.compressionType.createCompressionInstance();
			PropraPixelEncodeValues compressionValues = new PropraPixelEncodeValues();
			compressionValues.uncompressedPixelData = image;
			compressionValues.pixelResolution = pixelResolution;
			compressionValues.dimension = dimension;
			compressionValues.compressedPixelData = pixelDataOutputStream;
			compressionValues = createCompressionInstance.compressPixelData(compressionValues);
			try {
				pixelDataOutputStream.close();
			} catch (final IOException e1) {
			}

			final BigInteger pixelDataSize = BigInteger
					.valueOf((pixelResolution / 8) * dimension.height * dimension.width);
			this.writePixelDataSize(pixelDataSize);

			final long checksum = propraChecksumOutputStream.getActualChecksum();
			this.writeChecksum(checksum);

			this.writePixelData(byteArrayOutputStream);
		}

	}

	private void writePixelData(final InputStream pixelDataInputStream) throws ConversionException {
		this.out.setByteOrder(ByteOrder.BIG_ENDIAN);
		try {
			final byte[] buffer = new byte[1024];
			int read;
			while ((read = pixelDataInputStream.read(buffer, 0, 1024)) >= 0) {
				this.out.write(buffer, 0, read);
			}
		} catch (final IOException e) {
			throw new ConversionException("Pixeldaten konnten nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writePixelData(final ByteArrayOutputStream pixelDataByteArrayOutputStream) throws ConversionException {
		this.out.setByteOrder(ByteOrder.BIG_ENDIAN);
		try {
			pixelDataByteArrayOutputStream.writeTo(this.out);
		} catch (final IOException e) {
			throw new ConversionException("Pixeldaten konnten nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeChecksum(final long checksum) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedInt(checksum);
		} catch (final IOException e) {
			throw new ConversionException("Prüfsumme konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writePixelDataSize(final BigInteger pixelDataSize) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedNumber(pixelDataSize, 8);
		} catch (final IOException e) {
			throw new ConversionException("Pixeldatengröße konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeCompressionType(final PropraCompressionType compressionType) throws ConversionException {
		try {
			this.out.writeUnsignedByte(compressionType.getId());
		} catch (final IOException e) {
			throw new ConversionException("Kompremierungsverfahren konnte nicht geschrieben werden: " + e.getMessage(),
					e);
		}
	}

	private void writePixelResolution(final int resolutionBits) throws ConversionException {
		try {
			this.out.writeUnsignedByte(resolutionBits);
		} catch (final IOException e) {
			throw new ConversionException("Pixelauflösung konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeDimension(final Dimension size) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedShort(size.width);
			this.out.writeOrderedUnsignedShort(size.height);
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

	public void setCompressionType(final PropraCompressionType compressionType) {
		Objects.requireNonNull(compressionType, "Kompressionstyp ist erforderlich");
		this.compressionType = compressionType;
	}

	@Override
	public void close() throws IOException {
		this.out.close();
	}
}

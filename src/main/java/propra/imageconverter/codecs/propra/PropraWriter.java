package propra.imageconverter.codecs.propra;

import java.awt.Dimension;
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

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.InternalImage;
import propra.imageconverter.codecs.propra.PropraChecksum.PropraChecksumOutputStream;
import propra.imageconverter.codecs.propra.PropraCompression.PropraPixelEncodeValues;
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

		final PropraCompressionType compressionType = PropraCompressionType.None;
		this.writeCompressionType(compressionType);

		final BigInteger pixelDataSize = BigInteger.valueOf((pixelResolution / 8) * dimension.height * dimension.width);
		this.writePixelDataSize(pixelDataSize);

		try {
			// Versuche Pixeldaten in temp Datei zwischenzuspeichern, die Checksumme zu
			// berechnen/schreiben und dann die Pixeldaten aus der temp Datei zu kopieren
			Path pixelDataTempFile = Files.createTempFile("propra", "imagedata");
			if (!Files.isReadable(pixelDataTempFile) || !Files.isWritable(pixelDataTempFile)) {
				Files.delete(pixelDataTempFile);
				throw new IOException("Not readable nad writeable");
			}

			try {
				PropraChecksumOutputStream pixelDataOutputStream = new PropraChecksum.PropraChecksumOutputStream(
						Files.newOutputStream(pixelDataTempFile));
				final PropraCompression createCompressionInstance = compressionType.createCompressionInstance();
				PropraPixelEncodeValues compressionValues = new PropraPixelEncodeValues();
				compressionValues.uncompressedPixelData = image.getPixelData();
				compressionValues.pixelResolution = pixelResolution;
				compressionValues.dimension = dimension;
				compressionValues.compressedPixelData = pixelDataOutputStream;
				compressionValues = createCompressionInstance.compressPixelData(compressionValues);
				pixelDataOutputStream.close();

				final long checksum = pixelDataOutputStream.getActualChecksum();
				this.writeChecksum(checksum);

				InputStream pixelDataInputStream = Files.newInputStream(pixelDataTempFile);
				this.writePixelData(pixelDataInputStream);
				pixelDataInputStream.close();

				Files.deleteIfExists(pixelDataTempFile);
			} catch (Exception e) {
				throw new ConversionException("Dateisystem-Fehler: " + e.getMessage(), e);
			}
		} catch (IOException e) {
			// Fallback auf alte in memory Methode
			final ByteArrayOutputStream pixelDataOutputStream = new ByteArrayOutputStream(
					pixelDataSize.intValueExact());

			final PropraCompression createCompressionInstance = compressionType.createCompressionInstance();
			PropraPixelEncodeValues compressionValues = new PropraPixelEncodeValues();
			compressionValues.uncompressedPixelData = image.getPixelData();
			compressionValues.pixelResolution = pixelResolution;
			compressionValues.dimension = dimension;
			compressionValues.compressedPixelData = pixelDataOutputStream;
			compressionValues = createCompressionInstance.compressPixelData(compressionValues);

			@SuppressWarnings("deprecation")
			final long checksum = PropraChecksum.calculateChecksum(pixelDataOutputStream.toByteArray());
			this.writeChecksum(checksum);

			this.writePixelData(pixelDataOutputStream.toByteArray());
		}

	}

	private void writePixelData(InputStream pixelDataInputStream) throws ConversionException {
		this.out.setByteOrder(ByteOrder.BIG_ENDIAN);
		try {
			byte[] buffer = new byte[1024];
			int read;
			while ((read = pixelDataInputStream.read(buffer, 0, 1024)) >= 0) {
				out.write(buffer, 0, read);
			}
		} catch (IOException e) {
			throw new ConversionException("Pixeldaten konnten nicht geschrieben werden: " + e.getMessage(), e);
		}
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
			this.out.writeOrderedUnsignedInt(checksum);
		} catch (final IOException e) {
			throw new ConversionException("Prüfsumme konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writePixelDataSize(BigInteger pixelDataSize) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedNumber(pixelDataSize, 8);
		} catch (final IOException e) {
			throw new ConversionException("Pixeldatengröße konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeCompressionType(PropraCompressionType compressionType) throws ConversionException {
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

	@Override
	public void close() throws IOException {
		this.out.close();
	}
}

package propra.imageconverter.codecs.propra;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.InternalImage;
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
//		final String formatkennung = this.readFormatkennung();
//		if (!formatkennung.equals(PropraCodec.FILE_IDENTIFIER)) {
//			throw new ConversionException("Formatkennung fehlerhaft: " + formatkennung);
//		}
//
//		final Dimension dimension = this.readDimension();
//
//		final int pixelResolution = this.readPixelResolution();
//		if (!Arrays.stream(PropraCodec.PIXEL_RESOLUTIONS).anyMatch(Integer.valueOf(pixelResolution)::equals)) {
//			throw new ConversionException("Pixelauflösung nicht unterstützt: " + pixelResolution);
//		}
//
//		final CompressionType compressionType = this.readCompressionType();
//
//		final BigInteger pixelDataSize = this.readPixelDataSize();
//
//		final long checksum = this.readChecksum();
//
//		final byte[] compressedPixelData = this.readCompressedPixelData(pixelDataSize);
//		if (!this.checkChecksum(checksum, compressedPixelData)) {
//			throw new ConversionException("Prüfsumme stimmt nicht überein");
//		}
//
//		final Compression compression = compressionType.createCompressionInstance();
//		PixelCompressionValues compressionValues = new PixelCompressionValues();
//		compressionValues.dimension = dimension;
//		compressionValues.pixelResolution = pixelResolution;
//		compressionValues.compressedPixelData = compressedPixelData;
//		compressionValues = compression.uncompressPixelData(compressionValues);
//
//		final InternalImage internalImage = new InternalImage();
//		internalImage.setPixelData(compressionValues.uncompressedPixelData);
	}

	private boolean checkChecksum(long checksum, byte[] compressedPixelData) {
		final long calculatedChecksum = PropraChecksum.calculateChecksum(compressedPixelData);

		return checksum == calculatedChecksum;
	}

	@Override
	public void close() throws IOException {
		this.out.close();
	}
}

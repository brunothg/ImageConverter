package propra.imageconverter.codecs.tga;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Objects;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.InternalImage;
import propra.imageconverter.codecs.tga.TgaCompression.PixelCompressionValues;
import propra.imageconverter.codecs.tga.TgaImageAttributes.HorizontalOrigin;
import propra.imageconverter.codecs.tga.TgaImageAttributes.VerticalOrigin;
import propra.imageconverter.utils.ByteOutputStream;

/**
 * Klasse zum Schreiben von Tga-Bildern
 *
 * @author marvin
 *
 */
public class TgaWriter implements Closeable {

	private final ByteOutputStream out;

	public TgaWriter(OutputStream out) {
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
		// TODO writeImage

		this.writeImageIdLength(0);

		this.writeColorMapType(TgaColorMapType.None);

		final TgaImageType imageType = TgaImageType.Rgb;
		this.writeImageType(imageType);

		this.writeColorMapStart(0);

		this.writeColorMapSize(0);

		this.writeColorMapEntrySize(0);

		final Dimension dimension = image.getSize();
		final Point origin = new Point(0, dimension.height);
		this.writeOrigin(origin);
		this.writeImageDimension(dimension);

		final int pixelResolution = 24;
		this.writePixelResolution(pixelResolution);

		final TgaImageAttributes imageAttributes = new TgaImageAttributes();
		imageAttributes.setAttributeBitCount(0);
		imageAttributes.setHorizontalOrigin(HorizontalOrigin.Left);
		imageAttributes.setVerticalOrigin(VerticalOrigin.Top);
		this.writeImageAttributes(imageAttributes);

		final TgaCompression compression = imageType.createCompressionInstance();
		PixelCompressionValues compressionValues = new PixelCompressionValues();
		compressionValues.dimension = dimension;
		compressionValues.pixelResolution = pixelResolution;
		compressionValues.origin = origin;
		compressionValues.imageAttributes = imageAttributes;
		compressionValues.uncompressedPixelData = image.getPixelData();
		compressionValues = compression.compressPixelData(compressionValues);

		final byte[] compressedPixelData = compressionValues.compressedPixelData;
		this.writePixelData(compressedPixelData);
	}

	private void writePixelData(byte[] compressedPixelData) throws ConversionException {
		this.out.setByteOrder(ByteOrder.BIG_ENDIAN);
		try {
			this.out.writeOrderedBytes(compressedPixelData);
		} catch (final IOException e) {
			throw new ConversionException("Pixel-Daten konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeImageAttributes(TgaImageAttributes imageAttributes) throws ConversionException {
		try {
			this.out.writeUnsignedByte(TgaImageAttributes.toByte(imageAttributes));
		} catch (final IOException e) {
			throw new ConversionException("Bild-Attribut konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writePixelResolution(int pixelResolution) throws ConversionException {
		try {
			this.out.writeUnsignedByte(pixelResolution);
		} catch (final IOException e) {
			throw new ConversionException("Pixelauflösung konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeImageDimension(Dimension dimension) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedShort(dimension.width);
			this.out.writeOrderedUnsignedShort(dimension.height);
		} catch (final IOException e) {
			throw new ConversionException("Bildabmessung konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeOrigin(Point origin) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedShort(origin.x);
			this.out.writeOrderedUnsignedShort(origin.y);
		} catch (final IOException e) {
			throw new ConversionException("Nullpunktkoordinaten konnten nicht geschrieben werden: " + e.getMessage(),
					e);
		}
	}

	private void writeColorMapEntrySize(int colorMapEntrySize) throws ConversionException {
		try {
			this.out.writeUnsignedByte(colorMapEntrySize);
		} catch (final IOException e) {
			throw new ConversionException(
					"Farbpaletteneintragsgröße konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeColorMapSize(int colorMapSize) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedShort(colorMapSize);
		} catch (final IOException e) {
			throw new ConversionException("Farbpalettengröße konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeColorMapStart(int colorMapStart) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedShort(colorMapStart);
		} catch (final IOException e) {
			throw new ConversionException("Farbpalettenbeginn konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeImageType(TgaImageType imageType) throws ConversionException {
		try {
			this.out.writeUnsignedByte(imageType.getId());
		} catch (final IOException e) {
			throw new ConversionException("Bildtyp konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeColorMapType(TgaColorMapType colorMapType) throws ConversionException {
		try {
			this.out.writeUnsignedByte(colorMapType.getId());
		} catch (final IOException e) {
			throw new ConversionException("Farbpalettentyp konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeImageIdLength(int imageIdLength) throws ConversionException {
		try {
			this.out.writeUnsignedByte(imageIdLength);
		} catch (final IOException e) {
			throw new ConversionException("BildId-Länge konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	@Override
	public void close() throws IOException {
		this.out.close();
	}

}

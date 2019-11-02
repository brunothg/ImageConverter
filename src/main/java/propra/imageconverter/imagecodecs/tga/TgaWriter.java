package propra.imageconverter.imagecodecs.tga;

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Objects;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.imagecodecs.tga.TgaImageAttributes.HorizontalOrigin;
import propra.imageconverter.imagecodecs.tga.TgaImageAttributes.VerticalOrigin;
import propra.imageconverter.imagecodecs.tga.compression.TgaCompression;
import propra.imageconverter.imagecodecs.tga.compression.TgaCompression.TgaPixelEncodeValues;
import propra.imageconverter.utils.ByteOutputStream;

/**
 * Klasse zum Schreiben von Tga-Bildern
 *
 * @author marvin
 *
 */
public class TgaWriter implements Closeable {

	private final ByteOutputStream out;
	TgaImageType imageType = TgaImageType.Rgb;

	public TgaWriter(final OutputStream out) {
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

		this.writeImageIdLength(0);

		this.writeColorMapType(TgaColorMapType.None);

		this.writeImageType(this.imageType);

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

		final TgaCompression compression = this.imageType.createCompressionInstance();
		TgaPixelEncodeValues compressionValues = new TgaPixelEncodeValues();
		compressionValues.dimension = dimension;
		compressionValues.pixelResolution = pixelResolution;
		compressionValues.origin = origin;
		compressionValues.imageAttributes = imageAttributes;
		compressionValues.uncompressedPixelData = image;
		compressionValues.compressedPixelData = this.getPixelDataOutputStream();
		compressionValues = compression.compressPixelData(compressionValues);

	}

	private OutputStream getPixelDataOutputStream() {
		this.out.setByteOrder(ByteOrder.BIG_ENDIAN);
		return this.out;
	}

	private void writeImageAttributes(final TgaImageAttributes imageAttributes) throws ConversionException {
		try {
			this.out.writeUnsignedByte(TgaImageAttributes.toByte(imageAttributes));
		} catch (final IOException e) {
			throw new ConversionException("Bild-Attribut konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writePixelResolution(final int pixelResolution) throws ConversionException {
		try {
			this.out.writeUnsignedByte(pixelResolution);
		} catch (final IOException e) {
			throw new ConversionException("Pixelauflösung konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeImageDimension(final Dimension dimension) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedShort(dimension.width);
			this.out.writeOrderedUnsignedShort(dimension.height);
		} catch (final IOException e) {
			throw new ConversionException("Bildabmessung konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeOrigin(final Point origin) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedShort(origin.x);
			this.out.writeOrderedUnsignedShort(origin.y);
		} catch (final IOException e) {
			throw new ConversionException("Nullpunktkoordinaten konnten nicht geschrieben werden: " + e.getMessage(),
					e);
		}
	}

	private void writeColorMapEntrySize(final int colorMapEntrySize) throws ConversionException {
		try {
			this.out.writeUnsignedByte(colorMapEntrySize);
		} catch (final IOException e) {
			throw new ConversionException(
					"Farbpaletteneintragsgröße konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeColorMapSize(final int colorMapSize) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedShort(colorMapSize);
		} catch (final IOException e) {
			throw new ConversionException("Farbpalettengröße konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeColorMapStart(final int colorMapStart) throws ConversionException {
		this.out.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			this.out.writeOrderedUnsignedShort(colorMapStart);
		} catch (final IOException e) {
			throw new ConversionException("Farbpalettenbeginn konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeImageType(final TgaImageType imageType) throws ConversionException {
		try {
			this.out.writeUnsignedByte(imageType.getId());
		} catch (final IOException e) {
			throw new ConversionException("Bildtyp konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeColorMapType(final TgaColorMapType colorMapType) throws ConversionException {
		try {
			this.out.writeUnsignedByte(colorMapType.getId());
		} catch (final IOException e) {
			throw new ConversionException("Farbpalettentyp konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	private void writeImageIdLength(final int imageIdLength) throws ConversionException {
		try {
			this.out.writeUnsignedByte(imageIdLength);
		} catch (final IOException e) {
			throw new ConversionException("BildId-Länge konnte nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	public void setImageType(final TgaImageType imageType) {
		Objects.requireNonNull(imageType, "Bildtyp muss angegeben werden");
		this.imageType = imageType;
	}

	@Override
	public void close() throws IOException {
		this.out.close();
	}

}

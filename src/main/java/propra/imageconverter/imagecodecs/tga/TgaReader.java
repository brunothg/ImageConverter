package propra.imageconverter.imagecodecs.tga;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.imagecodecs.tga.TgaCompression.TgaPixelDecodeValues;
import propra.imageconverter.utils.ByteInputStream;
import propra.imageconverter.utils.LimitInputStream;;

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

		final int imageIdLength = this.readImageIdLength();

		final TgaColorMapType colormapType = this.readColormapType();

		final TgaImageType imageType = this.readImageType();

		this.readColorMapStart();

		final int colorMapLength = this.readColorMapLength();
		if ((colormapType == TgaColorMapType.None) && (colorMapLength != 0)) {
			throw new ConversionException("Nicht leere Farbpalette trotz Farbpalettentyp None");
		}

		this.readColorMapEntrySize();

		final Point origin = this.readOrigin();
		final Dimension dimension = this.readImageDimension();
		if ((dimension.height <= 0) || (dimension.width <= 0)) {
			throw new ConversionException("Nullgröße nicht erlaubt: " + dimension.toString());
		}

		// Es werden lesend alle Origins unterstützt
		// if (!((origin.x == 0) && (origin.y == dimension.height))) {
		// throw new ConversionException("Origin nicht unterstützt: " + origin);
		// }

		final int pixelResolution = this.readPixelResolution();
		if (!Arrays.stream(TgaCodec.PIXEL_RESOLUTIONS).anyMatch(Integer.valueOf(pixelResolution)::equals)) {
			throw new ConversionException("Pixelauflösung nicht unterstützt: " + pixelResolution);
		}

		final TgaImageAttributes imageAttributes = this.readImageAttributes();

		this.readImageId(imageIdLength);

		if (colormapType != TgaColorMapType.None) {
			throw new ConversionException("Farbpaletten nicht unterstützt");
			// XXX readColorMapBytes();
		}

		final BigInteger pixelDataSize = BigDecimal.valueOf(dimension.width)
				.multiply(BigDecimal.valueOf(dimension.height)).multiply(BigDecimal.valueOf(pixelResolution))
				.divide(BigDecimal.valueOf(8)).setScale(0, RoundingMode.CEILING).toBigInteger();

		final TgaCompression compression = imageType.createCompressionInstance();
		TgaPixelDecodeValues compressionValues = new TgaPixelDecodeValues();
		compressionValues.dimension = dimension;
		compressionValues.pixelResolution = pixelResolution;
		compressionValues.origin = origin;
		compressionValues.imageAttributes = imageAttributes;
		compressionValues.compressedPixelData = getPixelDataInputStream(pixelDataSize);
		compressionValues = compression.uncompressPixelData(compressionValues);

		final InternalImage internalImage = new InternalImage();
		internalImage.setPixelData(compressionValues.uncompressedPixelData);
		return internalImage;
	}

	private LimitInputStream getPixelDataInputStream(final BigInteger pixelDataSize) {
		this.in.setByteOrder(ByteOrder.BIG_ENDIAN);
		return new LimitInputStream(this.in, pixelDataSize);
	}

	private void readImageId(int imageIdLength) throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			final byte[] imageId = this.in.readOrderedBytes(imageIdLength);
			if (imageId.length != imageIdLength) {
				throw new ConversionException("BildId nicht vollständig");
			}
		} catch (final IOException e) {
			throw new ConversionException("Die BildId konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private TgaImageAttributes readImageAttributes() throws ConversionException {
		try {
			final int imageAttributeByte = this.in.readUnsignedByte();
			return TgaImageAttributes.fromByte(imageAttributeByte);
		} catch (final IOException e) {
			throw new ConversionException("Das Bild-Attribut konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private int readPixelResolution() throws ConversionException {
		try {
			return this.in.readUnsignedByte();
		} catch (final IOException e) {
			throw new ConversionException("Die Pixelauflösung konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private Dimension readImageDimension() throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			final int width = this.in.readOrderedUnsignedShort();
			final int height = this.in.readOrderedUnsignedShort();

			return new Dimension(width, height);
		} catch (final IOException e) {
			throw new ConversionException("Die Bildabmessung konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private Point readOrigin() throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			final int xOrigin = this.in.readOrderedUnsignedShort();
			final int yOrigin = this.in.readOrderedUnsignedShort();

			return new Point(xOrigin, yOrigin);
		} catch (final IOException e) {
			throw new ConversionException("Die Nullpunktkoordinaten konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private int readColorMapEntrySize() throws ConversionException {
		try {
			return this.in.readUnsignedByte();
		} catch (final IOException e) {
			throw new ConversionException(
					"Die Farbpaletteneintragsgröße konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private int readColorMapLength() throws ConversionException {
		this.in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		try {
			return this.in.readOrderedUnsignedShort();
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

	private TgaImageType readImageType() throws ConversionException {
		try {
			final int imageTypeId = this.in.readUnsignedByte();
			final TgaImageType imageType = TgaImageType.fromId(imageTypeId);

			if (imageType == null) {
				throw new ConversionException("Der Bildtyp ist unbekannt: " + imageTypeId);
			}

			return imageType;
		} catch (final IOException e) {
			throw new ConversionException("Der Bildtyp konnte nicht gelesen werden: " + e.getMessage(), e);
		}
	}

	private TgaColorMapType readColormapType() throws ConversionException {
		try {
			final int colorMapTypeId = this.in.readUnsignedByte();
			final TgaColorMapType colorMapType = TgaColorMapType.fromId(colorMapTypeId);

			if (colorMapType == null) {
				throw new ConversionException("Der Farbpalettentyp ist unbekannt: " + colorMapTypeId);
			}

			return colorMapType;
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

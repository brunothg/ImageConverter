package propra.imageconverter.imagecodecs.tga.compression;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.imagecodecs.InternalMemoryImage;
import propra.imageconverter.imagecodecs.tga.TgaImageAttributes.HorizontalOrigin;
import propra.imageconverter.imagecodecs.tga.TgaImageAttributes.VerticalOrigin;

/**
 * Liest/Schreibt unkomprimierte RGB Daten
 *
 * @author marvin
 *
 */
public class TgaRgbCompression extends TgaCompression {

	@Override
	public TgaPixelDecodeValues uncompressPixelData(final TgaPixelDecodeValues values) throws ConversionException {
		final InternalImage image = new InternalMemoryImage(values.dimension);

		final InputStream in = values.compressedPixelData;

		this.pixelLoop(values, (point) -> {
			try {
				final byte[] pixel = new byte[3];
				final int read = in.readNBytes(pixel, 0, pixel.length);
				if (read != pixel.length) {
					return new ConversionException("Pixel konnte nicht gelesen werden: " + point + " : Fehlende Daten");
				}

				final int b = Byte.toUnsignedInt(pixel[0]);
				final int g = Byte.toUnsignedInt(pixel[1]);
				final int r = Byte.toUnsignedInt(pixel[2]);

				image.setPixel(point, new Color(r, g, b));
			} catch (final IOException e) {
				return new ConversionException("Pixel konnte nicht gelesen werden: " + point + " : " + e.getMessage(),
						e);
			}
			return null;
		});

		values.uncompressedPixelData = image;
		return values;
	}

	@Override
	public TgaPixelEncodeValues compressPixelData(final TgaPixelEncodeValues values) throws ConversionException {
		final OutputStream out = values.compressedPixelData;
		this.pixelLoop(values, (point) -> {
			final Color color = values.uncompressedPixelData.getPixel(point);

			try {
				out.write(new byte[] { (byte) color.getBlue(), (byte) color.getGreen(), (byte) color.getRed() });
			} catch (final IOException e) {
				return new ConversionException("Pixeldaten können nicht geschrieben werden: " + e.getMessage(), e);
			}

			return null;
		});

		return values;
	}

	/**
	 * Hilfsfunktion, um die verschiedenen Origins zu handhaben
	 *
	 * @param values
	 * @param f      Funktion, die pro Pixel aufgerufen wird
	 * @throws ConversionException
	 */
	private void pixelLoop(final TgaPixelCompressionValues values, final Function<Point, Exception> f)
			throws ConversionException {
		final Exception e = this.verticalPixelLoop(values, (y) -> {
			final Exception eV = this.horizontalPixelLoop(values, (x) -> {
				final Exception eH = f.apply(new Point(x, y));
				return eH;
			});
			return eV;
		});

		if (e != null) {
			if (e instanceof ConversionException) {
				throw (ConversionException) e;
			} else {
				throw new ConversionException("PixelLoop Fehler: " + e.getMessage(), e);
			}
		}
	}

	private Exception verticalPixelLoop(final TgaPixelCompressionValues values, final Function<Integer, Exception> f) {
		final VerticalOrigin verticalOrigin = values.imageAttributes.getVerticalOrigin();
		switch (verticalOrigin) {
		case Bottom:
			for (int y = values.dimension.height - 1; y >= 0; y--) {
				final Exception e = f.apply(y);
				if (e != null) {
					return e;
				}
			}
			break;
		case Top:
			for (int y = 0; y < values.dimension.height; y++) {
				final Exception e = f.apply(y);
				if (e != null) {
					return e;
				}
			}
			break;
		default:
			throw new RuntimeException("VerticalOrigin Typ nicht unterstützt: " + verticalOrigin);

		}

		return null;
	}

	private Exception horizontalPixelLoop(final TgaPixelCompressionValues values,
			final Function<Integer, Exception> f) {
		final HorizontalOrigin horizontalOrigin = values.imageAttributes.getHorizontalOrigin();
		switch (horizontalOrigin) {
		case Left:
			for (int x = 0; x < values.dimension.width; x++) {
				final Exception e = f.apply(x);
				if (e != null) {
					return e;
				}
			}
			break;
		case Right:
			for (int x = values.dimension.width - 1; x >= 0; x--) {
				final Exception e = f.apply(x);
				if (e != null) {
					return e;
				}
			}
			break;
		default:
			return new ConversionException("HorizontalOrigin Typ nicht unterstützt: " + horizontalOrigin);
		}

		return null;
	}

}

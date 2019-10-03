package propra.imageconverter.codecs.tga;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.tga.TgaImageAttributes.HorizontalOrigin;
import propra.imageconverter.codecs.tga.TgaImageAttributes.VerticalOrigin;

/**
 * Liest/Schreibt unkomprimierte RGB Daten
 *
 * @author marvin
 *
 */
public class TgaRgbCompression extends TgaCompression {

	@Override
	public PixelCompressionValues uncompressPixelData(PixelCompressionValues values) throws ConversionException {
		final BufferedImage image = new BufferedImage(values.dimension.width, values.dimension.height,
				BufferedImage.TYPE_INT_RGB);

		final ByteArrayInputStream in = new ByteArrayInputStream(values.compressedPixelData);

		this.pixelLoop(values, (point) -> {
			final int b = in.read();
			final int g = in.read();
			final int r = in.read();

			image.setRGB(point.x, point.y, new Color(r, g, b).getRGB());
			return null;
		});

		try {
			in.close();
		} catch (final IOException e) {
		}

		values.uncompressedPixelData = image;
		return values;
	}

	@Override
	public PixelCompressionValues compressPixelData(PixelCompressionValues values) throws ConversionException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream(
				values.dimension.width * values.dimension.height * 3);
	
		this.pixelLoop(values, (point) -> {
			final int rgb = values.uncompressedPixelData.getRGB(point.x, point.y);
			final Color color = new Color(rgb);
	
			try {
				out.write(new byte[] { (byte) color.getBlue(), (byte) color.getGreen(), (byte) color.getRed() });
			} catch (final IOException e) {
				return new ConversionException("Pixeldaten können nicht geschrieben werden: " + e.getMessage(), e);
			}
	
			return null;
		});
	
		try {
			out.close();
		} catch (final IOException e) {
		}
	
		values.compressedPixelData = out.toByteArray();
		return values;
	}

	/**
	 * Hilfsfunktion, um die verschiedenen Origins zu handhaben
	 *
	 * @param values
	 * @param f      Funktion, die pro Pixel aufgerufen wird
	 * @throws ConversionException
	 */
	private void pixelLoop(PixelCompressionValues values, Function<Point, Exception> f) throws ConversionException {
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

	private Exception verticalPixelLoop(PixelCompressionValues values, Function<Integer, Exception> f) {
		final VerticalOrigin verticalOrigin = values.imageAttributes.getVerticalOrigin();
		switch (verticalOrigin) {
		case Bottom:
			for (int y = values.dimension.height - values.origin.y - 1; y >= 0; y--) {
				final Exception e = f.apply(y);
				if (e != null) {
					return e;
				}
			}
			break;
		case Top:
			for (int y = 0 + (values.dimension.height - values.origin.y); y < values.dimension.height; y++) {
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

	private Exception horizontalPixelLoop(PixelCompressionValues values, Function<Integer, Exception> f) {
		final HorizontalOrigin horizontalOrigin = values.imageAttributes.getHorizontalOrigin();
		switch (horizontalOrigin) {
		case Left:
			for (int x = 0 + values.origin.x; x < values.dimension.width; x++) {
				final Exception e = f.apply(x);
				if (e != null) {
					return e;
				}
			}
			break;
		case Right:
			for (int x = values.dimension.width - (values.dimension.width - values.origin.x) - 1; x >= 0; x--) {
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

package propra.imageconverter.codecs.tga;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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

	/**
	 * Hilfsfunktion, um die verschiedenen Origins zu handhaben
	 *
	 * @param values
	 * @param f      Funktion, die pro Pixel aufgerufen wird
	 * @throws ConversionException
	 */
	private void pixelLoop(PixelCompressionValues values, Function<Point, Void> f) throws ConversionException {
		this.verticalPixelLoop(values, (y) -> {
			this.horizontalPixelLoop(values, (x) -> {
				f.apply(new Point(x, y));
				return null;
			});
			return null;
		});
	}

	private void verticalPixelLoop(PixelCompressionValues values, Function<Integer, Void> f) {
		final VerticalOrigin verticalOrigin = values.imageAttributes.getVerticalOrigin();
		switch (verticalOrigin) {
		case Bottom:
			for (int y = values.dimension.height - values.origin.y - 1; y >= 0; y--) {
				f.apply(y);
			}
			break;
		case Top:
			for (int y = 0 + (values.dimension.height - values.origin.y); y < values.dimension.height; y++) {
				f.apply(y);
			}
			break;
		default:
			throw new RuntimeException("VerticalOrigin Typ nicht unterstützt: " + verticalOrigin);

		}
	}

	private void horizontalPixelLoop(PixelCompressionValues values, Function<Integer, Void> f) {
		final HorizontalOrigin horizontalOrigin = values.imageAttributes.getHorizontalOrigin();
		switch (horizontalOrigin) {
		case Left:
			for (int x = 0 + values.origin.x; x < values.dimension.width; x++) {
				f.apply(x);
			}
			break;
		case Right:
			for (int x = values.dimension.width - (values.dimension.width - values.origin.x) - 1; x >= 0; x--) {
				f.apply(x);
			}
			break;
		default:
			throw new RuntimeException("HorizontalOrigin Typ nicht unterstützt: " + horizontalOrigin);
		}
	}

	@Override
	public PixelCompressionValues compressPixelData(PixelCompressionValues values) throws ConversionException {
		// TODO compressPixelData
		values.compressedPixelData = new byte[0];
		return values;
	}

}

package propra.imageconverter.codecs.propra;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import propra.imageconverter.codecs.ConversionException;

/**
 * Liest/Schreibt Pixeldaten ohne Kompremierung
 *
 * @author marvin
 *
 */
public class NoCompression extends Compression {

	@Override
	public PixelCompressionValues uncompressPixelData(PixelCompressionValues values) {
		final BufferedImage image = new BufferedImage(values.dimension.width, values.dimension.height,
				BufferedImage.TYPE_INT_RGB);

		final ByteArrayInputStream in = new ByteArrayInputStream(values.compressedPixelData);
		for (int y = 0; y < values.dimension.height; y++) {
			for (int x = 0; x < values.dimension.width; x++) {
				final int b = in.read();
				final int g = in.read();
				final int r = in.read();

				image.setRGB(x, y, new Color(r, g, b).getRGB());
			}
		}
		try {
			in.close();
		} catch (final IOException e) {
		}

		values.uncompressedPixelData = image;
		return values;
	}

	@Override
	public PixelCompressionValues compressPixelData(PixelCompressionValues values) throws ConversionException {
		// TODO compressPixelData

		final ByteArrayOutputStream out = new ByteArrayOutputStream(
				values.dimension.width * values.dimension.height * 3);

		for (int y = 0; y < values.dimension.height; y++) {
			for (int x = 0; x < values.dimension.width; x++) {

				final int rgb = values.uncompressedPixelData.getRGB(x, y);
				final Color color = new Color(rgb);

				try {
					out.write(new byte[] { (byte) color.getBlue(), (byte) color.getGreen(), (byte) color.getRed() });
				} catch (final IOException e) {
					throw new ConversionException("Pixeldaten können nicht geschrieben werden: " + e.getMessage(), e);
				}
			}
		}
		try {
			out.close();
		} catch (final IOException e) {
		}

		values.compressedPixelData = out.toByteArray();
		return values;
	}

}

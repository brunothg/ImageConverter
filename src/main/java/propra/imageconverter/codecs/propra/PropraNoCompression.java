package propra.imageconverter.codecs.propra;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.codecs.ConversionException;

/**
 * Liest/Schreibt Pixeldaten ohne Kompremierung
 *
 * @author marvin
 *
 */
public class PropraNoCompression extends PropraCompression {

	@Override
	public PropraPixelDecodeValues uncompressPixelData(PropraPixelDecodeValues values) throws ConversionException {
		final BufferedImage image = new BufferedImage(values.dimension.width, values.dimension.height,
				BufferedImage.TYPE_INT_RGB);

		final InputStream in = new BufferedInputStream(values.compressedPixelData, 1024);
		for (int y = 0; y < values.dimension.height; y++) {
			for (int x = 0; x < values.dimension.width; x++) {
				try {
					final byte[] pixel = new byte[3];
					final int read = in.readNBytes(pixel, 0, pixel.length);
					if (read != pixel.length) {
						throw new ConversionException(
								"Pixel konnte nicht gelesen werden: " + new Point(x, y) + " : Fehlende Daten");
					}

					final int g = Byte.toUnsignedInt(pixel[0]);
					final int b = Byte.toUnsignedInt(pixel[1]);
					final int r = Byte.toUnsignedInt(pixel[2]);

					image.setRGB(x, y, new Color(r, g, b).getRGB());
				} catch (final IOException e) {
					throw new ConversionException(
							"Pixel konnte nicht gelesen werden: " + new Point(x, y) + " : " + e.getMessage(), e);
				}
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
	public PropraPixelEncodeValues compressPixelData(PropraPixelEncodeValues values) throws ConversionException {

		final OutputStream out = new BufferedOutputStream(values.compressedPixelData, 1024);

		for (int y = 0; y < values.dimension.height; y++) {
			for (int x = 0; x < values.dimension.width; x++) {

				final int rgb = values.uncompressedPixelData.getRGB(x, y);
				final Color color = new Color(rgb);

				try {
					out.write(new byte[] { (byte) color.getGreen(), (byte) color.getBlue(), (byte) color.getRed() });
				} catch (final IOException e) {
					throw new ConversionException("Pixeldaten können nicht geschrieben werden: " + e.getMessage(), e);
				}
			}
		}
		try {
			out.close();
		} catch (final IOException e) {
		}

		return values;
	}

}

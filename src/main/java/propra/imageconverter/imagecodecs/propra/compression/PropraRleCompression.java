package propra.imageconverter.imagecodecs.propra.compression;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.imagecodecs.ConversionException;

/**
 * Liest/Schreibt Pixeldaten mit RLE Kompremierung
 *
 * @author marvin
 *
 */
public class PropraRleCompression extends PropraCompression {

	@Override
	public PropraPixelDecodeValues uncompressPixelData(final PropraPixelDecodeValues values)
			throws ConversionException {
		final BufferedImage image = new BufferedImage(values.dimension.width, values.dimension.height,
				BufferedImage.TYPE_INT_RGB);

		final InputStream in = new BufferedInputStream(values.compressedPixelData, 1024);

		int x = 0;
		int y = 0;
		final int width = values.dimension.width;
		final int height = values.dimension.height;

		while (y < height) {
			try {
				final int steuerbit = in.read();
				if (steuerbit < 0) {
					throw new ConversionException(
							"Steuerbit konnte nicht gelesen werden: " + new Point(x, y) + " : Fehlende Daten");
				}
				final boolean rleRepeat = (steuerbit & 0b10000000) == 0b10000000;
				final int rleCounter = ((steuerbit & 0b01111111)) + 1;

				if (rleRepeat) {
					final Color pixel = this.readPixel(in);

					for (int i = 0; i < rleCounter; i++) {
						image.setRGB(x, y, pixel.getRGB());

						x++;
						if (x >= width) {
							x = 0;
							y++;
						}
					}
				} else {
					for (int i = 0; i < rleCounter; i++) {
						final Color pixel = this.readPixel(in);
						image.setRGB(x, y, pixel.getRGB());

						x++;
						if (x >= width) {
							x = 0;
							y++;
						}
					}
				}

			} catch (final IOException e) {
				throw new ConversionException(
						"Pixel konnte nicht gelesen werden: " + new Point(x, y) + " : " + e.getMessage(), e);
			}
		}

		try {
			in.close();
		} catch (final IOException e) {
		}

		values.uncompressedPixelData = image;
		return values;
	}

	private Color readPixel(final InputStream in) throws ConversionException, IOException {
		final byte[] pixel = new byte[3];
		final int read = in.readNBytes(pixel, 0, pixel.length);
		if (read != pixel.length) {
			throw new ConversionException("Pixel konnte nicht gelesen werden: Fehlende Daten");
		}

		final int g = Byte.toUnsignedInt(pixel[0]);
		final int b = Byte.toUnsignedInt(pixel[1]);
		final int r = Byte.toUnsignedInt(pixel[2]);

		return new Color(r, g, b);
	}

	@Override
	public PropraPixelEncodeValues compressPixelData(final PropraPixelEncodeValues values) throws ConversionException {

		final OutputStream out = new BufferedOutputStream(values.compressedPixelData, 1024);

		// TODO Kompression verbessern
		for (int y = 0; y < values.dimension.height; y++) {
			for (int x = 0; x < values.dimension.width; x++) {

				final int rgb = values.uncompressedPixelData.getRGB(x, y);
				final Color color = new Color(rgb);

				try {
					final boolean rleRepeat = false;
					final int rleCounter = 1;
					final int steuerbyte = ((rleCounter - 1) & 0b01111111) + ((rleRepeat) ? 0b10000000 : 0b00000000);

					out.write(steuerbyte);
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

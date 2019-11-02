package propra.imageconverter.imagecodecs.tga.compression;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.tga.TgaImageAttributes.HorizontalOrigin;
import propra.imageconverter.imagecodecs.tga.TgaImageAttributes.VerticalOrigin;

/**
 * Liest/Schreibt RLE komprimierte Daten
 *
 * @author marvin
 *
 */
public class TgaRleCompression extends TgaCompression {

	@Override
	public TgaPixelDecodeValues uncompressPixelData(final TgaPixelDecodeValues values) throws ConversionException {
		final BufferedImage image = new BufferedImage(values.dimension.width, values.dimension.height,
				BufferedImage.TYPE_INT_RGB);

		final InputStream in = values.compressedPixelData;

		final PixelLoop pixelLoop = new PixelLoop(values);
		Point pixelPosition = pixelLoop.init();
		while (pixelPosition != null) {

			try {
				final int steuerbyte = in.read();
				if (steuerbyte < 0) {
					throw new ConversionException(
							"Steuerbit konnte nicht gelesen werden: " + pixelPosition + " : Fehlende Daten");
				}
				final boolean rleRepeat = (steuerbyte & 0b10000000) == 0b10000000;
				final int rleCounter = ((steuerbyte & 0b01111111)) + 1;

				if (rleRepeat) {
					final Color pixel = this.readPixel(in);

					for (int i = 0; i < rleCounter; i++) {
						image.setRGB(pixelPosition.x, pixelPosition.y, pixel.getRGB());

						pixelPosition = pixelLoop.increment();
					}
				} else {
					for (int i = 0; i < rleCounter; i++) {
						final Color pixel = this.readPixel(in);
						image.setRGB(pixelPosition.x, pixelPosition.y, pixel.getRGB());

						pixelPosition = pixelLoop.increment();
					}
				}

			} catch (final IOException e) {
				throw new ConversionException(
						"Pixel konnte nicht gelesen werden: " + pixelPosition + " : " + e.getMessage(), e);
			}
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

		final int b = Byte.toUnsignedInt(pixel[0]);
		final int g = Byte.toUnsignedInt(pixel[1]);
		final int r = Byte.toUnsignedInt(pixel[2]);

		return new Color(r, g, b);
	}

	@Override
	public TgaPixelEncodeValues compressPixelData(final TgaPixelEncodeValues values) throws ConversionException {
		final OutputStream out = values.compressedPixelData;

		final PixelLoop pixelLoop = new PixelLoop(values);
		Point pixelPosition = pixelLoop.init();
		Color actualColor = null;
		int colorCounter = 0;
		while (pixelPosition != null) {
			final int rgb = values.uncompressedPixelData.getRGB(pixelPosition.x, pixelPosition.y);
			final Color color = new Color(rgb);

			if (actualColor == null) {
				actualColor = color;
				colorCounter = 1;
			} else {
				if ((colorCounter < (0b0111111 + 1)) /* Max Counter 128 */ && (pixelPosition.x > 0) /* Neue Zeile */
						&& actualColor.equals(color)) {
					colorCounter++;
				} else {
					// Neuer Pixelblock -> Alten wegschreiben
					this.writePixelRepeat(out, actualColor, colorCounter);

					actualColor = color;
					colorCounter = 1;
				}
			}

			pixelPosition = pixelLoop.increment();
		}
		// Restpixel schreiben
		this.writePixelRepeat(out, actualColor, colorCounter);

		return values;
	}

	private void writePixelRepeat(final OutputStream out, final Color actualColor, final int colorCounter)
			throws ConversionException {
		if ((colorCounter < 1) || (colorCounter > 128)) {
			throw new ConversionException("Wiederholungszahl ungültig (1 - 128):" + colorCounter);
		}
		try {
			final boolean rleRepeat = true;
			final int rleCounter = colorCounter; // Max 128
			final int steuerbyte = ((rleCounter - 1) & 0b01111111) + ((rleRepeat) ? 0b10000000 : 0b00000000);

			out.write(steuerbyte);
			out.write(new byte[] { (byte) actualColor.getBlue(), (byte) actualColor.getGreen(),
					(byte) actualColor.getRed() });
		} catch (final IOException e) {
			throw new ConversionException("Pixeldaten können nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	/**
	 * Hilfsklasse, um die verschiedenen Origins zu handhaben
	 *
	 * @author marvin
	 *
	 */
	private static class PixelLoop {

		private final TgaPixelCompressionValues values;

		private int x;
		private int y;
		private boolean eof;

		public PixelLoop(final TgaPixelCompressionValues values) {
			this.values = values;
		}

		/**
		 * Springt zum Anfang
		 */
		public Point init() throws ConversionException {
			final VerticalOrigin verticalOrigin = this.values.imageAttributes.getVerticalOrigin();
			final HorizontalOrigin horizontalOrigin = this.values.imageAttributes.getHorizontalOrigin();

			switch (verticalOrigin) {
			case Bottom:
				this.y = this.values.dimension.height - 1;
				break;
			case Top:
				this.y = 0;
				break;
			default:
				throw new ConversionException("VerticalOrigin Typ nicht unterstützt: " + verticalOrigin);

			}

			switch (horizontalOrigin) {
			case Left:
				this.x = 0;
				break;
			case Right:
				this.x = this.values.dimension.width - 1;
				break;
			default:
				throw new ConversionException("HorizontalOrigin Typ nicht unterstützt: " + horizontalOrigin);
			}

			this.eof = false;

			return new Point(this.x, this.y);
		}

		/**
		 * Inkrementiert und gibt die neuen Koordinaten
		 *
		 * @return Neue Koordinate oder null, wenn keine mehr vorhanden
		 */
		public Point increment() throws ConversionException {
			if (this.eof) {
				throw new ConversionException("No more Pixels");
			}

			final VerticalOrigin verticalOrigin = this.values.imageAttributes.getVerticalOrigin();
			final HorizontalOrigin horizontalOrigin = this.values.imageAttributes.getHorizontalOrigin();

			switch (horizontalOrigin) {
			case Left:
				this.x++;
				if (this.x >= this.values.dimension.width) {
					this.x = 0;

					switch (verticalOrigin) {
					case Bottom:
						this.y--;
						if (this.y < 0) {
							this.eof = true;
						}
						break;
					case Top:
						this.y++;
						if (this.y >= this.values.dimension.height) {
							this.eof = true;
						}
						break;
					default:
						throw new ConversionException("VerticalOrigin Typ nicht unterstützt: " + verticalOrigin);

					}

				}

				break;
			case Right:
				this.x--;
				if (this.x < 0) {
					this.x = this.values.dimension.width - 1;

					switch (verticalOrigin) {
					case Bottom:
						this.y--;
						if (this.y < 0) {
							this.eof = true;
						}
						break;
					case Top:
						this.y++;
						if (this.y >= this.values.dimension.height) {
							this.eof = true;
						}
						break;
					default:
						throw new ConversionException("VerticalOrigin Typ nicht unterstützt: " + verticalOrigin);

					}

				}
				break;
			default:
				throw new ConversionException("HorizontalOrigin Typ nicht unterstützt: " + horizontalOrigin);
			}

			if (this.eof) {
				return null;
			} else {
				return new Point(this.x, this.y);
			}
		}

//		public Point increment(final int times) throws ConversionException {
//			Point result = new Point(this.x, this.y);
//
//			for (int i = 0; i < times; i++) {
//				result = this.increment();
//			}
//
//			return result;
//		}
	}

}

package propra.imageconverter.imagecodecs.propra.compression;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.InternalImage;

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
		final InternalImage image = InternalImage.createInternalImage(values.dimension);

		final InputStream in = values.compressedPixelData;

		final PixelLoop pixelLoop = new PixelLoop(values);
		Point pixelPosition = pixelLoop.init();
		while (pixelPosition != null) {
			try {
				final int steuerbit = in.read();
				if (steuerbit < 0) {
					throw new ConversionException(
							"Steuerbit konnte nicht gelesen werden: " + pixelPosition + " : Fehlende Daten");
				}
				final boolean rleRepeat = (steuerbit & 0b10000000) == 0b10000000;
				final int rleCounter = ((steuerbit & 0b01111111)) + 1;

				if (rleRepeat) {
					final Color pixel = this.readPixel(in);

					for (int i = 0; i < rleCounter; i++) {
						image.setPixel(pixelPosition, pixel);

						pixelPosition = pixelLoop.increment();
					}
				} else {
					for (int i = 0; i < rleCounter; i++) {
						final Color pixel = this.readPixel(in);
						image.setPixel(pixelPosition, pixel);

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

		final int g = Byte.toUnsignedInt(pixel[0]);
		final int b = Byte.toUnsignedInt(pixel[1]);
		final int r = Byte.toUnsignedInt(pixel[2]);

		return new Color(r, g, b);
	}

	@Override
	public PropraPixelEncodeValues compressPixelData(final PropraPixelEncodeValues values) throws ConversionException {

		final OutputStream out = values.compressedPixelData;

		final PixelLoop pixelLoop = new PixelLoop(values);
		Point pixelPosition = pixelLoop.init();
		Color actualColor = null;
		int colorCounter = 0;
		while (pixelPosition != null) {
			final Color color = values.uncompressedPixelData.getPixel(pixelPosition);

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
			out.write(new byte[] { (byte) actualColor.getGreen(), (byte) actualColor.getBlue(),
					(byte) actualColor.getRed() });
		} catch (final IOException e) {
			throw new ConversionException("Pixeldaten können nicht geschrieben werden: " + e.getMessage(), e);
		}
	}

	/**
	 * Hilfsklasse, zum durchlaufen der Koordinaten
	 *
	 * @author marvin
	 *
	 */
	private static class PixelLoop {

		private final PropraPixelCompressionValues values;

		private int x;
		private int y;
		private boolean eof;

		public PixelLoop(final PropraPixelCompressionValues values) {
			this.values = values;
		}

		/**
		 * Springt zum Anfang
		 */
		public Point init() throws ConversionException {
			this.x = 0;
			this.y = 0;
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

			this.x++;
			if (this.x >= this.values.dimension.width) {
				this.x = 0;

				this.y++;
				if (this.y >= this.values.dimension.height) {
					this.eof = true;
				}
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

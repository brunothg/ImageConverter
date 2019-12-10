package propra.imageconverter.imagecodecs.propra.compression;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.utils.streams.huffman.HuffmanInputStream;
import propra.imageconverter.utils.streams.huffman.HuffmanOutputStream;

/**
 * Liest/Schreibt Pixeldaten mit Huffman Kompremierung
 *
 * @author marvin
 *
 */
public class PropraHuffmanCompression extends PropraCompression {

	@SuppressWarnings("resource")
	@Override
	public PropraPixelDecodeValues uncompressPixelData(final PropraPixelDecodeValues values)
			throws ConversionException {
		final InternalImage image = InternalImage.createInternalImage(values.dimension);

		final InputStream in = new HuffmanInputStream(values.compressedPixelData);
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

					image.setPixel(new Point(x, y), new Color(r, g, b));
				} catch (final IOException e) {
					throw new ConversionException(
							"Pixel konnte nicht gelesen werden: " + new Point(x, y) + " : " + e.getMessage(), e);
				}
			}
		}

		values.uncompressedPixelData = image;
		return values;
	}

	@SuppressWarnings("resource")
	@Override
	public PropraPixelEncodeValues compressPixelData(final PropraPixelEncodeValues values) throws ConversionException {
		final HuffmanOutputStream out = new HuffmanOutputStream(values.compressedPixelData);

		for (int y = 0; y < values.dimension.height; y++) {
			for (int x = 0; x < values.dimension.width; x++) {

				final Color color = values.uncompressedPixelData.getPixel(new Point(x, y));

				try {
					out.write(new byte[] { (byte) color.getGreen(), (byte) color.getBlue(), (byte) color.getRed() });
				} catch (final IOException e) {
					throw new ConversionException("Pixeldaten können nicht geschrieben werden: " + e.getMessage(), e);
				}
			}
		}

		try {
			out.flushBitBuffer();
		} catch (final IOException e) {
			new ConversionException("Bits konnten nicht geschrieben werden", e);
		}

		return values;
	}

}

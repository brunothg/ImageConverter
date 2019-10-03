package propra.imageconverter.codecs.tga;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import propra.imageconverter.codecs.ConversionException;

/**
 * Liest/Schreibt unkomprimierte RGB Daten
 *
 * @author marvin
 *
 */
public class TgaRgbCompression extends TgaCompression {

	@Override
	public PixelCompressionValues uncompressPixelData(PixelCompressionValues values) throws ConversionException {
		// TODO Auto-generated method stub

		try {
			values.uncompressedPixelData = ImageIO.read(new URL(
					"https://upload.wikimedia.org/wikipedia/en/thumb/9/99/Question_book-new.svg/100px-Question_book-new.svg.png"));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return values;
	}

	@Override
	public PixelCompressionValues compressPixelData(PixelCompressionValues values) throws ConversionException {
		// TODO compressPixelData
		values.compressedPixelData = new byte[0];
		return values;
	}

}

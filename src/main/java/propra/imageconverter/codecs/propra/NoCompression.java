package propra.imageconverter.codecs.propra;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

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
    public PixelCompressionValues compressPixelData(PixelCompressionValues values) {
	// TODO compressPixelData
	return values;
    }

}

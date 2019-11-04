package propra.imageconverter.imagecodecs;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import org.junit.Test;

public class InternalFileImageTest {

	@Test
	public void testReadWrite() throws Exception {
		final InternalFileImage image = new InternalFileImage(new Dimension(100, 100));

		boolean sw = false;
		for (int y = 0; y < 100; y++) {
			for (int x = 0; x < 100; x++) {
				image.setPixel(new Point(x, y), (sw) ? new Color(255, 255, 255) : new Color(0, 0, 0));
				sw = !sw;
			}
		}

		sw = false;
		for (int y = 0; y < 100; y++) {
			for (int x = 0; x < 100; x++) {
				final Color pixel = image.getPixel(new Point(x, y));
				assertEquals(x + ", " + y, (sw) ? new Color(255, 255, 255) : new Color(0, 0, 0), pixel);
				sw = !sw;
			}
		}

		image.close();
	}
}

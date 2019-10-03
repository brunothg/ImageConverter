package propra.imageconverter.codecs.tga;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import propra.imageconverter.codecs.tga.ImageAttributes.HorizontalOrigin;
import propra.imageconverter.codecs.tga.ImageAttributes.VerticalOrigin;

public class ImageAttributesTest {

	@Test
	public void testParseAttributeByte() throws Exception {
		final int attributeByte = 0B00100010;// Top,Left,2
		final ImageAttributes attributes = ImageAttributes.fromByte(attributeByte);

		assertEquals(HorizontalOrigin.Left, attributes.getHorizontalOrigin());
		assertEquals(VerticalOrigin.Top, attributes.getVerticalOrigin());
		assertEquals(2, attributes.getAttributeBitCount());
	}

	@Test
	public void testAttributeByteToByte() throws Exception {
		final int attributeByte = 0B00100010;// Top,Left,2
		final ImageAttributes attributes = new ImageAttributes();
		attributes.setAttributeBitCount(2);
		attributes.setHorizontalOrigin(HorizontalOrigin.Left);
		attributes.setVerticalOrigin(VerticalOrigin.Top);

		assertEquals(attributeByte, ImageAttributes.toByte(attributes));
	}
}

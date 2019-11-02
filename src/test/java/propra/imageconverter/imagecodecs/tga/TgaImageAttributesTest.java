package propra.imageconverter.imagecodecs.tga;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import propra.imageconverter.imagecodecs.tga.TgaImageAttributes;
import propra.imageconverter.imagecodecs.tga.TgaImageAttributes.HorizontalOrigin;
import propra.imageconverter.imagecodecs.tga.TgaImageAttributes.VerticalOrigin;

public class TgaImageAttributesTest {

	@Test
	public void testParseAttributeByte() throws Exception {
		final int attributeByte = 0B00100010;// Top,Left,2
		final TgaImageAttributes attributes = TgaImageAttributes.fromByte(attributeByte);

		assertEquals(HorizontalOrigin.Left, attributes.getHorizontalOrigin());
		assertEquals(VerticalOrigin.Top, attributes.getVerticalOrigin());
		assertEquals(2, attributes.getAttributeBitCount());
	}

	@Test
	public void testAttributeByteToByte() throws Exception {
		final int attributeByte = 0B00100010;// Top,Left,2
		final TgaImageAttributes attributes = new TgaImageAttributes();
		attributes.setAttributeBitCount(2);
		attributes.setHorizontalOrigin(HorizontalOrigin.Left);
		attributes.setVerticalOrigin(VerticalOrigin.Top);

		assertEquals(attributeByte, TgaImageAttributes.toByte(attributes));
	}
}

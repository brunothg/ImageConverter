package propra.imageconverter.utils.streams.bytes;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import propra.imageconverter.utils.streams.bytes.BitInputStream;

public class BitInputStreamTest {

	@Test
	public void testReadBits() throws Exception {
		final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
				new byte[] { (byte) 0b10001011, (byte) 0b00110111 });
		final BitInputStream in = new BitInputStream(arrayInputStream);

		assertEquals(1, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(-1, in.readBit());

		in.close();
	}

	@Test
	public void testBytes() throws Exception {
		final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
				new byte[] { (byte) 0b10001011, (byte) 0b00110111 });
		final BitInputStream in = new BitInputStream(arrayInputStream);

		assertEquals(0b10001011, in.read());
		assertEquals(0b00110111, in.read());
		assertEquals(-1, in.read());

		in.close();
	}

	@Test
	public void testReadBitsAndBytes() throws Exception {
		final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
				new byte[] { (byte) 0b10001011, (byte) 0b00110111 });
		final BitInputStream in = new BitInputStream(arrayInputStream);

		assertEquals(1, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(0, in.readBit());

//		assertEquals(1, in.readBit());
//		assertEquals(1, in.readBit());
//		assertEquals(0, in.readBit());
//		assertEquals(0, in.readBit());
//		assertEquals(1, in.readBit());
//		assertEquals(1, in.readBit());
//		assertEquals(0, in.readBit());
//		assertEquals(1, in.readBit());
		assertEquals(0b11001101, in.read());

		assertEquals(1, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(-1, in.readBit());

		in.close();
	}

	@Test
	public void testReadBitsAndBytesEof() throws Exception {
		final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
				new byte[] { (byte) 0b10001011, (byte) 0b00110111 });
		final BitInputStream in = new BitInputStream(arrayInputStream);

		assertEquals(1, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(1, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(0, in.readBit());
		assertEquals(1, in.readBit());

//		assertEquals(1, in.readBit());
//		assertEquals(0, in.readBit());
//		assertEquals(1, in.readBit());
//		assertEquals(1, in.readBit());
//		assertEquals(1, in.readBit());
		assertEquals(0b10111000, in.read());

		assertEquals(-1, in.readBit());

		in.close();
	}
}

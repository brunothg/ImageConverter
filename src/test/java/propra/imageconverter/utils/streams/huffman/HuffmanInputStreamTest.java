package propra.imageconverter.utils.streams.huffman;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import org.junit.Test;

public class HuffmanInputStreamTest {

	@Test
	public void testBuildTree() throws Exception {
		// 001[A00000|001]1[B0000|0010]01[C00|000011]1[D0|0000100]0|10010001|01100000
		final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
				new byte[] { (byte) 0b00100000, (byte) 0b00110000, (byte) 0b00100100, (byte) 0b00001110,
						(byte) 0b00001000, (byte) 0b10010001, (byte) 0b01100000 });
		final HuffmanInputStream in = new HuffmanInputStream(arrayInputStream);

		assertEquals(2, in.read());
		assertEquals(1, in.read());
		assertEquals(3, in.read());
		assertEquals(1, in.read());
		assertEquals(3, in.read());
		assertEquals(4, in.read());

		in.close();
	}
}

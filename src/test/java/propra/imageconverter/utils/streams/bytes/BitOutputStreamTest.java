package propra.imageconverter.utils.streams.bytes;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class BitOutputStreamTest {

	@Test
	public void testWriteBits() throws Exception {
		final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

		final BitOutputStream out = new BitOutputStream(arrayOutputStream);
		out.writeBit(1);
		out.writeBit(0);
		out.writeBit(0);
		out.writeBit(0);
		out.writeBit(1);
		out.writeBit(0);
		out.writeBit(1);
		out.writeBit(1);
		out.writeBit(0);
		out.writeBit(0);
		out.writeBit(1);
		out.writeBit(1);
		out.writeBit(0);
		out.writeBit(1);
		out.writeBit(1);
		out.writeBit(1);
		out.close();

		final byte[] expeced = new byte[] { (byte) 0b10001011, (byte) 0b00110111 };
		final byte[] result = arrayOutputStream.toByteArray();
		for (int i = 0; i < result.length; i++) {
			assertEquals(expeced[i], result[i]);
		}
	}

	@Test
	public void testWriteBytes() throws Exception {
		final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

		final BitOutputStream out = new BitOutputStream(arrayOutputStream);
		out.write(0b10001011);
		out.write(0b00110111);
		out.close();

		final byte[] expeced = new byte[] { (byte) 0b10001011, (byte) 0b00110111 };
		final byte[] result = arrayOutputStream.toByteArray();
		for (int i = 0; i < result.length; i++) {
			assertEquals(expeced[i], result[i]);
		}
	}

	@Test
	public void testWriteBitsAndBytes() throws Exception {
		final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

		final BitOutputStream out = new BitOutputStream(arrayOutputStream);
		out.writeBit(1);
		out.writeBit(0);
		out.writeBit(0);
		out.writeBit(0);
		out.writeBit(1);
		out.write(0b01100110);
		out.writeBit(1);
		out.writeBit(1);
		out.writeBit(1);
		out.close();

		final byte[] expeced = new byte[] { (byte) 0b10001011, (byte) 0b00110111 };
		final byte[] result = arrayOutputStream.toByteArray();
		for (int i = 0; i < result.length; i++) {
			assertEquals(expeced[i], result[i]);
		}
	}

	@Test
	public void testWriteBitsAndBytesEof() throws Exception {
		final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

		final BitOutputStream out = new BitOutputStream(arrayOutputStream);
		out.writeBit(1);
		out.writeBit(0);
		out.writeBit(0);
		out.writeBit(0);
		out.writeBit(1);
		out.write(0b01100110);
		out.writeBit(1);
		out.writeBit(1);
		out.writeBit(1);

		out.writeBit(1);
		out.writeBit(0);
		out.writeBit(1);
		out.close();

		final byte[] expeced = new byte[] { (byte) 0b10001011, (byte) 0b00110111, (byte) 0b10100000 };
		final byte[] result = arrayOutputStream.toByteArray();
		for (int i = 0; i < result.length; i++) {
			assertEquals(expeced[i], result[i]);
		}
	}
}

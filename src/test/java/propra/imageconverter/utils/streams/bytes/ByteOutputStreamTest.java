package propra.imageconverter.utils.streams.bytes;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

import propra.imageconverter.utils.streams.bytes.ByteOutputStream;

public class ByteOutputStreamTest {

	@Test
	public void testWriteOrderedString() throws Exception {
		final String test = "Test";
		final byte[] bytesBigEndian = new byte[] { 84, 101, 115, 116 };
		final byte[] bytesLittleEndian = new byte[] { 116, 115, 101, 84 };

		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(test.length());
		ByteOutputStream outputStream = new ByteOutputStream(outBuffer, ByteOrder.LITTLE_ENDIAN);
		outputStream.writeOrderedString(test, StandardCharsets.UTF_8, 4);
		outputStream.close();
		assertArrayEquals(bytesLittleEndian, outBuffer.toByteArray());

		outBuffer = new ByteArrayOutputStream(test.length());
		outputStream = new ByteOutputStream(outBuffer, ByteOrder.BIG_ENDIAN);
		outputStream.writeOrderedString(test, StandardCharsets.UTF_8, 4);
		outputStream.close();
		assertArrayEquals(bytesBigEndian, outBuffer.toByteArray());
	}

	@Test
	public void tesetReverseBytes() throws Exception {

		final byte[] bytesBigEndian = new byte[] { 84, 101, 115, 116 };
		final byte[] bytesLittleEndian = new byte[] { 116, 115, 101, 84 };

		final byte[] bytes = Arrays.copyOf(bytesBigEndian, bytesBigEndian.length);
		ByteOutputStream.reverse(bytesLittleEndian);

		assertArrayEquals(bytesLittleEndian, bytes);
	}

	@Test
	public void testWriteOrderedUnsignedShort() throws Exception {
		final int test = 40000; // 0x9C40

		final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(2);
		final ByteOutputStream outputStream = new ByteOutputStream(outBuffer, ByteOrder.LITTLE_ENDIAN);
		outputStream.writeOrderedUnsignedShort(test);
		outputStream.close();

		final byte[] bytes = outBuffer.toByteArray();
		assertArrayEquals(new byte[] { (byte) 0x40, (byte) 0x9C }, bytes);
	}

	@Test
	public void testWriteOrderedUnsignedInt() throws Exception {
		final long test = 2147583648L; // 0x800186A0

		final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(4);
		final ByteOutputStream outputStream = new ByteOutputStream(outBuffer, ByteOrder.LITTLE_ENDIAN);
		outputStream.writeOrderedUnsignedInt(test);
		outputStream.close();

		final byte[] bytes = outBuffer.toByteArray();
		assertArrayEquals(new byte[] { (byte) 0xA0, (byte) 0x86, (byte) 0x01, (byte) 0x80 }, bytes);
	}

	@Test
	public void testWriteUnsignedByte() throws Exception {
		final int test = 255; // 0xFF

		final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(1);
		final ByteOutputStream outputStream = new ByteOutputStream(outBuffer, ByteOrder.LITTLE_ENDIAN);
		outputStream.writeUnsignedByte(test);
		outputStream.close();

		final byte[] bytes = outBuffer.toByteArray();
		assertArrayEquals(new byte[] { (byte) 255 }, bytes);
	}

	@Test
	public void testWriteUnsignedNumber() throws Exception {
		final String test = "18446744073709551615"; // 0xFFFFFFFFFFFFFFFF

		final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(1);
		final ByteOutputStream outputStream = new ByteOutputStream(outBuffer, ByteOrder.LITTLE_ENDIAN);
		outputStream.writeOrderedUnsignedNumber(new BigInteger(test), 8);
		outputStream.close();

		final byte[] bytes = outBuffer.toByteArray();
		assertArrayEquals(new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff }, bytes);
	}

	@Test
	public void testWriteUnsignedNumber2() throws Exception {
		final String test = "1152921504606846975"; // 0x0FFFFFFFFFFFFFFF

		final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(1);
		final ByteOutputStream outputStream = new ByteOutputStream(outBuffer, ByteOrder.LITTLE_ENDIAN);
		outputStream.writeOrderedUnsignedNumber(new BigInteger(test), 8);
		outputStream.close();

		final byte[] bytes = outBuffer.toByteArray();
		assertArrayEquals(new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0x0f }, bytes);
	}
}

package propra.imageconverter.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

public class ByteInputStreamTest {

    @Test
    public void testReadOrderedString() throws Exception {
	final String test = "Test";
	final byte[] bytesBigEndian = new byte[] { 84, 101, 115, 116 };
	final byte[] bytesLittleEndian = new byte[] { 116, 115, 101, 84 };

	final ByteInputStream inputStream = new ByteInputStream(new ByteArrayInputStream(bytesLittleEndian),
		ByteOrder.LITTLE_ENDIAN);
	final String string = inputStream.readOrderedString(4, StandardCharsets.UTF_8);
	inputStream.close();

	assertEquals(test, string);
	assertArrayEquals(bytesBigEndian, string.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void tesetReverseBytes() throws Exception {

	final byte[] bytesBigEndian = new byte[] { 84, 101, 115, 116 };
	final byte[] bytesLittleEndian = new byte[] { 116, 115, 101, 84 };

	final byte[] bytes = Arrays.copyOf(bytesBigEndian, bytesBigEndian.length);
	ByteInputStream.reverse(bytesLittleEndian);

	assertArrayEquals(bytesLittleEndian, bytes);
    }

    @Test
    public void testReadUnsignedShort() throws Exception {
	final ByteBuffer dbuf = ByteBuffer.allocate(2);
	dbuf.putShort((short) 40000);
	final byte[] bytes = dbuf.array();
	ByteInputStream.reverse(bytes);

	final ByteInputStream inputStream = new ByteInputStream(new ByteArrayInputStream(bytes),
		ByteOrder.LITTLE_ENDIAN);
	final int unsinedShort = inputStream.readOrderedUnsinedShort();
	inputStream.close();

	assertEquals(40000, unsinedShort);
    }

    @Test
    public void testReadUnsignedInt() throws Exception {
	final ByteBuffer dbuf = ByteBuffer.allocate(4);
	dbuf.putInt((int) 2147583648L);
	final byte[] bytes = dbuf.array();
	ByteInputStream.reverse(bytes);

	final ByteInputStream inputStream = new ByteInputStream(new ByteArrayInputStream(bytes),
		ByteOrder.LITTLE_ENDIAN);
	final long unsinedInt = inputStream.readOrderedUnsinedInt();
	inputStream.close();

	assertEquals(2147583648L, unsinedInt);
    }

    @Test
    public void testReadUnsignedNumber() throws Exception {
	final byte[] bytes = { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
		(byte) 0xff, (byte) 0xff };
	ByteInputStream.reverse(bytes);

	final ByteInputStream inputStream = new ByteInputStream(new ByteArrayInputStream(bytes),
		ByteOrder.LITTLE_ENDIAN);
	final BigInteger unsinedNumber = inputStream.readOrderedUnsinedNumber(bytes.length);
	inputStream.close();

	assertEquals("18446744073709551615", unsinedNumber.toString());
    }

}

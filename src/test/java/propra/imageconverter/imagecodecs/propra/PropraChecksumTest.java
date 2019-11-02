package propra.imageconverter.imagecodecs.propra;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import propra.imageconverter.imagecodecs.propra.PropraChecksum;
import propra.imageconverter.imagecodecs.propra.PropraChecksum.PropraChecksumInputStream;
import propra.imageconverter.imagecodecs.propra.PropraChecksum.PropraChecksumOutputStream;

public class PropraChecksumTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testTextChecksum() throws Exception {
		assertEquals(0x00750076, PropraChecksum.calculateChecksum("t".getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x00DC0152, PropraChecksum.calculateChecksum("te".getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x015202A4, PropraChecksum.calculateChecksum("tes".getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x01CA046E, PropraChecksum.calculateChecksum("test".getBytes(StandardCharsets.UTF_8)));

		assertEquals(0x3C56F024, PropraChecksum.calculateChecksum(
				"Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
						.getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x07AEE0D6, PropraChecksum.calculateChecksum(
				"Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
						.getBytes(StandardCharsets.UTF_8)));

	}

	@SuppressWarnings("deprecation")
	@Test
	public void testByteChecksum() throws Exception {
		assertEquals(0x00000001, PropraChecksum.calculateChecksum(new byte[] {}));
		assertEquals(0x00010002, PropraChecksum.calculateChecksum(new byte[] { 0 }));
		assertEquals(0x00020003, PropraChecksum.calculateChecksum(new byte[] { 1 }));
		assertEquals(0x00040006, PropraChecksum.calculateChecksum(new byte[] { 0, 1 }));
		assertEquals(0x00040007, PropraChecksum.calculateChecksum(new byte[] { 1, 0 }));
		assertEquals(0x01820283, PropraChecksum.calculateChecksum(new byte[] { (byte) 255, (byte) 128 }));
	}
	
	@Test
	public void testTextChecksumInputStream() throws Exception {
		assertEquals(0x00750076, checksumByInputStream("t".getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x00DC0152, checksumByInputStream("te".getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x015202A4, checksumByInputStream("tes".getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x01CA046E, checksumByInputStream("test".getBytes(StandardCharsets.UTF_8)));

		assertEquals(0x3C56F024, checksumByInputStream(
				"Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
						.getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x07AEE0D6, checksumByInputStream(
				"Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
						.getBytes(StandardCharsets.UTF_8)));

	}

	@Test
	public void testByteChecksumInputStream() throws Exception {
		assertEquals(0x00000001, checksumByInputStream(new byte[] {}));
		assertEquals(0x00010002, checksumByInputStream(new byte[] { 0 }));
		assertEquals(0x00020003, checksumByInputStream(new byte[] { 1 }));
		assertEquals(0x00040006, checksumByInputStream(new byte[] { 0, 1 }));
		assertEquals(0x00040007, checksumByInputStream(new byte[] { 1, 0 }));
		assertEquals(0x01820283, checksumByInputStream(new byte[] { (byte) 255, (byte) 128 }));
	}
	
	long checksumByInputStream(byte[] data) throws IOException {
		PropraChecksumInputStream inputStream = new PropraChecksum.PropraChecksumInputStream(new ByteArrayInputStream(data));
		inputStream.consumeAllBytes();
		long actualChecksum = inputStream.getActualChecksum();
		inputStream.close();
		
		return actualChecksum;
	}
	
	@Test
	public void testTextChecksumOutputStream() throws Exception {
		assertEquals(0x00750076, checksumByOutputStream("t".getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x00DC0152, checksumByOutputStream("te".getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x015202A4, checksumByOutputStream("tes".getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x01CA046E, checksumByOutputStream("test".getBytes(StandardCharsets.UTF_8)));

		assertEquals(0x3C56F024, checksumByOutputStream(
				"Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
						.getBytes(StandardCharsets.UTF_8)));
		assertEquals(0x07AEE0D6, checksumByOutputStream(
				"Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
						.getBytes(StandardCharsets.UTF_8)));

	}

	@Test
	public void testByteChecksumOutputStream() throws Exception {
		assertEquals(0x00000001, checksumByOutputStream(new byte[] {}));
		assertEquals(0x00010002, checksumByOutputStream(new byte[] { 0 }));
		assertEquals(0x00020003, checksumByOutputStream(new byte[] { 1 }));
		assertEquals(0x00040006, checksumByOutputStream(new byte[] { 0, 1 }));
		assertEquals(0x00040007, checksumByOutputStream(new byte[] { 1, 0 }));
		assertEquals(0x01820283, checksumByOutputStream(new byte[] { (byte) 255, (byte) 128 }));
	}
	
	long checksumByOutputStream(byte[] data) throws IOException {
		PropraChecksumOutputStream ouputStream = new PropraChecksum.PropraChecksumOutputStream(new ByteArrayOutputStream(data.length));
		ouputStream.write(data);
		
		long actualChecksum = ouputStream.getActualChecksum();
		ouputStream.close();
		
		return actualChecksum;
	}

}

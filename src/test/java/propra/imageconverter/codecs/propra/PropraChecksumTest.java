package propra.imageconverter.codecs.propra;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class PropraChecksumTest {

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

	@Test
	public void testByteChecksum() throws Exception {
		assertEquals(0x00000001, PropraChecksum.calculateChecksum(new byte[] {}));
		assertEquals(0x00010002, PropraChecksum.calculateChecksum(new byte[] { 0 }));
		assertEquals(0x00020003, PropraChecksum.calculateChecksum(new byte[] { 1 }));
		assertEquals(0x00040006, PropraChecksum.calculateChecksum(new byte[] { 0, 1 }));
		assertEquals(0x00040007, PropraChecksum.calculateChecksum(new byte[] { 1, 0 }));
		assertEquals(0x01820283, PropraChecksum.calculateChecksum(new byte[] { (byte) 255, (byte) 128 }));
	}

}

package propra.imageconverter.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class MathUtilsTest {

    @Test
    public void testLog2() throws Exception {
	for (int i = 1; i < 62; i++) {
	    final long pow = (long) Math.pow(2, i);
	    final long log2 = MathUtils.log2(pow);
	    System.out.println(i + ": " + pow + ": " + log2);
	    assertEquals(i, log2);
	}
    }

    @Test
    public void testGetBit() throws Exception {
	final int i = 0b10101011;
	assertEquals(true, MathUtils.getBit(i, 0));
	assertEquals(true, MathUtils.getBit(i, 1));
	assertEquals(false, MathUtils.getBit(i, 2));
	assertEquals(true, MathUtils.getBit(i, 3));
	assertEquals(false, MathUtils.getBit(i, 4));
	assertEquals(true, MathUtils.getBit(i, 5));
	assertEquals(false, MathUtils.getBit(i, 6));
	assertEquals(true, MathUtils.getBit(i, 7));
    }

    @Test
    public void testSetBit() throws Exception {
	final int i = 0b10101011;
	assertEquals(0b10101011, MathUtils.setBit(i, 0, true));
	assertEquals(0b10101011, MathUtils.setBit(i, 1, true));
	assertEquals(0b10101111, MathUtils.setBit(i, 2, true));
	assertEquals(0b10101011, MathUtils.setBit(i, 3, true));
	assertEquals(0b10111011, MathUtils.setBit(i, 4, true));
	assertEquals(0b10101011, MathUtils.setBit(i, 5, true));
	assertEquals(0b11101011, MathUtils.setBit(i, 6, true));
	assertEquals(0b10101011, MathUtils.setBit(i, 7, true));

	assertEquals(0b10101010, MathUtils.setBit(i, 0, false));
	assertEquals(0b10101001, MathUtils.setBit(i, 1, false));
	assertEquals(0b10101011, MathUtils.setBit(i, 2, false));
	assertEquals(0b10100011, MathUtils.setBit(i, 3, false));
	assertEquals(0b10101011, MathUtils.setBit(i, 4, false));
	assertEquals(0b10001011, MathUtils.setBit(i, 5, false));
	assertEquals(0b10101011, MathUtils.setBit(i, 6, false));
	assertEquals(0b00101011, MathUtils.setBit(i, 7, false));
    }
}

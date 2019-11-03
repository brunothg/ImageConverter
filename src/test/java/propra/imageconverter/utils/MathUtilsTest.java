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
}

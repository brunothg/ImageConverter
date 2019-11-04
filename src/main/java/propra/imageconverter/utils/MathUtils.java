package propra.imageconverter.utils;

/**
 * Mathematische Hilfsfunktionen
 *
 * @author marvin
 *
 */
public class MathUtils {

    /**
     * Berechnet den Logarithmus zur Basis 2. Keine Nachkommastellen.
     *
     * @param n Max 2^62
     * @return
     */
    public static long log2(final long n) {
	if ((n <= 0) || (n > pow(2, 62))) {
	    throw new IllegalArgumentException();
	}
	return 63 - Long.numberOfLeadingZeros(n);
    }

    /**
     * Berechnet n^x für x >= 0
     *
     * @param n
     * @param x
     * @return
     */
    public static long pow(final int n, final int x) {
	if (n == 0) {
	    return 0;
	}
	if (x == 0) {
	    return 1;
	}

	long result = n;
	for (int i = 1; i < x; i++) {
	    result *= n;
	}

	return result;
    }

    /**
     * Testet, ob das Bit an der Stelle k gesetzt ist (unsigned byte)
     * 
     * @param n
     * @param k
     * @return
     */
    public static boolean getBit(int n, int k) {
	if (k < 0 || k >= 32) {
	    throw new IllegalArgumentException("Bit position invalid: " + k);
	}

	return ((n >> k) & 1) == 1;
    }

    /**
     * Setzt das bit an der Stelle k auf 0 (set=false) oder 1 (set=true)
     * 
     * @param n
     * @param k
     * @param set
     * @return
     */
    public static int setBit(int n, int k, boolean set) {
	if (k < 0 || k >= 32) {
	    throw new IllegalArgumentException("Bit position invalid: " + k);
	}

	if (set) {
	    n = n | (1 << k);
	} else {
	    n = n & ~(1 << k);
	}

	return n;
    }
}

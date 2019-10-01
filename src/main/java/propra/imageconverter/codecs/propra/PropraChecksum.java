package propra.imageconverter.codecs.propra;

/**
 * Hilfsklasse zum Berechnen und Überprüfen der Checksumme
 *
 * @author marvin
 *
 */
public class PropraChecksum {

	private static final int X = 65513;

	private PropraChecksum() {
	}

	/**
	 * Berechnet die Checksumme (siehe Aufgabenstellung) für ein Byte-Array
	 *
	 * @param data Die zu grunde liegenden Daten
	 * @return Die Checksumme
	 */
	public static long calculateChecksum(byte[] data) {
		if (data == null) {
			data = new byte[0];
		}

		final long bn = calculateBi(data.length, data);

		final long an = calculateAn(data.length, data);

		final long p = (an * ((long) Math.pow(2, 16))) + bn;

		return p;
	}

	/**
	 * Berechnung von An. An dieser Stelle sei für Weiteres auf die Aufgabenstellung
	 * verwiesen.
	 *
	 * @param n
	 * @param data
	 * @return
	 */
	private static long calculateAn(int n, byte[] data) {
		long an = 0;
		for (int i = 1; i <= n; i++) {
			an += i + Byte.toUnsignedInt(data[i - 1]);
		}
		an = Math.abs(an) % X;

		return an;
	}

	/**
	 * Berechnung von Bi. An dieser Stelle sei für Weiteres auf die Aufgabenstellung
	 * verwiesen.
	 *
	 * @param i
	 * @param data
	 * @return
	 */
	private static long calculateBi(int i, byte[] data) {
		if (i == 0) {
			return 1;
		}

		return Math.abs(calculateBi(i - 1, data) + calculateAn(i, data)) % X;
	}
}

package propra.imageconverter.codecs.propra;

// XXX Besser auf Streams umstellen, bisher keine Lösung gefunden
// Weg über Arrays ist leider wegen der Checksum-Berechnung notwendig, sie einen Zugriff per Index auf die Daten verlangt
/**
 * Hilfsklasse zum Berechnen und Überprüfen der Checksumme
 *
 * @author marvin
 *
 */
public class PropraChecksum {

	private static final int X = 65513;

	private final byte[] data;

	private final Long[] anSumCache;

	public PropraChecksum(byte[] data) {
		this.data = (data != null) ? data : new byte[0];
		this.anSumCache = new Long[this.data.length];
	}

	public long calculate() {

		final long an = this.calculateAn(this.data.length, this.data);
		final long bn = this.calculateBi(this.data.length, this.data);
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
	private long calculateAn(int n, byte[] data) {

		final Long previousValue = (n >= 2) ? this.anSumCache[n - 2] : null;

		long an = 0;
		if (n > 0) {
			if (previousValue != null) {
				// Greife auf bereits berechnete Werte zurück
				an = previousValue;
				an += n + Byte.toUnsignedInt(data[n - 1]);
			} else {
				// Summe anhand der Formel neu
				for (int i = 1; i <= n; i++) {
					an += i + Byte.toUnsignedInt(data[i - 1]);
				}
			}
			this.anSumCache[n - 1] = an;
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
	private long calculateBi(int i, byte[] data) {

		long bi = 1; // Initialisiere mit b0=1
		// Berechne alle weiteren Schritte bis bi
		for (int j = 1; j <= i; j++) {
			bi = Math.abs(bi + this.calculateAn(j, data)) % X;
		}

		return bi;

		// Kann alternativ auch rekursiv gelößt werden, ist kürzer aber ineffizient
		// return (i==0)?1:Math.abs(calculateBi(i-1, data)+calculateAn(i, data))%X;
	}

	/**
	 * Berechnet die Checksumme (siehe Aufgabenstellung) für ein Byte-Array
	 *
	 * @param data Die zu grunde liegenden Daten
	 * @return Die Checksumme
	 */
	public static long calculateChecksum(byte[] data) {
		final PropraChecksum checksum = new PropraChecksum(data);

		return checksum.calculate();
	}

}

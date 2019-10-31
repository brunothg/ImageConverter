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

    public PropraChecksum(byte[] data) {
	this.data = (data != null) ? data : new byte[0];
    }

    public long calculate() {
	final long n = this.data.length;

	long a = 0;
	long b = 1;
	long prevB = 0;

	for (int i = 0; i < n; i++) {
	    a = a + ((i + 1) + Byte.toUnsignedInt(this.data[i]));
	    prevB = b;
	    b = (prevB + a) % X;
	}
	a = a % X;

	final long p = (a * ((long) Math.pow(2, 16))) + b;

	return p;
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

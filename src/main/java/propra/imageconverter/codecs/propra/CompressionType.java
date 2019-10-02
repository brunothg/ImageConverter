package propra.imageconverter.codecs.propra;

/**
 * Kompressionstyp eines ProPra Bildes
 * 
 * @author marvin
 *
 */
public enum CompressionType {
    None(0);

    /**
     * Id des Kompressionstyps nach der Spezifikation
     */
    private int id;

    private CompressionType(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    /**
     * Gibt den Kompressionstyp zu einer Id
     * 
     * @param id Kompressions Id
     * @return {@link CompressionType} oder null, wenn nicht vorhanden
     */
    public static CompressionType fromId(int id) {
	for (final CompressionType type : CompressionType.values()) {
	    if (type.getId() == id) {
		return type;
	    }
	}

	return null;
    }
}

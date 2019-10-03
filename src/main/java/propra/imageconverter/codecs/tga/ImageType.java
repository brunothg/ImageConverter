package propra.imageconverter.codecs.tga;

/**
 * Tga-Bildtyp
 *
 * @author marvin
 *
 */
public enum ImageType {

	/**
	 * RGB (24 oder 32 Bit) unkomprimiert
	 */
	Rgb(2);

	/**
	 * Id des Bildtyp nach der Spezifikation
	 */
	private int id;

	private ImageType(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	/**
	 * Gibt den Bildtyp zu einer Id
	 *
	 * @param id Bildtyp Id
	 * @return {@link ImageType} oder null, wenn nicht vorhanden
	 */
	public static ImageType fromId(int id) {
		for (final ImageType type : ImageType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}

		return null;
	}
}

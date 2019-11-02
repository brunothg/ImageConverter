package propra.imageconverter.imagecodecs.tga;

/**
 * Tga-Farbpalettentyp
 *
 * @author marvin
 *
 */
public enum TgaColorMapType {

	None(0);

	/**
	 * Id des Bildtyp nach der Spezifikation
	 */
	private int id;

	private TgaColorMapType(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	/**
	 * Gibt den Farbpalettentyp zu einer Id
	 *
	 * @param id Farbpalettentyp Id
	 * @return {@link TgaColorMapType} oder null, wenn nicht vorhanden
	 */
	public static TgaColorMapType fromId(int id) {
		for (final TgaColorMapType type : TgaColorMapType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}

		return null;
	}
}

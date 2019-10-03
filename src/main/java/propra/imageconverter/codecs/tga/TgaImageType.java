package propra.imageconverter.codecs.tga;

import java.lang.reflect.InvocationTargetException;

/**
 * Tga-Bildtyp
 *
 * @author marvin
 *
 */
public enum TgaImageType {

	/**
	 * RGB (24 oder 32 Bit) unkomprimiert
	 */
	Rgb(2, TgaRgbCompression.class);

	/**
	 * Id des Bildtyp nach der Spezifikation
	 */
	private int id;

	private Class<? extends TgaCompression> compressionClass;

	private TgaImageType(int id, Class<? extends TgaCompression> compressionClass) {
		this.id = id;
		this.compressionClass = compressionClass;
	}

	public int getId() {
		return this.id;
	}

	public Class<? extends TgaCompression> getCompressionClass() {
		return this.compressionClass;
	}

	public TgaCompression createCompressionInstance() {
		try {
			return this.getCompressionClass().getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gibt den Bildtyp zu einer Id
	 *
	 * @param id Bildtyp Id
	 * @return {@link TgaImageType} oder null, wenn nicht vorhanden
	 */
	public static TgaImageType fromId(int id) {
		for (final TgaImageType type : TgaImageType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}

		return null;
	}
}

package propra.imageconverter.codecs.propra;

import java.lang.reflect.InvocationTargetException;

/**
 * Kompressionstyp eines ProPra Bildes
 *
 * @author marvin
 *
 */
public enum CompressionType {
	None(0, NoCompression.class);

	/**
	 * Id des Kompressionstyps nach der Spezifikation
	 */
	private int id;

	private Class<? extends Compression> compressionClass;

	private CompressionType(int id, Class<? extends Compression> compressionClass) {
		this.id = id;
		this.compressionClass = compressionClass;
	}

	public int getId() {
		return this.id;
	}

	public Class<? extends Compression> getCompressionClass() {
		return this.compressionClass;
	}

	public Compression createCompressionInstance() {
		try {
			return this.getCompressionClass().getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
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

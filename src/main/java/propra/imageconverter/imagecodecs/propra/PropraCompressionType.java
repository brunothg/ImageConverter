package propra.imageconverter.imagecodecs.propra;

import java.lang.reflect.InvocationTargetException;

import propra.imageconverter.imagecodecs.propra.compression.PropraCompression;
import propra.imageconverter.imagecodecs.propra.compression.PropraNoCompression;
import propra.imageconverter.imagecodecs.propra.compression.PropraRleCompression;

/**
 * Kompressionstyp eines ProPra Bildes
 *
 * @author marvin
 *
 */
public enum PropraCompressionType {
	None(0, PropraNoCompression.class), Rle(1, PropraRleCompression.class);

	/**
	 * Id des Kompressionstyps nach der Spezifikation
	 */
	private int id;

	private Class<? extends PropraCompression> compressionClass;

	private PropraCompressionType(final int id, final Class<? extends PropraCompression> compressionClass) {
		this.id = id;
		this.compressionClass = compressionClass;
	}

	public int getId() {
		return this.id;
	}

	public Class<? extends PropraCompression> getCompressionClass() {
		return this.compressionClass;
	}

	public PropraCompression createCompressionInstance() {
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
	 * @return {@link PropraCompressionType} oder null, wenn nicht vorhanden
	 */
	public static PropraCompressionType fromId(final int id) {
		for (final PropraCompressionType type : PropraCompressionType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}

		return null;
	}
}

package propra.imageconverter.imagecodecs.tga;

import java.lang.reflect.InvocationTargetException;

import propra.imageconverter.imagecodecs.tga.compression.TgaCompression;
import propra.imageconverter.imagecodecs.tga.compression.TgaRgbCompression;
import propra.imageconverter.imagecodecs.tga.compression.TgaRleCompression;

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
	Rgb(2, TgaRgbCompression.class),

	/**
	 * RGB (24 Bit) lauflängenkodiert
	 */
	Rle(10, TgaRleCompression.class);

	/**
	 * Id des Bildtyp nach der Spezifikation
	 */
	private int id;

	private Class<? extends TgaCompression> compressionClass;

	private TgaImageType(final int id, final Class<? extends TgaCompression> compressionClass) {
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
	public static TgaImageType fromId(final int id) {
		for (final TgaImageType type : TgaImageType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}

		return null;
	}
}

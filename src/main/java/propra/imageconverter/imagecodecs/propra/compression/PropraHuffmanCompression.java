package propra.imageconverter.imagecodecs.propra.compression;

import propra.imageconverter.imagecodecs.ConversionException;

/**
 * Liest/Schreibt Pixeldaten mit Huffman Kompremierung
 *
 * @author marvin
 *
 */
public class PropraHuffmanCompression extends PropraCompression {

	@Override
	public PropraPixelDecodeValues uncompressPixelData(final PropraPixelDecodeValues values)
			throws ConversionException {
		throw new RuntimeException("PropraHuffmanCompression - uncompressPixelData - not yet implemented");
		// TODO uncompressPixelData
	}

	@Override
	public PropraPixelEncodeValues compressPixelData(final PropraPixelEncodeValues values) throws ConversionException {
		throw new RuntimeException("PropraHuffmanCompression - uncompressPixelData - not yet implemented");
		// TODO compressPixelData
	}

}

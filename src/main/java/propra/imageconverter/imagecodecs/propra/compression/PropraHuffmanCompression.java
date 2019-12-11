package propra.imageconverter.imagecodecs.propra.compression;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.utils.streams.huffman.HuffmanInputStream;
import propra.imageconverter.utils.streams.huffman.HuffmanOutputStream;
import propra.imageconverter.utils.streams.huffman.HuffmanTree;

/**
 * Liest/Schreibt Pixeldaten mit Huffman Kompremierung
 *
 * @author marvin
 *
 */
public class PropraHuffmanCompression extends PropraCompression {

    @SuppressWarnings("resource")
    @Override
    public PropraPixelDecodeValues uncompressPixelData(final PropraPixelDecodeValues values)
	    throws ConversionException {
	final InternalImage image = InternalImage.createInternalImage(values.dimension);

	final InputStream in = new HuffmanInputStream(values.compressedPixelData);
	for (int y = 0; y < values.dimension.height; y++) {
	    for (int x = 0; x < values.dimension.width; x++) {
		try {
		    final byte[] pixel = new byte[3];
		    final int read = in.readNBytes(pixel, 0, pixel.length);
		    if (read != pixel.length) {
			throw new ConversionException(
				"Pixel konnte nicht gelesen werden: " + new Point(x, y) + " : Fehlende Daten");
		    }

		    final int g = Byte.toUnsignedInt(pixel[0]);
		    final int b = Byte.toUnsignedInt(pixel[1]);
		    final int r = Byte.toUnsignedInt(pixel[2]);

		    image.setPixel(new Point(x, y), new Color(r, g, b));
		} catch (final IOException e) {
		    throw new ConversionException(
			    "Pixel konnte nicht gelesen werden: " + new Point(x, y) + " : " + e.getMessage(), e);
		}
	    }
	}

	values.uncompressedPixelData = image;
	return values;
    }

    @SuppressWarnings("resource")
    @Override
    public PropraPixelEncodeValues compressPixelData(final PropraPixelEncodeValues values) throws ConversionException {
	final HuffmanOutputStream out = new HuffmanOutputStream(values.compressedPixelData,
		this.calculateHuffmanTree(values));

	for (int y = 0; y < values.dimension.height; y++) {
	    for (int x = 0; x < values.dimension.width; x++) {

		final Color color = values.uncompressedPixelData.getPixel(new Point(x, y));

		try {
		    out.write(new byte[] { (byte) color.getGreen(), (byte) color.getBlue(), (byte) color.getRed() });
		} catch (final IOException e) {
		    throw new ConversionException("Pixeldaten können nicht geschrieben werden: " + e.getMessage(), e);
		}
	    }
	}

	try {
	    out.flushBitBuffer();
	} catch (final IOException e) {
	    new ConversionException("Bits konnten nicht geschrieben werden", e);
	}

	return values;
    }

    /**
     * Berechnet den optimalen Huffman Baum Dazu wird das Bild virtuell geschrieben
     * und die Bytes gezählt um eine relative Verteilung zu erhalten.
     * 
     * @param values
     * @return Huffman Baum
     */
    private HuffmanTree calculateHuffmanTree(PropraPixelEncodeValues values) {
	HuffmanTree huffmanTree;

	List<Heuristic> heuristics = new LinkedList<>();
	for (int i = 0; i < 256; i++) {
	    final Heuristic e = new Heuristic();
	    e.setSymbol(i);
	    heuristics.add(e);
	}

	for (int y = 0; y < values.dimension.height; y++) {
	    for (int x = 0; x < values.dimension.width; x++) {

		final Color color = values.uncompressedPixelData.getPixel(new Point(x, y));

		heuristics.get(color.getGreen()).increase();
		heuristics.get(color.getBlue()).increase();
		heuristics.get(color.getRed()).increase();
	    }
	}
	heuristics = heuristics.stream().filter((heuristic) -> heuristic.getCounter() > 0).collect(Collectors.toList());

	if (heuristics.isEmpty()) {
	    huffmanTree = HuffmanTree.generateFullTree();
	} else {
	    while (heuristics.size() > 1) {
		heuristics.sort(null);

		final Heuristic heuristic1 = heuristics.remove(0);
		final Heuristic heuristic2 = heuristics.remove(0);

		final Heuristic combined = new Heuristic();
		combined.increase(heuristic1.getCounter());
		combined.increase(heuristic2.getCounter());
		combined.setSubTree(new HuffmanTree());
		if (heuristic1.getSubTree() != null) {
		    combined.getSubTree().setRightTree(heuristic1.getSubTree());
		} else {
		    combined.getSubTree().setRightValue(heuristic1.getSymbol());
		}
		if (heuristic2.getSubTree() != null) {
		    combined.getSubTree().setLeftTree(heuristic2.getSubTree());
		} else {
		    combined.getSubTree().setLeftValue(heuristic2.getSymbol());
		}
		heuristics.add(combined);
	    }

	    final Heuristic finalHeuristic = heuristics.get(0);
	    if (finalHeuristic.getSubTree() == null) {
		finalHeuristic.setSubTree(new HuffmanTree());
		finalHeuristic.getSubTree().setLeftValue(finalHeuristic.getSymbol());
	    }

	    huffmanTree = finalHeuristic.getSubTree();
	    if (huffmanTree.getLeftTree() == null) {
		huffmanTree.setLeftValue(0);
	    }
	    if (huffmanTree.getRightTree() == null) {
		huffmanTree.setRightValue(0);
	    }
	}

	return huffmanTree;
    }

    /**
     * Hilfsklasse (Heuristik) zum Erstellen des Huffman Baums
     * 
     * @author marvin
     *
     */
    private static class Heuristic implements Comparable<Heuristic> {
	private HuffmanTree subTree = null;
	private int symbol = 0;
	private long counter = 0;

	public int getSymbol() {
	    return this.symbol;
	}

	public void setSymbol(int symbol) {
	    this.symbol = symbol;
	    this.setSubTree(null);
	}

	/**
	 * @return the subTree
	 */
	public HuffmanTree getSubTree() {
	    return this.subTree;
	}

	/**
	 * @param subTree the subTree to set
	 */
	public void setSubTree(HuffmanTree subTree) {
	    this.subTree = subTree;
	}

	public long getCounter() {
	    return this.counter;
	}

	public void increase() {
	    if (this.counter < Long.MAX_VALUE) {
		this.counter++;
	    }
	}

	public void increase(long amount) {
	    for (int i = 0; i < amount; i++) {
		increase();
	    }
	}

	@Override
	public int compareTo(Heuristic o) {
	    if (o == null) {
		return 1;
	    }

	    int compare = Long.compare(this.counter, o.counter);
	    if (compare == 0) {
		compare = Integer.compare((this.getSubTree() != null) ? this.getSubTree().getTreeSize() : 0,
			(o.getSubTree() != null) ? o.getSubTree().getTreeSize() : 0);
	    }

	    return compare;
	}
    }

}

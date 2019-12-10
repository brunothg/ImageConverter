package propra.imageconverter.utils.streams.huffman;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import propra.imageconverter.utils.streams.bytes.BitInputStream;

/**
 * InputStream, der Bytes nach der Huffman Kompremiereung liest.
 *
 * @author marvin
 *
 */
public class HuffmanInputStream extends InputStream {

	private final BitInputStream in;
	private final HuffmanTree huffmanTree = new HuffmanTree();

	public HuffmanInputStream(final InputStream in) {
		this.in = new BitInputStream(Objects.requireNonNull(in, "in"));

		try {
			this.init();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initialisiert den Stream und liest den Huffmanbaum ein
	 *
	 * @throws IOException
	 */
	private void init() throws IOException {

		if (this.in.readBit() != 0) {
			throw new IOException("Wurzelknoten des Huffman Baums fehlt");
		}

		HuffmanTree actualTree = this.huffmanTree;
		while ((actualTree.getLeftTree() == null) || (actualTree.getRightTree() == null)) {
			final short typeBit = this.in.readBit();

			if (typeBit == 0) { // Neuer Unterbaum
				final HuffmanTree newSubTree = new HuffmanTree();
				if (actualTree.getLeftTree() == null) {
					actualTree.setLeftTree(newSubTree);
				} else {
					actualTree.setRightTree(newSubTree);
				}
				actualTree = newSubTree;
			} else if (typeBit == 1) { // Neues Symbol
				final int newValue = this.in.read();
				if (newValue == -1) {
					throw new IOException("Fehlende Daten beim lesen des Huffman Baums");
				}

				// Neues Symbol einfügen
				if (actualTree.getLeftTree() == null) {
					actualTree.setLeftValue(newValue);
				} else {
					actualTree.setRightValue(newValue);
				}

				// Baum zurücklaufen, bis links oder rechts ein freie Platz vorhanden ist oder
				// der Baum komplett ist
				while ((actualTree != null) && (actualTree.getLeftTree() != null)
						&& (actualTree.getRightTree() != null)) {
					actualTree = actualTree.getParent();
				}
				if (actualTree == null) {
					break;
				}
			} else {
				throw new IOException("Falsche oder fehlende Daten beim lesen des Huffman Baums");
			}
		}
	}

	@Override
	public int read() throws IOException {

		Integer byteValue = null;
		HuffmanTree actualTree = this.huffmanTree;
		short directionBit = this.in.readBit();

		if (directionBit == -1) {
			return -1;
		}

		while (true) {

			if (directionBit == 0 /* Links */) {
				actualTree = actualTree.getLeftTree();
			} else if (directionBit == 1 /* Rechts */) {
				actualTree = actualTree.getRightTree();
			} else if (directionBit == -1) {
				throw new IOException("EOF erreicht - Zu wenig Daten vorhanden");
			} else {
				throw new RuntimeException("Unbekanntes directionBit " + directionBit);
			}

			byteValue = actualTree.getValue();
			if (byteValue != null) {
				break;
			} else {
				directionBit = this.in.readBit();
			}
		}

		return byteValue;
	}

	@Override
	public void close() throws IOException {
		super.close();
		this.in.close();
	}
}

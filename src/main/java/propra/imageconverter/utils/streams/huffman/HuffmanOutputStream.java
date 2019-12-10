package propra.imageconverter.utils.streams.huffman;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Stack;

import propra.imageconverter.utils.streams.bytes.BitOutputStream;

/**
 * OutputStream, der Bytes nach der Huffman Kompremiereung schreibt. Nur
 * vollständige Bytes werden sofort geschrieben. Deswegen ist es wichtig den
 * Stream zu schließen bzw. das schreiben der Bits zu erzwingen.
 *
 * @author marvin
 *
 */
public class HuffmanOutputStream extends OutputStream {

	private final BitOutputStream out;
	private final short[][] symbols = new short[256][0];

	public HuffmanOutputStream(final OutputStream out, final HuffmanTree huffmanTree) {
		this.out = new BitOutputStream(Objects.requireNonNull(out, "out"));

		try {
			this.init(Objects.requireNonNull(huffmanTree, "huffmanTree"));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public HuffmanOutputStream(final OutputStream out) {
		this(out, HuffmanTree.generateFullTree());
	}

	/**
	 * Initialisiert den Stream. Schreibt zu Beginn den Huffman Baum und indexiert
	 * diesen.
	 *
	 * @param huffmanTree
	 *
	 * @throws IOException
	 */
	private void init(final HuffmanTree huffmanTree) throws IOException {

		final Stack<HuffmanTree> treeStack = new Stack<>();
		treeStack.push(huffmanTree);
		while (!treeStack.isEmpty()) {
			final HuffmanTree actualTree = treeStack.pop();
			final Integer value = actualTree.getValue();

			if (value != null) {
				this.out.writeBit(1);
				this.out.write(value);

				this.symbols[value] = this.getSymbolPath(actualTree);
			} else {
				this.out.writeBit(0);

				HuffmanTree rightTree = actualTree.getRightTree();
				if (rightTree == null) /* Ungültiger Baum - Benutze Default */ {
					actualTree.setRightValue(0);
					rightTree = actualTree.getRightTree();
				}

				HuffmanTree leftTree = actualTree.getLeftTree();
				if (leftTree == null) /* Ungültiger Baum - Benutze Default */ {
					actualTree.setLeftValue(0);
					leftTree = actualTree.getLeftTree();
				}

				treeStack.push(rightTree);
				treeStack.push(leftTree);
			}

		}
	}

	private short[] getSymbolPath(HuffmanTree symbolLeaf) {
		final Deque<Short> treePathId = new LinkedList<>();

		HuffmanTree symbolLeafParent = symbolLeaf;
		while ((symbolLeafParent = symbolLeafParent.getParent()) != null) {
			if (symbolLeafParent.getLeftTree() == symbolLeaf) {
				treePathId.push((short) 0);
			} else if (symbolLeafParent.getRightTree() == symbolLeaf) {
				treePathId.push((short) 1);
			} else {
				throw new RuntimeException("Huffman Baum ist defekt");
			}
			symbolLeaf = symbolLeafParent;
		}

		final short[] result = new short[treePathId.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = treePathId.poll();
		}
		return result;
	}

	@Override
	public void write(final int b) throws IOException {
		final int byteValue = b & 0xFF;

		final short[] symbolPathBits = this.symbols[byteValue];
		if (symbolPathBits.length <= 0) {
			throw new RuntimeException("Missing symbol: " + byteValue);
		}

		for (final short symbolPathBit : symbolPathBits) {
			this.out.writeBit(symbolPathBit);
		}
	}

	/**
	 * @see BitOutputStream#flushBitBuffer()
	 * @throws IOException
	 */
	public void flushBitBuffer() throws IOException {
		this.out.flushBitBuffer();
	}

	@Override
	public void flush() throws IOException {
		this.out.flush();
	}

	@Override
	public void close() throws IOException {
		this.out.close();
	}
}

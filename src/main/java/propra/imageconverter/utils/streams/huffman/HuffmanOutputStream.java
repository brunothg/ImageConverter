package propra.imageconverter.utils.streams.huffman;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
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
	private final HuffmanTree huffmanTree;

	public HuffmanOutputStream(final OutputStream out, final HuffmanTree huffmanTree) {
		this.out = new BitOutputStream(Objects.requireNonNull(out, "out"));
		this.huffmanTree = Objects.requireNonNull(huffmanTree, "huffmanTree");

		try {
			this.init();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public HuffmanOutputStream(final OutputStream out) {
		this(out, HuffmanTree.generateFullTree());
	}

	/**
	 * Initialisiert den Stream. Schreibt zu Beginn den Huffman Baum.
	 *
	 * @throws IOException
	 */
	private void init() throws IOException {

		final Stack<HuffmanTree> treeStack = new Stack<>();
		treeStack.push(this.huffmanTree);
		while (!treeStack.isEmpty()) {
			final HuffmanTree actualTree = treeStack.pop();
			final Integer value = actualTree.getValue();

			if (value != null) {
				this.out.writeBit(1);
				this.out.write(value);

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

	@Override
	public void write(final int b) throws IOException {
		final int byteValue = b & 0xFF;

		// Suchen der Symbols
		final List<HuffmanTree> stack = new LinkedList<>();
		stack.add(this.huffmanTree);

		HuffmanTree symbolLeaf = null;
		while (!stack.isEmpty()) {
			final HuffmanTree actual = stack.remove(0);
			final Integer value = actual.getValue();
			if (value == null) {
				if (actual.getLeftTree() != null) {
					stack.add(actual.getLeftTree());
				}
				if (actual.getRightTree() != null) {
					stack.add(actual.getRightTree());
				}
			} else if (value == byteValue) {
				symbolLeaf = actual;
				break;
			}
		}

		// Symbolpfad rekonstruieren
		if (symbolLeaf != null) {
			final Stack<Short> treePathId = new Stack<>();

			HuffmanTree symbolLeafParent = symbolLeaf;
			while ((symbolLeafParent = symbolLeafParent.getParent()) != null) {
				if (symbolLeafParent.getLeftTree() == symbolLeaf) {
					treePathId.push((short) 0);
				} else if (symbolLeafParent.getRightTree() == symbolLeaf) {
					treePathId.push((short) 1);
				} else {
					throw new IOException("Huffman Baum ist defekt");
				}
				symbolLeaf = symbolLeafParent;
			}

			while (!treePathId.empty()) {
				this.out.writeBit(treePathId.pop());
			}
		} else {
			throw new IOException("Symbol wurde nicht gefunden: " + byteValue);
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

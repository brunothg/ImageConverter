package propra.imageconverter.utils.streams.huffman;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Realisierung des Huffman Baums
 *
 * @author marvin
 *
 */
public class HuffmanTree {

    private HuffmanTree parent = null;
    private HuffmanTree leftTree = null;
    private HuffmanTree rightTree = null;

    private Integer value = null;

    public HuffmanTree() {
    }

    private HuffmanTree(final Integer value) {
	this.setValue(value);
    }

    /**
     * Gibt den Elternbaum
     *
     * @return Elternbaum oder null, wenn nicht vorhanden
     */
    public HuffmanTree getParent() {
	return this.parent;
    }

    private void setParent(final HuffmanTree parent) {
	this.parent = parent;
    }

    /**
     * Gibt den linken Teilbaum
     *
     * @return Linker Teilbaum oder null, wenn nicht vorhanden
     */
    public HuffmanTree getLeftTree() {
	return this.leftTree;
    }

    /**
     * Gibt den rechten Teilbaum
     *
     * @return Rechter Teilbaum oder null, wenn nicht vorhanden
     */
    public HuffmanTree getRightTree() {
	return this.rightTree;
    }

    /**
     * Setzt den linken Teilbaum. Löscht gegebenfalls vorhandenen Wert (Teilbaum
     * kann kein Blatt sein).
     *
     * @param leftTree
     */
    public void setLeftTree(final HuffmanTree leftTree) {
	if ((this.leftTree != null) && (this.leftTree.getParent() != null)) {
	    this.leftTree.setParent(null);
	}

	this.leftTree = leftTree;
	if (this.leftTree != null) {
	    this.leftTree.setParent(this);
	}
	this.value = null;
    }

    /**
     * Setzt den rechten Teilbaum. Löscht gegebenfalls vorhandenen Wert (Teilbaum
     * kann kein Blatt sein).
     *
     * @param rightTree
     */
    public void setRightTree(final HuffmanTree rightTree) {
	if ((this.rightTree != null) && (this.rightTree.getParent() != null)) {
	    this.rightTree.setParent(null);
	}

	this.rightTree = rightTree;
	if (this.rightTree != null) {
	    this.rightTree.setParent(this);
	}
	this.value = null;
    }

    /**
     * Gibt den Wert (0-255), wenn es sich um ein Blatt handelt
     *
     * @return Wert oder null, wenn kein Blatt
     */
    public Integer getValue() {
	return this.value;
    }

    /**
     * Setzt den Wert (0-255). Löscht linken und rechten Teilbaum (Blatt kann kein
     * Teilbaum sein)
     *
     * @param value
     */
    private void setValue(final Integer value) {
	this.setLeftTree(null);
	this.setRightTree(null);

	this.value = (value == null) ? null : ((value) & 0xFF);
    }

    /**
     * Setzt das linke Blatt
     *
     * @param value
     */
    public void setLeftValue(final Integer value) {
	this.setLeftTree(new HuffmanTree(value));
    }

    /**
     * Setzt das rechte Blatt
     *
     * @param value
     */
    public void setRightValue(final Integer value) {
	this.setRightTree(new HuffmanTree(value));
    }

    /**
     * Gibt die Anzahl der Knoten (inklusive Blätter) im Baum
     * 
     * @return Knotenzahl oder Integer.MAX_VALUE, wenn zu groß
     */
    public int getTreeSize() {
	int size = 0;

	final Queue<HuffmanTree> parts = new LinkedList<>();
	parts.add(this);
	while (!parts.isEmpty()) {
	    final HuffmanTree part = parts.poll();
	    size++;

	    if (part.getLeftTree() != null) {
		parts.add(part.getLeftTree());
	    }
	    if (part.getRightTree() != null) {
		parts.add(part.getRightTree());
	    }
	}

	return size;
    }

    @Override
    public String toString() {
	return "HuffmanTree [" + "value=" + this.value + ", leftTree=" + this.leftTree + ", rightTree=" + this.rightTree
		+ "]";
    }

    /**
     * Erstellt einen Huffman Baum, der alle 256 möglichen Byte-Werte enthält. Eine
     * Optimierung des Baums findet nicht statt.
     *
     * @return Vollständigen Huffman Baum
     */
    public static HuffmanTree generateFullTree() {
	final HuffmanTree huffmanTree = new HuffmanTree();

	HuffmanTree actualTree = huffmanTree;
	for (int i = 0; i < 256; i++) {
	    actualTree.setLeftValue(i);

	    final HuffmanTree newSubTree = new HuffmanTree();
	    actualTree.setRightTree(newSubTree);
	    actualTree = newSubTree;
	}
	if (actualTree.getLeftTree() == null) {
	    actualTree.setLeftValue(0);
	}
	if (actualTree.getRightTree() == null) {
	    actualTree.setRightValue(0);
	}

	return huffmanTree;
    }

}

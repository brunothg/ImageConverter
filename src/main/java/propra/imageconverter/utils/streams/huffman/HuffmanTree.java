package propra.imageconverter.utils.streams.huffman;

/**
 * Realisierung des Huffman Baums
 *
 * @author marvin
 *
 */
class HuffmanTree {

	private HuffmanTree parent = null;
	private HuffmanTree leftTree = null;
	private HuffmanTree rightTree = null;

	private Integer value = null;

	public HuffmanTree() {
	}

	private HuffmanTree(final Integer value) {
		this.setValue(value);
	}

	public HuffmanTree getParent() {
		return this.parent;
	}

	private void setParent(final HuffmanTree parent) {
		this.parent = parent;
	}

	public HuffmanTree getLeftTree() {
		return this.leftTree;
	}

	public HuffmanTree getRightTree() {
		return this.rightTree;
	}

	public void setLeftTree(final HuffmanTree leftTree) {
		this.leftTree = leftTree;
		this.leftTree.setParent(this);
		this.value = null;
	}

	public void setRightTree(final HuffmanTree rightTree) {
		this.rightTree = rightTree;
		this.rightTree.setParent(this);
		this.value = null;
	}

	public Integer getValue() {
		return this.value;
	}

	private void setValue(final Integer value) {
		this.value = (value == null) ? null : ((value) & 0xFF);
		this.leftTree = null;
		this.rightTree = null;
	}

	public void setLeftValue(final Integer newValue) {
		this.leftTree = new HuffmanTree(newValue);
		this.leftTree.setParent(this);
	}

	public void setRightValue(final Integer newValue) {
		this.rightTree = new HuffmanTree(newValue);
		this.leftTree.setParent(this);
	}

}

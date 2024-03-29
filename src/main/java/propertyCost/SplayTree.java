package propertyCost;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SplayTree<K extends Comparable<K>, V> {
	private Node root;

	private class Node {
		K key;
		V value;
		Node left, right;

		Node(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	public boolean contains(K key) {
		root = splay(root, key);
		return root != null && root.key.compareTo(key) == 0;
	}

	public V getValue(K key) {
		root = splay(root, key);
		return root != null && root.key.compareTo(key) == 0 ? root.value : null;
	}

	public void insert(K key, V value) {
		if (root == null) {
			root = new Node(key, value);
			return;
		}

		root = splay(root, key);

		int cmp = key.compareTo(root.key);
		if (cmp < 0) {
			Node newNode = new Node(key, value);
			newNode.left = root.left;
			newNode.right = root;
			root.left = null;
			root = newNode;
		} else if (cmp > 0) {
			Node newNode = new Node(key, value);
			newNode.right = root.right;
			newNode.left = root;
			root.right = null;
			root = newNode;
		} else {
			// Key already exists, update value
			root.value = value;
		}
	}

	public void remove(K key) {
		if (root == null)
			return;

		root = splay(root, key);

		if (root.key.compareTo(key) != 0)
			return;

		if (root.left == null) {
			root = root.right;
		} else {
			Node newRoot = root.right;
			root = splay(root.left, key);
			root.right = newRoot;
		}
	}

	public List<Map.Entry<K, V>> inOrderTraversal() {
		List<Map.Entry<K, V>> list = new ArrayList<>();
		inOrderTraversal(root, list);
		return list;
	}

	private void inOrderTraversal(Node node, List<Map.Entry<K, V>> list) {
		if (node != null) {
			inOrderTraversal(node.left, list);
			list.add(new AbstractMap.SimpleEntry<>(node.key, node.value));
			inOrderTraversal(node.right, list);
		}
	}

	private Node splay(Node h, K key) {
		if (h == null)
			return null;

		int cmp1 = key.compareTo(h.key);
		if (cmp1 < 0) {
			if (h.left == null)
				return h;
			int cmp2 = key.compareTo(h.left.key);
			if (cmp2 < 0) {
				h.left.left = splay(h.left.left, key);
				h = rotateRight(h);
			} else if (cmp2 > 0) {
				h.left.right = splay(h.left.right, key);
				if (h.left.right != null)
					h.left = rotateLeft(h.left);
			}

			if (h.left == null)
				return h;
			else
				return rotateRight(h);
		} else if (cmp1 > 0) {
			if (h.right == null)
				return h;
			int cmp2 = key.compareTo(h.right.key);
			if (cmp2 < 0) {
				h.right.left = splay(h.right.left, key);
				if (h.right.left != null)
					h.right = rotateRight(h.right);
			} else if (cmp2 > 0) {
				h.right.right = splay(h.right.right, key);
				h = rotateLeft(h);
			}

			if (h.right == null)
				return h;
			else
				return rotateLeft(h);
		} else {
			return h;
		}
	}

	private Node rotateRight(Node h) {
		Node x = h.left;
		h.left = x.right;
		x.right = h;
		return x;
	}

	private Node rotateLeft(Node h) {
		Node x = h.right;
		h.right = x.left;
		x.left = h;
		return x;
	}
	  
}
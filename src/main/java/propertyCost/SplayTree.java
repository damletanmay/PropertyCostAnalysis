package propertyCost;


// Importing libraries
import java.io.Serializable;
import java.util.*;


// Splay Tree 
// k ==> comparable <k>
//public interface Comparable<T> {
//    int compareTo(T o);
//}

// Serializable : - to serlialize
// here we have done this in order to save the data into .dat files


public class SplayTree <K extends Comparable<K>, V> implements Serializable{
	private Node root;

	private class Node implements Serializable {
		
		
		//here k and v i.e. key and value are type parameter
		// so it can be anything
		//int or char or anything thy are kidnd of objects
		K key;
		V value;
		Node left; // node for left side
		Node right;// node for right side

		Node(K key, V value) {
			
			// passing paramters to data 
			// parameterized constructor 
			
			this.key = key;
			this.value = value;
		}
	}

	public boolean contains(K key) {
		// Conatains function
		// contains(K) 
		// if present ==> true
		// if not ==> false
		
		
		root = splayoFST(root, key);
		// splaying each time 
		return root != null && root.key.compareTo(key) == 0; 
	}

	public V getValue(K key) {
		// splay tree 
		root = splayoFST(root, key);
		return root != null && root.key.compareTo(key) == 0 ? root.value : null;
	}

	
	// For inserting the key and value
	
	public void insert(K key, V value) {
		
		// Insert a node
		// if no node or null it will be root 
		if (root == null) {
			
		// else splay
			root = new Node(key, value);
			return;
		}
		
		
		// splaying each time 
		// while inserting each elements
		root = splayoFST(root, key);

		int compare = key.compareTo(root.key);
		
		// compare each node as insertion will depend on properties
		if (compare < 0) {
			// nNode=> New Node
			Node nNode = new Node(key, value);
			nNode.left = root.left;
			nNode.right = root;
			root.left = null;
			root = nNode;
		} else if (compare > 0) {
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

	// remove ==> give key
	// We can pass the key will be removed
	public void remove(K key) {
		if (root == null)
			return;

		// Splay the tree each time
		root = splayoFST(root, key);

		if (root.key.compareTo(key) != 0)
			return;

		// For the leftside
		if (root.left == null) {
			root = root.right;
		} else {
			Node newRoot = root.right;
			root = splayoFST(root.left, key);
			root.right = newRoot;
		}
	}

	// Mapping for inorder traverasal
	public List<Map.Entry<K, V>> inOrderTraversal() {
		List<Map.Entry<K, V>> list = new ArrayList<>();
		inOrderTraversal(root, list);
		return list;
	}

	// Here we are using inorder traversal
	// left current right
	
	// helper method 
	private void inOrderTraversal(Node node, List<Map.Entry<K, V>> list) {
		if (node != null) {
			// In Order traversal for left side
			inOrderTraversal(node.left, list); //calling
			list.add(new AbstractMap.SimpleEntry<>(node.key, node.value));
			// In Order traversal for right side
			inOrderTraversal(node.right, list); //calling
		}
	}

	
	// Logic for splaying of code
	private Node splayoFST(Node nodeOfsT, K key) {
		if (nodeOfsT == null)
			return null;

		// Comapring nodes
		int comparew1 = key.compareTo(nodeOfsT.key); 
		
		
		
		// Logic for splaying in both sides
		if (comparew1 < 0) {
			if (nodeOfsT.left == null)
				return nodeOfsT;
			int comparew2 = key.compareTo(nodeOfsT.left.key);
			if (comparew2 < 0) {
				nodeOfsT.left.left = splayoFST(nodeOfsT.left.left, key);
				nodeOfsT = rotateRight(nodeOfsT);
			} else if (comparew2 > 0) {
				nodeOfsT.left.right = splayoFST(nodeOfsT.left.right, key);
				if (nodeOfsT.left.right != null)
					nodeOfsT.left = rotateLeft(nodeOfsT.left);
			}

			if (nodeOfsT.left == null)
				return nodeOfsT;
			else
				return rotateRight(nodeOfsT);
		} else if (comparew1 > 0) {
			if (nodeOfsT.right == null)
				return nodeOfsT;
			int comparew2 = key.compareTo(nodeOfsT.right.key);
			if (comparew2 < 0) {
				nodeOfsT.right.left = splayoFST(nodeOfsT.right.left, key);
				if (nodeOfsT.right.left != null)
					nodeOfsT.right = rotateRight(nodeOfsT.right);
			} else if (comparew2 > 0) {
				nodeOfsT.right.right = splayoFST(nodeOfsT.right.right, key);
				nodeOfsT = rotateLeft(nodeOfsT);
			}

			if (nodeOfsT.right == null)
				return nodeOfsT;
			else
				return rotateLeft(nodeOfsT);
		} else {
			return nodeOfsT;
		}
	}

	
	// For Performing left and right rotation in trees
	
	// for RR & RL rotation
	
	private Node rotateRight(Node h1) {
		// Swaping method
		Node temp = h1.left;
		h1.left = temp.right;
		temp.right = h1;
		return temp;
	}

	// For lr & ll Rotation
	
	private Node rotateLeft(Node h2) {
		// Swaping method
		Node temp = h2.right;
		h2.right = temp.left;
		temp.left = h2;
		return temp;
	}
	  
}
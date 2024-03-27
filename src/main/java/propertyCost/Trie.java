package propertyCost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TrieNode {
    Map<Character, TrieNode> children;
    Set<String> documents;

    public TrieNode() {
        children = new HashMap<>();
        documents = new HashSet<>();
    }
}

public class Trie {
    static TrieNode root;

    public Trie() {
        if (root == null) {
            root = new TrieNode();
        }
    }

    public void insert(String term, String documentId) {
        term = term.toLowerCase();
        TrieNode node = root;
        for (char c : term.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.documents.add(documentId);
    }
    
    public Set<String> search(String term) {
    	term = term.toLowerCase();
        TrieNode node = root;
        for (char c : term.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new HashSet<>();
            }
            node = node.children.get(c);
        }
        return node.documents;
    }
}


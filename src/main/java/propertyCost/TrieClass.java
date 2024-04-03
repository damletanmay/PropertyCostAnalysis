package propertyCost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class NodeofTrie {
    Map<Character, NodeofTrie> nodeChildren;
    Set<String> nodeDocs;

    public NodeofTrie() {
        nodeChildren = new HashMap<>();
        nodeDocs = new HashSet<>();
    }
}

public class TrieClass {
    static NodeofTrie nodeRoot;

    public TrieClass() {
        if (nodeRoot == null) {
            nodeRoot = new NodeofTrie();
        }
    }

    public void insertNode(String cityName, String docName) {
        cityName = cityName.toLowerCase();
        NodeofTrie nodeOfTrie = nodeRoot;
        for (char ixp : cityName.toCharArray()) {
            nodeOfTrie.nodeChildren.putIfAbsent(ixp, new NodeofTrie());
            nodeOfTrie = nodeOfTrie.nodeChildren.get(ixp);
        }
        nodeOfTrie.nodeDocs.add(docName);
    }
    
    public Set<String> searchNode(String cityName) {
    	cityName = cityName.toLowerCase();
        NodeofTrie nodeOfTrie = nodeRoot;
        for (char ixp : cityName.toCharArray()) {
            if (!nodeOfTrie.nodeChildren.containsKey(ixp)) {
                return new HashSet<>();
            }
            nodeOfTrie = nodeOfTrie.nodeChildren.get(ixp);
        }
        return nodeOfTrie.nodeDocs;
    }
}


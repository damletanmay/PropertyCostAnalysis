package propertyCost;
import java.io.*;
import java.util.*;

public class TrieSerialization {

    // Save trie to a .dat file
    public static void saveTrieToDatFile(TrieClass trie, String filePath) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            Map<Character, NodeofTrie> rootChildren = TrieClass.nodeRoot.nodeChildren;
            saveNode(outputStream, rootChildren);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Recursively save trie node and its children
    private static void saveNode(ObjectOutputStream outputStream, Map<Character, NodeofTrie> children) throws IOException {
        outputStream.writeInt(children.size()); // Save number of children
        for (Map.Entry<Character, NodeofTrie> entry : children.entrySet()) {
            outputStream.writeChar(entry.getKey()); // Save character
            NodeofTrie node = entry.getValue();
            outputStream.writeInt(node.nodeDocs.size()); // Save number of documents
            for (String document : node.nodeDocs) {
                outputStream.writeUTF(document); // Save document
            }
            saveNode(outputStream, node.nodeChildren); // Recursively save children
        }
    }

    // Load trie from a .dat file
    public static TrieClass loadTrieFromDatFile(String filePath) {
        TrieClass trie = new TrieClass();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            Map<Character, NodeofTrie> rootChildren = new HashMap<>();
            loadNode(inputStream, rootChildren); // Load root node
            TrieClass.nodeRoot.nodeChildren = rootChildren;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trie;
    }

    // Recursively load trie node and its children
    private static void loadNode(ObjectInputStream inputStream, Map<Character, NodeofTrie> children) throws IOException {
        int numChildren = inputStream.readInt(); // Read number of children
        for (int i = 0; i < numChildren; i++) {
            char character = inputStream.readChar(); // Read character
            int numDocuments = inputStream.readInt(); // Read number of documents
            NodeofTrie node = new NodeofTrie();
            for (int j = 0; j < numDocuments; j++) {
                String document = inputStream.readUTF(); // Read document
                node.nodeDocs.add(document);
            }
            loadNode(inputStream, node.nodeChildren); // Recursively load children
            children.put(character, node);
        }
    }
}

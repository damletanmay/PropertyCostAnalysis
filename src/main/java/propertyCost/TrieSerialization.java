package propertyCost;
import java.io.*;
import java.util.*;

public class TrieSerialization {

    // Save trie to a .dat file
    public static void saveTrieToDatFile(Trie trie, String filePath) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            Map<Character, TrieNode> rootChildren = Trie.root.children;
            saveNode(outputStream, rootChildren);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Recursively save trie node and its children
    private static void saveNode(ObjectOutputStream outputStream, Map<Character, TrieNode> children) throws IOException {
        outputStream.writeInt(children.size()); // Save number of children
        for (Map.Entry<Character, TrieNode> entry : children.entrySet()) {
            outputStream.writeChar(entry.getKey()); // Save character
            TrieNode node = entry.getValue();
            outputStream.writeInt(node.documents.size()); // Save number of documents
            for (String document : node.documents) {
                outputStream.writeUTF(document); // Save document
            }
            saveNode(outputStream, node.children); // Recursively save children
        }
    }

    // Load trie from a .dat file
    public static Trie loadTrieFromDatFile(String filePath) {
        Trie trie = new Trie();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            Map<Character, TrieNode> rootChildren = new HashMap<>();
            loadNode(inputStream, rootChildren); // Load root node
            Trie.root.children = rootChildren;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trie;
    }

    // Recursively load trie node and its children
    private static void loadNode(ObjectInputStream inputStream, Map<Character, TrieNode> children) throws IOException {
        int numChildren = inputStream.readInt(); // Read number of children
        for (int i = 0; i < numChildren; i++) {
            char character = inputStream.readChar(); // Read character
            int numDocuments = inputStream.readInt(); // Read number of documents
            TrieNode node = new TrieNode();
            for (int j = 0; j < numDocuments; j++) {
                String document = inputStream.readUTF(); // Read document
                node.documents.add(document);
            }
            loadNode(inputStream, node.children); // Recursively load children
            children.put(character, node);
        }
    }
}

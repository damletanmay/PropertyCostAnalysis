import java.util.*;
import java.io.*;

public class AutocompleteTrieMap {

    static class TrieNode {
        char data;
        HashMap<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;

        TrieNode(char c) {
            this.data = c;
        }
    }

    static class Trie {

        TrieNode root = new TrieNode(' ');

        // Add a word to trie, Iteration, Time O(s), Space O(s), s is word length
        void insert(String word) {
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                if (!node.children.containsKey(ch))
                    node.children.put(ch, new TrieNode(ch));
                node = node.children.get(ch);
            }
            node.isEnd = true;
        }

        // find all word with given prefix
        // Time O(n), Space O(n), n is number of nodes involved (include prefix and branches)
        List<String> autocomplete(String prefix) {
            List<String> res = new LinkedList<String>();
            TrieNode node = root;
            for (char ch : prefix.toCharArray()) {
                if (node.children.containsKey(ch))
                    node = node.children.get(ch);
                else
                    return res;
            }
            helper(node, res, prefix.substring(0, prefix.length() - 1));
            return res;
        }

        // recursive function called by autocomplete
        // Time O(n), Space O(n), n is number of nodes in branches
        void helper(TrieNode node, List<String> res, String prefix) {
            if (node.isEnd)
                res.add(prefix + node.data);
            for (Character ch : node.children.keySet())
                helper(node.children.get(ch), res, prefix + node.data);
        }
    }

   

    // Load words from a dictionary file into the trie
    static void loadDictionary(String filename, Trie trie) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim().toLowerCase();
                trie.insert(word);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Dictionary file not found: " + filename);
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Trie t = new Trie();
        loadDictionary("C:\\Users\\vatsa\\Downloads\\AutocompleteWithTrie\\dictionary.txt", t);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nEnter region where you want to find houses (or type 'exit' to quit): ");
            String prefix = scanner.nextLine().trim().toLowerCase();
            if (prefix.equals("exit"))
                break;

            List<String> suggestions = t.autocomplete(prefix);
            if (!suggestions.isEmpty()) {
                System.out.println("\nDid you mean:");
                for (String suggestion : suggestions) {
                    System.out.println(suggestion);
                }
            } else {
                System.out.println("No autocomplete suggestions found.");
            }
        }
        scanner.close();
    }
}

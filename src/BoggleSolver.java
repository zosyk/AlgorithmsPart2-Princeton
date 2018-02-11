import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {

    private BoggleTrieST<Integer> dict = new BoggleTrieST<>();

    private static class BoggleTrieST<Value> {
        private static final int R = 26; // A-Z letters
        private static final int OFFSET = 65; // Offset of letter A in ASCII table

        private Node root = new Node();

        private static class Node {
            private Object val;
            private Node[] next = new Node[R];
        }

        public boolean isPrefix(String prefix) {
            return get(root, prefix, 0) != null;
        }

        /****************************************************
         * Is the key in the symbol table?
         ****************************************************/
        public boolean contains(String key) {
            return get(key) != null;
        }

        public Value get(String key) {
            Node x = get(root, key, 0);
            if (x == null)
                return null;
            return (Value) x.val;
        }

        private Node get(Node x, String key, int d) {
            if (x == null)
                return null;
            if (d == key.length())
                return x;
            char c = key.charAt(d);
            return get(x.next[c - OFFSET], key, d + 1);
        }

        /****************************************************
         * Insert key-value pair into the symbol table.
         ****************************************************/
        public void put(String key, Value val) {
            root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, Value val, int d) {
            if (x == null)
                x = new Node();
            if (d == key.length()) {
                x.val = val;
                return x;
            }
            char c = key.charAt(d);
            x.next[c - OFFSET] = put(x.next[c - OFFSET], key, val, d + 1);
            return x;
        }
    }


    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary)
            dict.put(word, 1);

    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {

        Set<String> set = new HashSet<>();

        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                findAllWords(i, j, board, "", set);
            }
        }

        return set;
    }

    private void findAllWords(int i, int j, BoggleBoard board, String currentWord, Set<String> set) {
        boolean[][] visited = new boolean[board.rows()][board.cols()];
        dfs(board, i, j, set, visited, currentWord);
    }

    private void dfs(BoggleBoard board, int i, int j, Set<String> words, boolean[][] visited, String prefix) {
        if (visited[i][j]) {
            return;
        }

        char letter = board.getLetter(i, j);
        prefix = prefix + (letter == 'Q' ? "QU" : letter);

        if (prefix.length() > 2 && dict.contains(prefix)) {
            words.add(prefix);
        }

        if (!dict.isPrefix(prefix)) {
            return;
        }

        visited[i][j] = true;

        // do a DFS for all adjacent cells
        if (i > 0) {
            dfs(board, i - 1, j, words, visited, prefix);
            if (j > 0) {
                dfs(board, i - 1, j - 1, words, visited, prefix);
            }
            if (j < board.cols() - 1) {
                dfs(board, i - 1, j + 1, words, visited, prefix);
            }
        }
        if (j > 0) {
            dfs(board, i, j - 1, words, visited, prefix);
        }
        if (j < board.cols() - 1) {
            dfs(board, i, j + 1, words, visited, prefix);
        }
        if (i < board.rows() - 1) {
            if (j > 0) {
                dfs(board, i + 1, j - 1, words, visited, prefix);
            }
            if (j < board.cols() - 1) {
                dfs(board, i + 1, j + 1, words, visited, prefix);
            }
            dfs(board, i + 1, j, words, visited, prefix);
        }
        visited[i][j] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (dict.contains(word)) {
            return scoreOfValid(word);
        }
        return 0;
    }

    private int scoreOfValid(String word) {
        int wordLength = word.length();

        switch (wordLength) {
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;

            default:
                return 11;
        }
    }

    public static void main(String[] args) {
        Stopwatch stopwatch = new Stopwatch();
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
        System.out.println("Time: " + stopwatch.elapsedTime());
    }
}
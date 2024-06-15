import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class HuffmanNode {
    int data;
    char c;
    HuffmanNode left;
    HuffmanNode right;

    HuffmanNode(int data, char c) {
        this.data = data;
        this.c = c;
        this.left = null;
        this.right = null;
    }

    HuffmanNode(int data) {
        this.data = data;
        this.c = '\0';  // No character for internal nodes
        this.left = null;
        this.right = null;
    }
}

class HuffmanComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {
        return x.data - y.data;
    }
}

public class Main {
    public static Map<Character, String> huffmanCodes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        String inputFile = "F:\\Sem 6\\My.txt";
        String compressedFile = "F:\\Sem 6\\compressed.txt";
        String decompressedFile = "F:\\Sem 6\\decompressed.txt";

        compress(inputFile, compressedFile);
//        decompress(compressedFile, decompressedFile);
    }

    private static void compress(String inputFile, String compressedFile) throws IOException {

        //Step1: Reads the data from the file and makes an array freq to store data
        FileInputStream fis = new FileInputStream(inputFile);
        int[] frequencies = new int[256];
        int n;
        while ((n = fis.read()) != -1) {
            frequencies[n]++;
        }
        fis.close();

        //Step2: make an priority queue to make a humffman tree with less freq char first
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(new HuffmanComparator());
        for (int i = 0; i < 256; i++) {
            if (frequencies[i] > 0) {
                HuffmanNode node = new HuffmanNode(frequencies[i], (char) i);
                pq.add(node);
            }
        }

        if (pq.isEmpty()) {
            throw new IllegalStateException("Input file is empty");
        }

        //Step3: Build tree
        while (pq.size() > 1) {
            HuffmanNode x = pq.remove();
            HuffmanNode y = pq.remove();
            HuffmanNode sum = new HuffmanNode(x.data + y.data);
            sum.left = x;
            sum.right = y;
            pq.add(sum);
        }

        HuffmanNode root = pq.peek();

        System.out.println(root.data);
        printInorder(root);
        if (root == null) {
            throw new IllegalStateException("Priority queue is empty");
        }

        generateCodes(root, "");

        FileInputStream fis2 = new FileInputStream(inputFile);
        FileOutputStream fos = new FileOutputStream(compressedFile);
        StringBuilder sb = new StringBuilder();
        while ((n = fis2.read()) != -1) {
            sb.append(huffmanCodes.get((char) n));
        }
        fis2.close();


        String encoded = sb.toString();
        System.out.println(encoded);

        for (int i = 0; i < encoded.length(); i += 8) {
            String byteString = encoded.substring(i, Math.min(i + 8, encoded.length()));
            int b = Integer.parseInt(byteString, 2);
            fos.write(b);
        }
            System.out.println("File compressed successfully.");
            System.out.println(huffmanCodes);
            fos.close();

    }
    public static void printInorder(HuffmanNode root)
    {
        if (root == null)
            return;

        // First recur on left subtree
        printInorder(root.left);

        // Now deal with the node
        System.out.print(root.c + " ");

        // Then recur on right subtree
        printInorder(root.right);
    }

    private static void generateCodes (HuffmanNode root, String s){
            if (root != null) {
                if (root.left == null && root.right == null) {
                    huffmanCodes.put(root.c, s);
                    return;
                }
                generateCodes(root.left, s + "0");
                generateCodes(root.right, s + "1");
            }

        }


        private static void decompress (String compressedFile, String decompressedFile) throws IOException {
            try (FileInputStream fis = new FileInputStream(compressedFile);
                 FileOutputStream fos = new FileOutputStream(decompressedFile)) {

                StringBuilder sb = new StringBuilder();
                int bit;
                while ((bit = fis.read()) != -1) {
                    System.out.println(Integer.toBinaryString(bit));
                    sb.append((char) bit);

                    // Check if the current string is a valid Huffman code
                    String code = sb.toString();
                    Character originalChar = getKeyByValue(huffmanCodes, code);
                    if (originalChar != null) {
                        fos.write(originalChar);
                        sb.setLength(0); // Clear the StringBuilder for the next code
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("File decompressed successfully.");
        }

        // Helper method to get a key from a map based on its value
        private static <K, V > K getKeyByValue(Map < K, V > map, V value) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (value.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
            return null;
        }

}

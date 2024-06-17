import java.io.*;
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
    private static HuffmanNode root;
    public static Map<Character, String> huffmanCodes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        String inputFile = "F:\\Sem 6\\Hello.txt";
        String compressedFile = "F:\\Sem 6\\compressed.txt";
        String decompressedFile = "F:\\Sem 6\\decompressed.txt";

        compress(inputFile, compressedFile);
        decompress(compressedFile, decompressedFile);
    }

    public static void compress(String inputFile, String compressedFile) throws IOException {

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

        root = pq.peek();

        if (root == null) {
            throw new IllegalStateException("Priority queue is empty");
        }

        //Step4: Generate codes for each character
        generateCodes(root, "");

        //Step5: Write the code genereated to the file
        FileInputStream fis2 = new FileInputStream(inputFile);
        FileOutputStream fos = new FileOutputStream(compressedFile);
        StringBuilder sb = new StringBuilder();

        while ((n = fis2.read()) != -1) {
            sb.append(huffmanCodes.get((char) n));
        }
        fis2.close();
        System.out.println("The encoded characters are:"+huffmanCodes);
        String encoded = sb.toString();
        System.out.println("The Encoded String is:"+encoded);

        for (int i = 0; i < encoded.length(); i += 8) {
            String byteString = encoded.substring(i, Math.min(i + 8, encoded.length()));
            if(byteString.length()<8){
                String BinaryString = String.format("%8s", byteString).replace(' ', '0');
                int temp = Integer.parseInt(BinaryString, 2);
                fos.write(temp);
            }
            else {
            int b = Integer.parseInt(byteString, 2);
            fos.write(b);
            }
        }
            System.out.println("File compressed successfully.");
            fos.close();
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
                    String binaryString = Integer.toBinaryString(bit);
                    String paddedBinaryString = String.format("%8s", binaryString).replace(' ', '0');
                    sb.append(paddedBinaryString);
                }
                    String code = sb.toString();
                    System.out.println("The Original string from the compressed file"+code);
                String decoded=decodeString(code,root);
                fos.write(decoded.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("File decompressed successfully.");
        }
    public static String decodeString(String encodedString, HuffmanNode root) {
        StringBuilder decodedString = new StringBuilder();
        HuffmanNode currentNode = root;
        int length = encodedString.length();

        for (int i = 0; i < length; i++) {
            char currentChar = encodedString.charAt(i);
            if (currentChar == '0') {
                currentNode = currentNode.left;
            } else if (currentChar == '1') {
                currentNode = currentNode.right;
            }

            // Check if a leaf node is reached
            if (currentNode.left == null && currentNode.right == null) {
                if (currentNode.c != '\0') {
                    decodedString.append(currentNode.c);
                }
                // Reset to root node to continue decoding the next character
                currentNode = root;
            }
        }
        return decodedString.toString();
    }
}

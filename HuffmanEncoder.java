/** PS3 - Huffman Encoding
 * @author Cindy Zhu
 * Lossless file compression
 */


import java.io.*;
import java.util.*;

public class HuffmanEncoder {

	// Instance variable for Huffman code table (used in code retrieval)
	protected static HashMap<Character, String> coded = new HashMap<Character,String>(); 
	
	// Given the path name, generate the initial frequency table: keys = character, value = frequency
	public static Map<Character, Integer> generateFrequencyTable(String pathName) throws IOException {
		Map<Character, Integer> frequencyTable = new HashMap<Character, Integer>(); // Create frequency table from map 
		BufferedReader file = null; // Make file null before trying to read it in case opening it doesn't work
		
		try {
			file = new BufferedReader(new FileReader(pathName)); // Open the file
			int currentInt = file.read();
			// Go through the file and put character + corresponding frequency in the frequency table
			while (currentInt != -1) {
				char character = (char)currentInt;
				// Update count if character is in the table
				if (frequencyTable.containsKey(character)) frequencyTable.put(character, frequencyTable.get(character)+1);
				// Set frequency to one if character is not in the table
				else frequencyTable.put(character, 1);
				currentInt = file.read();
			}
		}
		
		catch (IOException e) {
			System.out.println("Frequency table creation failed: " + e.getStackTrace()); 
		}
		
		finally {
			file.close();
		}
	//	System.out.println(frequencyTable); //For testing exercise
		return frequencyTable;
	}
	
	// Create min priority queue of the characters in the map (sort by frequency), each object in the queue being a binary tree with a single node
	public static PriorityQueue<BinaryTree<CFData>> createPQ(Map<Character, Integer> map) throws IOException{
		if (map.size() == 0) return null; // First handling the case of an empty file
		
		// Create new priority queue with initial capacity equaling size of map + utilizing TreeComparator to sort
		PriorityQueue<BinaryTree<CFData>> PQ = new PriorityQueue<BinaryTree<CFData>>(map.size(), new TreeComparator());
		
		// Loop through each character, create a single tree out of it, and add it to the priority queue.
		for (char character: map.keySet()) {
			BinaryTree<CFData> characterTree = new BinaryTree<CFData>(new CFData(character, map.get(character)));
			PQ.add(characterTree);
		}
	//	System.out.println(PQ); //For testing exercise
		return PQ;
		
	}

	// Create the tree
	public static BinaryTree<CFData> createTree(PriorityQueue<BinaryTree<CFData>> PQ) {
		// First, handle the case of an empty file (return a null tree)
		if (PQ == null) return new BinaryTree<CFData>(null);
		
		// Next, handle the case of a single character
		if (PQ.size() == 1) {
			BinaryTree<CFData> singleCharacter = PQ.poll();
			BinaryTree<CFData> singleCharacterTree = new BinaryTree<CFData>(new CFData(singleCharacter.data.getCharacter(), singleCharacter.data.getFrequency()));
			singleCharacterTree.setLeft(singleCharacter); // Set the single character as its left so that it has a Huffman code
			return singleCharacterTree;
		}
		
		// Add/combine the trees in the priority queue until there's only one tree left
		while (PQ.size() > 1) {
			BinaryTree<CFData> T1 = PQ.poll(); 
			BinaryTree<CFData> T2 = PQ.poll();
			CFData r = new CFData((char)1, (T1.getData().getFrequency() + T2.getData().getFrequency()));
			BinaryTree<CFData> T = new BinaryTree<CFData>(r, T1, T2);
			PQ.add(T);
		}
	//	System.out.println(PQ.poll()); // For testing exercise
		return PQ.poll();
	}
	
	// Retrieve the Huffman code by creating the chain of 0s and 1s (0 for left, 1 for right)
	public static Map<Character, String> retrieveCode(String codePath, BinaryTree<CFData> T) {
		// Recursively retrieve the code by calling retrieveCode on each node's child
		// First handle the case of the empty file by checking if the tree is null
		if (T.data != null) {
			if (T.isLeaf()) coded.put(T.data.getCharacter(), codePath); // Base case
			if (T.hasLeft()) retrieveCode(codePath + "0", T.left);
			if (T.hasRight()) retrieveCode(codePath + "1", T.right);
		}
	//	System.out.println(coded); // For testing exercise
		return coded;
	}
	
	// Compress the file
	public static void compress(String pathName, Map<Character, String> map) throws IOException{
		// Set input/output as null before trying in case opening throws exception
		BufferedReader input = null;
		BufferedBitWriter bitOutput = null;
		
		try {
			// Open file for reading
			input = new BufferedReader(new FileReader(pathName));
			// Create file for writing
			bitOutput = new BufferedBitWriter(pathName.substring(0,pathName.length()-4) + "_compressed.txt");
			int cInt = input.read(); 
			// Loop through reading characters
			while (cInt != -1) {
				char c = (char) cInt;
				for(char encodedChar : map.get(c).toCharArray()) { // Using character array, loop through each character
					 if (encodedChar == '0') bitOutput.writeBit(false); // False for 0, frue for 1
					 else bitOutput.writeBit(true);				  
				}
				cInt = input.read(); // Continue reading
			}
		}
		catch (IOException e) {
			System.out.println("Compressing failed: " + e.getStackTrace());
		} 
		finally {
			// Close files
			input.close();
			bitOutput.close();
		}
		

	}
	
	// Decompress the file
	public static void decompress(String compressedPathName, BinaryTree<CFData> tree) throws IOException{
		// Set input and output as null in case of exceptions
		BufferedBitReader bitInput = null;
		BufferedWriter output = null; 
		// New node to hold values of the tree
		BinaryTree<CFData> node = tree;

		try {
			// Open files for reading/writing
			bitInput = new BufferedBitReader(compressedPathName);
			output = new BufferedWriter(new FileWriter(compressedPathName.substring(0,compressedPathName.length()-4) + "_decompressed.txt")); 
			
			// Loop through input file
			while (bitInput.hasNext()) {
				boolean bit = bitInput.readBit();
				// If the bit is false (0), go to left child
				if (!bit) node = node.getLeft();
				// If the bit is true (1), go to right child
				else node = node.getRight();
				// If the node is a leaf, write the output
				if (node.isLeaf()) {
					output.write(node.data.getCharacter());
					node = tree; // Reset node as the root of the tree
				}
			}
		}
		catch (IOException e) {
			System.out.println("Decompressing failed: " + e.getStackTrace());
		}
		finally {
			// Close files
			output.close();
			bitInput.close();
		}
	}
	
	public static void main(String[] args) {
		try {
			// Create frequency table
			Map<Character, Integer> freqTable = generateFrequencyTable("inputs/USConstitution.txt");
			// Get Huffman code
			Map<Character, String> map = retrieveCode("", createTree(createPQ(freqTable)));
			// Compress
			compress("inputs/USConstitution.txt", map);
			// Create the tree for decompression
			BinaryTree<CFData> treemap = createTree(createPQ(generateFrequencyTable("inputs/USConstitution.txt")));
			
			decompress("inputs/USConstitution_compressed.txt", treemap);
		}
		catch (IOException e) {
			System.out.println(e.getStackTrace());
		}
		
	}
		
	


}

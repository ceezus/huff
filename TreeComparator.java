/** PS3 - TreeComparator.java
 * @author Cindy Zhu
 * Returns -1 if the first tree's frequency is smaller, 0 if they're equal, and 1 if the first tree's frequency is greater.
 */


import java.util.Comparator;

public class TreeComparator implements Comparator<BinaryTree<CFData>> {
	
	@Override
	public int compare(BinaryTree<CFData> t1, BinaryTree<CFData> t2) {
		if (t1.data.getFrequency() < t2.data.getFrequency()) return -1;
		if (t1.data.getFrequency() > t2.data.getFrequency()) return 1;
		return 0;
	}
}

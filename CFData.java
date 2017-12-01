/** PS3 - CFData
 * Data type that holds character and frequency 
 * @author Cindy Zhu
 *
 */

public class CFData {

	private int frequency;
	private char character;

	public CFData(char character, int frequency) {
		this.character = character;
		this.frequency = frequency;
	}

	public int getFrequency() {
		return frequency;
	}

	public char getCharacter() {
		return character;
	}
	
	@Override
	// Overrides the string method 
	public String toString() { 
		return "CHAR: " + character + ", FREQ: " + frequency;
	}
	

}

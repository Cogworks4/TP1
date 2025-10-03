package oneTimePassword;

import java.util.Random;

public class ModelOneTimePassword {

	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// the random type variables that are required to generate the random password
	private static Random rand = new Random();
	private static String specials = "!@#$%^&*()-_.";
	private static int randInt = 0;
	private static int randIntLower = 0;
	private static int randIntUpper = 0;
	private static char randCharLower = 0;
	private static char randCharUpper = 0;
	private static char randSpeical = 0;
	public static String password;
	private static int caseType = -1;
	
	
	/**********
	 * <p> Method: generateRandomPassword() </p>
	 * 
	 * <p> Description: This method generates a random password using a random lower case, upper case,
	 * number, and special character. Ensures it has one of each by placing a random type of each in certain
	 * locations of the password in each generation.</p>
	 * 
	 *
	 * 
	 */
	protected static String generateRandomPassword() {
		// Resets password to an empty string in case password is re-generated
		password = "";
	
	for(int i=1; i<11; i++) {
		// Randomizes the values and creates the character version of them if a char
		randInt = rand.nextInt(10);
		randIntLower = rand.nextInt(26) + 'a';
		randIntUpper = rand.nextInt(26) + 'A';
		randCharLower = (char) randIntLower;
		randCharUpper = (char) randIntUpper;
		randSpeical = specials.charAt(rand.nextInt(specials.length()));
		caseType = rand.nextInt(4);		//chooses a random case to use to password is different every generation
		
		if(i == 6) password += '-';								// Always splits the password with a dash
		
		if(i != 3 || i !=6 || i !=7 || i != 8 || i != 9) {		// If values are not the predetermined 
			switch(caseType) {									// places creates a random value based on case 
				case 0:											// Case 0: creates a random number and 
					password += Integer.toString(randInt);		// converts to string while adding to password string
					break;
				case 1:											// Case 1: creates a random lower case character
					password += randCharLower;
					break;
				case 2:											// Case 2: creates a random upper case character
					password +=randCharUpper;	
					break;
				case 3:											// Case 3: creates a random special character
					password += randSpeical;					
					break;
			}
		}else {													// The "predetermined" cases to ensure that password 
			switch(i) {											// always has one of each but remains random
				case 3:											// Creates a random number at location 3
					password += Integer.toString(randInt);
					break;
				case 7:											// Creates a random lower case at location 7
					password += randCharLower;
					break;
				case 8:											// Creates a random upper case at location 8
					password +=randCharUpper;
					break;
				case 9:											// Creates a random special character at location 9
					password += randSpeical;
					break;
			}
		}
		
	}
	// returns the random password
	return password;
	}
	
	
	

	
	
}

package oneTimePassword;

import java.util.Random;

public class ModelOneTimePassword {

	private static Random rand = new Random();
	private static String specials = "!@#$%^&*()-_.";
	private static int randInt = 0;
	private static int randIntLower = 0;
	private static int randIntUpper = 0;
	private static char randCharLower = 0;
	private static char randCharUpper = 0;
	private static char randSpeical = 0;
	public static String password;
	
	
	
	protected static String generateRandomPassword() {
		password = "";
	
	for(int i=1; i<11; i++) {
		randInt = rand.nextInt(10);
		randIntLower = rand.nextInt(26) + 'a';
		randIntUpper = rand.nextInt(26) + 'A';
		randCharLower = (char) randIntLower;
		randCharUpper = (char) randIntUpper;
		randSpeical = specials.charAt(rand.nextInt(specials.length()));
		int cased = rand.nextInt(4);
		
		if(i == 6) password += '-';
		
		if(i != 3 || i !=6 || i !=7 || i != 8 || i != 9) {
			switch(cased) {
				case 0:
					password += Integer.toString(randInt);
					break;
				case 1:
					password += randCharLower;
					break;
				case 2:
					password +=randCharUpper;
					break;
				case 3:
					password += randSpeical;
					break;
			}
		}else {
			switch(i) {
				case 3:
					password += Integer.toString(randInt);
					break;
				case 7:
					password += randCharLower;
					break;
				case 8:
					password +=randCharUpper;
					break;
				case 9:
					password += randSpeical;
					break;
			}
		}
		
	}
	
	return password;
	}
	
	
	

	
	
}

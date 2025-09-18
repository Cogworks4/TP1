package emailRecognizer;

/* 
 * Description:
 * The email recognizer takes the input of a of a text prompt from a GUI page 
 * and determines if the text input is a valid email or not, if it is valid
 * it returns an empty string, otherwise it returns a string of the valid 
 * error.
 */

public class emailRecognizer {
	
	static String errorMessage = "";
	static int inputIndex = 0;
	static char notAllowedChar[] = {'(', ')', ',', ':', ';', '<', '>', '[', ']', '\\', '"', '@', ' '};
	static int notAllowedCharSize = notAllowedChar.length;
	
	static public void main(String[] args) {
		testing(1, "jacob..henry@gmail.com");
		testing(2, "jacob]henry@gmail.com");
		testing(3, "jacob@henry@gmail.com");
		testing(4, "jacob.henry@gmail.c");
		testing(5, ".jacob.henry@gmail.com");
		testing(6, "jacobhenry.@gmail.com");
		testing(7, "jacobhenry@-gmail.com");
		testing(8, "jacobhenry@gmail-.com");
		testing(9, "jacobhenry@gmail.c0m");
		testing(10, "jacobhenry@gmail.com");
	}
	
	
	/*
	 * Input Validation Rules:
	 * 		- Can only use one @ symbol
	 * 		- Before the @ symbol, can only use alphabetic characters, numbers
	 * 		  underscores (_), dots (.), hyphens (-), and plus signs (+)
	 * 		- Before the @ symbol, you cannot start or end with a dot, or have
	 * 		  consecutive dots
	 * 		- After the @ symbol, you have to have one dot, cannot start or end
	 * 		  with a hyphen, TLD (.com) has to be minimum two characters
	 * 		- Must be <= 254 char, local <= 64, domain <= 253
	 */
	
	static public String validateEmail(String input) {
		
		inputIndex = input.length(); // updates the length of the input index
		int symbolCount = 0; // the count for a specified symbol
		
			// Checks email length
		if (inputIndex > 254){
			return errorMessage = "Your email is too long";
		}
		
			// Checks for any inappropriate characters or dots in inappropriate places
		for (int i = 0; i < inputIndex; i ++) {
			for (int j = 0; j < notAllowedCharSize; j++) {
				
					// Checks for inappropriate characters
				if (input.charAt(i) == notAllowedChar[j]) {
					
						// If it's only one @ symbol continue
					if (input.charAt(i) == '@') { 
						symbolCount += 1;
						// If there are are more than one @ symbol return the appropriate error 
						if (symbolCount > 1) {
							return errorMessage = "You cannont use the '@' character more than once";
						}
					} 
					
					if (input.charAt(i) != '@') {
						// Returns a appropriate error
						errorMessage = "You cannont use the ' ' character";
						StringBuilder sb = new StringBuilder(errorMessage);
						sb.setCharAt(21, input.charAt(i));
						return errorMessage = sb.toString();
					}
				}
				
					// Checks for any dots at the first index, last index before the @
					// symbol, or any subsequent dots
				if (input.charAt(i) == '.' && i == 0) { // dot at start
					return errorMessage = "You cannont use the '.' char at the beginning of your email";
				} else if (input.charAt(i) == '.' && input.charAt(i + 1) == '@') { // dot before @
					return errorMessage = "You cannont use the '.' char right before the @ symbol";
				} else if (input.charAt(i) == '.' && input.charAt(i + 1) == '.') { // consecutive dots
					return errorMessage = "You cannont use the '.' char consecutively";
				}
			}
		}
		
		symbolCount = 0;
		
			// Checks for extra dots and a minimum char limit for the TLD
		for (int i = 0; i < inputIndex; i++) {
			if (input.charAt(i) == '@') {
				
					// you cannot start with a hyphen after the @
				if (input.charAt(i + 1) == '-') {
					return errorMessage = "You cannont use a hyphen after the @ symbol";
				}
				
				for (int j = i; j < inputIndex; j++) {
					if (input.charAt(j) == '.') {
							// Checks for more than one . after the @
						symbolCount += 1;
						if (symbolCount > 1) {
							return errorMessage = "You cannont use the '.' character more than once after the @ symbol";
						}
						
							// Checks the minimum of two characters for the TLD
						if (j + 3 > inputIndex) {
							return errorMessage = "You must have a valid TLD (.com, .net, etc) for your email";
						}
						
							// you cannot end with a hyphen after the @ before the TLD
						if (input.charAt(j - 1) == '-') {
							return errorMessage = "You cannont use a hyphen right before the TLD (.com, .net, etc)";
						}
						
							// Checks for only alphabetic characters for the TLD
						for (int k = j + 1; k < inputIndex; k++) {
							if (!Character.isLetter(input.charAt(k))) {
								return errorMessage = "You can only use alphabetic characters for the TLD (.com, .net, etc)";
							}
						}
					}
				}
			}
		}
		
		return "All good!";
	}
	
	static public void testing(int number, String Email) {
		System.out.print(number + ".\n" + Email + "\nError Message:   ");
		System.out.println(validateEmail(Email) + "\n\n");
	}
	
	
}
package usernameRecognizer;



//Takes user input of username an checks to see if valid and follows the necessary rules. If not produces error message

/*Rules: Length > 4 but Length < 32 characters
*		Characters Allowed: A-z, a-z. 0-9, ., _, -, @, !
*		Username can't start with special characters 
*		Can't have consecutive special characters
*		No spaces allowed within username
*
*
*/
public class usernameRecognizer {
	
	//Sets variables for error message and index of username
	static String errorMessage = "";
	static int index = 0;
	
	//Sets allowed special characters
	static final char[] allowedSpecChar = { '.', '_', '-'};
	
	//tests
	static public void main(String[] args) {
		
		usernameTest(1, "jim"); //too short
		usernameTest(2, "jimP"); //valid
		usernameTest(3, "Jim_P"); //valid
		usernameTest(4, ".jim"); //invalid, specChar start
		usernameTest(5, "jim..p"); //invalid, consecutive specChar
		usernameTest(6, "jim-p"); //valid
		usernameTest(7, "jim p"); //invalid, uses space
		usernameTest(8, "jim#p"); //invalid char
		usernameTest(9, "jim_p1"); //valid
		usernameTest(10, "jimjimjimjimjimjimjimjimjimjimjim"); //invalid, too long
		usernameTest(11, "jim_"); //invalid, ends in specChar
		usernameTest(12, "jimjimjimjimjimjimjimjimjimjim_p"); //valid, 32 char long
		
	}
	
	static public String usernameValidate(String input) {
		index = input.length();
		
		//Length check and error messages
		if (index < 4) {
			return errorMessage = "Username is too short (minimum is 4 characters)";
		}
		if (index > 32) {
			return errorMessage = "Username is too long (maximum is 32 characters)";
		}
		
		//Checks if first character is allowed
		char first = input.charAt(0);
			if (!Character.isLetterOrDigit(first)) {
				if (charIsAllowed(first)) {
					return errorMessage = "Username can't start with a special character.";
				} else if (Character.isWhitespace(first)) {
					return errorMessage = "Username can't have a space.";
				} else {
					return errorMessage = "Username can't start with the '" + first + "' character.";
				}
			}
			//Checks if first character is allowed	
		char last = input.charAt(index - 1);
			if (!Character.isLetterOrDigit(last)) {
				if (charIsAllowed(last)) {
					return errorMessage = "Username can't end with a special character.";
				} else if (Character.isWhitespace(last)) {
					return errorMessage = "Username can't have a space.";
				} else {
					return errorMessage = "Username can't have the '" + last + "' character.";
				}
			}
			
		//Checks if previous char was a special character
		boolean previousChar = false;
			for(int i = 0; i < index; i++) {
				char c = input.charAt(i);
				
				if (Character.isLetterOrDigit(c)) {
					previousChar = false;
					continue;
				}
				
				if (Character.isWhitespace(c)) {
					return errorMessage = "Username can't have a space.";
					
				}
				
				if (!charIsAllowed(c)) {
					return errorMessage = "Username can't have the '" + c + "' character.";
				}
				
				if (previousChar) {
					return errorMessage = "Username cannot have consecutive special characters.";
				}
				previousChar = true;
				
			}
			
			return "";
		
	}
	
	
	
	static private boolean charIsAllowed(char c) {
		for (char s : allowedSpecChar) {
			if (c == s) return true;
		}
		return false;
	} 
	
	static public void usernameTest(int number, String username) {
		String finalUsername = usernameValidate(username);
		System.out.print(number + ".\n" + username + '\n');
			if (!finalUsername.isEmpty()) {
				System.out.println("Error: " + finalUsername + "\n");
			} else {
				System.out.println("Valid username\n");
			}
		
	}
	
	
	
}

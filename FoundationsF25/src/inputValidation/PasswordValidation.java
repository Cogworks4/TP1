package inputValidation;

import java.util.Scanner;

public class PasswordValidation {
	
	public static String adminPassword1 = "";			// passwords of input
	public static String adminPassword2 = "";			//
	
	// Flags set to check if password is valid
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;
	public static boolean foundTooLong = false;
	public static boolean running = false;
	
	// States for FSM
	private static int state = 0;
	private static int nextState = 0;
	
	// Input and errors
	public static String passwordErrorMessage = "";		// The error message text
	public static String passwordInput = "";
	public static int passwordIndexofError = -1;		// The index where the error was located
	
	// Current char and its index that is being evaluated 
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// Current index of character 
	
	// Enumeration of requirements to check and produce error messages
	private static enum Requirement {
	    UPPERCASE, LOWERCASE, DIGIT, SPECIAL, LONG_ENOUGH, TOO_LONG
	}
	
	
	/*******
	 * <p> Title: PasswordValidation main method that setups a console test and version</p>
	 * 
	 * <p> Description: This main method creates and input scanner for user to input a password
	 * and checks it. Ending the program if a a newline is entered, reprompts the user if it is invalid  </p>
	 * and resets the loop if valid as well to accept another password.
	 * 
	 * @param String[] args   The array of command lines parameters.
	 */
	public static void main(String[] args) {
		
		// Set to true/false if wanting to run program as test cases or interact in console, respectively
		boolean runTest = true; 
		
		if(runTest) 
		{
			//Positive test cases, should pass
			System.out.println("-----------Positve Test cases----------");
			testCases(1, "Asu_#360");
			testCases(2, "R@nd0m-542");
			testCases(3, "A1B@e345");
			testCases(4, "@nth0nyM");
			testCases(5, "mYf@V0rit3FoOd");
		
			System.out.println("\n\n\n----------Negative Test cases----------");
			//Negative test cases, shouldn't pass
			testCases(6, "1234");
			testCases(7, "password");
			testCases(8, "FavoiteTeam");
			testCases(9, "passwordWasTaken!");
			testCases(10, "ranOutOfIdeas");
		
		return;
		}
		
		Scanner input = new Scanner(System.in);
		String inputLine = "";
		
		//Start of program
		System.out.println("Enter a password below or enter newline to quit");
		
		//Prompt user to enter password
		System.out.println("\nEnter your password: \n");

		//loop until a empty line is entered.
		while (input.hasNextLine()) 
		{
			inputLine = input.nextLine();		// Fetch the next line
			if (inputLine.length() == 0) {		// Display the reason for terminating the loop if quit.
				System.out.println("\n*** Empty input line detected, the loop stops."); 
				input.close();					
				System.exit(0);
			}
			else {
				//get input and check first password for validity
				adminPassword1 = inputLine;
				//prompt user if password is invalid
				if(!checkValidity()) {
					System.out.println("\nPlease enter a new password: \n");
					//reset the password
					adminPassword1 = "";
					continue;
				}
				
				//prompt user to reenter password
				System.out.println("\nPlease enter your password again: \n");
				adminPassword2 = input.nextLine();
				
				//compare passwords and prompt if invalid
				if(adminPassword1.compareTo(adminPassword2) != 0) 
				{
					//passwords do not match, prompt user to re-try.
					System.out.println("\n\n--Error passwords do not match!--\n\n"
							+ "please re-enter your passwords: \n");
					
					//reset passwords and loop
					adminPassword1 = "";			
					adminPassword2 = "";
					continue;
					
				}
				//user entered two valid passwords and is prompted again to enter a new password
				else 
					System.out.println("Password Accepted!");
				
				System.out.println("\nEnter a password below or enter newline to quit"
						+ "\nEnter your password: \n");
			}
		}
		
	}
	
	/*******
	 * <p> Title: checkValidity - Public Method </p>
	 * 
	 * <p> Description: This method is called every time the user enters the password using the 
	 * requirements set and then evaluates the entered password with respect to those requirements.  
	 * The results of that evaluation are displayed to the user in console.</p>
	 */
	static public void testCases(int num, String pass) {
		// Case 1
		adminPassword1 = pass;
		System.out.println(num + "\n" + pass);
		checkValidity();
	}
	

	
	
	/*******
	 * <p> Title: checkValidity - Public Method </p>
	 * 
	 * <p> Description: This method is called every time the user enters the password using the 
	 * requirements set and then evaluates the entered password with respect to those requirements.  
	 * The results of that evaluation are displayed to the user in console.</p>
	 */
	
	static public boolean checkValidity() {		
		// If the input is empty, return false and prompt to re-enter password.
		if (adminPassword1.isEmpty()) {
			return false;
		}
		else
		{
			// There is user input, so evaluate it to see if it satisfies the requirements
			String errMessage = passwordEvaluator(adminPassword1);
			
			// An empty string means there is no error message, which means the input is valid
			if (errMessage != "") {
				
				// Since the output is not empty, at least one requirement have not been satisfied.
				System.out.println(errMessage);			// Display the message to the console
				
				// Tell the user that the password is not valid.
				System.out.println("\nFailure! The password did not satisfy the conditions above!.");	
				return false;
			}
			else {
				// All the requirements were satisfied - the password is valid
				System.out.println("Success! The password satisfies the requirements.");
				return true;
			} 
		}
	}
	
	/*******
	 * <p> Title: isLower - Private Method </p>
	 * 
	 * <p> Description: This method is called to check if character input is a lower case letter.</p>
	 */
	private static boolean isLower(char c){
		if(c >= 'a' && c <= 'z') {
		foundLowerCase = true;
		return true;
		}
		else {
			return false;
		}
	}
	
	/*******
	 * <p> Title: isUpper - Private Method </p>
	 * 
	 * <p> Description: This method is called to check if character input is an upper case letter.</p>
	 */
	private static boolean isUpper(char c){
		if(c >= 'A' && c <= 'Z') {
			foundUpperCase = true;
			return true;
			}
			else {
				return false;
			}
	}
	
	/*******
	 * <p> Title: isNum - Private Method </p>
	 * 
	 * <p> Description: This method is called to check if character input is a number.</p>
	 */
	private static boolean isNum(char c){
		if(c >= '0' && c <= '9') {
		foundNumericDigit = true;
		return true;
		}
		else {
			return false;
		}
	}
	
	/*******
	 * <p> Title: isAlphaNum - Private Method </p>
	 * 
	 * <p> Description: This method is called to check if character input is alphanumeric.</p>
	 */
	private static boolean isAlphaNum(char c){
		return isLower(c) || isUpper(c) || isNum(c);
		
	}
	
	/*******
	 * <p> Title: isLower - Private Method </p>
	 * 
	 * <p> Description: This method is called to check if character input is a special.</p>
	 */
	private static boolean isSpecial(char c){
		if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(c) >= 0)
		{
			foundSpecialChar = true;
			return true;
		}
		else {
			return false;
		}
	}
	
	/*******
	 * <p> Title: isValid - Private Method </p>
	 * 
	 * <p> Description: This method is called to check if character input is a valid input.</p>
	 */
	private static boolean isValid(char c) {
		return isAlphaNum(c) || isSpecial(c);
	}
	
	
	/*******
	 * <p> Title: passwordEvaluator - Public Method </p>
	 * 
	 * <p> Description: This method evaluates the password by going through each character and 
	 * creates an error message based on if the password is valid or not.</p>
	 */
	public static String passwordEvaluator(String input) {
		
		// The following are the local variable used to perform the Directed Graph simulation
		passwordErrorMessage = "";
		passwordIndexofError = 0;			// Initialize the IndexofError
//		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		
		if(input.length() <= 0) {
			return "*** Error *** The password is empty!";
		}
		
		// The input is not empty, so we can access the first character
		currentChar = input.charAt(0);		// The current character from the above indexed position

		// The Directed Graph simulation continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state.  This
		// local variable is a working copy of the input.
		passwordInput = input;				// Save a copy of the input
		

		// Initialize FSM
		// This flag determines whether the directed graph (FSM) loop is operating or not
		running = true;						// Start the loop
		nextState = -1;
		state = 0;
		
		foundUpperCase = false;				//reset flags after every call in-case an invalid password was passed
		foundLowerCase = false;
		foundNumericDigit = false;
		foundSpecialChar = false;
		foundLongEnough = false;
		foundTooLong = false;

		// The Directed Graph simulation continues until the end of the input is reached or at some
		// state the current character does not match any valid transition
		while (running) {
			switch(state) {
			case 0: 							//case where is first character inputed and if it is a alphanumeric character 
				if(isValid(currentChar)) { 		//if something else is inputed then user is notified and has to re-enter password.
					nextState = 1;
				}
				else {
					running = false;
					return "*** Error *** An invalid character has been found!";
				}
				break;
						
			case 1:								 //case where input is a alphanumeric character and satisfies the length of the password
				if(isValid(currentChar)) {		 //if something else is inputed then user is notified and has to re-enter password.
					nextState = 1;
				}
				else {
					running = false;
					return "*** Error *** An invalid character has been found!";
				}
				if(currentCharNdx >= 7 && currentCharNdx < 32)		//checks if password is in correct length
				{													//and sets flags according to
					foundLongEnough = true;
					foundTooLong = false;
				}
				else if(currentCharNdx < 7){
					foundLongEnough = false;
					foundTooLong = false;

				}
				else if(currentCharNdx >= 32){
					foundLongEnough = true;
					foundTooLong = true;
				}
				break;
			}			
			// Go to the next character if there is one
			currentCharNdx++;
			if (currentCharNdx >= input.length())
				running = false;
			else
				currentChar = input.charAt(currentCharNdx);
			
			//update states
			if (running) { state = nextState; nextState = -1;}
		}

		// Construct a String with a list of the requirement elements that were found.
		String errMessage = "";
		
		for (Requirement r : Requirement.values()) {
	        switch (r) {
	            case UPPERCASE:							//verifies if a UpperCase is used
	                if (!foundUpperCase)
	                	errMessage += "Upper case | ";
	                break;								
	            case LOWERCASE:							//verifies if a LowerCase is used
	                if (!foundLowerCase)
	                	errMessage += "Lower case | ";
	                break;
	            case DIGIT:								//verifies if a Digit is used
	                if (!foundNumericDigit) 
	                	errMessage += "Numeric digits | ";
	                break;
	            case SPECIAL:
	                if (!foundSpecialChar) 				//verifies if a Special Character is used
	                	errMessage += "Special character | ";
	                break;
	            case LONG_ENOUGH:
	                if (!foundLongEnough) 				//verifies if a password is greater than 7 characters
	                	errMessage += "Long Enough | ";
	                break;
	            case TOO_LONG:
	                if (foundTooLong) 					//verifies if a password is less than 32 characters
	                	errMessage += "Password Length | ";
	                break;
	        }
	    }
		if (errMessage == "")							//if none of the conditions are invalidated then pass no error message 
			return "";
		return errMessage + "\n--conditions were not satisfied--";	// if some condition is invalid then pass which ones were unsatisfied
	}																// and triggers a re-prompt to user for a new password 
}
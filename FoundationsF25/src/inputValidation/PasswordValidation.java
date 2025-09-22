package inputValidation;

import java.util.Scanner;

//TP1 Version
public class PasswordValidation {
	
	public static String adminPassword1 = "";			// global passwords
	public static String adminPassword2 = "";			//
	
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;
	public static boolean foundTooLong = false;
	public static boolean running = false;
	
	public static String passwordErrorMessage = "";		// The error message text
	public static String passwordInput = "";
	public static int passwordIndexofError = -1;		// The index where the error was located
	
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;	
	
	
	
	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in);
		String inputLine = "";
		
		//Start
		System.out.println("Enter a password below or enter newline to quit");
		
		//Prompt user to enter password
		System.out.println("\nEnter your password: \n");

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
				adminPassword1 = input.nextLine();
				if(!checkValidity()) {
					System.out.println("Error password was invalid,"
							+ "Please re-enter your password: \n");
					adminPassword1 = "";
				}
				
				//prompt user to reenter password
				System.out.println("\nPlease re-enter your password: \n");
				adminPassword2 = input.nextLine();
				
				//compare passwords and prompt if invalid
				if(adminPassword1.compareTo(adminPassword2) == 0) 
				{
					System.out.println("\n\n--Error passwords do not match!--\n\n"
							+ "please re-enter your passwords: \n");
					
					adminPassword1 = "";			
					adminPassword2 = "";
					continue;
					
				}
				else 
					System.out.println("Password Accepted!");
				
				System.out.println("\nEnter a password below or enter newline to quit"
						+ "\nEnter your password: \n");
			}
		}


		
		


		
	}

	
	
	/*******
	 * <p> Title: updatePassword - Public Method </p>
	 * 
	 * <p> Description: This method is called every time the user changes the password (e.g., with 
	 * every key pressed) using the GUI from the PasswordEvaluationGUITestbed.  It resets the 
	 * messages associated with each of the requirements and then evaluates the current password
	 * with respect to those requirements.  The results of that evaluation are display via the View
	 * to the user and via the console.</p>
	 */
	
	static public boolean checkValidity() {		
		// If the input is empty, clear the aspects of the user interface having to do with the
		// user input and tell the user that the input is empty.
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
				
				// Tell the user that the password is not valid with a red message
				System.out.println("Failure! The password is not valid.");	
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
	 * <p> Title: passwordEvaluator - Protected Method </p>
	 * 
	 * <p> Description: 
	 * 
	 * 
	 * 
	 * </p>
	 */
	
	protected static String passwordEvaluator(String input) {
		
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
		
		// The following are the attributes associated with each of the requirements
		foundUpperCase = false;				// Reset the Boolean flag
		foundLowerCase = false;				// Reset the Boolean flag
		foundNumericDigit = false;			// Reset the Boolean flag
		foundSpecialChar = false;			// Reset the Boolean flag
		foundNumericDigit = false;			// Reset the Boolean flag
		foundLongEnough = false;			// Reset the Boolean flag
		foundTooLong = false;
		
		// This flag determines whether the directed graph (FSM) loop is operating or not
		running = true;						// Start the loop

		// The Directed Graph simulation continues until the end of the input is reached or at some
		// state the current character does not match any valid transition
		while (running) {
			// The cascading if statement sequentially tries the current character against all of
			// the valid transitions, each associated with one of the requirements
			if (currentChar >= 'A' && currentChar <= 'Z') {
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			} else if (currentChar >= 'a' && currentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			} else if (currentChar >= '0' && currentChar <= '9') {
				System.out.println("Digit found");
				foundNumericDigit = true;
			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;
			} else {
				passwordIndexofError = currentCharNdx;
				return "*** Error *** An invalid character has been found!";
			}
			if (currentCharNdx >= 7) {
				System.out.println("At least 8 characters found");
				foundLongEnough = true;
			}
			if(input.length() > 32) {
				System.out.println("Password is too long!");
				foundTooLong = true;
			
			// Go to the next character if there is one
			currentCharNdx++;
			if (currentCharNdx >= input.length())
				running = false;
			else
				currentChar = input.charAt(currentCharNdx);
			}
			System.out.println();
		}
		
		// Construct a String with a list of the requirement elements that were found.
		String errMessage = "";
		if (!foundUpperCase)
			errMessage += "Upper case; ";
		
		if (!foundLowerCase)
			errMessage += "Lower case; ";
		
		if (!foundNumericDigit)
			errMessage += "Numeric digits; ";
			
		if (!foundSpecialChar)
			errMessage += "Special character; ";
			
		if (!foundLongEnough)
			errMessage += "Long Enough; ";
		
		if(foundTooLong)
			errMessage += "Password Length; ";
		
		if (errMessage == "")
			return "";
		
		// If it gets here, there something was not found, so return an appropriate message
		passwordIndexofError = currentCharNdx;
		return errMessage + "conditions were not satisfied";
	}
	
	
	/*******
	 * <p> Title: printErrorMessages - Protected Method </p>
	 * 
	 * <p> Description: 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * </p>
	 */
	
	static protected void printErrorMessages() {
		
		//
		if (foundUpperCase) {
			System.out.println("At least one upper case letter - Not yet satisfied");
			
			System.out.println("At least one upper case letter - Satisfied");
		}

		if (foundLowerCase) {
			System.out.println("At least one lower case letter - Not yet satisfied");

			System.out.println("At least one lower case letter - Satisfied");
		}

		if (foundNumericDigit) {
			System.out.println("At least one numeric digit - Not yet satisfied");

			System.out.println("At least one numeric digit - Satisfied");
		}

		if (foundSpecialChar) {
			System.out.println("At least one special character - Not yet satisfied");

			System.out.println("At least one special character - Satisfied");
		}

		if (foundLongEnough) {
			System.out.println("At least eight characters - Not yet satisfied");

			System.out.println("At least eight characters - Satisfied");
		}
		if (foundTooLong) {
			System.out.println("At most thirty-two characters - Satisfied");

			System.out.println("At most thirty-two characters - Not yet satisfied");
		}
	}

	

}

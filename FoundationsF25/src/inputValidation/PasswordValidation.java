package inputValidation;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color; 

import javafx.scene.text.Font;


public class PasswordValidation {
	
	public static String passwordErrorMessage = "";		// The error message text
	public static String adminPassword1 = "";			// The input being processed
	public static String adminPassword2 = "";
	public static String passwordInput = "";
	public static int passwordIndexofError = -1;		// The index where the error was located
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;
	public static boolean foundTooLong = false;
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;	
	
	protected static Label label_UpperCase = new Label();		// These empty labels change based on the
	protected static Label label_LowerCase = new Label();		// user's input
	protected static Label label_NumericDigit = new Label();	
	protected static Label label_SpecialChar = new Label();
	protected static Label label_LongEnough = new Label();
	protected static Label label_ShortEnough = new Label();
	protected static Label label_Requirements = new Label();
	protected static Label validPassword = new Label();
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	
	// A numeric value may not exceed 16 characters
	
	
	// Private method to move to the next character within the limits of the input line

	
	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method.
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it is a String
	 * 						with a helpful description of the error
	 */
	protected static void setAdminPasswords(String pass1, String pass2) {

		pass1 = adminPassword1;
		pass2 = adminPassword2;
		
		//checks if inputs are the same and sends error if not
		if (adminPassword1.compareTo(adminPassword2) == 0) {
			
			passwordInput = adminPassword1;
		
		}
		
	}
	
	
	/*******
	 * <p> Title: updatePassword - Protected Method </p>
	 * 
	 * <p> Description: This method is called every time the user changes the password (e.g., with 
	 * every key pressed) using the GUI from the PasswordEvaluationGUITestbed.  It resets the 
	 * messages associated with each of the requirements and then evaluates the current password
	 * with respect to those requirements.  The results of that evaluation are display via the View
	 * to the user and via the console.</p>
	 */
	
	public static void updatePassword() {


		
		// If the input is empty, clear the aspects of the user interface having to do with the
		// user input and tell the user that the input is empty.
		if (adminPassword1.isEmpty()) {

		}
		else
		{
			// There is user input, so evaluate it to see if it satisfies the requirements
			String errMessage = evaluatePassword(adminPassword1);
			
			// Based on the evaluation, change the flag to green for each satisfied requirement
			updateFlags();
			
			// An empty string means there is no error message, which means the input is valid
			if (errMessage != "") {
				
				// Since the output is not empty, at least one requirement have not been satisfied.
				System.out.println(errMessage);			// Display the message to the console
				
				// Tell the user that the password is not valid with a red message
				validPassword.setTextFill(Color.RED);
				validPassword.setText("Failure! The password is not valid.");
				
//				// Ensure the button is disabled
//				ViewFirstAdmin.button_AdminSetup.setDisable(true);
				
			}
			else {
				// All the requirements were satisfied - the password is valid
				System.out.println("Success! The password satisfies the requirements.");
				
				// Tell the user that the password is valid with a green message
				validPassword.setTextFill(Color.GREEN);
				validPassword.setText("Success! The password satisfies the requirements.");
				
//				// Enable the button so the user can accept this password or continue to add
//				// more characters to the password and make it longer.
//				ViewFirstAdmin.button_AdminSetup.setDisable(false);
			} 
		}
	}
	
	private static void updateFlags() 
	{
		if (foundUpperCase) {
			label_UpperCase.setText("At least one upper case letter - Satisfied");
			label_UpperCase.setTextFill(Color.GREEN);
		}

		if (foundLowerCase) {
			label_LowerCase.setText("At least one lower case letter - Satisfied");
			label_LowerCase.setTextFill(Color.GREEN);
		}

		if (foundNumericDigit) {
			label_NumericDigit.setText("At least one numeric digit - Satisfied");
			label_NumericDigit.setTextFill(Color.GREEN);
		}

		if (foundSpecialChar) {
			label_SpecialChar.setText("At least one special character - Satisfied");
			label_SpecialChar.setTextFill(Color.GREEN);
		}

		if (foundLongEnough) {
			label_LongEnough.setText("At least eight characters - Satisfied");
			label_LongEnough.setTextFill(Color.GREEN);
		}
		if (foundTooLong) {
			label_ShortEnough.setText("At most thirty-two characters - Not yet satisfied");
			label_ShortEnough.setTextFill(Color.RED);
		}
	}
	
	public static String evaluatePassword(String input) {
		
		// The following are the local variable used to perform the Directed Graph simulation
		passwordErrorMessage = "";
		passwordIndexofError = 0;			// Initialize the IndexofError
		inputLine = input;					// Save the reference to the input line as a global
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
			
			// Go to the next character if there is one
			currentCharNdx++;
			if (currentCharNdx >= inputLine.length())
				running = false;
			else
				currentChar = input.charAt(currentCharNdx);
			
			if(inputLine.length() > 32) {
				System.out.println("Password is too long!");
				foundTooLong = true;
				
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
	
	
	//call from View class and pass the labels to update the gui
	static protected void resetAssessments() 
	{
	    //updates Upper case error
		label_UpperCase.setText("At least one upper case letter - Not yet satisfied");
	    label_UpperCase.setTextFill(Color.RED);
	    
	  //updates Lower case error
	    label_LowerCase.setText("At least one lower case letter - Not yet satisfied");
	    label_LowerCase.setTextFill(Color.RED);
	    
	  //updates Numeric case error
	    label_NumericDigit.setText("At least one numeric digit - Not yet satisfied");
	    label_NumericDigit.setTextFill(Color.RED);
	    
	  //updates Special Character case error
	    label_SpecialChar.setText("At least one special character - Not yet satisfied");
	    label_SpecialChar.setTextFill(Color.RED);
	    
	  //updates Length case error
	    label_LongEnough.setText("At least eight characters - Not yet satisfied");
	    label_LongEnough.setTextFill(Color.RED);
	    
	  //updates Length case error
	    label_ShortEnough.setText("At most thirty-two characters - Satisfied");
	    label_ShortEnough.setTextFill(Color.GREEN);
	}

	
	public static void updateView(List<Label> labels) {
		
		label_Requirements = labels.get(1);
		
		setupLabelUI(labels.get(1), "Arial", 16, width-10, Pos.BASELINE_RIGHT, 10, 340);
		
		setupLabelUI(label_Requirements, "Arial", 14, width-10, Pos.BASELINE_LEFT, 30, 380);
	    
		setupLabelUI(label_UpperCase, "Arial", 14, width-10, Pos.BASELINE_LEFT, 30, 380);

		setupLabelUI(label_LowerCase, "Arial", 14, width-10, Pos.BASELINE_LEFT, 30, 410);
	    
		setupLabelUI(label_NumericDigit, "Arial", 14, width-10, Pos.BASELINE_LEFT, 30, 440);
	    
		setupLabelUI(label_SpecialChar, "Arial", 14, width-10, Pos.BASELINE_LEFT, 30, 470);
	    
		setupLabelUI(label_LongEnough, "Arial", 14, width-10, Pos.BASELINE_LEFT, 30, 500);
	    
		setupLabelUI(label_ShortEnough, "Arial", 14, width-10, Pos.BASELINE_LEFT, 30, 530);
	    
//		resetAssessments();	// This method is use after each change to establish an initial state
		
		// Setup the valid Password message, which is used when all the requirements have been met
		validPassword.setTextFill(Color.GREEN);
		validPassword.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(validPassword, "Arial", 18, width-150-10, Pos.BASELINE_LEFT, 10, 300);
	}
	
	public static List<Label> getGuiElements(Label label_Requirements, Label label_UpperCase, Label label_LowerCase, 
			Label label_NumericDigit, Label label_SpecialChar, Label label_LongEnough, Label label_ShortEnough, Label validPassword) 
	{
		PasswordValidation.label_Requirements = label_Requirements;
		PasswordValidation.label_UpperCase = label_UpperCase;	// These empty labels change based on the
		PasswordValidation.label_LowerCase = label_LowerCase;		// user's input
		PasswordValidation.label_NumericDigit = label_NumericDigit;	
		PasswordValidation.label_SpecialChar = label_SpecialChar;
		PasswordValidation.label_LongEnough = label_LongEnough;
		PasswordValidation.label_ShortEnough = label_ShortEnough;
		PasswordValidation.validPassword = validPassword;
		
		return List.of(label_Requirements, label_UpperCase, label_LowerCase, label_NumericDigit,
				label_SpecialChar,label_LongEnough,label_ShortEnough,validPassword);
		
		
	}
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}

}

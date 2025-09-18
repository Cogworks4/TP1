//package inputValidation;
//
//import guiFirstAdmin.ViewFirstAdmin;
//
//public class UserValidation {
//	
//	public static String userNameRecognizerErrorMessage = "";	// The error message text
//	public static String userNameRecognizerInput = "";			// The input being processed
//	public static int userNameRecognizerIndexofError = -1;		// The index of error location
//	private static int state = 0;						// The current state value
//	private static int nextState = 0;					// The next state value
//	private static String userInputLine = "";				// The input line
//	private static char userCurrentChar;					// The current character in the line
//	private static int userCurrentCharNdx;					// The index of the current character
//	private static boolean userRunning;						// The flag that specifies if the FSM is 
//														// running
//	private static int userNameSize = 0;
//	
//	private static void moveToNextCharacter() {
//		userCurrentCharNdx++;
//		if (userCurrentCharNdx < userInputLine.length())
//			userCurrentChar= userInputLine.charAt(userCurrentCharNdx);
//		else {
//			userCurrentChar= ' ';
//			userRunning = false;
//		}
//		
//		public static String checkForValidUserName(String input) {
//			// Check to ensure that there is input to process
//			if(input.length() <= 0) {
//				userNameRecognizerIndexofError = 0;	// Error at first character;
//				ViewFirstAdmin.button_AdminSetup.setDisable(true);
//				return("Username is Empty.");
////				return "\n*** ERROR *** The input is empty";
//			}
//			
//			// The local variables used to perform the Finite State Machine simulation
//			state = 0;							// This is the FSM state number
//			userInputLine = input;					// Save the reference to the input line as a global
//			userCurrentCharNdx = 0;					// The index of the current character
//			userCurrentChar= input.charAt(0);		// The current character from above indexed position
//		
//		
//			
//
//
//			// The Finite State Machines continues until the end of the input is reached or at some 
//			// state the current character does not match any valid transition to a next state
//
//			userNameRecognizerInput = input;	// Save a copy of the input
//			userRunning = true;						// Start the loop
//			nextState = -1;	
//			
//			
//
//			// There is no next state		
//// This is the place where semantic actions for a transition to the initial state occur
//
//userNameSize = 0;					// Initialize the UserName size
//
//// The Finite State Machines continues until the end of the input is reached or at some 
//// state the current character does not match any valid transition to a next state
//while (userRunning) {
//	// The switch statement takes the execution to the code for the current state, where
//	// that code sees whether or not the current character is valid to transition to a
//	// next state
//	switch (state) {
//	case 0: 
//		// State 0 has 1 valid transition that is addressed by an if statement.
//		
//		// The current character is checked against A-Z, a-z. If any are matched
//		// the FSM goes to state 1
//		
//		// A-Z, a-z -> State 1
//		if ((userCurrentChar>= 'A' && userCurrentChar<= 'Z' ) ||		// Check for A-Z
//				(userCurrentChar>= 'a' && userCurrentChar<= 'z' )) {	// Check for a-z
//			nextState = 1;
//			
//			// Count the character 
//			userNameSize++;
//			
//			// This only occurs once, so there is no need to check for the size getting
//			// too large.
//		}
//		// If it is none of those characters, the FSM halts
//		else 
//			userRunning = false;
//		
//		// The execution of this state is finished
//		break;
//	
//	case 1: 
//		// State 1 has two valid transitions, 
//		//	1: a A-Z, a-z, 0-9 that transitions back to state 1
//		//  2: a period that transitions to state 2 
//
//		
//		// A-Z, a-z, 0-9 -> State 1
//		if ((userCurrentChar>= 'A' && userCurrentChar<= 'Z' ) ||		// Check for A-Z
//				(userCurrentChar>= 'a' && userCurrentChar<= 'z' ) ||	// Check for a-z
//				(userCurrentChar>= '0' && userCurrentChar<= '9' )) {	// Check for 0-9
//			nextState = 1;
//			
//			// Count the character
//			userNameSize++;
//		}
//		// .,-,_ -> State 2
//		else if (userCurrentChar== '.' || userCurrentChar== '-'||userCurrentChar== '_') {							// Check for /
//			nextState = 2;
//			
//			// Count the .
//			userNameSize++;
//		}				
//		// If it is none of those characters, the FSM halts
//		else
//			userRunning = false;
//		
//		// The execution of this state is finished
//		// If the size is larger than 16, the loop must stop
//		if (userNameSize > 16)
//			userRunning = false;
//		break;			
//		
//	case 2: 
//		// State 2 deals with a character after a period/dash/underscore in the name.
//		
//		// A-Z, a-z, 0-9 -> State 1
//		if ((userCurrentChar>= 'A' && userCurrentChar<= 'Z' ) ||		// Check for A-Z
//				(userCurrentChar>= 'a' && userCurrentChar<= 'z' ) ||	// Check for a-z
//				(userCurrentChar>= '0' && userCurrentChar<= '9' )) {	// Check for 0-9
//			nextState = 1;
//			
//			// Count the odd digit
//			userNameSize++;
//			
//		}
//		// If it is none of those characters, the FSM halts
//		else 
//			userRunning = false;
//
//		// The execution of this state is finished
//		// If the size is larger than 16, the loop must stop
//		if (userNameSize > 16)
//			userRunning = false;
//		break;			
//	}
//	
//	if (userRunning) {
//		
//		// When the processing of a state has finished, the FSM proceeds to the next
//		// character in the input and if there is one, it fetches that character and
//		// updates the currentChar.  If there is no next character the userCurrentCharis
//		// set to a blank.
//		moveToNextCharacter();
//
//		// Move to the next state
//		state = nextState;
//
//
//		// Ensure that one of the cases sets this to a valid value
//		nextState = -1;
//	}
//	// Should the FSM get here, the loop starts again
//
//}
//
//// When the FSM halts, we must determine if the situation is an error or not.  That depends
//// of the current state of the FSM and whether or not the whole string has been consumed.
//// This switch directs the execution to separate code for each of the FSM states and that
//// makes it possible for this code to display a very specific error message to improve the
//// user experience.
//userNameRecognizerIndexofError = userCurrentCharNdx;	// Set index of a possible error;
//userNameRecognizerErrorMessage = "\n*** ERROR *** ";
//
//// The following code is a slight variation to support just console output.
//switch (state) {
//case 0:
//	// State 0 is not a final state, so we can return a very specific error message
//	userNameRecognizerErrorMessage += "A UserName must start with A-Z, or a-z.\n";
//	ViewFirstAdmin.button_AdminSetup.setDisable(true);
//	return userNameRecognizerErrorMessage;
//
//case 1:
//	// State 1 is a final state.  Check to see if the UserName length is valid.  If so we
//	// we must ensure the whole string has been consumed.
//
//	if (userNameSize < 4) {
//		// UserName is too small
//		userNameRecognizerErrorMessage += "A UserName must have at least 4 characters.\n";
//		ViewFirstAdmin.button_AdminSetup.setDisable(true);
//		return userNameRecognizerErrorMessage;
//	}
//	else if (userNameSize > 16) {
//		// UserName is too long
//		userNameRecognizerErrorMessage += 
//			"A UserName must have no more than 16 characters.\n";
//		ViewFirstAdmin.button_AdminSetup.setDisable(true);
//		return userNameRecognizerErrorMessage;
//	}
//	else if (userCurrentCharNdx < input.length()) {
//		// There are characters remaining in the input, so the input is not valid
//		userNameRecognizerErrorMessage += 
//			"A UserName character may only contain the characters A-Z, a-z, 0-9.\n";
//		ViewFirstAdmin.button_AdminSetup.setDisable(true);
//		return userNameRecognizerErrorMessage;
//	}
//	else {
//			// UserName is valid
//			userNameRecognizerIndexofError = -1;
//			userNameRecognizerErrorMessage = "";
//			return userNameRecognizerErrorMessage;
//	}
//
//case 2:
//	// State 2 is not a final state, so we can return a very specific error message
//	userNameRecognizerErrorMessage +=
//		"A UserName character after a period/dash/underscore must be A-Z, a-z, 0-9.\n";
//	ViewFirstAdmin.button_AdminSetup.setDisable(true);
//	return userNameRecognizerErrorMessage;
//	
//default:
//	// This is for the case where we have a state that is outside of the valid range.
//	// This should not happen
//	ViewFirstAdmin.button_AdminSetup.setDisable(true);
//
//	return "";
//}
//		
//	}
//		
//		
//
//}

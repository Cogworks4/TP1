package inputValidation;


public class emailRecognizer {
	/**
	 * <p> Title: FSM-translated emailRecognizer. </p>
	 * 
	 * <p> Description: A demonstration of the mechanical translation of Finite State Machine 
	 * diagram into an executable Java program using the Email Recognizer. The code 
	 * detailed design is based on a while loop with a select list</p>
	 * 
	 * @version 1.00		2025-09-30	Initial baseline derived from the FSM
	 * 
	 */

	/**********************************************************************************************
	 * 
	 * Result attributes to be used for GUI applications where a detailed error message and a 
	 * pointer to the character of the error will enhance the user experience.
	 * 
	 */

	public static String emailRecognizerErrorMessage = "";	// The error message text
	public static String emailRecognizerInput = "";			// The input being processed
	public static int emailRecognizerIndexofError = -1;		// The index of error location
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static boolean finalState = false;			// Is this state a final state?
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
														// running
	private static int emailSize = 0;					// A numeric value may not exceed 254 characters
    private static int TLDSize = 0;						// A numeric value must be more than 1
    private static int atsymbol = 0;
    private static boolean lastWasHyphen = false; 		// last domain char was '-'
    private static boolean inTLD = false;         		// we are in the TLD (state 6)
    private static boolean invalidChar = false;
    
	// Private method to display debugging data
	private static void displayDebuggingInfo() {
		// Display the current state of the FSM as part of an execution trace
		if (currentCharNdx >= inputLine.length())
			// display the line with the current state numbers aligned
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "None");
		else
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
				((finalState) ? "       F   " : "           ") + "  " + currentChar + " " + 
				((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + 
				nextState + "     " + emailSize);
	}
	
	// Private method to move to the next character within the limits of the input line
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = ' ';
			running = false;
		}
	}

	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method.
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it is a String
	 * 						with a helpful description of the error
	 */
	public static String checkForValidEmail(String input) {
		// Check to ensure that there is input to process
		if(input.length() <= 0) {
			emailRecognizerIndexofError = 0;	// Error at first character;
			return "\n*** ERROR *** The input is empty";
		}
		
		// The local variables used to perform the Finite State Machine simulation
		TLDSize = 0;						// This is the size of the TLD
		atsymbol = 0;					// This is the bool for the '@' symbol
		lastWasHyphen = false;				// Hyphen as last char
		inTLD = false;						// if we're in state 6
		invalidChar = false;
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from above indexed position

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		String emailInput = input;	// Save a copy of the input
		running = true;						// Start the loop
		nextState = -1;						// There is no next state
		System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");
		
		// This is the place where semantic actions for a transition to the initial state occur
		
		emailSize = 0;					// Initialize the Email size

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0: 
				// State 0 has 1 valid transition that is addressed by an if statement.
				
				// The current character is checked against A-Z, a-z, 0-9. If any are matched
				// the FSM goes to state 1
				
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					nextState = 1;
					
					// Count the character 
					emailSize++;
					
					// This only occurs once, so there is no need to check for the size getting
					// too large.
				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;
				
				// The execution of this state is finished
				break;
			
			case 1: 
				// State 1 has two valid transitions, 
				//	1: a A-Z, a-z, 0-9 that transitions back to state 1
				//  2: a period that transitions to state 2 

				
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
						(currentChar >= '0' && currentChar <= '9' ) ||
                        (currentChar == '_') || (currentChar == '-') || (currentChar == '+')) {	// Check for 0-9
					nextState = 1;
					
					// Count the character
					emailSize++;
				}
				// . -> State 2
				else if (currentChar == '.') {							// Check for /
					nextState = 2;
					
					// Count the .
					emailSize++;
				}
                // . -> State 3
				else if (currentChar == '@') {							// Check for /
					nextState = 3;
					
					// Count the .
					emailSize++;
				}				
				// If it is none of those characters, the FSM halts
				else {
					invalidChar = true;
					running = false;
				}
				
				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (emailSize > 254) {
					running = false;
				}
				break;			
				
			case 2: 
				// State 2 deals with a character after a period in the name.
				
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					nextState = 1;
					
					// Count the odd digit
					emailSize++;
					
				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (emailSize > 254)
					running = false;
				break;

            case 3:
                // State 3 deals with what precedes the @ symbol
            	
            	atsymbol += 1; // this means an @ symbol is present, more than one is invalid

                // A-Z, a-z, 0-9 -> State 4
                if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					nextState = 4;
					
					// Count the odd digit
					emailSize++;
				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (emailSize > 254)
					running = false;
				break;
            case 4:
                // State 4 deals with hypens in the domain

                if (currentChar == '-') { // sends to state 4 again
                    nextState = 4;
                    emailSize++;
                    lastWasHyphen = true;
                } else if ((currentChar >= 'A' && currentChar <= 'Z') ||
                           (currentChar >= 'a' && currentChar <= 'z') ||
                           (currentChar >= '0' && currentChar <= '9')) { // else brings to state 5
                    nextState = 5;
                    emailSize++;
                    lastWasHyphen = false;
                } else if (currentChar == '.') {
                    if (!lastWasHyphen) {           // sends to state 6 to start the TLD
                        nextState = 6;
                        emailSize++;
                        inTLD = true;
                        TLDSize = 0;                // start counting TLD anew
                    } else {
                        running = false;            // reject "-."
                    }
                } else {
                    running = false;
                }
                if (emailSize > 254) running = false;
                break;
            case 5:
                // State 5 deals with hypens in the domain being preceded by AN
                // or a . symbol that starts the TLD
            	
            	if (currentChar == '@') {
            		atsymbol += 1;
            	}

                if (currentChar == '-') {
                    nextState = 4;
                    emailSize++;
                    lastWasHyphen = true;
                } else if (currentChar == '.') {
                    if (!lastWasHyphen) {           // only allow label end if not '-'
                        nextState = 6;
                        emailSize++;
                        inTLD = true;
                        TLDSize = 0;
                    } else {
                        running = false;            // reject "-."
                    }
                } else if ((currentChar >= 'A' && currentChar <= 'Z') ||
                           (currentChar >= 'a' && currentChar <= 'z') ||
                           (currentChar >= '0' && currentChar <= '9')) {
                    nextState = 5;
                    emailSize++;
                    lastWasHyphen = false;
                } else {
                    running = false;
                }
                if (emailSize > 254) running = false;
                break; 
                
            case 6:
                // State 6 deals with only AN in the TLD and a minimum char limit
                TLDSize++;
            	
                if ((currentChar >= 'A' && currentChar <= 'Z') ||
                        (currentChar >= 'a' && currentChar <= 'z')) {
                        nextState = 6;
                        emailSize++;
                        lastWasHyphen = false; // not relevant in TLD, but keep tidy
                    } else if (currentChar == ' '){
                        running = false;
                    } else {
                    	invalidChar = true;
                    	running = false;
                    }
                break;
			}
			
			if (running) {
				displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar.  If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;
				
				// Is the new state a final state?  If so, signal this fact.
				if (state == 6) finalState = true;

				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again
	
		}
		displayDebuggingInfo();
		
		System.out.println("The loop has ended.");
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		emailRecognizerIndexofError = currentCharNdx;	// Set index of a possible error;
		emailRecognizerErrorMessage = "\n*** ERROR *** ";
		
		// The following code is a slight variation to support just console output.
		switch (state) {
	    case 0:
	    		// If it fails in state 0 then improper first char
	        emailRecognizerErrorMessage += "An email must start with A-Z, a-z, or 0-9.\n";
	        return emailRecognizerErrorMessage;
	    case 1:
	    		// Possible improper char, or no @ symbol
	        if (invalidChar) {
	        	emailRecognizerErrorMessage +=
		                "An email character must be A-Z, a-z, 0-9, +, _, or -.\n";
	        } else {
	            emailRecognizerErrorMessage += "An email must contain an '@' symbol.\n";
	        }
	        return emailRecognizerErrorMessage;
	    case 2:
	    		// improper char after a period
	        emailRecognizerErrorMessage +=
	            "After a dot in the local part, use A-Z, a-z, or 0-9 (no consecutive dots).\n";
	        return emailRecognizerErrorMessage;
	    case 3:
	    		// improper char after @ symbol
	        emailRecognizerErrorMessage +=
	            "Immediately after '@' you must have A-Z, a-z, or 0-9.\n";
	        return emailRecognizerErrorMessage;
	    case 4:
	    		// Can't use a hyphen before the period
	    	emailRecognizerErrorMessage +=
            "You may not use a hyphen directly before the period in a TLD.\n";
        return emailRecognizerErrorMessage;
	    case 5:
	    		// Possible too many @ symbols, invalid char, or a improper domain label
	        if (lastWasHyphen && inTLD) {
	            emailRecognizerErrorMessage +=
	                "A domain label cannot end with '-' before the TLD dot.\n";
	        } else if (atsymbol > 1) {
	        	emailRecognizerErrorMessage +=
		                "A valid email can only have one @ symbol.\n";
	        }else{
	            emailRecognizerErrorMessage +=
	                "Domain characters must be A-Z, a-z, 0-9, '-' or '.' (dot starts the TLD).\n";
	        }
	        return emailRecognizerErrorMessage;
	    case 6:
	    		// Final State, checks for valid TLD length, otherwise invalidChar is true
	        if (TLDSize < 2) {
	            emailRecognizerErrorMessage +=
	                "An email TLD must have at least two letters.\n";
	            return emailRecognizerErrorMessage;
	        } else if (invalidChar){
	        	emailRecognizerErrorMessage +=
		             "An email TLD must have A-Z, a-z.\n";
		        return emailRecognizerErrorMessage;
	        } else {
	        	return "";
	        }
		default:
			// This is for the case where we have a state that is outside of the valid range.
			// This should not happen
			return "";
		}
	}
	
	// Console Test 
	public static void emailTest(int number, String email) {
		String result = checkForValidEmail(email);
		System.out.println(number + ". " + email);
		if (!result.isEmpty()) {
			System.out.println(result + "\n");
		} else {
			System.out.println("Valid username\n");
		}
	}

	public static void main(String[] args) {
		// Run test cases
		emailTest(1, ".exmaplename@where.com"); // Started with a period
		emailTest(2, "example..name@where.com"); // Consecutive periods
		emailTest(3, "examplename.@where.com"); // Period before the @
		emailTest(4, "example+-.name@where.com"); // Valid
		emailTest(5, "ex@mplen@me@where.com"); // Multiple @ symbols
		emailTest(6, "examplename@-where.com"); // Hyphen after the @
		emailTest(7, "examplename@where-.com"); // Hyphen before the TLD
		emailTest(8, "examplename@wher.e.com"); // Multiple periods in the domain
		emailTest(9, "examplename@whe-re.com"); // valid
		emailTest(10, "examplename@where.r6"); // invalid numeric value
		emailTest(11, "examplename@where.k$"); // invalid special char
		emailTest(12, "examplenamewhere.com"); // invalid no @ symbol
		emailTest(13, "exampl/ename@where.com"); // invalid character
		emailTest(14, "Test@gmail.com"); // valid
	}
}
package usernameRecognizer;


/*
 * * Rules:
 *  - Length: 4 to 32 inclusive
 *  - Allowed: A–Z, a–z, 0–9, and special characters { '.', '_', '-' }
 *  - Cannot start with a special character
 *  - Cannot end with a special character
 *  - No consecutive special characters
 *  - No whitespace or other characters
 * 
 * 
 * */



public class usernameRecognizer {

	// Error state info
		public static String errorMsg = "";
		public static String userNameInput = "";
		public static int indexOfError = -1;

		private static final int MIN_LEN = 4;
		private static final int MAX_LEN = 32;

		// FSM state variables
		private static int state = 0;
		private static int nextState = 0;
		private static boolean finalState = false;
		private static String inputLine = "";
		private static char currentChar;
		private static int currentCharIndx;
		private static boolean isRunning;
		private static int usernameSize = 0;

		// Variables of letters, numbers, special char
		private static boolean isLetter(char c) {
			return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
		}
		private static boolean isNum(char c) {
			return (c >= '0' && c <= '9');
		}
		private static boolean isAlphaNum(char c) {
			return isLetter(c) || isNum(c);
		}
		private static boolean isSpecialChar(char c) {
			return c == '.' || c == '-' || c == '_';
		}

		private static void moveToNextCharacter() {
			currentCharIndx++;
			if (currentCharIndx < inputLine.length()) {
				currentChar = inputLine.charAt(currentCharIndx);
			} else {
				currentChar = ' ';
				isRunning = false;
			}
		}

		
		public static String usernameValidate(String input) {
			// Empty check
			if (input == null || input.length() == 0) {
				indexOfError = 0;
				return "ERROR: Username is empty.";
			}

			// Initialize FSM
			state = 0;
			inputLine = input;
			currentCharIndx = 0;
			currentChar = input.charAt(0);
			userNameInput = input;
			isRunning = true;
			nextState = -1;
			finalState = false;
			usernameSize = 0;

			// Process FSM
			while (isRunning) {
				switch (state) {
					case 0: // start
						if (isAlphaNum(currentChar)) {
							nextState = 1;
							usernameSize++;
						} else if (isSpecialChar(currentChar)) {
							isRunning = false; // can't start with special character
						} else if (Character.isWhitespace(currentChar)) {
							isRunning = false; // no spaces allowed
						} else {
							isRunning = false; // disallowed
						}
						break;

					case 1: // in valid run
						if (isAlphaNum(currentChar)) {
							nextState = 1;
							usernameSize++;
						} else if (isSpecialChar(currentChar)) {
							nextState = 2;
							usernameSize++;
						} else if (Character.isWhitespace(currentChar)) {
							isRunning = false;
						} else {
							isRunning = false;
						}
						if (usernameSize > MAX_LEN) isRunning = false;
						break;

					case 2: // after specialChar
						if (isAlphaNum(currentChar)) {
							nextState = 1;
							usernameSize++;
						} else {
							isRunning = false; // prevents consecutive specialChar or ending specialChar
						}
						if (usernameSize > MAX_LEN) isRunning = false;
						break;

					default:
						isRunning = false;
				}

				if (isRunning) {
					moveToNextCharacter();
					state = nextState;
					finalState = (state == 1);
					nextState = -1;
				}
			}

			// Decide error or success for each state
			indexOfError = currentCharIndx;
			errorMsg = "ERROR: ";

			switch (state) {
				case 0:
					char c0 = input.charAt(0);
					if (isSpecialChar(c0)) return errorMsg + "Username can't start with a special character.";
					if (Character.isWhitespace(c0)) return errorMsg + "Username can't have a space.";
					return errorMsg + "Username can't start with the '" + c0 + "' character.";

				case 1:
					if (usernameSize < MIN_LEN)
						return errorMsg + "Username is too short (minimum " + MIN_LEN + " characters).";
					if (usernameSize > MAX_LEN)
						return errorMsg + "Username is too long (maximum " + MAX_LEN + " characters).";
					if (currentCharIndx < input.length()) {
						char invalidChar = input.charAt(currentCharIndx);
						if (Character.isWhitespace(invalidChar)) return errorMsg + "Username can't have a space.";
						if (isSpecialChar(invalidChar)) return errorMsg + "Username cannot have consecutive special characters.";
						return errorMsg + "Username can't have the '" + invalidChar + "' character.";
					}
					// valid
					indexOfError = -1;
					errorMsg = "";
					return "";

				case 2:
					if (currentCharIndx >= input.length())
						return errorMsg + "Username can't end with a special character.";
					char invalidChar2 = input.charAt(currentCharIndx);
					if (isSpecialChar(invalidChar2)) return errorMsg + "Username cannot have consecutive special characters.";
					if (Character.isWhitespace(invalidChar2)) return errorMsg + "Username can't have a space.";
					return errorMsg + "Username can't have the '" + invalidChar2 + "' character.";

				default:
					return "";
			}
		}

		// Console Test 
		public static void usernameTest(int number, String username) {
			String result = usernameValidate(username);
			System.out.println(number + ". " + username);
			if (!result.isEmpty()) {
				System.out.println(result + "\n");
			} else {
				System.out.println("Valid username\n");
			}
		}

		public static void main(String[] args) {
			// Run test cases
			usernameTest(1, "jim"); // too short
			usernameTest(2, "jimP"); // valid
			usernameTest(3, "Jim_P"); // valid
			usernameTest(4, ".jim"); // invalid, special start
			usernameTest(5, "jim..p"); // invalid, consecutive special
			usernameTest(6, "jim-p"); // valid
			usernameTest(7, "jim p"); // invalid, space
			usernameTest(8, "jim#p"); // invalid char
			usernameTest(9, "jim_p1"); // valid
			usernameTest(10, "jimjimjimjimjimjimjimjimjimjimjim"); // invalid, too long
			usernameTest(11, "jim_"); // invalid, ends with special
			usernameTest(12, "jimjimjimjimjimjimjimjimjimjim_p"); // valid, 32 chars
		}
}

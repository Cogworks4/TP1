package emailRecognizer;

import java.util.Optional;

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
	static char notAllowedChar[] = { '(', ')', ',', ':', ';', '<', '>', '[', ']', '\\', '"', '@', ' ' };
	static int notAllowedCharSize = notAllowedChar.length;

	static public void main(String[] args) {
		// testing(1, "jacob..henry@gmail.com");
		// testing(2, "jacob]henry@gmail.com");
		// testing(3, "jacob@henry@gmail.com");
		// testing(4, "jacob.henry@gmail.c");
		// testing(5, ".jacob.henry@gmail.com");
		// testing(6, "jacobhenry.@gmail.com");
		// testing(7, "jacobhenry@-gmail.com");
		// testing(8, "jacobhenry@gmail-.com");
		// testing(9, "jacobhenry@gmail.c0m");
		// testing(10, "jacobhenry");
		// testing(11, "jacobhenry@.gmail.com");
		// testing(12, "jacobhenry@gmail.com");
	}

	/*
	 * Input Validation Rules: - Can only use one @ symbol - Before the @ symbol,
	 * can only use alphabetic characters, numbers underscores (_), dots (.),
	 * hyphens (-), and plus signs (+) - Before the @ symbol, you cannot start or
	 * end with a dot, or have consecutive dots - After the @ symbol, you have to
	 * have one dot, cannot start or end with a hyphen, TLD (.com) has to be minimum
	 * two characters - Must be <= 254 char, local <= 64, domain <= 253
	 */

	/**
	 * @param result
	 * @return
	 */
	static public String validateEmail(Optional<String> result) {
		if (result.isPresent()) {
			inputIndex = result.get().length(); // updates the length of the input index
			int symbolCount = 0; // the count for a specified symbol
			int atSymbol = 0; // remembers if the email has a domain

			// Checks email length
			if (inputIndex > 254) {
				return errorMessage = "Your email is too long";
			}

			// Checks for any inappropriate characters or dots in inappropriate places
			for (int i = 0; i < inputIndex; i++) {
				for (int j = 0; j < notAllowedCharSize; j++) {

					// Checks for inappropriate characters
					if (result.get().charAt(i) == notAllowedChar[j]) {

						// If it's only one @ symbol continue
						if (result.get().charAt(i) == '@') {
							symbolCount += 1;
							// If there are are more than one @ symbol return the appropriate error
							if (symbolCount > 1) {
								return errorMessage = "You cannont use the '@' character more than once";
							}
						}

						if (result.get().charAt(i) != '@') {
							// Returns a appropriate error
							errorMessage = "You cannont use the ' ' character";
							StringBuilder sb = new StringBuilder(errorMessage);
							sb.setCharAt(21, result.get().charAt(i));
							return errorMessage = sb.toString();
						}
					}

					// Checks for any dots at the first index, last index before the @ or after the
					// @
					// symbol, or any subsequent dots
					if (result.get().charAt(i) == '.' && i == 0) { // dot at start
						return errorMessage = "You cannont use the '.' char at the beginning of your email";
					} else if (result.get().charAt(i) == '.' && result.get().charAt(i + 1) == '@') { // dot before @
						return errorMessage = "You cannont use the '.' char right after the @ symbol";
					} else if (result.get().charAt(i) == '.' && result.get().charAt(i + 1) == '.') { // consecutive dots
						return errorMessage = "You cannont use the '.' char consecutively";
					}
				}
			}

			symbolCount = 0;

			// Checks for extra dots and a minimum char limit for the TLD
			for (int i = 0; i < inputIndex; i++) {
				if (result.get().charAt(i) == '@') {

					// Remembers the @ symbol was in the input
					atSymbol = 1;

					// you cannot start with a hyphen after the @
					if (result.get().charAt(i + 1) == '-') {
						return errorMessage = "You cannont use a hyphen after the @ symbol";
					}

					for (int j = i; j < inputIndex; j++) {
						if (result.get().charAt(j) == '.') {

							// Checks the minimum of two characters for the TLD
							if (j + 3 > inputIndex) {
								return errorMessage = "You must have a valid TLD (.com, .net, etc) for your email";
							}

							// you cannot end with a hyphen after the @ before the TLD
							if (result.get().charAt(j - 1) == '-') {
								return errorMessage = "You cannont use a hyphen right before the TLD (.com, .net, etc)";
							}

							// Checks for only alphabetic characters for the TLD
							for (int k = j + 1; k < inputIndex; k++) {
								if (!Character.isLetter(result.get().charAt(k))) {
									return errorMessage = "You must have a valid TLD (.com, .net, etc)";
								}
							}
						}
					}
				}
			}

			// if there is no @ symbol issue the correct output
			if (atSymbol == 0) {
				return errorMessage = "You must use an @ symbol and a domain in your email";
			}

			return "";
		} else
			return "";
	}

	static public void testing(int number, Optional<String> Email) {
		System.out.print(number + ".\n" + Email + "\nError Message:   ");
		System.out.println(validateEmail(Email) + "\n\n");
	}

}
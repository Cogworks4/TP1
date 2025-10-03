package guiUserUpdate;

public class Model {

	/**
	 * Checks if inputted parameter really contains an actual error message if it
	 * does contain a message, shift layout positions to accomedate new label
	 * 
	 * @param error
	 * @return boolean value for conditional statement, true if there isn't a error,
	 *         false if there is
	 */
	static boolean showErrorMessage(String error) {
		if (error.length() <= 0) {
			ViewUserUpdate.button_ProceedToUserHomePage.setLayoutY(450);
			ViewUserUpdate.label_ErrorMessage.setText("");
			return true;
		} else {
			int tot = 0;
			for (int i = 0; i < error.length(); i++) {
				char c = error.charAt(i);

				if (c == '\n') {
					tot++;
				}
				ViewUserUpdate.button_ProceedToUserHomePage.setLayoutY(505 + ((tot - 1) * 18));
			}
		}
		return false;

	}

	static String guiPwdError(String error) {
		String[] errCode = { "Upper case", "Lower case", "Numeric digits", "Special character", "Long Enough",
				"Password Length" };
		String[] messages = { "There is no Upper Case", "There is no Lower Case", "Password is missing Numeric Digits", "Password is missing Special Characters",
				"Password is longer than 7", "Password is shorter than 32" };
		String result = "In Password:";
		
		for (int i = 0; i < errCode.length; i++) {
			if (error.contains(errCode[i])) {
				result += "\n" + messages[i];
			}
		}
		
		return result;
	}
}

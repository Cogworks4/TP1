package guiUserUpdate;

public class Model {
	
	
	/**
	 * Checks if inputted parameter really contains an actual error message
	 * if it does contain a message, shift layout positions to accomedate 
	 * new label
	 * 
	 * @param error
	 * @return boolean value for conditional statement, true if there isn't a error, false if there is
	 */
	static boolean showErrorMessage(String error) {
		if (error.length() <= 0) {
			ViewUserUpdate.button_ProceedToUserHomePage.setLayoutY(450);
			ViewUserUpdate.label_ErrorMessage.setText("");
			return true;
		} else
			ViewUserUpdate.button_ProceedToUserHomePage.setLayoutY(500);
		return false;

	}
}

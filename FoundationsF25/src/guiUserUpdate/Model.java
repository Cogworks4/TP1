package guiUserUpdate;

public class Model {

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

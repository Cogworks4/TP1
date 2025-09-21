package guiFirstAdmin;

/*******
 * <p> Title: ModelFirstAdmin Class. </p>
 * 
 * <p> Description: The First System Startup Page Model.  This class is not used as there is no
 * data manipulated by this MVC beyond accepting a username and password and then saving it in the
 * database.  When the code is enhanced for input validation, this model may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */

public class ModelFirstAdmin {
	
	

	/**
	 * Takes in error message is input parameter, if there is an error
	 * adjust UI elements to accomadate error message returns true if no error appears
	 * returns false otherwise
	 * 
	 * @param errorMessage - string input, error message returned textbox check
	 * @return  - boolean value, true if there is error, false otherwise
	 */
	static boolean guiUsernameErrors(String errorMessage) {
		if (errorMessage.length() <= 0) {
			ViewFirstAdmin.label_UsernameError.setText("");
			ViewFirstAdmin.text_AdminPassword1.setLayoutY(210);
			ViewFirstAdmin.text_AdminPassword2.setLayoutY(260);
			return true;
		}
		else {
			ViewFirstAdmin.label_UsernameError.setText(errorMessage);
			ViewFirstAdmin.text_AdminPassword1.setLayoutY(215);
			ViewFirstAdmin.text_AdminPassword2.setLayoutY(265);
			return false;
		}
	}
}

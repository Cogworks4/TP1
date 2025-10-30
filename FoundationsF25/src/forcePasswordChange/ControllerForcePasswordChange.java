package forcePasswordChange;

import database.Database;

public class ControllerForcePasswordChange {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;	
	
	
	/**********
	 * <p> Method: paintTheWindow() </p>
	 * 
	 * <p> Description: This method paints the window and resets all the attributes </p>
	 * 
	 */
	protected static void paintTheWindow() {
		// Clears the previous children
		ViewForcePasswordChange.theRootPane.getChildren().clear();

		// Clears all the text field and resets the button to default
		ViewForcePasswordChange.textField_createPass1.clear();
		ViewForcePasswordChange.textField_createPass2.clear();
		ViewForcePasswordChange.label_Pass1Valid.setText("");
		ViewForcePasswordChange.label_Pass2Valid.setText("");
		ViewForcePasswordChange.button_setPass.setDisable(true);
    
		// Adds the children to the scene
		ViewForcePasswordChange.theRootPane.getChildren().addAll(
			ViewForcePasswordChange.label_pageTitle, ViewForcePasswordChange.label_User,
			ViewForcePasswordChange.prompt, ViewForcePasswordChange.line_Separator1,
			ViewForcePasswordChange.label_Password1, ViewForcePasswordChange.textField_createPass1,
			ViewForcePasswordChange.label_Pass1Valid, ViewForcePasswordChange.label_Password2,
			ViewForcePasswordChange.textField_createPass2, ViewForcePasswordChange.passReqs,
			ViewForcePasswordChange.button_checkPass, ViewForcePasswordChange.button_setPass,
			ViewForcePasswordChange.line_Separator4, ViewForcePasswordChange.button_quit);
	
		// Sets title of stage and sets scene
		ViewForcePasswordChange.theStage.setTitle("CSE 360 Foundation Code: Change Password Page");
		ViewForcePasswordChange.theStage.setScene(ViewForcePasswordChange.theForcePaswordChangeScene);
		ViewForcePasswordChange.theStage.show();

	}
	/**********
	 * <p> Method: checkPass() </p>
	 * 
	 * <p> Description: This method calls PasswordValidation and checks if passwords are valid. </p>
	 * 
	 */
	protected static boolean checkPass() {
		inputValidation.PasswordValidation.adminPassword1 = ViewForcePasswordChange.Password1;
		inputValidation.PasswordValidation.adminPassword2 = ViewForcePasswordChange.Password2;
		// checks if password is valid and equal and saves it to actualPassword in View
		if(inputValidation.PasswordValidation.checkValidity()) {
			System.out.println("Password was Valid!");
			ViewForcePasswordChange.actualPassword = ViewForcePasswordChange.Password1;
			return true;
		}
		else {	//clears password inputs if invalid
			ViewForcePasswordChange.textField_createPass1.clear();
			ViewForcePasswordChange.textField_createPass2.clear();
			System.out.println("Password was not Valid!");
			return false;
		}
		
	}
	
	/**********
	 * <p> Method: createPass() </p>
	 * 
	 * <p> Description: This method creates a password and saves it to View to use. </p>
	 * 
	 */
	protected static void createPass() {
		ViewForcePasswordChange.Password1 = ViewForcePasswordChange.textField_createPass1.getText();
		ViewForcePasswordChange.Password2 = ViewForcePasswordChange.textField_createPass2.getText();
		System.out.println(ViewForcePasswordChange.Password1);
		System.out.println(ViewForcePasswordChange.Password2);
		
	}
	
	/**********
	 * <p> Method: setPassword() </p>
	 * 
	 * <p> Description: This method sets the password and saves it to the database to use, 
	 * removing it when after password is changed. </p>
	 * 
	 */
	protected static void setPassword() {
		theDatabase.updatePassword(ViewForcePasswordChange.user, ViewForcePasswordChange.actualPassword);		
		theDatabase.removeOneTimePassword(ViewForcePasswordChange.user);
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewForcePasswordChange.theStage);
		
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}

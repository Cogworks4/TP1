package oneTimePassword;

import database.Database;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;

public class ControllerOneTimePassword {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	
	/**********
	 * <p> Method: doSelectUser() </p>
	 * 
	 * <p> Description: This method uses the ComboBox widget, fetches which item in the ComboBox
	 * was selected (a user in this case), and establishes that user and the current user, setting
	 * easily accessible values without needing to do a query. </p>
	 * 
	 */
	protected static void doAction() {
		ViewOneTimePassword.theSelectedUser = 
				(String) ViewOneTimePassword.combobox_selectUser.getValue();
		theDatabase.getUserAccountDetails(ViewOneTimePassword.theSelectedUser);
		
		ViewOneTimePassword.theSelectedGen = 
				(String) ViewOneTimePassword.combobox_randOrCreate.getValue();
		
		repaintTheWindow();
	}
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewOneTimePassword.theRootPane.getChildren().clear();
				
		//Determine which of the views to display based on selected options
		if(ViewOneTimePassword.theSelectedUser.compareTo("<Select a User>") == 0) {
			// Only show the request to select a user via ComboBox
			ViewOneTimePassword.theRootPane.getChildren().addAll(
					ViewOneTimePassword.label_pageTitle, ViewOneTimePassword.label_User, 
					ViewOneTimePassword.button_UpdateThisUser, ViewOneTimePassword.line_Separator1, 
					ViewOneTimePassword.label_SelectUser, ViewOneTimePassword.combobox_selectUser,
					ViewOneTimePassword.line_Separator4, ViewOneTimePassword.button_return,
					ViewOneTimePassword.button_logout, ViewOneTimePassword.button_quit);
		}
		else {
			// Show all the fields after user has been selected
			ViewOneTimePassword.theRootPane.getChildren().addAll(
					ViewOneTimePassword.label_pageTitle, ViewOneTimePassword.label_User, 
					ViewOneTimePassword.button_UpdateThisUser, ViewOneTimePassword.line_Separator1,
					ViewOneTimePassword.label_SelectUser, ViewOneTimePassword.combobox_selectUser,
					ViewOneTimePassword.label_generate, ViewOneTimePassword.combobox_randOrCreate,
					ViewOneTimePassword.line_Separator4, ViewOneTimePassword.button_return,
					ViewOneTimePassword.button_logout, ViewOneTimePassword.button_quit);
		}
		if(ViewOneTimePassword.theSelectedGen.compareTo("<Select generation>") == 0 
				&& ViewOneTimePassword.theSelectedUser.compareTo("<Select a User>") != 0) {
			ViewOneTimePassword.theRootPane.getChildren().clear();
			ViewOneTimePassword.theRootPane.getChildren().addAll(
					ViewOneTimePassword.label_pageTitle, ViewOneTimePassword.label_User, 
					ViewOneTimePassword.button_UpdateThisUser, ViewOneTimePassword.line_Separator1,
					ViewOneTimePassword.label_SelectUser, ViewOneTimePassword.combobox_selectUser,
					ViewOneTimePassword.label_generate, ViewOneTimePassword.combobox_randOrCreate,
					ViewOneTimePassword.line_Separator4, ViewOneTimePassword.button_return,
					ViewOneTimePassword.button_logout, ViewOneTimePassword.button_quit);
			
		}
		else if(ViewOneTimePassword.theSelectedGen.compareTo("Create") == 0
				&& ViewOneTimePassword.theSelectedUser.compareTo("<Select a User>") != 0) {
			ViewOneTimePassword.theRootPane.getChildren().clear();
			ViewOneTimePassword.theRootPane.getChildren().addAll(
					ViewOneTimePassword.label_pageTitle, ViewOneTimePassword.label_User, 
					ViewOneTimePassword.button_UpdateThisUser, ViewOneTimePassword.line_Separator1,
					ViewOneTimePassword.label_SelectUser, ViewOneTimePassword.combobox_selectUser,
					ViewOneTimePassword.label_generate, ViewOneTimePassword.combobox_randOrCreate,
					ViewOneTimePassword.button_checkPass, ViewOneTimePassword.passReqs,
					ViewOneTimePassword.textField_createPass, ViewOneTimePassword.line_Separator4,
					ViewOneTimePassword.button_return, ViewOneTimePassword.button_logout, 
					ViewOneTimePassword.button_quit, ViewOneTimePassword.button_sendOneTime);
			
					
		}
		else if(ViewOneTimePassword.theSelectedGen.compareTo("Randomize") == 0
				&& ViewOneTimePassword.theSelectedUser.compareTo("<Select a User>") != 0) {
			
			ViewOneTimePassword.theRootPane.getChildren().clear();
			ViewOneTimePassword.theRootPane.getChildren().addAll(
			ViewOneTimePassword.label_pageTitle, ViewOneTimePassword.label_User, 
			ViewOneTimePassword.button_UpdateThisUser, ViewOneTimePassword.line_Separator1,
			ViewOneTimePassword.label_SelectUser, ViewOneTimePassword.combobox_selectUser,
			ViewOneTimePassword.label_generate, ViewOneTimePassword.combobox_randOrCreate,
			ViewOneTimePassword.button_randomizePass, ViewOneTimePassword.displayRandomPassword,
			ViewOneTimePassword.line_Separator4, ViewOneTimePassword.button_return,
			ViewOneTimePassword.button_logout, ViewOneTimePassword.button_quit, 
			ViewOneTimePassword.button_sendOneTime);	
		}
			ViewOneTimePassword.theStage.setTitle("CSE 360 Foundation Code: Admin Opertaions Page");
			ViewOneTimePassword.theStage.setScene(ViewOneTimePassword.theOneTimePasswordScene);
			ViewOneTimePassword.theStage.show();
	}
	
	
	protected static void setOneTime() {
		
		
		
		
	}
	
	protected static void generateRand() {
		ViewOneTimePassword.Password = ModelOneTimePassword.generateRandomPassword();
		System.out.println(ModelOneTimePassword.password);
		ViewOneTimePassword.displayRandomPassword.setText("One Time Password: " + ViewOneTimePassword.Password);
		ViewOneTimePassword.button_sendOneTime.setDisable(false);
		
		
	}
	
	protected static boolean checkPass() {
		inputValidation.PasswordValidation.adminPassword1 = ViewOneTimePassword.Password;
		inputValidation.PasswordValidation.adminPassword2 = ViewOneTimePassword.Password;
		if(inputValidation.PasswordValidation.checkValidity()) {
			System.out.println("Password was Valid!");
			return true;
		}
		else {
			ViewOneTimePassword.textField_createPass.clear();
			System.out.println("Password was not Valid!");
			return false;
		}

		
		
	}
	
	protected static void createPass() {
		ViewOneTimePassword.Password = ViewOneTimePassword.textField_createPass.getText();
		System.out.println(ViewOneTimePassword.Password);
		
	}
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user (who must be an Admin as only admins are the
	 * only users who have access to this page) to the Admin Home page. </p>
	 * 
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewOneTimePassword.theStage,
				ViewOneTimePassword.theUser);
	}
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewOneTimePassword.theStage);
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

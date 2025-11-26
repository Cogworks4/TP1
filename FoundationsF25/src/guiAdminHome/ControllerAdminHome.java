package guiAdminHome;

import javafx.scene.control.ChoiceDialog;
import java.util.List;
import java.util.Optional;

import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import guiAdminHome.ViewAdminHome;




/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		
		//deadline instituted for 24 hours to respond to invitation
		LocalDateTime deadline = LocalDateTime.now().plusHours(24);
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole, deadline);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	
	/**********
	 * <p>Title: adminTickets() Method.</p>
	 *
	 * <p>Description: Opens the Admin Tickets page so admins can create
	 * and manage open/closed admin requests.</p>
	 **********/
	protected static void adminTickets() {
	    guiAdminTickets.ViewAdminTickets.displayAdminTickets(
	            ViewAdminHome.theStage,
	            ViewAdminHome.theUser
	    );
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that calls the OneTimePassword scene allowing user
	 * to create a OTP and change a certain users password. </p>
	 */
	protected static void setOnetimePassword () {
		oneTimePassword.ViewOneTimePassword.displayOneTimePassword(ViewAdminHome.theStage,
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method to select a user from the database and delete
	 * their account </p>
	 */
	protected static void deleteUser() {
		
		// This method creates a GUI widget that will allow an admin to select
		// and delete another user. The selected user cannot be the current user
		// and cannot be the last admin
		
		List<String> usernames = theDatabase.getUserList();
		// Remove the "<Select a User>" entry
		usernames.remove(0);
		
		// Widget attributes for the GUI 
		ChoiceDialog<String> dialog = new ChoiceDialog<>(usernames.get(0), usernames);
		dialog.setTitle("Delete User");
		dialog.setHeaderText("Select a user to delete:");
		dialog.setContentText("Username:");

		Optional<String> result = dialog.showAndWait();
		if (!result.isPresent()) return;  // User cancelled
		String usernameToDelete = result.get();
		
		
		boolean found = theDatabase.getUserAccountDetails(usernameToDelete);
		if (!found) {
		    showError("Could not find user details for " + usernameToDelete);
		    return;
		}
		// Attributes for deletion validation
		boolean targetIsAdmin = theDatabase.getCurrentAdminRole();
		String currentUsername = ViewAdminHome.theUser.getUserName();
		boolean isDeletingSelf = usernameToDelete.equals(currentUsername);
		int adminCount = theDatabase.getNumberOfAdmins();
		
		// Block deleting your own admin account
		if (isDeletingSelf) {
		    showError("You cannot delete your own account as an admin.");
		    return;
		}
		// Block deleting the last remaining admin
		if (targetIsAdmin && adminCount <= 1) {
		    showError("You cannot delete the last remaining admin.");
		    return;
		}
		
		// Confirm with user before deleting
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirm Deletion");
		confirm.setHeaderText("Are you sure you want to delete this user?");
		confirm.setContentText("This action cannot be undone.");

		Optional<ButtonType> confirmationResult = confirm.showAndWait();
		if (confirmationResult.isEmpty() || confirmationResult.get() != ButtonType.OK) {
		    return; // User canceled
		}
		
		// Display result to user
		boolean success = theDatabase.deleteUser(usernameToDelete); 
		if (success) {
		    showInfo("User deleted successfully.");
		} else {
		    showError("Failed to delete user.");
		}
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
		guiListUsers.ViewListUsers.displayListUsers(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		if (emailAddress.length() == 0) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
	
	// helper method for showing errors
	private static void showError(String msg) {
	    Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setTitle("Error");
	    alert.setHeaderText("Cannot Continue");
	    alert.setContentText(msg);
	    alert.showAndWait();
	}
	// helper method for showing info
	private static void showInfo(String msg) {
	    Alert alert = new Alert(Alert.AlertType.INFORMATION);
	    alert.setTitle("Info");
	    alert.setHeaderText(null);
	    alert.setContentText(msg);
	    alert.showAndWait();
	}
}

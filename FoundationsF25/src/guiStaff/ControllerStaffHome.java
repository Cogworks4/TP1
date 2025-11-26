package guiStaff;

import java.sql.SQLException;

import database.Database;
import guiStudentPosts.ViewAddPost;
import guiStudentPosts.ViewStudentPosts;

public class ControllerStaffHome {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */
	
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
	protected static void StaffPosts(String Thread) {
		guiStudentPosts.ViewStudentPosts.displayStudentPosts(ViewStaffHome.theStage, 
				ViewStaffHome.theUser, Thread);
	}
	
	/**
     * Repaints the Staff Posts window with all UI elements 
     * 
     * <p>Repaints the current window with all of the content from the View within
     * the package<p>
     */
		protected static void repaintTheWindow() {
			ViewStaffHome.theRootPane.getChildren().setAll(
					ViewStaffHome.label_PageTitle, 
					ViewStaffHome.label_UserDetails,
					ViewStaffHome.line_Separator1,
					ViewStaffHome.line_Separator2,
					ViewStaffHome.list_Threads,
					ViewStaffHome.button_Quit,
					ViewStaffHome.button_Logout,
					ViewStaffHome.button_AddThread);
			
			// Set the title for the window
			ViewStaffHome.theStage.setTitle("CSE 360 Foundation Code: Staff Home Page");
			ViewStaffHome.theStage.setScene(ViewStaffHome.theViewStaffHomeScene);
			ViewStaffHome.theStage.show();
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffHome.theStage);
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
	
	/**********
	 * <p> Method: performAddThread() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performAddThread() {
	    ViewAddThread.open(ViewStaffHome.theStage, "", "", input -> {

	        String title = input.title();
	        String tags  = input.tags();

	        try {
	            theDatabase.writeThread(title, tags);
	        } catch (SQLException e) {
	            System.err.println("*** ERROR *** Database error trying to write a Thread: " + e.getMessage());
	            e.printStackTrace();
	            return;
	        }

	        ViewStaffHome.list_Threads.getItems().add(0, title + " - " + tags);

	    }, true);
	}
}
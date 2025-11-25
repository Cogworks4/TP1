package guiStudent;

import guiStudent.ViewStudentHome;
import guiStudentPosts.ViewStudentPosts;

public class ControllerStudentHome {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */
	
	protected static void StudentPosts(String Thread) {
		guiStudentPosts.ViewStudentPosts.displayStudentPosts(ViewStudentHome.theStage, 
				ViewStudentHome.theUser, Thread);
	}
	
	/**
     * Repaints the Student Posts window with all UI elements 
     * 
     * <p>Repaints the current window with all of the content from the View within
     * the package<p>
     */
		protected static void repaintTheWindow() {
			ViewStudentHome.theRootPane.getChildren().setAll(
					ViewStudentHome.label_PageTitle, 
					ViewStudentHome.label_UserDetails,
					ViewStudentHome.line_Separator1,
					ViewStudentHome.line_Separator2,
					ViewStudentHome.list_Threads,
					ViewStudentHome.button_Quit,
					ViewStudentHome.button_Logout);
			
			// Set the title for the window
			ViewStudentHome.theStage.setTitle("CSE 360 Foundation Code: Student Home Page");
			ViewStudentHome.theStage.setScene(ViewStudentHome.theViewStudentHomeScene);
			ViewStudentHome.theStage.show();
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStudentHome.theStage);
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
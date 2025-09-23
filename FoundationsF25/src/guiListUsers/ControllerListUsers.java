package guiListUsers;

/*******
 * <p> Title: ControllerlListUsers Class. </p>
 * 
 * <p> Description: </p>
 * 
 * @author Jacob Sheridan
 * 
 * @version 1.00		2025-09-22 Initial version
 *  
 */


import database.Database;


public class ControllerListUsers {
	
		// Reference for the in-memory database so this package has access
		private static Database theDatabase = applicationMain.FoundationsMain.database;	
		
		protected static void repaintTheWindow() {
			ViewListUsers.theRootPane.getChildren().setAll(
					ViewListUsers.label_PageTitle, ViewListUsers.label_UserDetails,
					ViewListUsers.line_Separator1,
					ViewListUsers.line_Separator2,
					ViewListUsers.list_Users,
					ViewListUsers.button_Return);
			
			// Set the title for the window
			ViewListUsers.theStage.setTitle("CSE 360 Foundation Code: List Users Page");
			ViewListUsers.theStage.setScene(ViewListUsers.theListUsersScene);
			ViewListUsers.theStage.show();
		}
		
		protected static void updateList() {
			
		}
		
		protected static void performReturn() {
			guiAdminHome.ViewAdminHome.displayAdminHome(ViewListUsers.theStage,
					ViewListUsers.theUser);
		}
		
	
}
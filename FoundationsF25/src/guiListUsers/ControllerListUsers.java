package guiListUsers;

import database.Database;
import guiAddRemoveRoles.ViewAddRemoveRoles;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;


public class ControllerListUsers {
	
		// Reference for the in-memory database so this package has access
		private static Database theDatabase = applicationMain.FoundationsMain.database;	
		
		protected static void repaintTheWindow() {
			ViewListUsers.theRootPane.getChildren().addAll(
					ViewListUsers.label_PageTitle, ViewListUsers.label_UserDetails, 
					ViewListUsers.button_UpdateThisUser, ViewListUsers.line_Separator1,
					ViewListUsers.label_SelectUser, ViewListUsers.combobox_SelectUser, 
					ViewListUsers.line_Separator4, ViewListUsers.button_Return,
					ViewListUsers.button_Logout, ViewListUsers.button_Quit);
			
			// Add the list of widgets to the stage and show it
			
			// Set the title for the window
			ViewListUsers.theStage.setTitle("CSE 360 Foundation Code: Admin Opertaions Page");
			ViewListUsers.theStage.setScene(ViewListUsers.theAddRemoveRolesScene);
			ViewListUsers.theStage.show();
		}
		
		protected static void performReturn() {
			guiAdminHome.ViewAdminHome.displayAdminHome(ViewListUsers.theStage,
					ViewListUsers.theUser);
		}
		
	
}
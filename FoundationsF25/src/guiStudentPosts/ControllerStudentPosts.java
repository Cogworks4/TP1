package guiStudentPosts;

import java.sql.SQLException;

import database.Database;

/*******
 * <p> Title: ControllerlStudentPost Class. </p>
 * 
 * <p> Description: </p>
 * 
 * @author Jacob Sheridan
 * 
 * @version 1.00		2025-09-22 Initial version
 *  
 */

public class ControllerStudentPosts {
	
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
		protected static void repaintTheWindow() {
			ViewStudentPosts.theRootPane.getChildren().setAll(
					ViewStudentPosts.label_PageTitle, 
					ViewStudentPosts.label_UserDetails,
					ViewStudentPosts.label_ThreadTitle,
					ViewStudentPosts.line_Separator1,
					ViewStudentPosts.line_Separator2,
					ViewStudentPosts.list_Posts,
					ViewStudentPosts.button_Return,
					ViewStudentPosts.button_AddPost);
			
			// Set the title for the window
			ViewStudentPosts.theStage.setTitle("CSE 360 Foundation Code: Student Posts Page");
			ViewStudentPosts.theStage.setScene(ViewStudentPosts.theStudentPostScene);
			ViewStudentPosts.theStage.show();
		}
		
		protected static void performReturn() {
			guiStudent.ViewStudentHome.displayStudentHome(ViewStudentPosts.theStage,
					ViewStudentPosts.theUser);
		}
		
		protected static void performAddPost() {
			ViewAddPost.open(ViewStudentPosts.theStage, "", "", input -> {
		        // Create in your store
				var post = ViewStudentPosts.postStore.create(
					    ViewStudentPosts.theUser.getUserName(),
					    input.title(),
					    input.body(),
					    ViewStudentPosts.CurrentThread,
					    java.util.List.of()
					);
				
				try {
					// Create a new User object with admin role and register in the database
					theDatabase.writePost(post);
				} catch (SQLException e) {
					System.err.println("*** ERROR *** Database error trying to register a user: " + e.getMessage());
					e.printStackTrace();
					System.exit(0);
				}
				
		        // Update the list view (prepend newest)
		        ViewStudentPosts.list_Posts.getItems().add(0, post.getAuthorId() + " — " + post.getTitle());
		    }, /*blockOwner=*/true);
		}
	
}
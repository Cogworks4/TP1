package guiStudentPost;


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

public class ControllerStudentPost {
		
	
		protected static void repaintTheWindow() {
			ViewStudentPost.theRootPane.getChildren().setAll(
					ViewStudentPost.label_PageTitle, 
					ViewStudentPost.label_UserDetails,
					ViewStudentPost.label_ThreadTitle,
					ViewStudentPost.line_Separator1,
					ViewStudentPost.line_Separator2,
					ViewStudentPost.list_Posts,
					ViewStudentPost.button_Return,
					ViewStudentPost.button_AddPost);
			
			// Set the title for the window
			ViewStudentPost.theStage.setTitle("CSE 360 Foundation Code: Student Posts Page");
			ViewStudentPost.theStage.setScene(ViewStudentPost.theStudentPostScene);
			ViewStudentPost.theStage.show();
		}
		
		protected static void performReturn() {
			guiStudent.ViewStudentHome.displayStudentHome(ViewStudentPost.theStage,
					ViewStudentPost.theUser);
		}
		
		protected static void performAddPost() {
			
			ViewAddPost.open(ViewStudentPost.theStage, "", "", input -> {
		        // Create in your store
				var post = ViewStudentPost.postStore.create(
					    ViewStudentPost.theUser.getUserName(),
					    input.title(),
					    input.body(),
					    ViewStudentPost.CurrentThread,
					    java.util.List.of()
					);
		        // Update the list view (prepend newest)
		        ViewStudentPost.list_Posts.getItems().add(0, post.getAuthorId() + " — " + post.getTitle());
		    }, /*blockOwner=*/true);
		}
	
}
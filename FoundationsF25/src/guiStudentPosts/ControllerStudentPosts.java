package guiStudentPosts;

import java.sql.SQLException;
import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Controller for the Student Posts screen.
 *
 * <p>Owns the UI actions for the posts page: painting the scene, returning to the
 * home screen, and opening the “Add Post” dialog. On submit, it creates a post
 * via the in-memory store and persists it with {@code Database.writePost}, then
 * updates the list UI.</p>
 *
 * @author Jacob
 * @since 1.0
 */
public class ControllerStudentPosts {
	
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
	    /**
	     * Rebuilds the posts scene and shows it on {@code theStage}.
	     * Sets the title, scene, and visible controls for the page.
	     */
		protected static void repaintTheWindow() {
			ViewStudentPosts.theRootPane.getChildren().setAll(
					ViewStudentPosts.label_PageTitle, 
					ViewStudentPosts.label_UserDetails,
					ViewStudentPosts.label_ThreadTitle,
					
					ViewStudentPosts.line_Separator1,
					ViewStudentPosts.line_Separator2,
					ViewStudentPosts.list_Posts,
					ViewStudentPosts.text_searchBar,
					ViewStudentPosts.button_Return,
					ViewStudentPosts.button_AddPost);
			
			// Set the title for the window
			ViewStudentPosts.theStage.setTitle("CSE 360 Foundation Code: Student Posts Page");
			ViewStudentPosts.theStage.setScene(ViewStudentPosts.theStudentPostScene);
			ViewStudentPosts.theStage.show();
		}
		
		/**
	    * Navigates back to the Student Home page, preserving the current stage and user.
	    */
		protected static void performReturn() {
			guiStudent.ViewStudentHome.displayStudentHome(ViewStudentPosts.theStage,
					ViewStudentPosts.theUser);
		}
		
	    /**
	     * Opens the “Add Post” dialog. When the user submits:
	     * <ul>
	     *   <li>Creates a new post in {@code postStore}</li>
	     *   <li>Writes it to the database</li>
	     *   <li>Prepends a display row to the posts ListView</li>
	     * </ul>
	     */
		protected static void performAddPost() {
			ViewAddPost.open(ViewStudentPosts.theStage, "", "", input -> {
				var post = ViewStudentPosts.postStore.create(
					    ViewStudentPosts.theUser.getUserName(),
					    input.title(),
					    input.body(),
					    ViewStudentPosts.CurrentThread,
					    java.util.List.of()
					);
				try {
					theDatabase.writePost(post);
				} catch (SQLException e) {
					System.err.println("*** ERROR *** Database error trying to register a user: " + e.getMessage());
					e.printStackTrace();
					System.exit(0);
				}
		        ViewStudentPosts.list_Posts.getItems().add(0, post.getAuthorId() + " — " + post.getTitle());
		    }, true);
		}
		
		protected static void searchPosts() {
			String input = ViewStudentPosts.text_searchBar.getText();
			ViewStudentPosts.PopulateStudentPostList();
			ObservableList<String> allPosts = ViewStudentPosts.list_Posts.getItems();
			ObservableList<String> filtered = FXCollections.observableArrayList();
			
			for (String post : allPosts) {
				if (post.toLowerCase().contains(input)) {
					filtered.add(post);
				}
			}
			
			ViewStudentPosts.list_Posts.setItems(filtered);
		}
	
}
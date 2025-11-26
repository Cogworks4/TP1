package guiStudentPosts;

import java.sql.SQLException;
import java.util.function.Consumer;

import database.Database;
import guiStudentPosts.ViewAddPost.PostInput;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

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
	
	/**
	 *  Database instance
	 */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
    /**
     * Repaints the Student Posts window with all UI elements 
     * 
     * <p>Repaints the current window with all of the content from the View within
     * the package<p>
     */
		protected static void repaintTheWindow() {
			ViewStudentPosts.theRootPane.getChildren().setAll(
					ViewStudentPosts.label_PageTitle, 
					ViewStudentPosts.label_UserDetails,
					ViewStudentPosts.label_ThreadTitle,
					ViewStudentPosts.checkbox_read,
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
		 * Navigates back to the Student Home page
		 * 
		 * <p> This method navigates back to the Student Home page, preserving 
		 * the current stage and user.<p>
	    */
		protected static void performReturn() {
			guiStudent.ViewStudentHome.displayStudentHome(ViewStudentPosts.theStage,
					ViewStudentPosts.theUser);
		}
		
		
	    /**
	     * Opens the "Add Post" dialog handing post creation
	     * 
	     * <p>Adds the post by creating a post obj and calling the write post function in the
	     * database, which writes it to the PostDB table<p>
	     * 
	     * @throws SQLException if there is an error when writing the post to database
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
					System.err.println("*** ERROR *** Database error trying write a post: " + e.getMessage());
					e.printStackTrace();
					System.exit(0);
				}
		        ViewStudentPosts.list_Posts.getItems().add(0, post.getAuthorId() + " — " + post.getTitle());
		    }, true);
		}
		
		/**
		 * Filters the display of post based on the user's search input
		 * 
		 * <p>Filters the posts based on the match of the input in the search bar, calls
		 * populateStudentPostList() to refresh the feed in real time<p>
		 */
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
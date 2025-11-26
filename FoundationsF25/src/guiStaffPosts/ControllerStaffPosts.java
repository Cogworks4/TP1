package guiStaffPosts;

import java.sql.SQLException;
import java.util.function.Consumer;

import database.Database;
import guiStaff.ViewStaffHome;
import guiStaffPosts.ViewAddPost.PostInput;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 * Controller for the Staff Posts screen.
 *
 * <p>Owns the UI actions for the posts page: painting the scene, returning to the
 * home screen, and opening the “Add Post” dialog. On submit, it creates a post
 * via the in-memory store and persists it with {@code Database.writePost}, then
 * updates the list UI.</p>
 *
 * @author Jacob
 * @since 1.0
 */
public class ControllerStaffPosts {
	
	/**
	 *  Database instance
	 */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
    /**
     * Repaints the Staff Posts window with all UI elements 
     * 
     * <p>Repaints the current window with all of the content from the View within
     * the package<p>
     */
		protected static void repaintTheWindow() {
			ViewStaffPosts.theRootPane.getChildren().setAll(
					ViewStaffPosts.label_PageTitle, 
					ViewStaffPosts.label_UserDetails,
					ViewStaffPosts.label_ThreadTitle,
					ViewStaffPosts.checkbox_read,
					ViewStaffPosts.line_Separator1,
					ViewStaffPosts.line_Separator2,
					ViewStaffPosts.list_Posts,
					ViewStaffPosts.text_searchBar,
					ViewStaffPosts.button_Return,
					ViewStaffPosts.button_AddPost,
					ViewStaffPosts.button_ModifyThread);
			
			// Set the title for the window
			ViewStaffPosts.theStage.setTitle("CSE 360 Foundation Code: Staff Posts Page");
			ViewStaffPosts.theStage.setScene(ViewStaffPosts.theStaffPostScene);
			ViewStaffPosts.theStage.show();
		}
		
		/**
		 * Navigates back to the Staff Home page
		 * 
		 * <p> This method navigates back to the Staff Home page, preserving 
		 * the current stage and user.<p>
	    */
		protected static void performReturn() {
			guiStaff.ViewStaffHome.displayStaffHome(ViewStaffPosts.theStage,
					ViewStaffPosts.theUser);
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
			ViewAddPost.open(ViewStaffPosts.theStage, "", "", input -> {
				var post = ViewStaffPosts.postStore.create(
					    ViewStaffPosts.theUser.getUserName(),
					    input.title(),
					    input.body(),
					    ViewStaffPosts.CurrentThread,
					    java.util.List.of()
					);
				try {
					theDatabase.writePost(post);
				} catch (SQLException e) {
					System.err.println("*** ERROR *** Database error trying write a post: " + e.getMessage());
					e.printStackTrace();
					System.exit(0);
				}
		        ViewStaffPosts.list_Posts.getItems().add(0, post.getAuthorId() + " — " + post.getTitle());
		    }, true);
		}
		
		protected static void performModifyThread() {
			ViewModifyThread.open(
			        ViewStaffPosts.theStage,
			        ViewStaffPosts.CurrentThread,
			        ViewStaffPosts.CurrentTags,
			        result -> {
			            if (result.delete()) {
			                theDatabase.deleteThread(ViewStaffPosts.CurrentThread);
			                ViewStaffHome.displayStaffHome(ViewStaffPosts.theStage, ViewStaffPosts.theUser);
			            } else {
			                theDatabase.updateThread(ViewStaffPosts.CurrentThread,
			                                         result.newTitle(),
			                                         result.newTags());
			                ViewStaffHome.displayStaffHome(ViewStaffPosts.theStage, ViewStaffPosts.theUser);
			            }
			        },
			        true
			);
		}
		
		/**
		 * Filters the display of post based on the user's search input
		 * 
		 * <p>Filters the posts based on the match of the input in the search bar, calls
		 * populateStaffPostList() to refresh the feed in real time<p>
		 */
		protected static void searchPosts() {
			String input = ViewStaffPosts.text_searchBar.getText();
			ViewStaffPosts.PopulateStaffPostList();
			ObservableList<String> allPosts = ViewStaffPosts.list_Posts.getItems();
			ObservableList<String> filtered = FXCollections.observableArrayList();
			
			for (String post : allPosts) {
				if (post.toLowerCase().contains(input)) {
					filtered.add(post);
				}
			}
			
			ViewStaffPosts.list_Posts.setItems(filtered);
		}
	
}
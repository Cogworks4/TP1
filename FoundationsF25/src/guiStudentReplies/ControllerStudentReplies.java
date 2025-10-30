package guiStudentReplies;

import java.sql.SQLException;

import database.Database;
import entityClasses.Reply;
import guiStudentPosts.ViewAddPost;
import guiStudentPosts.ViewStudentPosts;
import guiStudentReplies.ViewStudentReplies;
import java.util.*;

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

public class ControllerStudentReplies {
	
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
		protected static void repaintTheWindow() {
			ViewStudentReplies.theRootPane.getChildren().setAll(
					ViewStudentReplies.label_PageTitle, 
					ViewStudentReplies.label_UserDetails,
					ViewStudentReplies.label_PostTitle,
					ViewStudentReplies.line_Separator1,
					ViewStudentReplies.line_Separator2,
					ViewStudentReplies.list_Replies,
					ViewStudentReplies.button_Return,
					ViewStudentReplies.button_AddReply);
			
			// Set the title for the window
			ViewStudentReplies.theStage.setTitle("CSE 360 Foundation Code: Student Replies Page");
			ViewStudentReplies.theStage.setScene(ViewStudentReplies.theStudentRepliescene);
			ViewStudentReplies.theStage.show();
		}
		
		protected static void performReturn() {
			guiStudentPosts.ViewStudentPosts.displayStudentPosts(ViewStudentReplies.theStage,
					ViewStudentReplies.theUser, ViewStudentReplies.CurrentThread);
		}
		
		protected static void performAddReply(UUID CurrentPostId) {

			System.out.println("[DBG] performAddReply postId=" + CurrentPostId);
			
			ViewAddReply.open(ViewStudentReplies.theStage, "", input -> {
		        String author = ViewStudentReplies.theUser.getUserName();
		        String body   = input.body();

		        Reply reply = ViewStudentReplies.replyStore.create( CurrentPostId, author, body);

		        try {
		            theDatabase.writeReply(reply);   // persist to DB
		            List<String> updatedContent = theDatabase.postContent(ViewStudentReplies.CurrentPost, ViewStudentReplies.theUser.getUserName(), ViewStudentReplies.CurrentThread);
				    ViewStudentReplies.list_Replies.setItems(javafx.collections.FXCollections.observableArrayList(updatedContent));
		        } catch (SQLException e) {
		            System.err.println("*** ERROR *** DB error creating reply: " + e.getMessage());
		            e.printStackTrace();
		            return;
		        }
		    }, /*blockOwner=*/true);
		}

}
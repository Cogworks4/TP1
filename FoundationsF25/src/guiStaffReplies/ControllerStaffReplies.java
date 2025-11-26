package guiStaffReplies;

import java.sql.SQLException;

import database.Database;
import entityClasses.Reply;
import guiStaff.ViewStaffHome;
import guiStaffPosts.ViewAddPost;
import guiStaffPosts.ViewModifyThread;
import guiStaffPosts.ViewStaffPosts;
import guiStaffReplies.ViewStaffReplies;
import java.util.*;

/*******
 * <p> Title: ControllerlStaffPost Class. </p>
 * 
 * <p> Description: </p>
 * 
 * @author Jacob Sheridan
 * 
 * @version 1.00		2025-09-22 Initial version
 *  
 */

public class ControllerStaffReplies {
	
	// creates obj of database to connect to and use static references
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/**
     * repaints the current window with all of the content from the View within
     * the package
     */
		protected static void repaintTheWindow() {
			ViewStaffReplies.theRootPane.getChildren().setAll(
					ViewStaffReplies.label_PageTitle, 
					ViewStaffReplies.label_UserDetails,
					ViewStaffReplies.label_PostTitle,
					ViewStaffReplies.line_Separator1,
					ViewStaffReplies.line_Separator2,
					ViewStaffReplies.list_Replies,
					ViewStaffReplies.button_Return,
					ViewStaffReplies.button_AddReply,
					ViewStaffReplies.button_ModifyPost);
			
			// Set the title for the window
			ViewStaffReplies.theStage.setTitle("CSE 360 Foundation Code: Staff Replies Page");
			ViewStaffReplies.theStage.setScene(ViewStaffReplies.theStaffRepliescene);
			ViewStaffReplies.theStage.show();
		}
		
		/**
	     * returns to the previous page
	     */
		protected static void performReturn() {
			guiStaffPosts.ViewStaffPosts.displayStaffPosts(ViewStaffReplies.theStage,
					ViewStaffReplies.theUser, ViewStaffReplies.CurrentThread);
		}
		
		/**
	     * adds a reply to the database by calling the method in database to write it
	     * to the ReplyDB table, updates the list afterwards to update it in real time
	     * 
	     * @param CurrentPostId the current post id inwhich the user is located
	     */
		protected static void performAddReply(UUID CurrentPostId) {

			System.out.println("[DBG] performAddReply postId=" + CurrentPostId);
			
			ViewAddReply.open(ViewStaffReplies.theStage, "", input -> {
		        String author = ViewStaffReplies.theUser.getUserName();
		        String body   = input.body();

		        Reply reply = ViewStaffReplies.replyStore.create( CurrentPostId, author, body);

		        try {
		            theDatabase.writeReply(reply);   // persist to DB
		            List<String> updatedContent = theDatabase.postContent(ViewStaffReplies.CurrentPost, ViewStaffReplies.theUser.getUserName(), ViewStaffReplies.CurrentThread);
				    ViewStaffReplies.list_Replies.setItems(javafx.collections.FXCollections.observableArrayList(updatedContent));
		        } catch (SQLException e) {
		            System.err.println("*** ERROR *** DB error creating reply: " + e.getMessage());
		            e.printStackTrace();
		            return;
		        }
		    }, /*blockOwner=*/true);
		}
		
		/* 
		 * allows the staff to update the Post or delete it and all corresponding replies
		 */
		protected static void performModifyPost() {
			ViewModifyPost.open(
			        ViewStaffReplies.theStage,
			        ViewStaffReplies.CurrentPost,
			        theDatabase.grabPostBody(ViewStaffReplies.CurrentPostId),
			        result -> {
			            if (result.delete()) {
			                try {
								theDatabase.deletePost(ViewStaffReplies.CurrentPostId);
							} catch (SQLException e) {
								e.printStackTrace();
							}
			                ViewStaffPosts.displayStaffPosts(ViewStaffReplies.theStage, ViewStaffReplies.theUser, ViewStaffReplies.CurrentThread);
			            } else {
			                try {
								theDatabase.updatePost(ViewStaffReplies.CurrentPostId,
								                         result.newTitle(),
								                         result.newTags());
								ViewStaffPosts.displayStaffPosts(ViewStaffReplies.theStage, ViewStaffReplies.theUser, ViewStaffReplies.CurrentThread);
							} catch (SQLException e) {
								e.printStackTrace();
							}
			            }
			        },
			        true
			);
		}
		
		protected static void performModifyReply(String reply) throws SQLException {
		    String[] parts = reply.split(" - ", 2);

			String user = parts[0];
			String body = parts.length > 1 ? parts[1] : "";

			java.util.UUID replyId = theDatabase.grabReplyID(body, user);
			
		    ViewModifyReply.open(
		            ViewStaffReplies.theStage,
		            null,                      // no title field in this dialog
		            body,               // initial body text
		            result -> {
		                try {
		                    if (result.delete()) {
		                        // Delete the reply
		                        theDatabase.deleteReply(replyId);
		                    } else {
		                        // Update the reply body
		                        theDatabase.updateReply(replyId, result.NewBody());
		                    }
		                    
		                    ViewStaffReplies.displayStaffReplies(ViewStaffReplies.theStage, ViewStaffReplies.theUser, ViewStaffReplies.CurrentPost, ViewStaffReplies.CurrentThread);
		                    
		                } catch (java.sql.SQLException e) {
		                    e.printStackTrace();
		                }
		            },
		            true
		    );
		}


}
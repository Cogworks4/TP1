package guiStaffReplies;

/*******
 * <p> Title: ViewStaffPost Class. </p>
 * 
 * <p> Description: </p>
 * 
 * @author Jacob Sheridan
 * 
 * @version 1.00		2025-08-20 Initial version
 *  
 */

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import store.PostStore;
import store.ReplyStore;
import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiStaffPosts.ControllerStaffPosts;
import guiStaffReplies.ViewStaffReplies;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.util.*;

public class ViewStaffReplies {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings.
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Label label_PostTitle = new Label();
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	protected static ListView<String> list_Replies = new ListView<>();
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator2 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static Button button_Return = new Button("Return");
	protected static Button button_AddReply = new Button("Add Reply");
	protected static Button button_ModifyPost = new Button("Modify Post");
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewStaffReplies theView;	// Used to determine if instantiation of the class
	protected static String CurrentThread;
	protected static String CurrentPost;
	protected static UUID CurrentPostId;
												// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		
	
	protected static final ReplyStore replyStore = new ReplyStore(Set.of(), theDatabase.getConnection());
	
	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
	
	public static Scene theStaffRepliescene = null;	// The Scene each invocation populates
	
	 /**
     * Creates (on first use) and displays the Staff Replies page for the given user, post, and thread.
     * Populates the UI, loads the current thread’s posts, and asks the controller to paint.
     *
     * @param ps    the JavaFX stage to render into
     * @param user  the signed-in user
     * @param post  the post name to list all replies
     * @param thread the thread name get content for the post
     */
	public static void displayStaffReplies(Stage ps, User user, String post, String thread) {
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		CurrentThread = thread;
		int dashIndex = post.indexOf("- ");
		CurrentPost = dashIndex != -1 ? post.substring(dashIndex + 2) : post;
		
		CurrentPostId = theDatabase.grabPostId(CurrentPost, theUser.getUserName());
		
		javafx.application.Platform.runLater(() ->
			label_PostTitle.setText(CurrentPost)
		);
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewStaffReplies();
		
		guiStaffReplies.ViewStaffReplies.PopulateStaffReplyList();

		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.  This page is different from the others.  Since there are two 
		// modes (1: user has not been selected, and 2: user has been selected) there are two
		// lists of widgets to be displayed.  For this reason, we have implemented the following 
		// two controller methods to deal with this dynamic aspect.
		ControllerStaffReplies.repaintTheWindow();
	}
	
	
public ViewStaffReplies() {
		
		// This page is used by all roles, so we do not specify the role being used		
			
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theStaffRepliescene = new Scene(theRootPane, width, height);
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Staff Replies Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		label_PostTitle.setText(CurrentPost);
		setupLabelUI(label_PostTitle, "Arial", 20, width, Pos.CENTER, 0, 55);
	
		// GUI Area 3		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((event) -> {ControllerStaffReplies.performReturn(); });
		
		setupButtonUI(button_AddReply, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_AddReply.setOnAction((event) -> {ControllerStaffReplies.performAddReply(CurrentPostId);});
		
		setupButtonUI(button_ModifyPost, "Dialog", 18, 200, Pos.CENTER, 570, 540);
        button_ModifyPost.setOnAction((event) -> {ControllerStaffReplies.performModifyPost(); });
        
        list_Replies.setOnMouseClicked(e -> {
		    if (e.getClickCount() == 2) {
		        // Get the selected item
		        String selectedReply = list_Replies.getSelectionModel().getSelectedItem();

		        if (selectedReply != null) {
		            try {
						ControllerStaffReplies.performModifyReply(selectedReply);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
		        }
		    }
		});

		
		// This is the end of the GUI Widgets for the page
		
		// Due to the very dynamic nature of this page, setting the widget into the Root Pane has 
		// has been delegated to the repaintTheWindow and doSelectUser controller methods.
		// Don't follow this pattern if formatting of the page does not change dynamically.
	}

	/*
	 * Populates the Staff reply list by setting the list_replies params, and
	 * by calling the post content method in database
	 */
	public static void PopulateStaffReplyList() {
	    if (!theRootPane.getChildren().contains(list_Replies)) {
	        theRootPane.getChildren().add(list_Replies);
	
	        list_Replies.setLayoutX(20);
	        list_Replies.setLayoutY(line_Separator1.getStartY() + 8);
	        list_Replies.setPrefWidth(width - 40);
	        list_Replies.setPrefHeight(line_Separator2.getStartY() - line_Separator1.getStartY() - 16);
	        list_Replies.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 14;");
	    }
	    
	    List<String> content = theDatabase.postContent(CurrentPost, theUser.getUserName(), CurrentThread);
	    
	    list_Replies.setItems(javafx.collections.FXCollections.observableArrayList(content));
	}

	/*-*******************************************************************************************

	Helper methods used to minimizes the number of lines of code needed above
	
	*/

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w,
			double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}
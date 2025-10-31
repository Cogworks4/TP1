package guiStudentPosts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import database.Database;
import entityClasses.User;
import guiStudentPosts.ViewStudentPosts;
import javafx.scene.control.ListView;
import store.PostStore;

import javafx.scene.control.CheckBox;
import java.sql.SQLException;
import java.util.*;

/**
 * JavaFX view for listing posts within a thread for a student user.
 *
 * <p>Renders page labels, separators, a posts ListView, and page controls.
 * Double-clicking a list item opens the Replies view for the selected post.
 * The view delegates actions to {@link ControllerStudentPosts}.</p>
 *
 * <p>Call {@link #displayStudentPosts(Stage, entityClasses.User, String)} to show the page.</p>
 *
 * @author Jacob
 * @since 1.0
 */
public class ViewStudentPosts {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// a button to allow this user to update the account settings, the thread title of which thread
	// the user is currently in, the search bar for the posts, and a checkbox to filter by read
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Label label_ThreadTitle = new Label();
	protected static TextField text_searchBar = new TextField();
	protected static CheckBox checkbox_read = new CheckBox("Filter Read");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	// This is the ListView with all of the posts
	protected static ListView<String> list_Posts = new ListView<>();
	
	// This is the query for the search bar
	protected static String query;
	
	// This creates the post store obj for each thread
	protected static final PostStore postStore = new PostStore(Set.of("General", "Homework"));
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator2 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static Button button_Return = new Button("Return");
	protected static Button button_AddPost = new Button("Add Post");
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewStudentPosts theView;	// Used to determine if instantiation of the class
	protected static String CurrentThread;
												// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
	
	public static Scene theStudentPostScene = null;	// The Scene each invocation populates
	
    /**
     * Creates (on first use) and displays the Student Posts page for the given user and thread.
     * Populates the UI, loads the current thread’s posts, and asks the controller to paint.
     *
     * @param ps    the JavaFX stage to render into
     * @param user  the signed-in user
     * @param thread the thread name to list posts for
     */
	public static void displayStudentPosts(Stage ps, User user, String thread) {
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		CurrentThread = thread;
		
		javafx.application.Platform.runLater(() ->
			label_ThreadTitle.setText(CurrentThread)
		);
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewStudentPosts();
		
		guiStudentPosts.ViewStudentPosts.PopulateStudentPostList();

		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.  This page is different from the others.  Since there are two 
		// modes (1: user has not been selected, and 2: user has been selected) there are two
		// lists of widgets to be displayed.  For this reason, we have implemented the following 
		// two controller methods to deal with this dynamic aspect.
		ControllerStudentPosts.repaintTheWindow();
	}
	
	/**
	 * Constructs the Student Posts view and initializes its UI components.
	 *
	 * <p>Builds the layout, configures event handlers, and prepares
	 * the scene for displaying posts within the current thread. It sets up
	 * the search bar, read filter, and navigation buttons, delegating user actions
	 * to {@link ControllerStudentPosts}.</p>
	 *
	 * <p>This view is instantiated only once and reused by
	 * {@link #displayStudentPosts(Stage, entityClasses.User, String)}.</p>
	 */
public ViewStudentPosts() {
		
		// This page is used by all roles, so we do not specify the role being used		
			
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theStudentPostScene = new Scene(theRootPane, width, height);
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Student Posts Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		label_ThreadTitle.setText(CurrentThread);
		setupLabelUI(label_ThreadTitle, "Arial", 20, width, Pos.CENTER, 0, 55);
		
		setupTextUI(text_searchBar, "Arial", 18, 200, Pos.BASELINE_LEFT, 570, 55, true);
		text_searchBar.setPromptText("Enter Search Query");
		text_searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
			ControllerStudentPosts.searchPosts();
		});
		
		setupCheckBoxUI(checkbox_read, "Arial", 14, 100, Pos.CENTER, 570, 32);
		checkbox_read.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
		    try {
		        if (isNowSelected) {
		            List<String> unreadPosts = theDatabase.listUnreadPosts(CurrentThread);
		            list_Posts.setItems(FXCollections.observableArrayList(unreadPosts));
		        } else {
		            PopulateStudentPostList();
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		});
	
		// GUI Area 3		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((event) -> {ControllerStudentPosts.performReturn(); });
		
		setupButtonUI(button_AddPost, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_AddPost.setOnAction((event) -> {ControllerStudentPosts.performAddPost();});
		
		list_Posts.setOnMouseClicked(e -> {
		    if (e.getClickCount() == 2) {
		        // Get the selected item
		        String selectedPost = list_Posts.getSelectionModel().getSelectedItem();

		        if (selectedPost != null) {
		            // Pass the post title (or ID) to the next page
		            guiStudentReplies.ViewStudentReplies.displayStudentReplies(
		                theStage, 
		                theUser, 
		                selectedPost,
		                CurrentThread
		            );
		        }
		    }
		});
		
		// This is the end of the GUI Widgets for the page
		
		// Due to the very dynamic nature of this page, setting the widget into the Root Pane has 
		// has been delegated to the repaintTheWindow and doSelectUser controller methods.
		// Don't follow this pattern if formatting of the page does not change dynamically.
	}



/**
 * Populates or refreshes the posts list for the current thread.
 *
 * <p>If the posts list has not yet been added to the root pane, this method
 * initializes its layout and styling. Then it retrieves the posts from the
 * database via {@code Database.listPosts(CurrentThread)} and updates the
 * {@link #list_Posts} view accordingly.</p>
 *
 * <p>If a database access error occurs, the stack trace is printed and the list
 * remains unchanged.</p>
 */
public static void PopulateStudentPostList() {
    if (!theRootPane.getChildren().contains(list_Posts)) {
        theRootPane.getChildren().add(list_Posts);

        // layout only once (or move to constructor)
        list_Posts.setLayoutX(20);
        list_Posts.setLayoutY(line_Separator1.getStartY() + 8);
        list_Posts.setPrefWidth(width - 40);
        list_Posts.setPrefHeight(line_Separator2.getStartY() - line_Separator1.getStartY() - 16);
        list_Posts.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 14;");
    }
    
    List<String> postTitles;
	try {
		postTitles = theDatabase.listPosts(CurrentThread);
	} catch (SQLException e) {
		postTitles = null;
		e.printStackTrace();
	}

    list_Posts.setItems(javafx.collections.FXCollections.observableArrayList(postTitles));
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
	
	/**********
	 * Private local method to initialize the standard fields for a text field
	 * 
	 * @param t  The TextField object to be initialized
	 * @param ff The font to be used
	 * @param f  The size of the font to be used
	 * @param w  The width of the Button
	 * @param p  The alignment (e.g. left, centered, or right)
	 * @param x  The location from the left edge (x axis)
	 * @param y  The location from the top (y axis)
	 * @param e  The flag (Boolean) that specifies if this field is editable
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}
	
	/*******
	 * Private local method to initialize the standard fields for a CheckBox.
	 *
	 * @param c  The CheckBox object to be initialized
	 * @param ff The font family to be used
	 * @param f  The size of the font to be used
	 * @param w  The minimum width of the CheckBox
	 * @param p  The alignment (e.g., left, centered, or right)
	 * @param x  The location from the left edge (x-axis)
	 * @param y  The location from the top edge (y-axis)
	 */
	private static void setupCheckBoxUI(CheckBox c, String ff, double f, double w, Pos p, double x, double y) {
	    c.setFont(Font.font(ff, f));
	    c.setMinWidth(w);
	    c.setAlignment(p);
	    c.setLayoutX(x);
	    c.setLayoutY(y);
	}
}
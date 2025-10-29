package guiStudentPost;

/*******
 * <p> Title: ViewStudentPost Class. </p>
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
import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiStudentPost.ViewStudentPost;
import javafx.scene.control.ListView;
import store.PostStore;
import store.ReplyStore;
import java.util.*;

public class ViewStudentPost {
	
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
	protected static Label label_ThreadTitle = new Label();
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	protected static ListView<String> list_Posts = new ListView<>();
	
	
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
	private static ViewStudentPost theView;	// Used to determine if instantiation of the class
	protected static String CurrentThread;
												// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
	
	public static Scene theStudentPostScene = null;	// The Scene each invocation populates
	
	public static void displayStudentPost(Stage ps, User user, String thread) {
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		CurrentThread = thread;
		
		javafx.application.Platform.runLater(() ->
			label_ThreadTitle.setText(CurrentThread)
		);
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewStudentPost();
		
		guiStudentPost.ViewStudentPost.PopulateStudentPostList();
		
		list_Posts.setLayoutX(20);
		list_Posts.setLayoutY(line_Separator1.getStartY() + 8);
		list_Posts.setPrefWidth(width - 40);
		list_Posts.setPrefHeight(line_Separator2.getStartY() - line_Separator1.getStartY() - 16);
		list_Posts.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 14;");

		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.  This page is different from the others.  Since there are two 
		// modes (1: user has not been selected, and 2: user has been selected) there are two
		// lists of widgets to be displayed.  For this reason, we have implemented the following 
		// two controller methods to deal with this dynamic aspect.
		ControllerStudentPost.repaintTheWindow();
	}
	
public ViewStudentPost() {
		
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
	
		// GUI Area 3		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((event) -> {ControllerStudentPost.performReturn(); });
		
		setupButtonUI(button_AddPost, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_AddPost.setOnAction((event) -> {ControllerStudentPost.performAddPost();});
		
		// This is the end of the GUI Widgets for the page
		
		// Due to the very dynamic nature of this page, setting the widget into the Root Pane has 
		// has been delegated to the repaintTheWindow and doSelectUser controller methods.
		// Don't follow this pattern if formatting of the page does not change dynamically.
	}

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

    List<Post> allPosts = postStore.listAll();
    List<String> postTitles = allPosts.stream()
        .filter(p -> !p.isDeleted())
        .map(p -> p.getAuthorId() + " — " + p.getTitle())
        .toList();

    list_Posts.setItems(javafx.collections.FXCollections.observableArrayList(postTitles));
}

public static void AddPostToList() {
	
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
package oneTimePassword;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.User;
import guiAddRemoveRoles.ViewAddRemoveRoles;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;




public class ViewOneTimePassword {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface

	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
		
	// GUI Area 1: Informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings.
	protected static Label label_pageTitle = new Label();
	protected static Label label_User = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	//add line separator
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// Area 2a: add main elements to window
	//This allows the admin to select a user of the system to send a OTP.
	// The act of selecting a user causes the change is the GUI. The Admin does
	// not need to push a button to make this happen.
	protected static Label label_SelectUser = new Label("Select a user:");
	protected static ComboBox <String> combobox_selectUser = new ComboBox <String>();
	protected static List<String> selectUserList = new ArrayList<String>();
	
	protected static Label label_generate = new Label("Create password:");
	protected static ComboBox <String> combobox_randOrCreate = new ComboBox <String>();
	protected static List<String> selectGenList = new ArrayList<String>();
	
	// Area 2b: 
	// This window generates if admin selects the password to be created 
	// instead of randomized. The act of selecting "create a password", generates 
	// a text box for admin to input their own password.
	protected static TextField textField_createPass = new TextField();
	protected static Button button_cretePassword = new Button("Create Password");
	
	
	// Area 2c:
	// This window generates if admin selects the password to be randomized 
	// instead of created. The act of selecting "randomize a password", generates 
	// a random password, and displays it.
	protected static Button button_randomizePass = new Button("Generate Random Password");
	protected static Label displayRandomPassword = new Label();
	
	// Area 3:
	// button at the bottom activates after you select how to generate the OTP, if "create" is 
	// selected then user needs to input a valid password, and then button activates.
	protected static Button button_sendOneTime = new Button("Send One Time Password");

	//add line separator
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);

	// Area 4:
	//add exit elements at bottom of screen
	protected static Button button_return = new Button("Return");
	protected static Button button_quit = new Button("Quit");
	protected static Button button_logout = new Button("Logout");
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewOneTimePassword theView;	// Used to determine if instantiation of the class
													// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
		
	public static Scene theOneTimePasswordScene = null;	// The Scene each invocation populates
	protected static String theSelectedUser = "";	// The user whose roles are being updated
	
	
	
	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayAddRemoveRoles(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the AddRevove page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	public static void displayOneTimePassword(Stage ps, User user) {
		
	}
	
	
	/**********
	 * <p> Method: GUIOneTimePasswordPage() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton, so this is performed just one.  Subsequent uses fill in the changeable
	 * fields using the displayOneTimePassword method.</p>
	 * 
	 */
	public ViewOneTimePassword() {
	// This page is used by all roles, so we do not specify the role being used		
		
	// Create the Pane for the list of widgets and the Scene for the window
	theRootPane = new Pane();
	theOneTimePasswordScene = new Scene(theRootPane, width, height);
				
	// Populate the window with the title and other common widgets and set their static state
	
	//GUI Area 1
	label_pageTitle.setText("One Time Password Page");
	setupLabelUI(label_pageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
	
	label_User.setText("User: " + theUser.getUserName());
	setupLabelUI(label_User, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
	}
	
	
	


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

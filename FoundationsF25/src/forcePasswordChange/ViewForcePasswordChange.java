package forcePasswordChange;

import entityClasses.User;
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

public class ViewForcePasswordChange {
	
	
	/*-*******************************************************************************************

	Attributes
	
	*/

	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1: Informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings.
	protected static Label label_pageTitle = new Label();
	protected static Label label_User = new Label();
	protected static Label prompt = new Label();
	
	//add line separator
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	// Area 2: Change password
	// This forces the user to change their password after using a OTP
	protected static Label label_Password1 = new Label("Enter a Password:");
	protected static TextField textField_createPass1 = new TextField();
	protected static Label label_Pass1Valid = new Label("");
	
	// Re-enter the password
	protected static Label label_Password2 = new Label("Re-enter Password:");
	protected static TextField textField_createPass2 = new TextField();
	protected static Label label_Pass2Valid = new Label("");
	
	// List the requirements of the password
	protected static Label passReqs = new Label("Password must include:\n"
			+ " 1 UpperCase\n"
			+ " 1 LowerCase\n"
			+ " 1 SpeicalChar\n"
			+ " 8 characters min\n"
			+ "35 characters max");
	
	// Checks if password is valid
	protected static Button button_checkPass = new Button("Check if Password is Valid");
	
	// If password was valid is activated and will set the new password
	public static Button button_setPass = new Button("Set New Password");
	
	//add line separator
		protected static Line line_Separator4 = new Line(20, 525, width-20,525);

	// Area 3:
	//add exit element at bottom of screen
	protected static Button button_quit = new Button("Quit");
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewForcePasswordChange theView;	// Used to determine if instantiation of the class
													// is needed

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
			
	public static Scene theForcePaswordChangeScene = null;	// The Scene each invocation populates
	protected static String Password1 = "";					// Holds the passwords to compare and save if valid
	protected static String Password2 = "";
	protected static String actualPassword = "";			// After validation new password is stored here to be used
	public static String user = "";
	
	
	
	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayOneTimePassword(Stage ps, User user) </p>
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
	public static void displayForcePasswordChange(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewForcePasswordChange();
		
		ControllerForcePasswordChange.paintTheWindow();
		
		
	}
	
	/**********
	 * <p> Method: GUIForcePasswordChange() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton, so this is performed just one.  Subsequent uses fill in the changeable
	 * fields using the displayForcePasswordChange method.</p>
	 * 
	 */
	public ViewForcePasswordChange() {
	user = theUser.getUserName();
	// This page is used by all roles, so we do not specify the role being used		
		
	// Create the Pane for the list of widgets and the Scene for the window
	theRootPane = new Pane();
	theForcePaswordChangeScene = new Scene(theRootPane, width, height);
				
	// Populate the window with the title and other common widgets and set their static state
	
	//GUI Area 1
	// Shows title
	label_pageTitle.setText("Password Change Page");
	setupLabelUI(label_pageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
	
	// Shows current user that is logged in changing password
	label_User.setText("User: " + theUser.getUserName());
	setupLabelUI(label_User, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
	
	// Prompt the user to change the password and lets them know they will need to log-in again
	prompt.setText("Please change your password and log in again");
	setupLabelUI(prompt, "Arial", 20, width, Pos.BASELINE_CENTER, 20, 55);

		
	// GUI Area 2
	// Prompts the first password to be entered
	setupLabelUI(label_Password1, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 125);
	setupTextUI(textField_createPass1, "Arial", 18, 300, Pos.BASELINE_CENTER, 280, 125, true);
	
	// Where the first password is entered
	textField_createPass1.setPromptText("Enter a Password");
	textField_createPass1.textProperty().addListener((observable, oldValue,newValue) -> {
		ControllerForcePasswordChange.createPass();
		});
	setupLabelUI(label_Pass1Valid, "Arial", 20, 300, Pos.BASELINE_CENTER, 500, 250);

	// Prompts the second password to be entered
	setupLabelUI(label_Password2, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 200);
	setupTextUI(textField_createPass2, "Arial", 18, 300, Pos.BASELINE_CENTER, 280, 200, true);
	
	// Where the second password is entered
	textField_createPass2.setPromptText("Re-Enter a Password");
	textField_createPass2.textProperty().addListener((observable, oldValue,newValue) -> {
		ControllerForcePasswordChange.createPass();
		});
	
	// List the passwords requirements
	setupLabelUI(passReqs, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 300);
	
	// Button to check if password is valid
	setupButtonUI(button_checkPass, "Dialog", 15, 170, Pos.BASELINE_CENTER, 330, 250);
	button_checkPass.setOnAction((event) ->
			{
				if(ControllerForcePasswordChange.checkPass() ) { 	// Checks if password is valid
					if(Password1.compareTo(Password2) == 0) {		// and compares to check if equal
					button_setPass.setDisable(false);				// Sets the submit button to "enabled" if passwords 
					label_Pass1Valid.setText("Password is valid!");	// are valid and equal to each other
					} else label_Pass1Valid.setText("Passwords must match!"); // Notifies user that passwords aren't equal
					
				}
					
				else label_Pass1Valid.setText("**Password is invalid**");	// Shows that password is invalid so user can change it
				});
	
	// Button to submit the password and save it as a new password
	setupButtonUI(button_setPass, "Dialog", 18, 170, Pos.CENTER, 285, 470);
	button_setPass.setOnAction((event) ->
			{ControllerForcePasswordChange.setPassword(); });
	button_setPass.setDisable(true);
		
	
	// GUI Area 4
	// Allows user to quit the program
	setupButtonUI(button_quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
	button_quit.setOnAction((event) -> {ControllerForcePasswordChange.performQuit(); });

	
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
	protected static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
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
	 *  @param t		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param e		The boolean that allows text to be editable
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}
}

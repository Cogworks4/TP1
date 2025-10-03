package oneTimePassword;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import database.Database;
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




public class ViewOneTimePassword {
	
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
	protected static Button button_UpdateThisUser = new Button("Account Update");

	//add line separator
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// Area 2a: add main elements to window
	// This allows the admin to select a user of the system to send a OTP.
	// The act of selecting a user causes the change is the GUI. The Admin does
	// not need to push a button to make this happen.
	protected static Label label_SelectUser = new Label("Select a user:");
	protected static ComboBox <String> combobox_selectUser = new ComboBox <String>();
	protected static List<String> selectUserList = new ArrayList<String>();
	
	
	// Area 2b:
	// This window generates when admin chooses the user to send password to
	// It prompts them to choose how they want the password to generated with either
	// random or by creating it themselves.
	protected static Label label_generate = new Label("Create password:");
	protected static ComboBox <String> combobox_randOrCreate = new ComboBox <String>();
	protected static List<String> selectGenList = new ArrayList<String>();
	
	// Area 2c: 
	// This window generates if admin selects the password to be created 
	// instead of randomized. The act of selecting "create a password", generates 
	// a text box for admin to input their own password.
	protected static TextField textField_createPass = new TextField();
	protected static Button button_checkPass = new Button("Check if Password is Valid");
	protected static Label passReqs = new Label("Password must include:\n"
			+ " 1 UpperCase\n"
			+ " 1 LowerCase\n"
			+ " 1 SpeicalChar\n"
			+ " 8 characters min\n"
			+ "35 characters max");
	
	
	// Area 2d:
	// This window generates if admin selects the password to be randomized 
	// instead of created. The act of selecting "randomize a password", generates 
	// a random password, and displays it.
	protected static Button button_randomizePass = new Button("Generate Random Password");
	protected static Label displayRandomPassword = new Label("One Time Password: ");
	
	// Area 3:
	// button at the bottom activates after you select how to generate the OTP, if "create" is 
	// selected then user needs to input a valid password, and then button activates.
	public static Button button_sendOneTime = new Button("Send One Time Password");

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
	protected static String theSelectedUser = "";	// The user whose is getting sent a OTP
	protected static String theSelectedGen = "";	// The way to generate the password
	protected static String Password = "";
	
	
	
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
	public static void displayOneTimePassword(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewOneTimePassword();
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.  This page is different from the others.  Since there are two 
		// modes (1: user has not been selected, and 2: user has been selected) there are two
		// lists of widgets to be displayed.  For this reason, we have implemented the following 
		// two controller methods to deal with this dynamic aspect.
		ControllerOneTimePassword.repaintTheWindow();
		ControllerOneTimePassword.doAction();
		
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
	
	//GUI Area 1: creates the "title" part of the page letting user know what page it is and gives an option to return to UserUpdate
	label_pageTitle.setText("One Time Password Page");
	setupLabelUI(label_pageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
	
	label_User.setText("User: " + theUser.getUserName());
	setupLabelUI(label_User, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
	
	setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
	button_UpdateThisUser.setOnAction((event) -> 
		{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
	// GUI Area 2a: Is what is initially shown asking user to select a user
	// Label of a prompt to select the user via a combo-box
	setupLabelUI(label_SelectUser, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
	
	// The combo-box where the user is selected to send a OTP to
	setupComboBoxUI(combobox_selectUser, "Dialog", 16, 250, 280, 125);
	List<String> userList = theDatabase.getUserList();
	combobox_selectUser.setItems(FXCollections.observableArrayList(userList));
	combobox_selectUser.getSelectionModel().select(0);
	combobox_selectUser.getSelectionModel().selectedItemProperty()
	.addListener((ObservableValue<? extends String> observable, 
		String oldvalue, String newValue) -> {ControllerOneTimePassword.doAction();});
	
	// GUI Area 2b: After user selects the user to send a OTP to ask user if the password should be randomized or created by them
	// Label which ask user to select how password should be generated
	setupLabelUI(label_generate, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 200);
	
	// The combo-box where user chooses the generation method
	setupComboBoxUI(combobox_randOrCreate, "Dialog", 16, 250, 280, 200);
	selectGenList.clear();
	selectGenList.add("<Select generation>");
	selectGenList.add("Create");
	selectGenList.add("Randomize");
	
	combobox_randOrCreate.setItems(FXCollections
			.observableArrayList(selectGenList));
	combobox_randOrCreate.getSelectionModel().select(0);
	combobox_randOrCreate.getSelectionModel().selectedItemProperty()
	.addListener((ObservableValue<? extends String> observable, 
		String oldvalue, String newValue) -> {ControllerOneTimePassword.doAction();});

	
	// GUI Area 2c: If user selects password to be created, creates a text-box for input and checks the password for validity
	// The text-field that user enters the password into
	setupTextUI(textField_createPass, "Arial", 18, 300, Pos.BASELINE_LEFT, 20, 270, true);
	textField_createPass.setPromptText("Enter the Password");
	textField_createPass.textProperty().addListener((observable, oldValue,newValue) -> {
		ControllerOneTimePassword.createPass();
		});
	// The requirements of the password are displayed as a reminder of what is needed in the password
	setupLabelUI(passReqs, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 300);
	
	// A button where password is checked for validity and if it is then activates the submit password button
	setupButtonUI(button_checkPass, "Dialog", 15, 170, Pos.BASELINE_RIGHT, 570, 200);
	button_checkPass.setOnAction((event) ->
			{
				if(ControllerOneTimePassword.checkPass())
					button_sendOneTime.setDisable(false);
				else System.out.println("failed");
				});
	
	
	// GUI Area 2d
	// The random password generator that generates a different password with each press for the OTP
	setupButtonUI(button_randomizePass, "Dialog", 15, 170, Pos.BASELINE_RIGHT, 570, 200);
	button_randomizePass.setOnAction((event) ->
			{ControllerOneTimePassword.generateRand(); });
	
	// Displays this random password so user knows the random password
	setupLabelUI(displayRandomPassword, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 270);
	System.out.println(Password);
	
	
	// GUI Area 3
	// The submit password button that updates the database with the OTP and changes the users password
	setupButtonUI(button_sendOneTime, "Dialog", 18, 170, Pos.CENTER, 285, 470);
	button_sendOneTime.setOnAction((event) ->
			{ControllerOneTimePassword.setOneTime(); });
	button_sendOneTime.setDisable(true);
	
	
	
	// GUI Area 4
	// The "exit" buttons where the user can return to previous page, logout, or close the program
	setupButtonUI(button_return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
	button_return.setOnAction((event) -> {ControllerOneTimePassword.performReturn(); });

	setupButtonUI(button_logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
	button_logout.setOnAction((event) -> {ControllerOneTimePassword.performLogout(); });

	setupButtonUI(button_quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
	button_quit.setOnAction((event) -> {ControllerOneTimePassword.performQuit(); });
	
	
	
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

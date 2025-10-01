package guiFirstAdmin;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/*******
 * <p>
 * Title: ModelFirstAdmin Class.
 * </p>
 * 
 * <p>
 * Description: The First System Startup Page Model. This class is not used as
 * there is no data manipulated by this MVC beyond accepting a username and
 * password and then saving it in the database. When the code is enhanced for
 * input validation, this model may be needed.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-08-15 Initial version
 * 
 */

public class ModelFirstAdmin {

	protected static int[] positions = { 210, 260, 245 };
	protected static int[][] errorShown = { { 0, 5 }, { 0, 100 } };

	/**
	 * Takes in error message is input parameter, if there is an error adjust UI
	 * elements to accomadate error message returns true if no error appears returns
	 * false otherwise
	 * 
	 * @param errorMessage - string input, error message returned textbox check
	 * @return - boolean value, true if there is error, false otherwise
	 */
	static boolean guiUsernameErrors(String errorMessage) {
		if (errorMessage.length() <= 0) {
			ViewFirstAdmin.label_UsernameError.setText("");
			errorShown[0][0] = 0;
			UpdateLayout();
			return true;
		} else {
			ViewFirstAdmin.label_UsernameError.setText("*" + errorMessage);
			errorShown[0][0] = 1;
			UpdateLayout();
			return false;
		}
	}

	/**
	 * @param ErrorMessage
	 * @return
	 * 
	 * 
	 */
	static boolean guiPasswordErrors(String ErrorMessage) {
		int bitmap = 0;

		String[] errCode = { "Upper case", "Lower case", "Numeric digits", "Special character", "Long Enough",
				"Password Length" };
		String[] messages = { "Upper Case", "Lower Case", "Numeric Digits", "Special Characters",
				"Password is longer than 7", "Password is shorter than 32" };

		for (int i = 0; i < errCode.length; i++) {
			if (ErrorMessage.contains(errCode[i])) {
				bitmap |= (1 << i);
			}
		}
		
		//bitmap = (~bitmap);

		if (bitmap == 0) {
			ViewFirstAdmin.label_PasswordError.setGraphic(null);
			errorShown[1][0] = 0;
			UpdateLayout();
			return true;
		} else {

			TextFlow label_PasswordError = new TextFlow();
			label_PasswordError.setLayoutY(300);
			Text first = new Text("A secure password must contain:\n");
			label_PasswordError.getChildren().add(first);

			for (int i = 0; i < messages.length; i++) {
			    boolean ok = ((bitmap >> i) & 1) == 1;
			    Text t = new Text(messages[i] + "\n");
			    t.setStyle(ok ? "-fx-fill: red;" : "-fx-fill: green;");
			    label_PasswordError.getChildren().add(t);
			}
			ViewFirstAdmin.label_PasswordError.setGraphic(label_PasswordError);
			errorShown[1][0] = 1;
			UpdateLayout();
			return false;
		}
	}

	/**
	 * 
	 */
	static void UpdateLayout() {
		for (int i = 0; i < positions.length; i++) {
			if (errorShown[1][0] == 1) i = 1;
			int position = positions[i];
			for (int j = 0; j < errorShown.length; j++) {
				if (errorShown[j][0] == 1)
					position += errorShown[j][1];
			}

			switch (i) {
				case 0: 
					ViewFirstAdmin.text_AdminPassword1.setLayoutY(position);
					System.out.println("Password1 triggered pos-" + position);
					break;
				case 1:
					ViewFirstAdmin.text_AdminPassword2.setLayoutY(position);
					if (errorShown[1][0] == 1) i = 3;
					System.out.println("Password2 triggered pos-" + position);
					break;
				case 2:
					ViewFirstAdmin.label_PasswordError.setLayoutY(position);
					System.out.println("PasswordError triggered pos-" + position);
					break;
				default:
					System.out.println("default triggered");
					break;
			}
		}
		System.out.println("layout updated: user-" + errorShown[0][0] + " pass-" + errorShown[1][0]);
	}
}
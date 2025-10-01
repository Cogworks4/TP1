package nameValidator;

import java.util.List;
import java.util.ArrayList;


public class userUpdateNameValidator {
	
	// This class validates user input for the UserUpdate gui. Specifically, it 
	// validates the fields : first name, middle name, last name, preferred first name
	
	
	//This is a generic method that takes a name and fieldName and validates it
	// If unable to validate, the method returns a List<String> of the errors
	//
	// -- Validation rules --
	// Character limit: must be between 2 and 25 characters
	// Allowed characters: [a-zA-Z\\s'-]
	// First character must be upper case
	public static String validateGenericName(String name, String fieldName) {
	    List<String> errors = new ArrayList<>();
		if (name == null || name.trim().isEmpty())
	        return (fieldName + " cannot be empty");
	    if (name.length() < 2)
	        errors.add("must be at least 2 characters");
	    if (!name.matches("^[a-zA-Z\\s'-]+$")) {
	        errors.add("can only contain letters, spaces, hyphens, and apostrophes");
	    }
	    if (name.length() > 25) errors.add("is too long");
	    
	    if (!Character.isUpperCase(name.charAt(0))) {
	    	errors.add("must start with a capital letter");
	    }
	    if (errors.isEmpty()) return ""; // No errors
	    
	    String formatted =fieldName + ":\n" + String.join(".\n", errors);
	    return formatted;
	    
	}
	//These methods are case specific and call on the generic method
	public static String validateFirstName(String name) { return validateGenericName(name, "First Name"); }
	public static String validateLastName(String name) { return validateGenericName(name, "Last Name"); }
	public static String validatePreferredName(String name) { return validateGenericName(name, "Preferred Name"); }
	
	// Middle Name has an additional requirement that if
	// the first character is the only character it must be capitalized
	public static String validateMiddleName(String name) {
		if (name.length() == 1 && Character.isLetter(name.charAt(0))) {
			if (!Character.isUpperCase(name.charAt(0))) return ("Middle Name must start with a capital letter.");
			return "";
		}
		return validateGenericName(name, "Middle Name");
	}


}

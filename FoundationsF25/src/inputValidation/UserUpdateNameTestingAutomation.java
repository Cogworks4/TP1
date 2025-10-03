package inputValidation;


import inputValidation.userUpdateNameValidator;

public class UserUpdateNameTestingAutomation {

    static int numPassed = 0;
    static int numFailed = 0;

    public static void main(String[] args) {
        System.out.println("______________________________________");
        System.out.println("\nTesting Automation: userUpdateNameValidator");

        // Test cases: first name 
        performTestCase(1, "", "First Name", false); // cannot be empty
        performTestCase(2, "A", "First Name", false); // must be at least 2 characters
        performTestCase(3, "ABC!", "First Name", false); // contains invalid special characters
        performTestCase(4, "Ababababababababababababab", "First Name", false); // must be between 2 and 25 characters
        performTestCase(5, "Mary Jane", "First Name", true); // spaces are allowed
        
        // Test cases: middle name 
        performTestCase(6, "anne", "Middle Name", false); // must be capitalized
        performTestCase(7, "A", "Middle Name", true); // will pass for middle name only
        performTestCase(8, "?ABC*", "Middle Name", false); // contains invalid special characters
        performTestCase(9, "Ababababababababababababab", "Middle Name", false); // must be between 2 and 25 characters
        performTestCase(10, "Mary-Kate", "Middle Name", true); // dashes are allowed
        
        // Test cases: last name 
        performTestCase(11, "", "Last Name", false); // cannot be empty
        performTestCase(12, "A", "Last Name", false); // must be at least 2 characters
        performTestCase(13, "ABC//", "Last Name", false); // contains invalid special characters
        performTestCase(14, "Ababababababababababababab", "Last Name", false); // must be between 2 and 25 characters
        performTestCase(15, "O'Connor", "Last Name", true); // apostrophes are allowed
        
     // Test cases: preferred first name 
        performTestCase(16, "", "Preferred Name", false); // cannot be empty
        performTestCase(17, "A", "Preferred Name", false); // must be at least 2 characters
        performTestCase(18, "ABC!", "Preferred Name", false); // contains invalid special characters
        performTestCase(19, "Ababababababababababababab", "Preferred Name", false); // must be between 2 and 25 characters
        performTestCase(20, "Mary", "Preferred Name", true); // 
        
        
        System.out.println("____________________________________________________________________________");
        System.out.println("Number of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
    }

    private static void performTestCase(int testCase, String input, String field, boolean expectedPass) {
        System.out.println("\n____________________________________________________________________________");
        System.out.println("Test case: " + testCase);
        System.out.println("Input: \"" + input + "\"");
        
        String errors = "";

        if (field.equals("Middle Name")) {
        	errors = userUpdateNameValidator.validateMiddleName(input);
        } else {
        	errors = userUpdateNameValidator.validateGenericName(input, field);
        }	

        boolean passed = (expectedPass && errors.isEmpty()) || (!expectedPass && !errors.isEmpty());

        if (passed) {
            System.out.println("***Success***");
            numPassed++;
        } else {
            System.out.println("***Failure***");
            numFailed++;
        }

        if (!errors.isEmpty()) {
            System.out.println("Errors: " + errors);
        }
    }
}


module FoundationsF25 {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}

module FoundationsF25 {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	requires javafx.base;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}

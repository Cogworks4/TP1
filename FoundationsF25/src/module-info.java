module FoundationsF25 {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	requires javafx.base;
	requires java.desktop;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}

module proj3DataStrucre {

	requires javafx.controls;
	requires javafx.fxml;

	exports application;

	opens application to javafx.base, javafx.graphics;

}

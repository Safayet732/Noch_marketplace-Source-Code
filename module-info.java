module com.noch.noch {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;

    opens com.noch.noch to javafx.fxml;
    opens com.noch.noch.controllers to javafx.fxml;

    exports com.noch.noch;
    exports com.noch.noch.controllers;
}
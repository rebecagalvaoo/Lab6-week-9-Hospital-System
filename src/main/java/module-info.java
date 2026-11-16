module ca.hospital.week9_hospitalsystem {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens ca.hospital.week9_hospitalsystem to javafx.fxml;

    exports ca.hospital.week9_hospitalsystem;
}
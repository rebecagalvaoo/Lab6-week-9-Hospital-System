
package ca.hospital.week9_hospitalsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class HelloApplication extends Application {

    private final TextField patientIdField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField ageField = new TextField();
    private final ComboBox<String> genderBox = new ComboBox<>();
    private final TextField phoneField = new TextField();
    private final TextField diagnosisField = new TextField();
    private final DatePicker admissionDatePicker = new DatePicker();

    private final TextField searchField = new TextField();

    private final TableView<ObservableList<String>> patientTable =
            new TableView<>();

    private final Label statusLabel = new Label("Enter patient details.");

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label heading = new Label("Hospital Management System");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Patient Registration");

        VBox header = new VBox(5, heading, subtitle);
        root.setTop(header);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(14);
        form.setPadding(new Insets(15));

        patientIdField.setEditable(false);
        patientIdField.setPromptText("Auto generated");

        nameField.setPromptText("Full name");
        ageField.setPromptText("Age");
        phoneField.setPromptText("Phone number");
        diagnosisField.setPromptText("Diagnosis");

        genderBox.getItems().addAll(
                "Female", "Male", "Non-binary", "Prefer not to say"
        );

        genderBox.setPromptText("Select gender");

        addRow(form, 0, "Patient ID:", patientIdField);
        addRow(form, 1, "Full name:", nameField);
        addRow(form, 2, "Age:", ageField);
        addRow(form, 3, "Gender:", genderBox);
        addRow(form, 4, "Phone:", phoneField);
        addRow(form, 5, "Diagnosis:", diagnosisField);
        addRow(form, 6, "Admission date:", admissionDatePicker);

        ColumnConstraints labels = new ColumnConstraints(110);
        ColumnConstraints fields = new ColumnConstraints();
        fields.setHgrow(Priority.ALWAYS);

        form.getColumnConstraints().addAll(labels, fields);

        Button addButton = new Button("Add Patient");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        Button clearButton = new Button("Clear");

        HBox buttons = new HBox(
                8, addButton, updateButton, deleteButton, clearButton
        );
        buttons.setAlignment(Pos.CENTER);

        VBox formSection = new VBox(
                12, new Label("Patient Details"), form, buttons
        );

        formSection.setPrefWidth(480);

        searchField.setPromptText("Search by name or phone");

        Button searchButton = new Button("Search");
        Button showAllButton = new Button("Show All");

        HBox searchBox = new HBox(
                8, searchField, searchButton, showAllButton
        );

        patientTable.getColumns().addAll(
                createColumn("ID", 0),
                createColumn("Name", 1),
                createColumn("Age", 2),
                createColumn("Gender", 3),
                createColumn("Phone", 4),
                createColumn("Diagnosis", 5),
                createColumn("Admission Date", 6)
        );

        VBox recordsSection = new VBox(
                12,
                new Label("Patient Records"),
                searchBox,
                patientTable
        );

        VBox.setVgrow(patientTable, Priority.ALWAYS);

        HBox content = new HBox(20, formSection, recordsSection);
        HBox.setHgrow(recordsSection, Priority.ALWAYS);

        root.setCenter(content);
        root.setBottom(statusLabel);

        addButton.setOnAction(e -> addPatient());
        updateButton.setOnAction(e -> updatePatient());
        deleteButton.setOnAction(e -> deletePatient());
        clearButton.setOnAction(e -> clearForm());
        searchButton.setOnAction(e -> searchPatient());
        showAllButton.setOnAction(e -> loadPatients());

        patientTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldRow, newRow) -> {
                    if (newRow != null) {
                        patientIdField.setText(newRow.get(0));
                        nameField.setText(newRow.get(1));
                        ageField.setText(newRow.get(2));
                        genderBox.setValue(newRow.get(3));
                        phoneField.setText(newRow.get(4));
                        diagnosisField.setText(newRow.get(5));
                        admissionDatePicker.setValue(
                                LocalDate.parse(newRow.get(6))
                        );
                    }
                });

        Scene scene = new Scene(root, 1200, 650);

        stage.setTitle("Hospital Management System");
        stage.setScene(scene);
        stage.show();

        loadPatients();
    }

    private void addRow(GridPane grid, int row,
                        String text, Control control) {

        Label label = new Label(text);
        control.setMaxWidth(Double.MAX_VALUE);

        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private TableColumn<ObservableList<String>, String>
    createColumn(String title, int index) {

        TableColumn<ObservableList<String>, String> column =
                new TableColumn<>(title);

        column.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().get(index))
        );

        return column;
    }

    private boolean validateForm() {

        if (nameField.getText().trim().isEmpty()
                || ageField.getText().trim().isEmpty()
                || genderBox.getValue() == null
                || phoneField.getText().trim().isEmpty()
                || diagnosisField.getText().trim().isEmpty()
                || admissionDatePicker.getValue() == null) {

            statusLabel.setText("Please complete all fields.");
            return false;
        }

        try {
            int age = Integer.parseInt(ageField.getText().trim());

            if (age < 0 || age > 125) {
                statusLabel.setText("Age must be between 0 and 125.");
                return false;
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Age must be a whole number.");
            return false;
        }

        if (!phoneField.getText().trim().matches("[0-9+() -]{7,20}")) {
            statusLabel.setText("Enter a valid phone number.");
            return false;
        }

        if (admissionDatePicker.getValue().isAfter(LocalDate.now())) {
            statusLabel.setText("Date cannot be in the future.");
            return false;
        }

        return true;
    }

    private void addPatient() {

        if (!validateForm()) {
            return;
        }

        String sql = "INSERT INTO patients "
                + "(name, age, gender, phone, diagnosis, admission_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = db_connection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nameField.getText().trim());
            statement.setInt(2, Integer.parseInt(ageField.getText().trim()));
            statement.setString(3, genderBox.getValue());
            statement.setString(4, phoneField.getText().trim());
            statement.setString(5, diagnosisField.getText().trim());
            statement.setDate(6, java.sql.Date.valueOf(
                    admissionDatePicker.getValue()));

            statement.executeUpdate();

            statusLabel.setText("Patient added successfully!");

            loadPatients();
            clearForm();

        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPatients() {
        showPatients("");
    }

    private void searchPatient() {
        showPatients(searchField.getText().trim());
    }

    private void showPatients(String search) {

        ObservableList<ObservableList<String>> data =
                FXCollections.observableArrayList();

        String sql = "SELECT patient_id, name, age, gender, phone, "
                + "diagnosis, admission_date FROM patients";

        if (!search.isEmpty()) {
            sql += " WHERE name LIKE ? OR phone LIKE ?";
        }

        try (Connection connection = db_connection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (!search.isEmpty()) {
                statement.setString(1, "%" + search + "%");
                statement.setString(2, "%" + search + "%");
            }

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    ObservableList<String> row =
                            FXCollections.observableArrayList();

                    row.add(String.valueOf(result.getInt("patient_id")));
                    row.add(result.getString("name"));
                    row.add(String.valueOf(result.getInt("age")));
                    row.add(result.getString("gender"));
                    row.add(result.getString("phone"));
                    row.add(result.getString("diagnosis"));
                    row.add(result.getDate("admission_date").toString());

                    data.add(row);
                }
            }

            patientTable.setItems(data);
            statusLabel.setText("Records loaded: " + data.size());

        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePatient() {

        if (patientIdField.getText().isEmpty()) {
            statusLabel.setText("Select a patient first.");
            return;
        }

        if (!validateForm()) {
            return;
        }

        String sql = "UPDATE patients SET name=?, age=?, gender=?, "
                + "phone=?, diagnosis=?, admission_date=? WHERE patient_id=?";

        try (Connection connection = db_connection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nameField.getText().trim());
            statement.setInt(2, Integer.parseInt(ageField.getText().trim()));
            statement.setString(3, genderBox.getValue());
            statement.setString(4, phoneField.getText().trim());
            statement.setString(5, diagnosisField.getText().trim());
            statement.setDate(6, java.sql.Date.valueOf(
                    admissionDatePicker.getValue()));
            statement.setInt(7, Integer.parseInt(patientIdField.getText()));

            statement.executeUpdate();

            statusLabel.setText("Patient updated successfully!");

            loadPatients();
            clearForm();

        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deletePatient() {

        if (patientIdField.getText().isEmpty()) {
            statusLabel.setText("Select a patient first.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Patient");
        alert.setHeaderText("Delete selected patient?");
        alert.setContentText("Are you sure?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        String sql = "DELETE FROM patients WHERE patient_id=?";

        try (Connection connection = db_connection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, Integer.parseInt(patientIdField.getText()));

            statement.executeUpdate();

            statusLabel.setText("Patient deleted successfully!");

            loadPatients();
            clearForm();

        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {

        patientIdField.clear();
        nameField.clear();
        ageField.clear();
        genderBox.getSelectionModel().clearSelection();
        phoneField.clear();
        diagnosisField.clear();
        admissionDatePicker.setValue(null);

        patientTable.getSelectionModel().clearSelection();

        statusLabel.setText("Form cleared.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

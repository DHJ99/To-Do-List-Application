package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Task;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ViewScreenController implements Initializable {

    @FXML
    private Button btnReload;

    @FXML
    private Button btnSearch;

    @FXML
    private TableColumn<Task, LocalDate> colDate;

    @FXML
    private TableColumn<Task, String> colDescription;

    @FXML
    private TableColumn<Task, String> colTitle;

    @FXML
    private TableView<Task> tblTasks;

    @FXML
    private TextField txtSearchDate;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtDate;

    private List<Task> allTasks = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        tblTasks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setTextToValues(newValue);
        });
        loadTasksFromDatabase();
    }

    private void setTextToValues(Task task) {
        if (task != null) {
            txtTitle.setText(task.getTitle());
            txtDescription.setText(task.getDescription());
            txtDate.setText(task.getDate().toString());
        } else {
            txtTitle.clear();
            txtDescription.clear();
            txtDate.clear();
        }
    }

    private void setupTableColumns() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("task"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    @FXML
    void btnReload(ActionEvent event) {
        loadTasksFromDatabase();
    }

    private void loadTasksFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/mytodolist";
        String user = "root";
        String password = "12345";

        allTasks.clear();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement psTm = connection.prepareStatement("SELECT * FROM tasks");
             ResultSet resultSet = psTm.executeQuery()) {

            while (resultSet.next()) {
                Task task = new Task(
                        resultSet.getString("task_title"),
                        resultSet.getString("task_description"),
                        resultSet.getDate("completion_date").toLocalDate()
                );
                allTasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateTableView();
    }

    private void updateTableView() {
        ObservableList<Task> taskObservableList = FXCollections.observableArrayList(allTasks);
        tblTasks.setItems(taskObservableList);
    }

    @FXML
    void btnSearch(ActionEvent event) {
        String searchDate = txtSearchDate.getText().trim();
        if (searchDate.isEmpty()) {
            updateTableView(); // Show all tasks if search field is empty
            return;
        }

        try {
            LocalDate date = LocalDate.parse(searchDate, DateTimeFormatter.ISO_LOCAL_DATE);
            List<Task> filteredTasks = allTasks.stream()
                    .filter(task -> task.getDate().equals(date))
                    .collect(Collectors.toList());

            if (!filteredTasks.isEmpty()) {
                tblTasks.setItems(FXCollections.observableArrayList(filteredTasks));
            } else {
                tblTasks.getItems().clear();
                System.out.println("No tasks found for the given date.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
        }
    }
}
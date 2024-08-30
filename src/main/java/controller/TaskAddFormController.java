package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Task;

import java.time.LocalDate;
import java.util.ArrayList;

public class TaskAddFormController {

    @FXML
    private Button btnDone;

    @FXML
    private Label lblTaskName;

    @FXML
    private TextArea txtArea;

    private ArrayList<Task> tempTasks;

    public void setTaskName(String text) {
        lblTaskName.setText(text);
    }

    public void setTempTasks(ArrayList<Task> tempTasks) {
        this.tempTasks = tempTasks;
    }

    @FXML
    void btnDone(ActionEvent event) {
        String taskName = lblTaskName.getText();
        String description = txtArea.getText();

        if (!taskName.isEmpty() && !description.isEmpty()) {
            Task newTask = new Task(taskName, description, LocalDate.now());
            tempTasks.add(newTask);

            Stage stage = (Stage) btnDone.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Error", "Please enter both task name and description.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
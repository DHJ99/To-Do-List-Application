package controller;

import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Task;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeScreenController implements Initializable {

    @FXML private Button btnAdd;
    @FXML private Button btnView;
    @FXML private Button btnClear;
    @FXML private CheckBox chkBx1, chkBx2, chkBx3, chkBx4, chkBx5, chkBx6;
    @FXML private DatePicker dateAdd;
    @FXML private Label lblF1, lblF2, lblF3, lblF4, lblF5, lblF6;
    @FXML private Label lblTask1, lblTask2, lblTask3, lblTask4, lblTask5, lblTask6;
    @FXML private JFXTextField txtTask;
    @FXML private JFXTextField txtDescription;

    private int nextAvailableTaskLabel = 1;
    private int nextAvailableFinishedLabel = 1;

    private List<Task> tempTasks = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadExistingTasks();
    }

    private void loadExistingTasks() {
        List<Task> tasks = DBConnection.getInstance().getAllTasks();
        for (Task task : tasks) {
            if (task.isCompleted()) {
                updateFinishedLabels(task);
            } else {
                updateTaskLabels(task);
            }
        }
    }

    private void updateTaskLabels(Task task) {
        if (nextAvailableTaskLabel <= 6) {
            Label lblTask = (Label) getFieldByName("lblTask" + nextAvailableTaskLabel);
            if (lblTask != null) {
                lblTask.setText(task.getTitle());
                nextAvailableTaskLabel++;
            }
        } else {
            showAlert("Error 1", "No more space to display tasks.");
        }
    }

    @FXML
    void btnAdd(ActionEvent event) {
        String taskTitle = txtTask.getText().trim();
        LocalDate selectedDate = dateAdd.getValue();

        if (taskTitle.isEmpty() || selectedDate == null) {
            showAlert("Error 2", "Please add Task Title and Date!");
            return;
        }

        if (tempTasks.isEmpty()) {
            showAlert("Error 3", "Please add a description using the 'More' button before adding the task.");
            return;
        }

        Task lastTempTask = tempTasks.get(tempTasks.size() - 1);
        Task newTask = new Task(taskTitle, lastTempTask.getDescription(), selectedDate);
        DBConnection.getInstance().addTask(newTask);

        updateTaskLabels(newTask);
        clearFields();
        tempTasks.clear();
    }

    private void updateFinishedTasks(CheckBox source) {
        if (source.isSelected() && nextAvailableFinishedLabel <= 6) {
            int taskNumber = Integer.parseInt(source.getId().replace("chkBx", ""));
            Label lblTask = (Label) getFieldByName("lblTask" + taskNumber);
            String taskTitle = lblTask.getText();

            if (!taskTitle.isEmpty()) {
                Label lblFinished = (Label) getFieldByName("lblF" + nextAvailableFinishedLabel);
                lblFinished.setText(taskTitle);
                lblTask.setText("");
                nextAvailableFinishedLabel++;

                DBConnection.getInstance().markTaskAsCompleted(taskTitle);
            }
        }
    }

    private void updateFinishedLabels(Task task) {
        if (nextAvailableFinishedLabel <= 6) {
            Label lblFinished = (Label) getFieldByName("lblF" + nextAvailableFinishedLabel);
            lblFinished.setText(task.getTitle());
            nextAvailableFinishedLabel++;
        }
    }

    @FXML
    void btnMore(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/task_add_form.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            TaskAddFormController taskAddFormController = loader.getController();
            taskAddFormController.setTaskName(txtTask.getText());
            taskAddFormController.setTempTasks((ArrayList<Task>) tempTasks);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open task add form: " + e.getMessage());
        }
    }

    @FXML
    void btnView(ActionEvent event) {
        openNewStage();
    }

    private void openNewStage() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/view_screen.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open new window: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtTask.clear();
        txtDescription.clear();
        dateAdd.setValue(null);
        chkBx1.setSelected(false);
        chkBx2.setSelected(false);
        chkBx3.setSelected(false);
        chkBx4.setSelected(false);
        chkBx5.setSelected(false);
        chkBx6.setSelected(false);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Object getFieldByName(String fieldName) {
        try {
            Field field = getClass().getDeclaredField(fieldName);
            return field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void chkBx1(ActionEvent event) { updateFinishedTasks((CheckBox) event.getSource()); }
    public void chkBx2(ActionEvent event) { updateFinishedTasks((CheckBox) event.getSource()); }
    public void chkBx3(ActionEvent event) { updateFinishedTasks((CheckBox) event.getSource()); }
    public void chkBx4(ActionEvent event) { updateFinishedTasks((CheckBox) event.getSource()); }
    public void chkBx5(ActionEvent event) { updateFinishedTasks((CheckBox) event.getSource()); }
    public void chkBx6(ActionEvent event) { updateFinishedTasks((CheckBox) event.getSource()); }

    @FXML
    void btnClear(ActionEvent event) {
        for (int i = 1; i <= 6; i++) {
            Label label = (Label) getFieldByName("lblF" + i);
            if (label != null) {
                label.setText("");
            }
        }
        DBConnection.getInstance().clearTasks();
        loadExistingTasks();
    }
}
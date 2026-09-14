package com;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    private TableView<Project> table;
    private ObservableList<Project> data = FXCollections.observableArrayList();
    private ProjectDAO dao = new ProjectDAO();
    private ProjectController controller = new ProjectController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Группа 5 — JavaFX Трекер проектов");

        table = new TableView<>();
        table.setItems(data);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<Project, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().id));
        idCol.setPrefWidth(50);

        TableColumn<Project, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().name));
        nameCol.setPrefWidth(120);

        TableColumn<Project, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().description));
        descCol.setPrefWidth(180);

        TableColumn<Project, String> startCol = new TableColumn<>("Start");
        startCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().startDate));
        startCol.setPrefWidth(90);

        TableColumn<Project, String> endCol = new TableColumn<>("End");
        endCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().endDate));
        endCol.setPrefWidth(90);

        TableColumn<Project, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().status));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, nameCol, descCol, startCol, endCol, statusCol);

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setOnAction(e -> loadData());

        Button createBtn = new Button("Create");
        createBtn.setOnAction(e -> {
            controller.handleProjectAction(1);
            showProjectDialog(null, saved -> {
                dao.insertProject(saved);
                loadData();
            });
        });

        Button updateBtn = new Button("Update");
        updateBtn.setOnAction(e -> {
            Project sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controller.handleProjectAction(2);
                showProjectDialog(sel, updated -> {
                    updated.id = sel.id;
                    dao.updateProject(updated);
                    loadData();
                });
            } else {
                showAlert("Выбери проект в таблице для редактирования!");
            }
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Project sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controller.handleProjectAction(3);
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить проект '" + sel.name + "' и связанные задачи?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        dao.deleteProject(sel.id);
                        loadData();
                    }
                });
            } else {
                showAlert("Выбери проект в таблице для удаления!");
            }
        });

        Button statsBtn = new Button("Stats");
        statsBtn.setOnAction(e -> {
            Project sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controller.handleProjectAction(4);
                String stats = dao.getProjectStats(sel.id, sel.name);
                showAlert(stats);
            } else {
                showAlert("Выбери проект в таблице для просмотра статистики.");
            }
        });

        HBox buttonBar = new HBox(10, refreshBtn, createBtn, updateBtn, deleteBtn, statsBtn);
        buttonBar.setPadding(new Insets(10));

        VBox root = new VBox(table, buttonBar);
        Scene scene = new Scene(root, 750, 500);

        stage.setScene(scene);
        stage.show();

        loadData();
    }

    private void loadData() {
        data.clear();
        List<Project> list = dao.getAllProjects(null);
        data.addAll(list);
    }

    @FunctionalInterface
    private interface ProjectCallback { void accept(Project p); }

    private void showProjectDialog(Project existing, ProjectCallback callback) {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Создать проект" : "Редактировать проект");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField(existing != null ? existing.name : "");
        TextField descField = new TextField(existing != null ? existing.description : "");
        TextField startField = new TextField(existing != null ? existing.startDate : "2026-09-01");
        TextField endField = new TextField(existing != null ? existing.endDate : "2026-09-30");
        TextField statusField = new TextField(existing != null ? existing.status : "IN_PROGRESS");

        nameField.setPromptText("Name");
        descField.setPromptText("Description");
        startField.setPromptText("Start (YYYY-MM-DD)");
        endField.setPromptText("End (YYYY-MM-DD)");
        statusField.setPromptText("Status");

        VBox grid = new VBox(10, new Label("Name:"), nameField, new Label("Description:"), descField,
                new Label("Start:"), startField, new Label("End:"), endField, new Label("Status:"), statusField);
        grid.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Project(0, nameField.getText(), descField.getText(), startField.getText(), endField.getText(), statusField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(callback::accept);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
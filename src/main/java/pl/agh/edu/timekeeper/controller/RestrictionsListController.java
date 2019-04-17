package pl.agh.edu.timekeeper.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;

public class RestrictionsListController {

    public ObservableList<String> getAppsNames() {
        return appsNames;
    }

    public void setAppsNames(ObservableList<String> appsNames) {
        this.appsNames = appsNames;
    }

    private ObservableList<String> appsNames = FXCollections.observableArrayList();
    private final String RESTRICTION_VIEW_PATH = "/views/restrictionView.fxml";
    private final String ADD_RESTRICTION_VIEW_PATH = "/views/addRestrictionView.fxml";

    @FXML
    private TabPane restrictionTabPane;

    @FXML
    private ListView<String> restrictionListView;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button removeButton;

    @FXML
    public void initialize() {
        // Two line only for testing :TODO need to join real apps names
        this.appsNames.setAll("Halo", "Hello", "Hi");
        restrictionListView.setItems(this.appsNames);
        removeButton.disableProperty().bind(Bindings.isEmpty(restrictionListView.getSelectionModel().getSelectedItems()));
        editButton.disableProperty().bind(Bindings.isEmpty(restrictionListView.getSelectionModel().getSelectedItems()));
    }

    @FXML
    public void addButtonClicked() {
        openWindow(new FXMLLoader(this.getClass().getResource(ADD_RESTRICTION_VIEW_PATH)), "Add restriction");
    }

    @FXML
    public void editButtonClicked() {
        openWindow(new FXMLLoader(this.getClass().getResource(ADD_RESTRICTION_VIEW_PATH)), "Edit restriction");
        //:TODO should not allow to change name/group of restriction
    }

    @FXML
    public void removeButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sFocusedure to remove this restriction ?");
        alert.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            restrictionListView.getItems().removeAll(restrictionListView.getSelectionModel().getSelectedItem());
        }
    }

    private void openWindow(FXMLLoader loader, String title) {
        try {
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load(), 450, 450));
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

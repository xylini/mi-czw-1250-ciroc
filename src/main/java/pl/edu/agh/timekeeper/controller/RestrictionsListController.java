package pl.edu.agh.timekeeper.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.event.ChangeListener;
import java.io.*;

public class RestrictionsListController {

    private static final String RESTRICTION_VIEW_PATH = "/views/restrictionView.fxml";
    private static final String ADD_RESTRICTION_VIEW_PATH = "/views/addRestrictionView.fxml";

    public void setRestrictionsSplitPane(SplitPane restrictionsSplitPane) {
        this.restrictionsSplitPane = restrictionsSplitPane;
    }

    @FXML
    private SplitPane restrictionsSplitPane;

    public SplitPane getRestrictionsSplitPane() {
        return restrictionsSplitPane;
    }

    @FXML
    private TabPane restrictionTabPane;

    @FXML
    public ListView<String> restrictionListView;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button removeButton;

    private MainScreenController mainScreenController;

    private AddRestrictionController addRestrictionController = new AddRestrictionController();

    private ObservableList<String> appsNames = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Two line only for testing :TODO need to join real apps names
        this.appsNames.setAll("Halo", "Hello", "Hi");
//        restrictionListView.itemsProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println(observable.toString());
//            System.out.println(oldValue + " NEW VALUE: " + newValue);
//        });
        restrictionListView.setItems(this.appsNames);
        this.restrictionTabPane.tabDragPolicyProperty().setValue(TabPane.TabDragPolicy.REORDER);
        openAppTab();
        removeButton.disableProperty().bind(Bindings.isEmpty(restrictionListView.getSelectionModel().getSelectedItems()));
        editButton.disableProperty().bind(Bindings.isEmpty(restrictionListView.getSelectionModel().getSelectedItems()));
        addRestrictionController.setRestrictionsListController(this);
    }

    private void openAppTab() {
        restrictionListView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    String item = restrictionListView.getSelectionModel().getSelectedItem();
                    if (item != null) {
                        boolean isNew = true;
                        for (Tab tab : restrictionTabPane.getTabs())
                            if (tab.getText().equals(item)) {
                                isNew = false;
                            }
                        if (isNew) {
                            Tab tab = new Tab();
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getResource(RESTRICTION_VIEW_PATH));
                            ScrollPane restrictionScrollPane = null;
                            try {
                                restrictionScrollPane = loader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            tab.setContent(restrictionScrollPane);
                            tab.setText(item);
                            this.restrictionTabPane.getTabs().add(0,tab);
                            restrictionTabPane.getSelectionModel().select(0);
                        }
                    }
                }
            }
        });
    }

    public ObservableList<String> getAppsNames() {
        return appsNames;
    }

    public void setAppsNames(ObservableList<String> appsNames) {
        this.appsNames = appsNames;
    }

    public void setMainScreenController(MainScreenController mainScreenController) {
        this.mainScreenController = mainScreenController;
    }

    @FXML
    private void addButtonClicked() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(ADD_RESTRICTION_VIEW_PATH));
        loader.setController(addRestrictionController);
        openWindow(loader, "Add restriction");
    }

    @FXML
    private void editButtonClicked() {
        openWindow(new FXMLLoader(this.getClass().getResource(ADD_RESTRICTION_VIEW_PATH)), "Edit restriction");
        //:TODO should not allow to change name/group of restriction
    }

    @FXML
    private void removeButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure to remove this restriction?");
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
            stage.setScene(new Scene(loader.load(), 500, 550));
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBindings() {
        restrictionsSplitPane.prefHeightProperty().bind(mainScreenController.getMainVBox().prefHeightProperty());
        restrictionsSplitPane.prefWidthProperty().bind(mainScreenController.getMainVBox().prefWidthProperty());
    }
}

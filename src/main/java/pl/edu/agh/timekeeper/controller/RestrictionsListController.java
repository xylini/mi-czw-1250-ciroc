package pl.edu.agh.timekeeper.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.edu.agh.timekeeper.db.dao.RestrictionDao;
import pl.edu.agh.timekeeper.model.Restriction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestrictionsListController {

    private static final String RESTRICTION_VIEW_PATH = "/views/restrictionView.fxml";
    private static final String ADD_RESTRICTION_VIEW_PATH = "/views/addRestrictionView.fxml";

    @FXML
    private SplitPane restrictionsSplitPane;

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

    private MainScreenController mainScreenController;

    private ObservableList<String> restrictionNames = FXCollections.observableArrayList();

    private RestrictionDao restrictionDao = new RestrictionDao();

    @FXML
    private void initialize() {
        restrictionDao.getAll().get().forEach(r -> this.restrictionNames.add(r.getName()));
        restrictionListView.setItems(this.restrictionNames);
        this.restrictionTabPane.tabDragPolicyProperty().setValue(TabPane.TabDragPolicy.REORDER);
        openAppTab();
        removeButton.disableProperty().bind(Bindings.isEmpty(restrictionListView.getSelectionModel().getSelectedItems()));
        editButton.disableProperty().bind(Bindings.isEmpty(restrictionListView.getSelectionModel().getSelectedItems()));
    }

    private void openAppTab() {
        restrictionListView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                String item = restrictionListView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    boolean isNew = true;
                    for (Tab tab : restrictionTabPane.getTabs())
                        if (tab.getText().equals(item)) {
                            isNew = false;
                            restrictionTabPane.getSelectionModel().select(tab);
                        }
                    if (isNew) {
                        Restriction r = restrictionDao.getByName(item).get();
                        //TODO: set fields with r attributes
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
                        this.restrictionTabPane.getTabs().add(0, tab);
                        restrictionTabPane.getSelectionModel().select(0);
                    }
                }
            }
        });
    }

    public ListView<String> getRestrictionListView() {
        return this.restrictionListView;
    }

    public void setRestrictionsSplitPane(SplitPane restrictionsSplitPane) {
        this.restrictionsSplitPane = restrictionsSplitPane;
    }

    public SplitPane getRestrictionsSplitPane() {
        return restrictionsSplitPane;
    }

    public void setAppsNames(ObservableList<String> appsNames) {
        this.appsNames = appsNames;

    public void setMainScreenController(MainScreenController mainScreenController) {
        this.mainScreenController = mainScreenController;
    }

    @FXML
    private void addButtonClicked() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(ADD_RESTRICTION_VIEW_PATH));
        openWindow(loader, "Add restriction");
        ((AddRestrictionController) loader.getController()).setRestrictionsListController(this);
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
            List<Tab> tabsToRemove = new ArrayList<>();
            for (Tab tab : restrictionTabPane.getTabs()) {
                if (tab.getText().equals(restrictionListView.getSelectionModel().getSelectedItem()))
                    tabsToRemove.add(tab);
            }
            String restrictionName = restrictionListView.getSelectionModel().getSelectedItem();
            restrictionTabPane.getTabs().removeAll(tabsToRemove);
            restrictionListView.getItems().remove(restrictionName);
            restrictionDao.deleteByName(restrictionName);

        }
    }

    private void openWindow(FXMLLoader loader, String title) {
        try {
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load(), 335, 480));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UTILITY);
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

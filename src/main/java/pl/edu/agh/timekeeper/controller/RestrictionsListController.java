package pl.edu.agh.timekeeper.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.edu.agh.timekeeper.db.dao.RestrictionDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestrictionsListController {

    @FXML
    private SplitPane restrictionsSplitPane;

    @FXML
    private VBox listVBox;

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

    private static final String RESTRICTION_VIEW_PATH = "/views/restrictionView.fxml";
    private static final String ADD_RESTRICTION_VIEW_PATH = "/views/addRestrictionView.fxml";

    private RestrictionTabController restrictionTabController;

    private ObservableList<String> restrictionNames = FXCollections.observableArrayList();

    private RestrictionDao restrictionDao = new RestrictionDao();

    @FXML
    private void initialize() {
        restrictionDao.getAll().get().forEach(r -> this.restrictionNames.add(r.getName()));
        restrictionListView.setItems(this.restrictionNames);
        this.restrictionTabPane.tabDragPolicyProperty().setValue(TabPane.TabDragPolicy.REORDER);
        setOnRestrictionListClicked();
        removeButton.disableProperty().bind(Bindings.isEmpty(restrictionListView.getSelectionModel().getSelectedItems()));
        editButton.disableProperty().bind(Bindings.isEmpty(restrictionListView.getSelectionModel().getSelectedItems()));
        listVBox.prefHeightProperty().bind(restrictionsSplitPane.heightProperty());
        listVBox.prefWidthProperty().bind(restrictionsSplitPane.widthProperty());
        listVBox.maxWidthProperty().bind(restrictionsSplitPane.widthProperty().multiply(0.3));
        restrictionListView.prefHeightProperty().bind(listVBox.heightProperty().subtract(addButton.heightProperty()));
        restrictionListView.prefWidthProperty().bind(listVBox.widthProperty().subtract(addButton.widthProperty()));
        restrictionTabPane.prefHeightProperty().bind(restrictionsSplitPane.heightProperty());
        restrictionTabPane.prefWidthProperty().bind(restrictionsSplitPane.widthProperty());
        initRestrictionTabController();
    }

    private void initRestrictionTabController() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(RESTRICTION_VIEW_PATH));
        ScrollPane restrictionScrollPane = null;
        try {
            restrictionScrollPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        restrictionTabController = loader.getController();
        restrictionTabController.setRestrictionScrollPane(restrictionScrollPane);
    }

    public ListView<String> getRestrictionListView() {
        return this.restrictionListView;
    }

    public SplitPane getRestrictionsSplitPane() {
        return restrictionsSplitPane;
    }

    private void setOnRestrictionListClicked() {
        restrictionListView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                Optional<String> selectedItem = Optional.ofNullable(restrictionListView.getSelectionModel().getSelectedItem());
                selectedItem.ifPresent(item -> {
                    boolean isOpen = false;
                    for (Tab tab : restrictionTabPane.getTabs())
                        if (tab.getText().equals(item)) {
                            isOpen = true;
                            restrictionTabPane.getSelectionModel().select(tab);
                        }
                    if (!isOpen) {
                        initRestrictionTabController();
                        restrictionTabController.setRestriction(restrictionDao.getByName(item).get());
                        Tab tab = new Tab();
                        tab.setContent(restrictionTabController.getRestrictionScrollPane());
                        tab.setText(item);
                        this.restrictionTabPane.getTabs().add(0, tab);
                        restrictionTabPane.getSelectionModel().select(0);
                    }
                });
            }
        });
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
}

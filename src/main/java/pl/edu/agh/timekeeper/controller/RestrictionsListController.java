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
import org.hibernate.cfg.Configuration;
import pl.edu.agh.timekeeper.db.SessionService;
import pl.edu.agh.timekeeper.db.dao.RestrictionDao;
import pl.edu.agh.timekeeper.log.LogApplication;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Restriction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private ListView<String> restrictionListView;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button resetButton;

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
        resetButton.disableProperty().bind(Bindings.isEmpty(restrictionListView.getSelectionModel().getSelectedItems()));
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

    public ListView<String> getRestrictionListView(){
        return this.restrictionListView;
    }

    public void setRestrictionNames(ObservableList<String> restrictionNames) {
        this.restrictionNames = restrictionNames;
    }

    public void setMainScreenController(MainScreenController mainScreenController) {
        this.mainScreenController = mainScreenController;
    }

    @FXML
    private void addButtonClicked() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(ADD_RESTRICTION_VIEW_PATH));
        AddRestrictionController addRestrictionController = new AddRestrictionController();
        addRestrictionController.setRestrictionsListController(this);
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

    @FXML
    private void resetStatistics(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure to reset statistics for this restriction?");
        alert.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            String nameToRestart = restrictionListView.getSelectionModel().getSelectedItem();
            SessionService.openSession(new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory());
            SessionService.getCurrentSession().beginTransaction();
            String hql = "from Application where name = :name";
            Application application = (Application) SessionService.getCurrentSession().createQuery(hql)
                    .setParameter("name", nameToRestart).getSingleResult();

            Set<LogApplication> myLogs = application.getLogApplications();
            for(LogApplication myLog : myLogs){
                myLog.setApplication(null);
            }
            application.setLogApplications(new HashSet<>());
            SessionService.getCurrentSession().getTransaction().commit();
            SessionService.closeCurrentSession();
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

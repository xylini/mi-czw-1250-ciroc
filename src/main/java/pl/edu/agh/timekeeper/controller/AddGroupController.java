package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class AddGroupController {

    private static final String IMAGE_DELETE_PATH = "images/delete.png";

    @FXML
    public Pane mainPane;

    @FXML
    public TextField groupNameField;

    @FXML
    public VBox groupVBox;

    @FXML
    public Button okButton;

    private Button browseButton;

    private ControllerUtils controllerUtils = new ControllerUtils();

    private ObservableList<String> applicationsList = FXCollections.observableArrayList();

    private void makeBrowseButton() {
        this.browseButton = new Button("Add application");
        browseButton.setOnAction(this::browseClicked);
    }

    @FXML
    private void initialize() {
        makeBrowseButton();
        groupVBox.getChildren().add(groupVBox.getChildren().size() - 1, browseButton);

    }

    private void browseClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to impose a restriction");
        Stage stage = (Stage) mainPane.getScene().getWindow();
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        if (list != null)
            for (File file: list)
                groupVBox.getChildren().add(groupVBox.getChildren().size() - 2, makeHBox(file.getAbsolutePath()));
    }

    public void okClicked(ActionEvent actionEvent) {
        for (Object child : groupVBox.getChildren()) {
            if (child instanceof HBox) {
                for (Object grandchild : ((HBox) child).getChildren())
                    if (grandchild instanceof TextField)
                        applicationsList.add(((TextField) grandchild).getText());
            }
        }
        // TODO make group in database Group(groupNameField, applicationsList)
        System.out.print(applicationsList);
    }

    private HBox makeHBox(String path) {
        HBox hBox = new HBox();
        hBox.getChildren().addAll(
                new TextField(path),
                controllerUtils.createButton(IMAGE_DELETE_PATH, deleteEvent)
        );
        return hBox;
    }

    private EventHandler<MouseEvent> deleteEvent = new EventHandler<>() {
        @Override
        public void handle(final MouseEvent ME) {
            Object button = ME.getSource();
            if (button instanceof Button) {
                HBox parent = (HBox) ((Button) button).getParent();
                groupVBox.getChildren().remove(parent);
            }
        }
    };
}

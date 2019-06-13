package pl.edu.agh.timekeeper.timer;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.edu.agh.timekeeper.model.Application;

public class TimerView extends Application {
    private Stage utilityStage;
    private Stage timerStage;
    private Text text;
    private BorderPane borderPane;

    public TimerView(String startText, Integer width, Integer height, Double initial_x, Double initial_y) {
        this.utilityStage = new Stage();
        this.utilityStage.initStyle(StageStyle.UTILITY);
        this.utilityStage.setOpacity(0);
        this.utilityStage.setHeight(0);
        this.utilityStage.setWidth(0);
        this.utilityStage.show();

        this.text = new Text(startText);
        this.borderPane = new BorderPane(this.text);
        this.borderPane.setStyle("-fx-background-color: #eaa7ef");
        this.borderPane.setCenter(text);
        this.timerStage = new Stage();
        this.timerStage.initOwner(this.utilityStage);
        this.timerStage.initStyle(StageStyle.UNDECORATED);
        this.timerStage.setAlwaysOnTop(true);
        this.timerStage.setScene(new Scene(borderPane, width, height));
        this.timerStage.setX(initial_x);
        this.timerStage.setY(initial_y);
        this.timerStage.show();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void setCoordinates(Double x, Double y) {
        this.timerStage.setX(x);
        this.timerStage.setY(y);
    }

    public boolean isTimerStageFocused() {
        return this.timerStage.isFocused() || this.utilityStage.isFocused();
    }
}

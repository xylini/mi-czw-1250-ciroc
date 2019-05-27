package pl.edu.agh.timekeeper.timer;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import pl.edu.agh.timekeeper.windows.FocusedWindowDataExtractor;

public class TimerController {
    private TimerView timerView;
    private final FocusedWindowDataExtractor fwde;

    public TimerController(){
        this.fwde = new FocusedWindowDataExtractor();
        this.timerView = new TimerView("00:00", 100, 25, -50.0, -50.0);
        updateTimerViewCoordsWorker(this.timerView);
//        updateTimerViewTimeWorker(this.timerView);
    }

    private void updateTimerViewCoordsWorker(TimerView timerView){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        Thread.sleep(16);

                        double xScale = Screen.getPrimary().getOutputScaleX();
                        double yScale = Screen.getPrimary().getOutputScaleY();

                        double scaledMaxX = Screen.getPrimary().getBounds().getMaxX();
                        double scaledMaxY = Screen.getPrimary().getBounds().getMaxY();

                        double originalResolutionX = xScale * scaledMaxX;
                        double originalResolutionY = yScale * scaledMaxY;

                        double scaledWindowRight = fwde.getForegroundWindowRect().right / Screen.getPrimary().getOutputScaleX();
                        double scaledWindowTop = fwde.getForegroundWindowRect().top / Screen.getPrimary().getOutputScaleY();

                        double xShiftFactor = 248.5 / originalResolutionX;
                        double yShiftFactor = 3.8 / originalResolutionY;

                        double scaledRightLeftShift = -scaledMaxX * xShiftFactor * Screen.getPrimary().getOutputScaleX();
                        double scaledTopDownShift = scaledMaxY * yShiftFactor * Screen.getPrimary().getOutputScaleY();

                        double resultX = scaledWindowRight + scaledRightLeftShift;
                        double resultY = scaledWindowTop + scaledTopDownShift;


                        timerView.setCoordinates(resultX, resultY);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void updateTimerViewTimeWorker(TimerView timerView){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        Thread.sleep(1000);
                        String currentTimeUsage = "00:00"; // TODO: Time counting logic
                        timerView.setText(currentTimeUsage);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.setDaemon(true);
        t.start();
    }


}


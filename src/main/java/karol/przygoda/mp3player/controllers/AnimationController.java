package karol.przygoda.mp3player.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;

public class AnimationController {

    public static void scrollPaneToEnd(ScrollPane pane, Label label, int labelMaxWidth, double duration) {
        double maxHValue = pane.getHmax();
        double animationDuration = label.getWidth() * duration;
        if (label.getWidth() <= (double)labelMaxWidth) {
            animationDuration = 0.3;
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(animationDuration), new KeyValue(pane.hvalueProperty(), maxHValue)));
        timeline.setOnFinished((event) -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished((e) -> scrollPaneToStart(pane, label, labelMaxWidth, duration));
            pause.play();
        });
        timeline.play();
    }

    public static void scrollPaneToStart(ScrollPane pane, Label label, int labelMaxWidth, double duration) {
        double minHValue = pane.getHmin();
        double animationDuration = label.getWidth() * duration;
        if (label.getWidth() <= (double)labelMaxWidth) {
            animationDuration = 0.3;
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(animationDuration), new KeyValue(pane.hvalueProperty(), minHValue)));
        timeline.setOnFinished((event) -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished((e) -> scrollPaneToEnd(pane, label, labelMaxWidth, duration));
            pause.play();
        });
        timeline.play();
    }
}
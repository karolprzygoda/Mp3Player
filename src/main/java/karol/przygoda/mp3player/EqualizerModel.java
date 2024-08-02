package karol.przygoda.mp3player;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import karol.przygoda.mp3player.controllers.EqualizerController;

public class EqualizerModel {
    private final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("fxmls/EqualizerView.fxml"));
    public static EqualizerController equalizerController;

    public Node initialize() throws IOException {
        Node node = this.fxmlLoader.load();
        equalizerController = this.fxmlLoader.getController();
        return node;
    }
}

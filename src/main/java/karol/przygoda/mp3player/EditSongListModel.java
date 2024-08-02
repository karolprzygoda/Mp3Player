package karol.przygoda.mp3player;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class EditSongListModel {
    private final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("fxmls/EditSongListView.fxml"));


    public Node initialize() throws IOException {
        return this.fxmlLoader.load();
    }
}

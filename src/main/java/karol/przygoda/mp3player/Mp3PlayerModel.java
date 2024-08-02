package karol.przygoda.mp3player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import karol.przygoda.mp3player.controllers.Mp3PlayerController;
import karol.przygoda.mp3player.data.DataBaseManager;
import karol.przygoda.mp3player.data.Songs;

public class Mp3PlayerModel extends Application {
    private double x = 0.0;
    private double y = 0.0;
    public static List<Songs> songsList = new ArrayList<>();
    public static Mp3PlayerController mp3PlayerController;


    public void start(Stage stage) throws IOException {
        DataBaseManager dbManager = new DataBaseManager();
        dbManager.initDataBase();
        songsList = dbManager.getAllSongs();
        FXMLLoader fxmlLoader = new FXMLLoader(Mp3PlayerModel.class.getResource("fxmls/Mp3PlayerView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400.0, 640.0);
        scene.setFill(Color.TRANSPARENT);
        String iconPath = "/karol/przygoda/mp3player/assets/cover.jpg";
        Image icon = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(iconPath)));
        scene.setOnMousePressed((event) -> {
            this.x = event.getSceneX();
            this.y = event.getSceneY();
        });
        scene.setOnMouseDragged((event) -> {
            stage.setX(event.getScreenX() - this.x);
            stage.setY(event.getScreenY() - this.y);
        });
        stage.getIcons().add(icon);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Mp3Player - Karol Przygoda");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest((windowEvent) -> {
            Platform.exit();
            System.exit(0);
        });

        Platform.runLater(() -> mp3PlayerController = fxmlLoader.getController());
    }


    public static void main(String[] args) {
        launch();
    }
}

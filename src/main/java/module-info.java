module karol.przygoda.mp3player {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.tika.core;
    requires java.xml;
    requires MaterialFX;
    requires javafx.media;
    requires java.sql;


    opens karol.przygoda.mp3player to javafx.fxml;
    opens karol.przygoda.mp3player.controllers to javafx.fxml;
    opens karol.przygoda.mp3player.fxmls to javafx.fxml;
    exports karol.przygoda.mp3player;
}
package karol.przygoda.mp3player.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import karol.przygoda.mp3player.Mp3PlayerModel;
import karol.przygoda.mp3player.data.DataBaseManager;
import karol.przygoda.mp3player.data.MetaDataCollector;
import karol.przygoda.mp3player.data.Songs;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

public class EditSongListController implements Initializable {
    @FXML
    private ScrollPane cardContainer;
    DataBaseManager dbManager = new DataBaseManager();
    private final MetaDataCollector metaDataCollector = new MetaDataCollector();
    private Songs newSong;
    private Image addSongIcon = new Image(Objects.requireNonNull(this.getClass().getResource("/karol/przygoda/mp3player/assets/icons/addSongIcon.png")).toString());
    private Image deleteIcon = new Image(Objects.requireNonNull(this.getClass().getResource("/karol/przygoda/mp3player/assets/icons/deleteIcon.png")).toString());
    private Image changeCoverIcon = new Image(Objects.requireNonNull(this.getClass().getResource("/karol/przygoda/mp3player/assets/icons/changeCover.png")).toString());
    private Image cover = new Image(Objects.requireNonNull(this.getClass().getResource("/karol/przygoda/mp3player/assets/cover.jpg")).toString());

    public EditSongListController() {
    }

    private AnchorPane createSongCard(Songs song) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(320.0, 70.0);
        anchorPane.setMaxWidth(320.0);
        anchorPane.getStyleClass().add("songCard");
        anchorPane.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; ");
        ImageView coverImage;
        if(!song.getAlbumCoverPath().isEmpty() && song.getAlbumCoverPath() != null) {
            File songCover = new File(song.getAlbumCoverPath());
            coverImage = new ImageView(new Image(songCover.toURI().toString()));
        }else{
            coverImage = new ImageView(new Image(this.cover.getUrl()));
        }
        AnchorPane iconContainer = new AnchorPane();
        iconContainer.setPrefSize(50.0, 50.0);
        coverImage.setFitHeight(50.0);
        coverImage.setFitWidth(50.0);
        coverImage.setOpacity(0.9);
        ImageView addIcon = new ImageView(new Image(this.changeCoverIcon.getUrl()));
        addIcon.setFitHeight(25.0);
        addIcon.setFitWidth(25.0);
        addIcon.setLayoutX(12.5);
        addIcon.setLayoutY(12.5);
        iconContainer.getChildren().addAll(coverImage, addIcon);
        Button button = new Button();
        button.setLayoutX(15.0);
        button.setLayoutY(10.0);
        button.setPadding(Insets.EMPTY);
        button.setGraphic(iconContainer);
        button.setCursor(Cursor.HAND);
        button.setOnAction((e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wybierz plik JPG jako okładkę danego utworu");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki JPG", "*.jpg"));
            File selectedFile = fileChooser.showOpenDialog(button.getParent().getScene().getWindow());
            if (selectedFile != null) {
                File destinationDir;
                try {
                    String projectPath = new File("").getAbsolutePath();
                    destinationDir = new File(projectPath, "albumCovers");

                    if (!destinationDir.exists()) {
                        destinationDir.mkdirs();
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    String coverName = selectedFile.getName();
                    Files.copy(selectedFile.toPath(), new File(destinationDir, selectedFile.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);

                    dbManager.updateSongCover(song.getId(), destinationDir + "/" + coverName);
                    Mp3PlayerModel.songsList = this.dbManager.getAllSongs();
                    Mp3PlayerModel.mp3PlayerController.refreshListAfterUpdate(Mp3PlayerModel.songsList.get(song.getId() - 1));
                    this.refreshSongList();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        Rectangle clip = new Rectangle(50.0, 50.0);
        clip.setArcWidth(10.0);
        clip.setArcHeight(10.0);
        coverImage.setClip(clip);
        Label titleLabel = new Label(song.getTitle());
        titleLabel.setStyle("-fx-font-family: 'Lato Bold'; -fx-font-size: 14;");
        ScrollPane scrollPane = new ScrollPane(titleLabel);
        scrollPane.setLayoutX(75.0);
        scrollPane.setLayoutY(16.0);
        scrollPane.setPrefWidth(150.0);
        scrollPane.setMaxWidth(150.0);
        scrollPane.setFitToHeight(true);
        ImageView deleteIcon = new ImageView(this.deleteIcon);
        deleteIcon.setFitHeight(30.0);
        deleteIcon.setFitWidth(30.0);
        Button delButton = new Button();
        delButton.setGraphic(deleteIcon);
        delButton.setLayoutX(280.0);
        delButton.setLayoutY(20.0);
        delButton.setPrefSize(30.0, 30.0);
        delButton.setMinSize(30.0, 30.0);
        delButton.setOpacity(0.8);
        delButton.setCursor(Cursor.HAND);
        delButton.setOnAction((e) -> {
            this.dbManager.deleteSong(song.getId());
            Mp3PlayerModel.songsList = this.dbManager.getAllSongs();
            this.refreshSongList();
            Mp3PlayerModel.mp3PlayerController.refreshListAfterDel();
        });
        Label artistLabel = new Label(song.getAuthor());
        artistLabel.setLayoutX(76.0);
        artistLabel.setLayoutY(36.0);
        artistLabel.setStyle("-fx-font-family: 'Lato'; -fx-font-size: 12;");
        anchorPane.getChildren().addAll(button, scrollPane, artistLabel, delButton);
        AnimationController.scrollPaneToEnd(scrollPane, titleLabel, 200, 0.007);
        return anchorPane;
    }

    private AnchorPane createAddNewSongCard() {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(320.0, 70.0);
        anchorPane.setMaxWidth(320.0);
        anchorPane.getStyleClass().add("songCard");
        anchorPane.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; ");
        ImageView addIcon = new ImageView(this.addSongIcon);
        addIcon.setFitHeight(50.0);
        addIcon.setFitWidth(50.0);
        Button addButton = new Button();
        addButton.setGraphic(addIcon);
        addButton.setPadding(Insets.EMPTY);
        addButton.setLayoutX(15.0);
        addButton.setLayoutY(10.0);
        addButton.setPrefSize(50.0, 50.0);
        addButton.setOpacity(0.8);
        addButton.setCursor(Cursor.HAND);
        addButton.setOnAction((e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wybierz plik MP3 do przeniesienia");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki MP3", "*.mp3"));
            File selectedFile = fileChooser.showOpenDialog(addButton.getParent().getScene().getWindow());
            if (selectedFile != null) {
                File destinationDir;
                try {
                    String projectPath = new File("").getAbsolutePath();
                    destinationDir = new File(projectPath, "music");

                    if (!destinationDir.exists()) {
                        destinationDir.mkdirs();
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    String songName = selectedFile.getName();
                    this.newSong = this.metaDataCollector.getMetaData(selectedFile);
                    Files.copy(selectedFile.toPath(), new File(destinationDir, selectedFile.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    dbManager.addSong(newSong.getTitle(), newSong.getAuthor(), destinationDir + "/" + songName);
                    Mp3PlayerModel.songsList = dbManager.getAllSongs();
                    this.refreshSongList();
                    if(Mp3PlayerModel.songsList.size() == 0){
                        Mp3PlayerModel.mp3PlayerController.refreshListAfterAdd(Mp3PlayerModel.songsList.get(0));
                    }else{
                        Mp3PlayerModel.mp3PlayerController.refreshListAfterAdd(Mp3PlayerModel.songsList.get(Mp3PlayerModel.songsList.size() - 1));
                    }
                } catch (TikaException | SAXException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        Label label = new Label();
        label.setText("Dodaj utwór");
        label.setLayoutX(76.0);
        label.setLayoutY(25.0);
        label.setStyle("-fx-font-family: 'Lato Bold'; -fx-font-size: 14;");
        anchorPane.getChildren().addAll(addButton, label);
        return anchorPane;
    }


    private void refreshSongList() {
        VBox vbox = new VBox();
        vbox.setSpacing(15.0);
        AnchorPane addNewSongCard = this.createAddNewSongCard();
        vbox.getChildren().add(addNewSongCard);

        for (Songs song : Mp3PlayerModel.songsList) {
            AnchorPane songCard = this.createSongCard(song);
            vbox.getChildren().add(songCard);
        }

        this.cardContainer.setContent(vbox);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Mp3PlayerModel.songsList != null) {

            VBox vbox = new VBox();
            vbox.setSpacing(15.0);
            this.cardContainer.setContent(vbox);
            AnchorPane addNewSongCard = this.createAddNewSongCard();
            vbox.getChildren().add(addNewSongCard);

            for (Songs song : Mp3PlayerModel.songsList) {
                AnchorPane songCard = this.createSongCard(song);
                vbox.getChildren().add(songCard);
            }

         }
    }
}

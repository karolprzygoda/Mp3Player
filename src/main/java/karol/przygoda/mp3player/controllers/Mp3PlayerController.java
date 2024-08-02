package karol.przygoda.mp3player.controllers;

import io.github.palexdev.materialfx.controls.MFXSlider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import karol.przygoda.mp3player.EditSongListModel;
import karol.przygoda.mp3player.EqualizerModel;
import karol.przygoda.mp3player.data.Songs;

import static karol.przygoda.mp3player.Mp3PlayerModel.songsList;

public class Mp3PlayerController implements Initializable {
    @FXML
    private Button openEqualizerButton;
    @FXML
    private AnchorPane equalizerScreen;
    @FXML
    private ScrollPane titleContainer;
    private boolean isSliderBeingDragged = false;
    @FXML
    private Label authorLabel;
    @FXML
    private Button closeButton;
    @FXML
    private ImageView coverImageView;
    @FXML
    private Button changeScreenButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button playMediaButton;
    @FXML
    private Label songCurrentTimeLabel;
    @FXML
    private Circle isRandomCircle;
    @FXML
    private Label songFullTimeLabel;
    @FXML
    private MFXSlider songTimelineSlider;
    @FXML
    private Label titleLabel;
    @FXML
    private MFXSlider volumeSlider;
    private final EditSongListModel editSongListModel = new EditSongListModel();
    private final EqualizerModel equalizerModel = new EqualizerModel();
    @FXML
    private ImageView playStopIcon;
    private int songNumber = 0;
    @FXML
    private ImageView changeScreenIcon;
    @FXML
    private AnchorPane settingsScreen;
    private Timer timer;
    private boolean running;
    private Media media;
    public static MediaPlayer mediaPlayer;
    private File songFile;
    private File songCoverFile;

    private VBox vbox;
    ImageView iconRef = null;
    @FXML
    AnchorPane songsListScreen;
    @FXML
    AnchorPane currentSongScreen;
    private boolean changed = false;
    private boolean isRandom = false;
    @FXML
    private ScrollPane cardContainer;
    private final ArrayList<ImageView> songsListButtonsIcon = new ArrayList<>();
    private final ArrayList<ImageView> songsCovers = new ArrayList<>();
    private boolean equalizerInitialized = false;
    Image pauseIcon = new Image(Objects.requireNonNull(getClass().getResource("/karol/przygoda/mp3player/assets/icons/pauseIcon.png")).toString());
    Image playIcon = new Image(Objects.requireNonNull(getClass().getResource("/karol/przygoda/mp3player/assets/icons/playIcon.png")).toString());
    Image forwardIcon = new Image(Objects.requireNonNull(getClass().getResource("/karol/przygoda/mp3player/assets/icons/arrowForwardIcon.png")).toString());
    Image backIcon = new Image(Objects.requireNonNull(getClass().getResource("/karol/przygoda/mp3player/assets/icons/arrowBackIcon.png")).toString());
    private Image cover;
    private Image defaultCover = new Image(Objects.requireNonNull(this.getClass().getResource("/karol/przygoda/mp3player/assets/cover.jpg")).toString());

    @FXML
    void setRandom() {
        isRandom = !isRandom;
        isRandomCircle.setVisible(!isRandomCircle.isVisible());
    }

    @FXML
    void playMedia(int songId) {
        songFile = new File(songsList.get(songId).getSongPath());
        File coverImageFile;
        Image coverImage;
        if (running) {
            if (media.getSource().equals(songFile.toURI().toString()) && mediaPlayer.getStatus().equals(Status.PLAYING)) {
                mediaPlayer.pause();
                songsListButtonsIcon.get(songId).setImage(playIcon);
                playStopIcon.setImage(playIcon);
                songsListButtonsIcon.get(songNumber).setImage(playIcon);
            } else if (media.getSource().equals(songFile.toURI().toString()) && mediaPlayer.getStatus().equals(Status.PAUSED)) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                EqualizerModel.equalizerController.changeSpeed(null);
                EqualizerModel.equalizerController.updateEqualizer();
                mediaPlayer.play();
                songsListButtonsIcon.get(songId).setImage(playIcon);
                playStopIcon.setImage(pauseIcon);
                songsListButtonsIcon.get(songNumber).setImage(pauseIcon);
            } else {
                iconRef.setImage(playIcon);
                mediaPlayer.stop();
                cancelTimer();
                playStopIcon.setImage(pauseIcon);
                beginTimer();
                if(!songsList.get(songId).getAlbumCoverPath().isEmpty() && songsList.get(songId).getAlbumCoverPath() != null) {
                    coverImageFile = new File(songsList.get(songId).getAlbumCoverPath());
                    coverImage = new Image(coverImageFile.toURI().toString());
                    coverImageView.setImage(coverImage);
                }else{
                    coverImageView.setImage(defaultCover);
                }
                songNumber = songId;
                songsListButtonsIcon.get(songId).setImage(pauseIcon);
                media = new Media(songFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                playStopIcon.setImage(pauseIcon);
                songsListButtonsIcon.get(songNumber).setImage(pauseIcon);
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                EqualizerModel.equalizerController.changeSpeed(null);
                EqualizerModel.equalizerController.updateEqualizer();
                mediaPlayer.play();
            }
        } else {
            songsListButtonsIcon.get(songId).setImage(pauseIcon);
            beginTimer();
            if(!songsList.get(songId).getAlbumCoverPath().isEmpty() && songsList.get(songId).getAlbumCoverPath() != null) {
                coverImageFile = new File(songsList.get(songId).getAlbumCoverPath());
                coverImage = new Image(coverImageFile.toURI().toString());
                coverImageView.setImage(coverImage);
            }else{
                coverImageView.setImage(defaultCover);
            }
            songNumber = songId;
            songFile = new File(songsList.get(songId).getSongPath());
            media = new Media(songFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            if (!equalizerInitialized) {
                try {
                    equalizerScreen.getChildren().add(equalizerModel.initialize());
                    equalizerInitialized = true;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            EqualizerModel.equalizerController.changeSpeed(null);
            EqualizerModel.equalizerController.updateEqualizer();
            mediaPlayer.play();
            playStopIcon.setImage(pauseIcon);
        }

        setMediaMetadata();
        iconRef = songsListButtonsIcon.get(songId);
    }

    @FXML
    void nextMedia() {


        if (songNumber < songsList.size() - 1) {
            songsListButtonsIcon.get(songNumber).setImage(playIcon);
            if (!isRandom) {
                ++songNumber;
            } else {
                Random random = new Random();
                int randomSongNumber = random.nextInt(songsList.size());

                while (songNumber == randomSongNumber){
                    randomSongNumber = random.nextInt(songsList.size());
                }



                songNumber = randomSongNumber;
            }

            mediaPlayer.stop();
            if (running) {
                cancelTimer();
            }

            if(!songsList.get(songNumber).getAlbumCoverPath().isEmpty() && songsList.get(songNumber).getAlbumCoverPath() != null) {
                songCoverFile = new File(songsList.get(songNumber).getAlbumCoverPath());
                cover = new Image(songCoverFile.toURI().toString());
                coverImageView.setImage(cover);
            }else{
                coverImageView.setImage(defaultCover);
            }

            songFile = new File(songsList.get(songNumber).getSongPath());
            media = new Media(songFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            setMediaMetadata();
            playMedia(songNumber);
        } else {
            songsListButtonsIcon.get(songNumber).setImage(playIcon);
            if (!isRandom) {
                songNumber = 0;
            } else {
                Random random = new Random();
                int randomSongNumber = random.nextInt(songsList.size());

                while (songNumber == randomSongNumber){
                    randomSongNumber = random.nextInt(songsList.size());
                }
            }

            mediaPlayer.stop();
            if (running) {
                cancelTimer();
            }

            if(!songsList.get(songNumber).getAlbumCoverPath().isEmpty() && songsList.get(songNumber).getAlbumCoverPath() != null) {
                songCoverFile = new File(songsList.get(songNumber).getAlbumCoverPath());
                cover = new Image(songCoverFile.toURI().toString());
                coverImageView.setImage(cover);
            }else{
                coverImageView.setImage(defaultCover);
            }

            songFile = new File(songsList.get(songNumber).getSongPath());
            media = new Media(songFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            setMediaMetadata();
            playMedia(songNumber);
        }

    }

    @FXML
    void previousMedia() {
        if (songNumber > 0) {
            songsListButtonsIcon.get(songNumber).setImage(playIcon);
            --songNumber;
            mediaPlayer.stop();
            if (running) {
                cancelTimer();
            }

            if(!songsList.get(songNumber).getAlbumCoverPath().isEmpty() && songsList.get(songNumber).getAlbumCoverPath() != null) {
                songCoverFile = new File(songsList.get(songNumber).getAlbumCoverPath());
                cover = new Image(songCoverFile.toURI().toString());
                coverImageView.setImage(cover);
            }else{
                coverImageView.setImage(defaultCover);
            }

            songFile = new File(songsList.get(songNumber).getSongPath());
            media = new Media(songFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            setMediaMetadata();
            playMedia(songNumber);
        } else {
            songsListButtonsIcon.get(songNumber).setImage(playIcon);
            songNumber = songsList.size() - 1;
            mediaPlayer.stop();
            if (running) {
                cancelTimer();
            }

            if(!songsList.get(songNumber).getAlbumCoverPath().isEmpty() && songsList.get(songNumber).getAlbumCoverPath() != null) {
                songCoverFile = new File(songsList.get(songNumber).getAlbumCoverPath());
                cover = new Image(songCoverFile.toURI().toString());
                coverImageView.setImage(cover);
            }else{
                coverImageView.setImage(defaultCover);
            }

            songFile = new File(songsList.get(songNumber).getSongPath());
            media = new Media(songFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            setMediaMetadata();
            playMedia(songNumber);
        }

    }

    @FXML
    void resetMedia() {
        mediaPlayer.seek(Duration.seconds(0.0));
    }

    public void beginTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                if (current / end == 1.0) {
                    cancelTimer();
                }

            }
        };
        timer.scheduleAtFixedRate(task, 0L, 1000L);
    }

    public void cancelTimer() {
        running = false;
        timer.cancel();
    }

    private void updateSongCurrentTimeLabel() {
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double currentTimeInMillis = newValue.toMillis();
            int minutes = (int) (currentTimeInMillis / 60000.0);
            int seconds = (int) (currentTimeInMillis / 1000.0 % 60.0);
            String formattedSeconds = String.format("%02d", seconds);
            songCurrentTimeLabel.setText("" + minutes + ":" + formattedSeconds);
        });
    }

    @FXML
    public void close() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void minimize() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    private void setMediaMetadata() {
        titleLabel.setText(songsList.get(songNumber).getTitle());
        authorLabel.setText(songsList.get(songNumber).getAuthor());
        updateSongFullTimeLabel();
        updateSongCurrentTimeLabel();
        updateSongTimelineSlider();
        mediaPlayer.setOnEndOfMedia(this::nextMedia);
    }

    private void updateSongTimelineSlider() {
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!isSliderBeingDragged) {
                double currentTimeInMillis = newValue.toMillis();
                double totalDurationInMillis = media.getDuration().toMillis();
                double progress = currentTimeInMillis / totalDurationInMillis;
                songTimelineSlider.setValue(progress * 100.0);
                if (songTimelineSlider.getPopupSupplier() != null) {
                    songTimelineSlider.setPopupSupplier(null);
                }
            }

        });
        songTimelineSlider.setOnMousePressed((event) -> isSliderBeingDragged = true);
        songTimelineSlider.setOnMouseReleased((event) -> isSliderBeingDragged = false);
    }

    private void updateSongFullTimeLabel() {
        mediaPlayer.setOnReady(() -> {
            double durationInMillis = media.getDuration().toMillis();
            int minutes = (int) (durationInMillis / 60000.0);
            int seconds = (int) (durationInMillis / 1000.0 % 60.0);
            String formattedSeconds = String.format("%02d", seconds);
            songFullTimeLabel.setText("" + minutes + ":" + formattedSeconds);
        });
    }

    @FXML
    private void changeScreen(Button button) {
        if (button.equals(settingsButton) && !settingsScreen.isVisible()) {
            settingsScreen.setVisible(true);
            currentSongScreen.setVisible(false);
            songsListScreen.setVisible(false);
            equalizerScreen.setVisible(false);
            changeScreenIcon.setImage(backIcon);
        } else if (button.equals(changeScreenButton) && settingsScreen.isVisible()) {
            changeScreenIcon.setImage(forwardIcon);
            settingsScreen.setVisible(false);
            currentSongScreen.setVisible(false);
            equalizerScreen.setVisible(false);
            songsListScreen.setVisible(true);
        } else if (button.equals(settingsButton) && settingsScreen.isVisible()) {
            changeScreenIcon.setImage(forwardIcon);
            settingsScreen.setVisible(false);
            currentSongScreen.setVisible(false);
            equalizerScreen.setVisible(false);
            songsListScreen.setVisible(true);
        } else if (button.equals(openEqualizerButton) && !equalizerScreen.isVisible()) {
            changeScreenIcon.setImage(backIcon);
            settingsScreen.setVisible(false);
            currentSongScreen.setVisible(false);
            equalizerScreen.setVisible(true);
            songsListScreen.setVisible(false);
        } else if (button.equals(changeScreenButton) && equalizerScreen.isVisible()) {
            changeScreenIcon.setImage(backIcon);
            settingsScreen.setVisible(false);
            currentSongScreen.setVisible(true);
            equalizerScreen.setVisible(false);
            songsListScreen.setVisible(false);
        } else if (changed) {
            songsListScreen.setVisible(true);
            currentSongScreen.setVisible(false);
            changed = false;
            changeScreenIcon.setImage(forwardIcon);
        } else {
            if(!songsList.isEmpty()) {
                songsListScreen.setVisible(false);
                currentSongScreen.setVisible(true);
                changed = true;
                changeScreenIcon.setImage(backIcon);
            }
        }

    }

    private AnchorPane createSongCard(Songs song) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(320.0, 70.0);
        anchorPane.getStyleClass().add("songCard");
        anchorPane.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; ");
        ImageView coverImage;
        if(!song.getAlbumCoverPath().isEmpty() && song.getAlbumCoverPath() != null) {
            File songCover = new File(song.getAlbumCoverPath());
             coverImage = new ImageView(new Image(songCover.toURI().toString()));
        }else{
             coverImage = new ImageView(new Image(this.defaultCover.getUrl()));

        }

        coverImage.setFitHeight(50.0);
        coverImage.setFitWidth(50.0);
        coverImage.setLayoutX(15.0);
        coverImage.setLayoutY(10.0);
        Rectangle clip = new Rectangle(50.0, 50.0);
        clip.setArcWidth(10.0);
        clip.setArcHeight(10.0);
        coverImage.setClip(clip);
        songsCovers.add(coverImage);
        ImageView playIcon = new ImageView(this.playIcon);
        songsListButtonsIcon.add(playIcon);
        playIcon.setFitHeight(30.0);
        playIcon.setFitWidth(30.0);
        Label titleLabel = new Label(song.getTitle());
        titleLabel.setStyle("-fx-font-family: 'Lato Bold'; -fx-font-size: 14;");
        ScrollPane scrollPane = new ScrollPane(titleLabel);
        scrollPane.setLayoutX(75.0);
        scrollPane.setLayoutY(16.0);
        scrollPane.setPrefWidth(150.0);
        scrollPane.setMaxWidth(150.0);
        scrollPane.setFitToHeight(true);
        Label artistLabel = new Label(song.getAuthor());
        artistLabel.setLayoutX(76.0);
        artistLabel.setLayoutY(36.0);
        artistLabel.setStyle("-fx-font-family: 'Lato'; -fx-font-size: 12;");
        Button playButton = new Button();
        playButton.setGraphic(playIcon);
        playButton.setLayoutX(270.0);
        playButton.setLayoutY(18.0);
        playButton.setPrefSize(30.0, 30.0);
        playButton.setOpacity(0.8);
        playButton.setCursor(Cursor.HAND);
        playButton.setPadding(Insets.EMPTY);
        playButton.setOnAction((event) -> playMedia(song.getId() - 1));
        anchorPane.getChildren().addAll(coverImage, playButton, scrollPane, artistLabel);
        AnimationController.scrollPaneToEnd(scrollPane, titleLabel, 200, 0.007);
        return anchorPane;
    }

    public void refreshListAfterAdd(Songs song) {
        AnchorPane songCard = createSongCard(song);
        vbox.getChildren().add(songCard);
        if(songsList.size() == 1){
            if(!songsList.get(0).getAlbumCoverPath().isEmpty() && songsList.get(0).getAlbumCoverPath() != null) {
                songCoverFile = new File(songsList.get(0).getAlbumCoverPath());
                cover = new Image(songCoverFile.toURI().toString());
                coverImageView.setImage(cover);
            }else{
                coverImageView.setImage(defaultCover);
            }
            songFile = new File(songsList.get(0).getSongPath());
            media = new Media(songFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            setMediaMetadata();
        }
    }

    public void refreshListAfterUpdate(Songs song) {
        File file = new File(song.getAlbumCoverPath());
        songsCovers.get(song.getId() - 1).setImage(new Image(file.toURI().toString()));
        if (songNumber == song.getId() - 1) {
            coverImageView.setImage(new Image(file.toURI().toString()));
        }

    }

    public void refreshListAfterDel() {
        mediaPlayer.stop();
        vbox.getChildren().clear();
        songsListButtonsIcon.clear();
        songsCovers.clear();

        for (Songs song : songsList) {
            AnchorPane songCard = createSongCard(song);
            vbox.getChildren().add(songCard);
        }

        cardContainer.setContent(vbox);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        vbox = new VBox();
        vbox.setSpacing(15.0);
        cardContainer.setContent(vbox);

        for (Songs song : songsList) {
            AnchorPane songCard = createSongCard(song);
            vbox.getChildren().add(songCard);
        }

        Rectangle clip = new Rectangle(coverImageView.getFitWidth(), coverImageView.getFitHeight());
        clip.setArcWidth(20.0);
        clip.setArcHeight(20.0);
        coverImageView.setClip(clip);
        if (!songsList.isEmpty()) {
            if(!songsList.get(songNumber).getAlbumCoverPath().isEmpty() && songsList.get(songNumber).getAlbumCoverPath() != null) {
                songCoverFile = new File(songsList.get(songNumber).getAlbumCoverPath());
                cover = new Image(songCoverFile.toURI().toString());
                coverImageView.setImage(cover);
            }else{
                coverImageView.setImage(defaultCover);
            }
            songFile = new File(songsList.get(songNumber).getSongPath());
            media = new Media(songFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            setMediaMetadata();

            try {
                equalizerScreen.getChildren().add(equalizerModel.initialize());
                equalizerInitialized = true;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        AnimationController.scrollPaneToEnd(titleContainer, titleLabel, 360, 0.002);
        volumeSlider.valueProperty().addListener((observableValue, number, t1) -> mediaPlayer.setVolume(volumeSlider.getValue() * 0.01));

        try {
            settingsScreen.getChildren().add(editSongListModel.initialize());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        playMediaButton.setOnAction((event) -> playMedia(songNumber));
        changeScreenButton.setOnAction((event) -> changeScreen(changeScreenButton));
        settingsButton.setOnAction((event) -> changeScreen(settingsButton));
        openEqualizerButton.setOnAction((event) -> changeScreen(openEqualizerButton));
        songTimelineSlider.setOnMouseClicked((event) -> {
            double mouseX = event.getX();
            double width = songTimelineSlider.getWidth();
            double percent = mouseX / width;
            double totalDurationInMillis = media.getDuration().toMillis();
            double seekTimeInMillis = percent * totalDurationInMillis;
            mediaPlayer.seek(Duration.millis(seekTimeInMillis));
        });
    }
}

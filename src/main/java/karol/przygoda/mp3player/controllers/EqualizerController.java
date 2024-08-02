package karol.przygoda.mp3player.controllers;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXSlider;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.EqualizerBand;

public class EqualizerController implements Initializable {
    @FXML
    private MFXSlider Slider16kHz;
    @FXML
    private MFXSlider Slider1kHz;
    @FXML
    private MFXSlider Slider250Hz;
    @FXML
    private MFXSlider Slider4kHz;
    @FXML
    private MFXSlider Slider60Hz;
    private final double[] gainValues = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
    private MFXSlider[] sliders;
    private final int[] speeds = new int[]{25, 50, 75, 100, 125, 150, 175, 200};
    @FXML
    public MFXComboBox<String> speedBox;

    @FXML
    public void changeSpeed(ActionEvent event) {
        if (this.speedBox.getValue() == null) {
            Mp3PlayerController.mediaPlayer.setRate(1.0);
        } else {
            Mp3PlayerController.mediaPlayer.setRate((double)Integer.parseInt(this.speedBox.getValue().substring(0, this.speedBox.getValue().length() - 1)) * 0.01);
        }

    }

    public void updateEqualizer() {
        if (Mp3PlayerController.mediaPlayer != null) {
            for(int i = 0; i < 5; ++i) {
                Mp3PlayerController.mediaPlayer.getAudioEqualizer().getBands().get(i).setGain(this.gainValues[i]);
            }
        }

    }

    @FXML
    private void resetEqualizer() {
        for(int i = 0; i < 5; ++i) {
            this.sliders[i].setValue(0.0);
        }

    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.sliders = new MFXSlider[]{this.Slider60Hz, this.Slider250Hz, this.Slider1kHz, this.Slider4kHz, this.Slider16kHz};
        double[] frequencies = new double[]{60.0, 250.0, 1000.0, 4000.0, 16000.0};

        for(int i = 0; i < 5; ++i) {
            int finalI = i;
            this.sliders[i].valueProperty().addListener((observable, oldValue, newValue) -> {
                double gain = newValue.doubleValue();
                this.gainValues[finalI] = gain;
                this.updateEqualizer();
            });
        }


        for (int speed : speeds) {
            this.speedBox.getItems().add("" + speed + "%");
        }

        this.speedBox.setOnAction(this::changeSpeed);

        for (double frequency : frequencies) {
            EqualizerBand band = new EqualizerBand(frequency, 1.0, 0.0);
            Mp3PlayerController.mediaPlayer.getAudioEqualizer().getBands().add(band);
        }

    }
}

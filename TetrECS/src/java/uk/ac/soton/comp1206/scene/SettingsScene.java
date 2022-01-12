package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * SettingsScene holds sliders used for audio manipulation
 */
public class SettingsScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(SettingsScene.class);
    public static double MUSIC_BASE = 0.1;
    public static double SFX_BASE = 0.2;

    /**
     * Create a new settings scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public SettingsScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var settingsPane = new BorderPane();
        settingsPane.setMaxWidth(gameWindow.getWidth());
        settingsPane.setMaxHeight(gameWindow.getHeight());
        settingsPane.getStyleClass().add("menu");
        root.getChildren().add(settingsPane);

        var stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);
        settingsPane.setCenter(stackPane);

        //Background rectangle
        var background = new Rectangle(400, 300);
        backgroundRect(stackPane, background);

        //Main box used for placement
        var settingsMainBox = new VBox();
        settingsMainBox.setSpacing(50);
        settingsMainBox.setPadding(new Insets(160,0,0,0));
        settingsMainBox.setAlignment(Pos.TOP_CENTER);
        stackPane.getChildren().add(settingsMainBox);

        //Title for settings
        var settingsTitle = new Label("Settings");
        settingsTitle.getStyleClass().add("title");
        settingsMainBox.getChildren().add(settingsTitle);

        //Box where all setting nodes are stored
        var settingsBox = new VBox();
        settingsBox.setSpacing(5);
        settingsBox.setAlignment(Pos.CENTER);
        settingsMainBox.getChildren().add(settingsBox);

        //Text for music volume slider
        var musicVolume = new Text("Music Volume:");
        musicVolume.getStyleClass().add("chat");
        settingsBox.getChildren().add(musicVolume);

        //Music volume slider to adjust intensity
        var musicSlider = new Slider(0, 1.0, MUSIC_BASE);
        musicSlider.setMajorTickUnit(0.1);
        musicSlider.setBlockIncrement(0.1);
        musicSlider.setShowTickLabels(true);
        musicSlider.setShowTickMarks(true);
        musicSlider.setMaxWidth(250);
        settingsBox.getChildren().add(musicSlider);

        //Listener for the music slider
        musicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Multimedia.musicVolume = (double) newValue;
            Multimedia.musicPlayer.setVolume(Multimedia.musicVolume);
            MUSIC_BASE = (double) newValue;
        });

        //Text for SFX slider
        var SFXVolume = new Text("SFX Volume:");
        SFXVolume.getStyleClass().add("chat");
        settingsBox.getChildren().add(SFXVolume);

        //SFX volume slider to adjust intensity
        var SFXSlider = new Slider(0, 1.0, SFX_BASE);
        SFXSlider.setMajorTickUnit(0.1);
        SFXSlider.setBlockIncrement(0.1);
        SFXSlider.setShowTickLabels(true);
        SFXSlider.setShowTickMarks(true);
        SFXSlider.setMaxWidth(250);
        settingsBox.getChildren().add(SFXSlider);

        //Listener for the SFX slider
        SFXSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Multimedia.SFXVolume = (double) newValue;
            Multimedia.audioPlayer.setVolume(Multimedia.SFXVolume);
            SFX_BASE = (double) newValue;
        });
    }

    @Override
    public void initialise() { }
}

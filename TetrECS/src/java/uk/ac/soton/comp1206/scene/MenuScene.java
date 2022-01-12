package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     *
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var mainPane = new BorderPane();
        mainPane.setMaxWidth(gameWindow.getWidth());
        mainPane.setMaxHeight(gameWindow.getHeight());
        mainPane.getStyleClass().add("menu");
        root.getChildren().add(mainPane);

        var title = new Text("TetrECS");
        title.getStyleClass().add("bigtitle");
        mainPane.setCenter(title);
        beginAnimation(title, 1.5);

        var buttonBox = new VBox();
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);
        buttonBox.setSpacing(8);
        mainPane.setBottom(buttonBox);

        var play = new Button("Play");
        play.setPrefWidth(205);
        play.getStyleClass().add("menuItem");

        play.setOnAction(this::startGame);

        var multiplayer = new Button("Multiplayer");
        multiplayer.setPrefWidth(205);
        multiplayer.getStyleClass().add("menuItem");

        multiplayer.setOnAction(this::showLobby);

        var instructions = new Button("How To Play");
        instructions.setPrefWidth(205);
        instructions.getStyleClass().add("menuItem");

        instructions.setOnAction(this::showInstructions);

        var settings = new Button("Settings");
        settings.setPrefWidth(205);
        settings.getStyleClass().add("menuItem");

        settings.setOnAction(this::showSettings);

        var exit = new Button("Exit");
        exit.getStyleClass().add("menuItem");

        exit.setOnAction(this::exitGame);

        buttonBox.getChildren().addAll(play, multiplayer, instructions, settings, exit);
    }

    /**
     * Initialise the menu by playing the appropriate
     * song
     */
    @Override
    public void initialise() {
        Multimedia.setMusicPlayer("ocean.mp3");
    }

    /**
     * Handle when the Start Game button is pressed
     * and launch a single player game
     *
     * @param event event
     */
    private void startGame(ActionEvent event) {
        Multimedia.setAudioPlayer("gamestart.wav");
        gameWindow.startChallenge();
        Multimedia.setMusicPlayer("menu1.3.wav");
    }

    /**
     * Handle when the multiplayer button is pressed
     * and show lobby scene
     *
     * @param event event
     */
    private void showLobby(ActionEvent event) {
        Multimedia.setAudioPlayer("buttonclick1.wav");
        gameWindow.startLobby();
    }

    /**
     * Handle when the instructions button is pressed
     * and show instructions scene
     *
     * @param event event
     */
    private void showInstructions(ActionEvent event) {
        Multimedia.setAudioPlayer("buttonclick1.wav");
        gameWindow.startInstructions();
    }

    /**
     * Handle when the settings button is pressed
     * and show settings scene
     *
     * @param event event
     */
    private void showSettings(ActionEvent event) {
        Multimedia.setAudioPlayer("buttonclick1.wav");
        gameWindow.startSettings();
    }

    /**
     * Handle when the exit button is pressed
     * and close the game
     *
     * @param event event
     */
    private void exitGame(ActionEvent event) {
        Multimedia.setAudioPlayer("buttonclick1.wav");
        Multimedia.audioPlayer.setOnEndOfMedia(this::shutdown);
    }

    /**
     * Shutdown application method
     */
    private void shutdown() {
        App.getInstance().shutdown();
    }
}

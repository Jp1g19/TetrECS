package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Instructions scene shows controls and dynamically generated pieces used
 * in the game
 */
public class InstructionScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(InstructionScene.class);

    /**
     * Create a new Instructions scene
     *
     * @param gameWindow the Game Window
     */
    public InstructionScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instruction Scene");
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var mainPane = new BorderPane();
        mainPane.setMaxWidth(gameWindow.getWidth());
        mainPane.setMaxHeight(gameWindow.getHeight());
        mainPane.getStyleClass().add("menu");
        root.getChildren().add(mainPane);

        var pieces = new VBox();
        pieces.setAlignment(Pos.CENTER);
        pieces.setSpacing(7);
        mainPane.setBottom(pieces);

        var instructionsTitle = new Label("Instructions");
        instructionsTitle.getStyleClass().add("title");
        pieces.getChildren().add(instructionsTitle);

        var image = new Image(InstructionScene.class.getResource("/images/Instructions1.png").toExternalForm());
        var instructions = new ImageView(image);
        instructions.setFitHeight(300);
        instructions.setFitWidth(500);
        pieces.getChildren().add(instructions);

        var piecesLabel = new Label("Game Pieces");
        piecesLabel.getStyleClass().add("title");
        pieces.getChildren().add(piecesLabel);

        createLines(pieces);
    }

    @Override
    public void initialise() {
    }

    /**
     * Used to create dynamically generated pieces
     *
     * @param pieces
     */
    public void createLines(VBox pieces) {
        int pieceCounter = 0;
        for (int line = 0; line < 3; line++) {
            var piecesLine = new HBox();
            piecesLine.setAlignment(Pos.CENTER);
            piecesLine.setSpacing(10);
            pieces.getChildren().add(piecesLine);
            for (int piece = 0; piece < 5; piece++) {
                var pieceBoard = new PieceBoard(3, 3, 50, 50);
                pieceBoard.showPiece(GamePiece.createPiece(pieceCounter));
                piecesLine.getChildren().add(pieceBoard);
                pieceCounter++;
            }
        }
    }
}

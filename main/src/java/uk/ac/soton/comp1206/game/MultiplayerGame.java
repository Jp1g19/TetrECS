package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.network.Communicator;

import java.util.LinkedList;
import java.util.Queue;

public class MultiplayerGame extends Game {
    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

    private final Communicator communicator;
    private Queue<GamePiece> pieceQueue = new LinkedList<>();

    /**
     * Create a new Multiplayer game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows, Communicator communicator) {
        super(cols, rows);
        this.communicator = communicator;
        this.communicator.addListener(this::receiveCommunication);
        for (int i = 0; i<10; i++) {
            this.communicator.send("PIECE");
        }
    }

    @Override
    public void start() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(this::initialiseGame);
    }

    @Override
    public void initialiseGame() {
        setTimer();
        loopListen(getTimerDelay());
        Platform.runLater(() -> {
            currentPiece = spawnPiece();
            followingPiece =spawnPiece();
            nextPieceListen();
        });
    }

    @Override
    public void nextPiece() {
        logger.info("Getting Next Piece");
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        nextPieceListen();
        communicator.send("SCORES");
    }

    /**
     * Used to handle communicator messages
     *
     * @param message
     */
    private void receiveCommunication(String message) {
        getCommand(message);
    }

    /**
     * Handles communicator messages based
     * on content of the message
     *
     * @param message
     */
    private void getCommand(String message) {
        if (message.startsWith("PIECE")) {
            this.addPiece(message);
        }
    }

    /**
     * Adds piece retrieved from communicator to the queue
     *
     * @param piece
     */
    private void addPiece(String piece) {
        logger.info("Adding " + piece);
        piece = piece.replace("PIECE ","");
        pieceQueue.add(GamePiece.createPiece(Integer.parseInt(piece)));
    }

    @Override
    public GamePiece spawnPiece() {
        return pieceQueue.poll();
    }

    @Override
    public void blockClicked(GameBlock gameBlock) {
        super.blockClicked(gameBlock);
        StringBuilder board = new StringBuilder("BOARD ");
        for (int column = 0; column < cols; column++) {
            for (int row = 0; row < rows; row++) {
                board.append(grid.getGridProperty(column,row).getValue() + " ");
            }
        }
        communicator.send("PIECE");
        communicator.send(board.toString());
    }

    @Override
    public void score(int lines, int blocks) {
        super.score(lines,blocks);
        communicator.send("SCORE " + score.getValue());
    }
}

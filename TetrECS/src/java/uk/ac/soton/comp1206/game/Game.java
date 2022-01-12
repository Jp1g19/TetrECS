package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.LivesListener;
import uk.ac.soton.comp1206.event.NextPieceListener;


import java.util.*;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Current piece
     */
    protected GamePiece currentPiece;

    /**
     * Following piece
     */
    protected GamePiece followingPiece;

    /**
     * Score property
     */
    protected SimpleIntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Level property
     */
    protected SimpleIntegerProperty level = new SimpleIntegerProperty(0);

    /**
     * Lives property
     */
    protected SimpleIntegerProperty lives = new SimpleIntegerProperty(3);

    /**
     * Multiplier property
     */
    protected SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

    /**
     * Next piece listener
     */
    private NextPieceListener nextPieceListener;

    /**
     * Line cleared listener
     */
    private LineClearedListener lineClearedListener;

    /**
     * Game loop listener
     */
    private GameLoopListener gameLoopListener;

    /**
     * Lives listener
     */
    private LivesListener livesListener;

    /**
     * Game timer
     */
    private Timer timer;

    /**
     * Timer task
     */
    private TimerTask timerTask;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        currentPiece = spawnPiece();
        followingPiece = spawnPiece();
        nextPieceListen();
        setTimer();
        loopListen(getTimerDelay());
    }

    /**
     * Handle what should happen when a particular block is clicked
     *
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        if (grid.canPlayPiece(currentPiece, x, y)) {
            grid.playPiece(currentPiece, x, y);
            timer.cancel();
            timerTask.cancel();
            setTimer();
            loopListen(getTimerDelay());
            afterPiece();
            nextPiece();
        } else {
            Multimedia.setAudioPlayer("incorrect.wav");
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     *
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Spawns a new piece
     *
     * @return new piece
     */
    public GamePiece spawnPiece() {
        logger.info("Spawning Piece");
        Random random = new Random();
        return GamePiece.createPiece(random.nextInt(15));
    }

    /**
     * Spawns next pieces
     */
    public void nextPiece() {
        logger.info("Getting next piece");
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        nextPieceListen();
    }

    /**
     * Cleans up after a piece is played by
     * checking if there are lines to be cleared
     * and the score needs to be updated
     */
    public void afterPiece() {
        logger.info("Starting Cleanup");
        ArrayList<Integer> rows = checkRow();
        ArrayList<Integer> cols = checkCol();
        HashSet<GameBlockCoordinate> coordinates = getCoordinates(cols, rows);
        int lines = cols.size() + rows.size();
        int blocks = coordinates.size();

        lineCleared(coordinates);

        for (GameBlockCoordinate gameBlockCoordinate : coordinates) {
            logger.info("Cleaning");
            int X = gameBlockCoordinate.getX();
            int Y = gameBlockCoordinate.getY();
            grid.set(X, Y, 0);
        }
        int oldScore = getScore().get();
        score(lines, blocks);
        int newScore = getScore().get();

        if (oldScore != newScore) {
            Multimedia.setAudioPlayer("lineclear.mp3");
        }
    }

    /**
     * Checks if any row is filled
     *
     * @return dirtyRows
     */
    public ArrayList<Integer> checkRow() {
        ArrayList<Integer> dirtyRows = new ArrayList<>();
        for (int r = 0; r < getRows(); r++) {
            int rowCounter = 0;
            for (int c = 0; c < getCols(); c++) {
                if (getGrid().get(c, r) != 0) {
                    if (rowCounter + 1 == 5) {
                        logger.info("Adding row to clean: " + r);
                        dirtyRows.add(r);
                        rowCounter = 0;
                    }
                    rowCounter++;
                }
            }
        }
        return dirtyRows;
    }

    /**
     * Checks if any columns are filled
     *
     * @return dirtyCols
     */
    public ArrayList<Integer> checkCol() {
        ArrayList<Integer> dirtyCols = new ArrayList<>();
        for (int c = 0; c < getCols(); c++) {
            int colCounter = 0;
            for (int r = 0; r < getRows(); r++) {
                if (getGrid().get(c, r) != 0) {
                    if (colCounter + 1 == 5) {
                        logger.info("Adding col to clean: " + c);
                        dirtyCols.add(c);
                        colCounter = 0;
                    }
                    colCounter++;
                }
            }
        }
        return dirtyCols;
    }

    /**
     * Gets the coordinates for all the rows that
     * are filled
     *
     * @param Columns
     * @param Rows
     * @return coordinates
     */
    public HashSet<GameBlockCoordinate> getCoordinates(ArrayList<Integer> Columns, ArrayList<Integer> Rows) {
        HashSet<GameBlockCoordinate> coordinates = new HashSet<>();
        for (int row : Rows) {
            for (int col = 0; col < getCols(); col++) {
                GameBlockCoordinate gameBlockCoordinate = new GameBlockCoordinate(col, row);
                coordinates.add(gameBlockCoordinate);
            }
        }
        for (int col : Columns) {
            for (int row = 0; row < getRows(); row++) {
                GameBlockCoordinate gameBlockCoordinate = new GameBlockCoordinate(col, row);
                coordinates.add(gameBlockCoordinate);
            }
        }
        return coordinates;
    }

    /**
     * Getter for score
     *
     * @return score
     */
    public SimpleIntegerProperty getScore() {
        return score;
    }

    /**
     * Getter for level
     *
     * @return level
     */
    public SimpleIntegerProperty getLevel() {
        return level;
    }

    /**
     * Getter for lives
     *
     * @return lives
     */
    public SimpleIntegerProperty getLives() {
        return lives;
    }

    /**
     * Getter for multiplier
     *
     * @return multiplier
     */
    public SimpleIntegerProperty getMultiplier() {
        return multiplier;
    }

    /**
     * Getter for current piece
     *
     * @return current piece
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Getter for followingPiece
     *
     * @return followingPiece
     */
    public GamePiece getFollowingPiece() {
        return followingPiece;
    }

    /**
     * Getter for timer
     *
     * @return timer
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * Updates score and checks if the level
     * needs updating as well as the multiplier
     *
     * @param lines
     * @param blocks
     */
    public void score(int lines, int blocks) {
        var oldScore = score.getValue();
        score.setValue(score.getValue() + lines * blocks * 10 * multiplier.getValue());
        checkLevel();
        checkMultiplier(oldScore);
    }

    /**
     * Updates level based on score
     */
    protected void checkLevel() {
        logger.info("Checking Level");
        level.setValue(Math.floorDiv(getScore().getValue(), 1000));
    }

    /**
     * Checks the multiplier to see if it needs
     * updating
     *
     * @param oldScore
     */
    public void checkMultiplier(int oldScore) {
        if (score.getValue() != oldScore) {
            multiplier.setValue(getMultiplier().getValue() + 1);
            logger.info("Multiplier increased to: " + multiplier.getValue());
        } else {
            logger.info("Resetting Multiplier");
            multiplier.setValue(1);
        }
    }

    /**
     * Rotates the current piece
     *
     * @param amount
     */
    public void rotateCurrentPiece(int amount) {
        Multimedia.setAudioPlayer("rotate.mp3");
        currentPiece.rotate(amount);
    }

    /**
     * Swaps pieces
     */
    public void swapCurrentPiece() {
        Multimedia.setAudioPlayer("rotate.mp3");
        var prevCurrent = currentPiece;
        currentPiece = followingPiece;
        followingPiece = prevCurrent;

    }

    /**
     * Sets next piece listener
     *
     * @param listener
     */
    public void setNextPieceListener(NextPieceListener listener) {
        this.nextPieceListener = listener;
    }

    /**
     * Shows next pieces on the piece boards
     */
    public void nextPieceListen() {
        if (nextPieceListener != null) {
            nextPieceListener.nextPiece(currentPiece, followingPiece);
        }
    }

    /**
     * Sets line cleared listener
     *
     * @param listener
     */
    public void setLineClearedListener(LineClearedListener listener) {
        this.lineClearedListener = listener;
    }

    /**
     * Sends coordinates for the game blocks
     * to be animated
     *
     * @param coordinates
     */
    public void lineCleared(HashSet<GameBlockCoordinate> coordinates) {
        if (lineClearedListener != null) {
            lineClearedListener.lineCleared(coordinates);
        }
    }

    /**
     * Sets lives cleared listener
     *
     * @param listener
     */
    public void setLivesListener(LivesListener listener) {
        this.livesListener = listener;
    }

    /**
     * Checks to see if there are any lives left
     */
    public void livesListen() {
        if (livesListener != null) {
            livesListener.checkLives();
        }
    }

    /**
     * Sets game loop listener
     * @param listener
     */
    public void setGameLoopListener(GameLoopListener listener) {
        this.gameLoopListener = listener;
    }

    /**
     * Restarts the game loop for the time bar
     *
     * @param time
     */
    public void loopListen(int time) {
        if (gameLoopListener != null) {
            gameLoopListener.gameLoop(time);
        }
    }

    /**
     * Sets game timer
     */
    public void setTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> gameLoop());
            }
        };
        timer.schedule(timerTask, getTimerDelay());
    }

    /**
     * Sets the timers delay
     *
     * @return timer delay
     */
    public int getTimerDelay() {
        return Math.max(2500, (12000 - 500 * level.getValue()));
    }

    /**
     * Restarts the game loop, reduces the lives
     * and checks if there are any lives left
     */
    public void gameLoop() {
        logger.info("Restarting Timer");
        multiplier.setValue(1);
        lives.setValue(lives.get() - 1);
        livesListen();
        if (lives.get() >= 0) {
            Multimedia.setAudioPlayer("loselife.mp3");
            nextPiece();
            timer.cancel();
            timerTask.cancel();
            setTimer();
            loopListen(getTimerDelay());
        } else {
            logger.info("Closing Loop");
        }
    }
}

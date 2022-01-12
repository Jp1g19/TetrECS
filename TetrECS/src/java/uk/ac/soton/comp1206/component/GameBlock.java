package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The Visual User Interface component representing a single block in the grid.
 * <p>
 * Extends Canvas and is responsible for drawing itself.
 * <p>
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * <p>
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.rgb(245, 127, 159, 1),
            Color.rgb(247, 104, 104, 1),
            Color.rgb(250, 162, 90, 1),
            Color.rgb(250, 255, 112, 1),
            Color.rgb(207, 255, 112, 1),
            Color.rgb(179, 255, 112, 1),
            Color.rgb(179, 255, 112, 1),
            Color.rgb(59, 212, 77, 1),
            Color.rgb(46, 209, 193, 1),
            Color.rgb(71, 200, 255, 1),
            Color.rgb(133, 255, 235, 1),
            Color.rgb(0, 255, 213, 1),
            Color.rgb(79, 135, 255, 1),
            Color.rgb(175, 94, 255, 1),
            Color.rgb(180, 2, 245, 1)
    };

    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Used to determine if a circle should be drawn at
     * the center of the block
     */
    private boolean shouldDrawCircle = false;

    /**
     * Determines if a block can be highlighted
     */
    private boolean canHighlight = false;


    /**
     * Create a new single Game Block
     *
     * @param gameBoard the board this block belongs to
     * @param x         the column the block exists in
     * @param y         the row the block exists in
     * @param width     the width of the canvas to render
     * @param height    the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);

        //Highlights when mouse moves on block
        this.setOnMouseEntered(mouseEvent -> {
            if (canHighlight && !gameBoard.highlighted) {
                gameBoard.highlighted = true;
                var gc = getGraphicsContext2D();
                gc.setFill(Color.rgb(255, 255, 255, 0.2));
                gc.fillRect(0, 0, width, height);
            }
        });

        //Removes the highlight on mouse exit
        this.setOnMouseExited(mouseEvent -> {
            gameBoard.highlighted = false;
            paint();
        });
    }

    /**
     * When the value of this block is updated,
     *
     * @param observable what was updated
     * @param oldValue   the old value
     * @param newValue   the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if (value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);

        //Fill
        gc.setFill(Color.rgb(75, 0, 130, 0.4));
        gc.fillRect(0, 0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, width, height);
    }

    /**
     * Paint this canvas with the given colour
     *
     * @param colour the colour to paint
     */
    private void paintColor(Color colour) {
        var gc = getGraphicsContext2D();

        //Coordinates used to create the block's shape
        double[] x = {0, width, width/1.33 , width/4};
        double[] y = {0, 0, height/4, height/4};

        double[] x1 = {0, width, width/1.33, width/4 };
        double[] y1 = {height, height, height/1.33, height/1.33};

        double[] x2 = {0, 0, width/4, width/4 };
        double[] y2 = {0, height, height/1.33, height/4};

        double[] x3 = {width, width, width/1.33, width/1.33};
        double[] y3 = {0, height, height/1.33, height/4};

        //Clear
        gc.clearRect(0, 0, width, height);

        //Fill center rectangle
        gc.setFill(colour.saturate());
        gc.fillRect(width/4,height/4,width/2,height/2);
        gc.setStroke(Color.rgb(0, 0, 0, 0.3));
        gc.strokeRect(width/4,height/4,width/2,height/2);

        //Fill top polygon
        gc.setFill(colour.desaturate().brighter());
        gc.fillPolygon(x, y, 4);
        gc.setStroke(Color.rgb(0, 0, 0, 0.2));
        gc.strokePolygon(x, y, 4);

        //Fill bottom polygon
        gc.setFill(colour.darker().darker());
        gc.fillPolygon(x1, y1, 4);
        gc.setStroke(Color.rgb(0, 0, 0, 0.2));
        gc.strokePolygon(x1, y1, 4);

        //Fill left polygon
        gc.setFill(colour.brighter());
        gc.fillPolygon(x2, y2, 4);
        gc.setStroke(Color.rgb(0, 0, 0, 0.2));
        gc.strokePolygon(x2, y2, 4);

        //Fill right polygon
        gc.setFill(colour.darker());
        gc.fillPolygon(x3, y3, 4);
        gc.setStroke(Color.rgb(0, 0, 0, 0.2));
        gc.strokePolygon(x3, y3, 4);

        //Border
        gc.setStroke(Color.rgb(0, 0, 0, 0.2));
        gc.strokeRect(0, 0, width, height);

        //Checks if circle needs to be drawn
        if (shouldDrawCircle) {
            gc.setFill(Color.rgb(254, 254, 254, 0.6));
            gc.fillOval(width / 4, height / 4, width / 2, height / 2);
            gc.strokeOval(width / 4, height / 4, width / 2, height / 2);
        }
    }

    /**
     * Get the column of this block
     *
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     *
     * @return row number
     */
    public int getY() {
        return y;
    }


    /**
     * Get the current value held by this block, representing it's colour
     *
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Sets the circle highlight on a specific gameblock
     *
     */
    public void setShouldDrawCircle() {
        this.shouldDrawCircle = true;
    }

    /**
     * Sets highlight option for specific blocks
     * (Since instructions and piece boards shouldn't be highlighted)
     *
     */
    public void setCanHighlight() {
        this.canHighlight = true;
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     *
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    /**
     * Highlights the specific block
     * Used for keyboard movement
     */
    public void setHighlight() {
        logger.info("Highlighting Keyboard Movement");
        var gc = getGraphicsContext2D();
        gc.setFill(Color.rgb(255, 255, 255, 0.2));
        gc.fillRect(0, 0, width, height);
    }

    /**
     * Animation for line cleared
     */
    public void fadeOut() {
        logger.info("Fading Line");
        var gc = getGraphicsContext2D();

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200));
        fadeTransition.setFromValue(gc.getCanvas().getOpacity());
        fadeTransition.setToValue(0);
        fadeTransition.setCycleCount(2);
        fadeTransition.setAutoReverse(true);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200));
        scaleTransition.setToX(2);
        scaleTransition.setToY(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);

        ParallelTransition parallelTransition = new ParallelTransition(gc.getCanvas(), scaleTransition, fadeTransition);
        parallelTransition.play();
    }

}

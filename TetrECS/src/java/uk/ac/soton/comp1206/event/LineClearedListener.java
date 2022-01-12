package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.HashSet;

/**
 * LineClearedListener is used to check if a lines is cleared
 * in order to animate the full lines
 */
public interface LineClearedListener {

    /**
     * Provides the coordinates of the full lines to animate
     * @param coordinates
     */
    public void lineCleared(HashSet<GameBlockCoordinate> coordinates);
}

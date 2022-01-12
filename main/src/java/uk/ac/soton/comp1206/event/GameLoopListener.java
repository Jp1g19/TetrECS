package uk.ac.soton.comp1206.event;

/**
 * GameLoopListener is used to loop the time bar
 */
public interface GameLoopListener {

    /**
     * Resets the time bar loop
     *
     * @param time
     */
    public void gameLoop(int time);
}

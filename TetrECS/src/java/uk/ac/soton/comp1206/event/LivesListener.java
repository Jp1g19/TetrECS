package uk.ac.soton.comp1206.event;

/**
 * LivesListener is used to check if there are lives left and
 * act accordingly
 */
public interface LivesListener {

    /**
     * Checks how many lives are left
     */
    public void checkLives();
}

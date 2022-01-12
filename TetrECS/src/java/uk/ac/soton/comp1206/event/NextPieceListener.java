package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * NextPieceListener is used to provide the next piece to the piece board
 * to display
 */
public interface NextPieceListener {

    /**
     * Retrieves the next piece and displays
     *
     * @param nextPiece
     * @param followingPiece
     */
    public void nextPiece(GamePiece nextPiece, GamePiece followingPiece);
}

package GameLogic;

/**
 * Created by frans on 5-10-2015.
 */
public class Player {
    public final PieceColor pieceColor;
    public int piecesLeft = 18;         // Default for standard and tournament games
    public boolean hasPlacedNormalPieces = false;
    public boolean isPlacingGipfPieces = true;

    public Player(PieceColor pieceColor, int nrOfPieces, boolean isAllowedToPlaceGipfPieces) {
        this.pieceColor = pieceColor;
        this.piecesLeft = nrOfPieces;
        this.isPlacingGipfPieces = isAllowedToPlaceGipfPieces;
        this.hasPlacedNormalPieces = !isAllowedToPlaceGipfPieces;
    }

    public void toggleIsPlacingGipfPieces() {
        if (hasPlacedNormalPieces) {
            isPlacingGipfPieces = false;
        } else {
            isPlacingGipfPieces = !isPlacingGipfPieces;
        }
    }

    public boolean getIsPlacingGipfPieces() {
        return isPlacingGipfPieces;
    }
}

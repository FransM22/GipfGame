package GameLogic;

/**
 * Created by frans on 5-10-2015.
 */
public class Player {
    public final PieceColor pieceColor;
    public int reserve = 18;         // Default for standard and tournament games
    public boolean hasPlacedNormalPieces = false;
    public boolean isPlacingGipfPieces = true;
    public boolean hasPlacedGipfPieces = false;

    public Player(PieceColor pieceColor, int nrOfPieces, boolean isAllowedToPlaceGipfPieces) {
        this.pieceColor = pieceColor;
        this.reserve = nrOfPieces;
        this.isPlacingGipfPieces = isAllowedToPlaceGipfPieces;
        this.hasPlacedNormalPieces = !isAllowedToPlaceGipfPieces;
    }

    public void toggleIsPlacingGipfPieces() {
        isPlacingGipfPieces = !hasPlacedNormalPieces && !isPlacingGipfPieces;
    }

    public boolean getIsPlacingGipfPieces() {
        return isPlacingGipfPieces;
    }
}

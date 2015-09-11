package GameLogic;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by frans on 9-9-2015.
 */
public class Move {
    GipfBoard.Piece addedPiece;             // The piece that is added to the board
    GipfBoard.Position startPos;            // A newly added piece moves from the startPos to the endPos
    GipfBoard.Position endPos;
    Set<GipfBoard.Position> removedPiecePositions;  // Pieces that are removed during this move

    /**
     * Constructor, creates a Move with the following properties
     * @param addedPiece the piece that is added to the board
     * @param startPos the position where the piece is added
     * @param endPos the position to where the newly added piece is moved
     * @param removedPiecePositions the pieces that are removed
     */
    public Move(GipfBoard.Piece addedPiece,
                GipfBoard.Position startPos,
                GipfBoard.Position endPos,
                Set<GipfBoard.Position> removedPiecePositions) {
        this.addedPiece = addedPiece;
        this.startPos = startPos;
        this.endPos = endPos;
        this.removedPiecePositions = removedPiecePositions;
    }

    /**
     * Constructor, creates a Move with the following properties. This constructor only uses the required fields.
     * @param addedPiece the piece that is added to the board
     * @param startPos the position where the piece is added
     * @param endPos the position to where the newly added piece is moved
     */
    public Move(GipfBoard.Piece addedPiece,
                GipfBoard.Position startPos,
                GipfBoard.Position endPos) {
        this.addedPiece = addedPiece;
        this.startPos = startPos;
        this.endPos = endPos;
        this.removedPiecePositions = new HashSet<>();   // An empty set
    }
}

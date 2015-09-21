package GameLogic;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores all information related to a move operation. Can be used to track back to a previous state on the board, or to
 * determine which possible moves there are from a given board state.
 *
 * Created by frans on 9-9-2015.
 */
public class Move {
    GipfBoard.Piece addedPiece;             // The piece that is added to the board
    Position startPos;                      // A newly added piece moves from the startPos to the endPos
    Direction direction;                    // The direction in which the piece moves
    Set<Position> removedPiecePositions;    // Pieces that are removed during this move

    /**
     * Constructor, creates a Move with the following properties
     *
     * @param addedPiece            the piece that is added to the board
     * @param startPos              the position where the piece is added
     * @param direction             the direction in which the newly added piece is moved
     * @param removedPiecePositions the pieces that are removed
     */
    public Move(GipfBoard.Piece addedPiece,
                Position startPos,
                Direction direction,
                Set<Position> removedPiecePositions) {
        this.addedPiece = addedPiece;
        this.startPos = startPos;
        this.direction = direction;
        this.removedPiecePositions = removedPiecePositions;
    }

    /**
     * Constructor, creates a Move with the following properties. This constructor only uses the required fields.
     *
     * @param addedPiece the piece that is added to the board
     * @param startPos   the position where the piece is added
     * @param direction  the direction in which the piece moves
     */
    public Move(GipfBoard.Piece addedPiece,
                Position startPos,
                Direction direction) {
        this.addedPiece = addedPiece;
        this.startPos = startPos;
        this.direction = direction;
        this.removedPiecePositions = new HashSet<>();   // An empty set
    }

    public enum Direction {
        NORTH,
        NORTH_EAST,
        NORTH_WEST,
        SOUTH,
        SOUTH_EAST,
        SOUTH_WEST
    }
}

package GameLogic;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Stores all information related to a move operation. Can be used to track back to a previous state on the board, or to
 * determine which possible moves there are from a given board state.
 * <p/>
 * Created by frans on 9-9-2015.
 */
public class Move {
    final Game.Piece addedPiece;             // The piece that is added to the board
    final Position startPos;                      // A newly added piece moves from the startPos to the endPos
    final Direction direction;                    // The direction in which the piece moves
    Set<Position> removedPiecePositions;    // Pieces that are removed during this move

    /**
     * Constructor, creates a Move with the following properties
     *
     * @param addedPiece            the piece that is added to the board
     * @param startPos              the position where the piece is added
     * @param direction             the direction in which the newly added piece is moved
     * @param removedPiecePositions the pieces that are removed
     */
    public Move(Game.Piece addedPiece,
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
    public Move(Game.Piece addedPiece,
                Position startPos,
                Direction direction) {
        this.addedPiece = addedPiece;
        this.startPos = startPos;
        this.direction = direction;
        this.removedPiecePositions = new HashSet<>();   // An empty set
    }

    @Override
    public String toString() {
        Position toPos = new Position(startPos.getPosId() + direction.getDeltaPos());

        return "" + Game.getPieceColor(addedPiece) + ": "
                + (Game.getPieceType(addedPiece) == Game.PieceType.GIPF ? "G" : "")
                + startPos.getName() +
                "-" + toPos.getName() +
                ", removed=" + removedPiecePositions.stream().map(Position::getName).collect(toSet()) +
                '}';
    }

    public Position getStartingPosition() { return startPos; }
    public Direction getDirection() { return direction; }
}

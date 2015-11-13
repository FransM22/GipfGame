package GameLogic;

import java.util.Optional;
import java.util.Set;

/**
 * Stores all information related to a move operation. Can be used to track back to a previous state on the board, or to
 * determine which possible moves there are from a given board state.
 * <p/>
 * Created by frans on 9-9-2015.
 */
public class Move implements Comparable<Move>{
    public final Piece addedPiece;             // The piece that is added to the board
    public final Position startPos;                      // A newly added piece moves from the startPos to the endPos
    public final Direction direction;                    // The direction in which the piece moves
    public Optional<Set<Position>> piecesToWhite;
    public Optional<Set<Position>> piecesToBlack;
    public Optional<Set<Position>> piecesRemoved;    // Pieces that are removed during this move

    /**
     * Constructor, creates a Move with the following properties
     *
     * @param addedPiece            the piece that is added to the board
     * @param startPos              the position where the piece is added
     * @param direction             the direction in which the newly added piece is moved
     * @param piecesRemoved the pieces that are removed
     */
    public Move(Piece addedPiece,
                Position startPos,
                Direction direction,
                Optional<Set<Position>> piecesToWhite,
                Optional<Set<Position>> piecesToBlack,
                Optional<Set<Position>> piecesRemoved) {
        this.addedPiece = addedPiece;
        this.startPos = startPos;
        this.direction = direction;
        this.piecesToWhite = piecesToWhite;
        this.piecesToBlack = piecesToBlack;
        this.piecesRemoved = piecesRemoved;
    }

    public Move(Move otherMove) {
        this.addedPiece = otherMove.addedPiece;
        this.startPos = otherMove.startPos;
        this.direction = otherMove.direction;
        this.piecesToWhite = otherMove.piecesToWhite;
        this.piecesToBlack = otherMove.piecesToBlack;
        this.piecesRemoved = otherMove.piecesRemoved;
    }

    @Override
    public String toString() {
        Position toPos = new Position(startPos.getPosId() + direction.getDeltaPos());

        String returnString =  "" + addedPiece.getPieceColor() + ": "
                + (addedPiece.getPieceType() == PieceType.GIPF ? "G" : "")
                + startPos.getName() +
                "-" + toPos.getName();

        if (piecesToWhite.isPresent()) returnString += " white: " + piecesToWhite.get();
        if (piecesToBlack.isPresent()) returnString += " white: " + piecesToBlack.get();
        if (piecesRemoved.isPresent()) returnString += " white: " + piecesRemoved.get();

        return returnString;
    }

    public Position getStartingPosition() { return startPos; }
    public Direction getDirection() { return direction; }

    @Override
    public int compareTo(Move o) {
        return this.startPos.getPosId() - o.startPos.getPosId();
    }
}

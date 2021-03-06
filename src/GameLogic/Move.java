package GameLogic;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores all information related to a move operation. Can be used to track back to a previous state on the board, or to
 * determine which possible moves there are from a given board state.
 * <p/>
 * Created by frans on 9-9-2015.
 */
public class Move implements Comparable<Move> {
    public final Piece addedPiece;             // The piece that is added to the board
    public final Position startPos;                      // A newly added piece moves from the startPos to the endPos
    public final Direction direction;                    // The direction in which the piece moves
    public boolean isCompleteMove;                      // Contains all information about the move (don't have to ask the player whether he wants to remove a line / gipf piece)
    public Set<Position> piecesToWhite;
    public Set<Position> piecesToBlack;
    public Set<Position> piecesRemoved;    // Pieces that are removed during this move

    /**
     * Constructor, creates a Move with the following properties
     *
     * @param addedPiece    the piece that is added to the board
     * @param startPos      the position where the piece is added
     * @param direction     the direction in which the newly added piece is moved
     * @param piecesRemoved the pieces that are removed
     */
    public Move(Piece addedPiece,
                Position startPos,
                Direction direction,
                Set<Position> piecesToWhite,
                Set<Position> piecesToBlack,
                Set<Position> piecesRemoved) {
        this.addedPiece = addedPiece;
        this.startPos = startPos;
        this.direction = direction;
        this.isCompleteMove = true;
        this.piecesToWhite = piecesToWhite;
        this.piecesToBlack = piecesToBlack;
        this.piecesRemoved = piecesRemoved;
    }

    /**
     * Constructor, creates a Move with the following properties
     *
     * @param addedPiece the piece that is added to the board
     * @param startPos   the position where the piece is added
     * @param direction  the direction in which the newly added piece is moved
     */
    public Move(Piece addedPiece,
                Position startPos,
                Direction direction) {
        this.addedPiece = addedPiece;
        this.startPos = startPos;
        this.direction = direction;
        this.isCompleteMove = false;
        this.piecesToWhite = new HashSet<>();
        this.piecesToBlack = new HashSet<>();
        this.piecesRemoved = new HashSet<>();
    }

    @Override
    public String toString() {
        Position toPos = new Position(startPos.getPosId() + direction.getDeltaPos());

        String returnString = "" + addedPiece.getPieceColor() + ": "
                + (addedPiece.getPieceType() == PieceType.GIPF ? "G" : "")
                + startPos.getName() +
                "-" + toPos.getName();

        if (isCompleteMove) {
            returnString += " white: " + piecesToWhite
                    + " black: " + piecesToBlack
                    + " removed: " + piecesRemoved;
        }

        return returnString;
    }

    public Position getStartingPosition() {
        return startPos;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public int compareTo(Move o) {
        int comparison1 = this.startPos.getPosId() - o.startPos.getPosId();
        if (comparison1 != 0) return comparison1;

        // Need a second comparator, because TreeMap cannot distinguish between two objects for which the compareTo method returns 0
        return hashCode() - o.hashCode();
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Move)) return false;
//
//        Move move = (Move) o;
//
//        if (isCompleteMove != move.isCompleteMove) return false;
//        if (addedPiece != move.addedPiece) return false;
//        if (!startPos.equals(move.startPos)) return false;
//        if (direction != move.direction) return false;
//        if (!piecesToWhite.equals(move.piecesToWhite)) return false;
//        if (!piecesToBlack.equals(move.piecesToBlack)) return false;
//        return piecesRemoved.equals(move.piecesRemoved);
//
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;

        Move move = (Move) o;

        if (isCompleteMove != move.isCompleteMove) return false;
        if (addedPiece != move.addedPiece) return false;
        if (startPos != null ? !startPos.equals(move.startPos) : move.startPos != null) return false;
        if (direction != move.direction) return false;
        if (piecesToWhite != null ? !piecesToWhite.equals(move.piecesToWhite) : move.piecesToWhite != null)
            return false;
        if (piecesToBlack != null ? !piecesToBlack.equals(move.piecesToBlack) : move.piecesToBlack != null)
            return false;
        return !(piecesRemoved != null ? !piecesRemoved.equals(move.piecesRemoved) : move.piecesRemoved != null);

    }

    @Override
    public int hashCode() {
        int result = addedPiece != null ? addedPiece.hashCode() : 0;
        result = 31 * result + (startPos != null ? startPos.hashCode() : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (isCompleteMove ? 1 : 0);
        result = 31 * result + (piecesToWhite != null ? piecesToWhite.hashCode() : 0);
        result = 31 * result + (piecesToBlack != null ? piecesToBlack.hashCode() : 0);
        result = 31 * result + (piecesRemoved != null ? piecesRemoved.hashCode() : 0);
        return result;
    }
}

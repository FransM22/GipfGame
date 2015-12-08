package AI;

import GameLogic.*;
import GameLogic.Game.BasicGame;
import GameLogic.Game.Game;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by frans on 8-12-2015.
 */
public class AssignRingValue implements Function<GipfBoardState, Long> {
    @Override
    public Long apply(GipfBoardState gipfBoardState) {
        long value = 0;

        for (Map.Entry<Position, Piece> pieceEntry : gipfBoardState.getPieceMap().entrySet()) {
            Position position = pieceEntry.getKey();
            Piece piece = pieceEntry.getValue();

            value += getColorValue(piece) * getRingValueForPosition(position);
        }

        if (gipfBoardState.players.current().pieceColor == PieceColor.WHITE) {
            return value;
        } else
            return -value;
    }

    int getRingValueForPosition(Position p) {
        Game temporaryGame = new BasicGame();
        if (new Line.Segment(temporaryGame, new Position('b', 2), new Position('b', 5), Direction.NORTH).getAllPositions().contains(p) ||
                new Line.Segment(temporaryGame, new Position('b', 5), new Position('e', 8), Direction.NORTH_EAST).getAllPositions().contains(p) ||
                new Line.Segment(temporaryGame, new Position('e', 8), new Position('h', 5), Direction.SOUTH_EAST).getAllPositions().contains(p) ||
                new Line.Segment(temporaryGame, new Position('h', 5), new Position('h', 2), Direction.SOUTH).getAllPositions().contains(p) ||
                new Line.Segment(temporaryGame, new Position('h', 2), new Position('e', 2), Direction.SOUTH_WEST).getAllPositions().contains(p) ||
                new Line.Segment(temporaryGame, new Position('e', 2), new Position('b', 2), Direction.NORTH_WEST).getAllPositions().contains(p)) {
            return 1;
        }

        if (new Line.Segment(temporaryGame, new Position('c', 3), new Position('c', 5), Direction.NORTH).getAllPositions().contains(p) ||
                new Line.Segment(temporaryGame, new Position('c', 5), new Position('e', 7), Direction.NORTH_EAST).getAllPositions().contains(p) ||
                new Line.Segment(temporaryGame, new Position('e', 7), new Position('g', 5), Direction.SOUTH_EAST).getAllPositions().contains(p) ||
                new Line.Segment(temporaryGame, new Position('g', 5), new Position('g', 3), Direction.SOUTH).getAllPositions().contains(p) ||
                new Line.Segment(temporaryGame, new Position('g', 3), new Position('e', 3), Direction.SOUTH_WEST).getAllPositions().contains(p) ||
                new Line.Segment(temporaryGame, new Position('e', 3), new Position('c', 3), Direction.NORTH_WEST).getAllPositions().contains(p)) {
            return 2;
        }

        if (p.equals(new Position('d', 4)) ||
                p.equals(new Position('d', 5)) ||
                p.equals(new Position('e', 6)) ||
                p.equals(new Position('f', 5)) ||
                p.equals(new Position('f', 4)) ||
                p.equals(new Position('e', 4))) {
            return 3;
        }

        if (p.equals(new Position('e', 5))) {
            return 4;
        }

        return 0;   // Won't happen
    }

    int getColorValue(Piece p) {
        if (p.getPieceColor() == PieceColor.WHITE) {
            return 1;
        }
        return -1;
    }
}

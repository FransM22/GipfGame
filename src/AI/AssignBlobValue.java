package AI;

import GameLogic.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;
import static java.util.stream.Collectors.toSet;

/**
 * Created by frans on 8-12-2015.
 */
public class AssignBlobValue implements Function<GipfBoardState, Long> {
    @Override
    public Long apply(GipfBoardState gipfBoardState) {
        Map<Position, Piece> pieceMap = gipfBoardState.getPieceMap();

        long value = 0;
        for (Map.Entry<Position, Piece> pieceEntry : pieceMap.entrySet()) {
            Position position = pieceEntry.getKey();
            Piece piece = pieceEntry.getValue();

            // Only consider the white player's pieces for subtraction
            if (piece.getPieceColor() == WHITE) {
                value -= neighborsOf(position).stream()
                        .filter(pieceMap::containsKey)
                        .filter(neighborPosition -> pieceMap.get(neighborPosition).getPieceColor() == WHITE)
                        .count();
            }
            // Else, add the values
            else {
                value += neighborsOf(position).stream()
                        .filter(pieceMap::containsKey)
                        .filter(neighborPosition -> pieceMap.get(neighborPosition).getPieceColor() == BLACK)
                        .count();
            }


            // We don't want to empty the reserve, so we add it, combined with a high coefficient
            long whitePiecesInReserve = gipfBoardState.players.white.reserve;
            long blackPiecesInReserve = gipfBoardState.players.black.reserve;

//            value -= whitePiecesInReserve * 50;
//            value += blackPiecesInReserve * 50;

            // Include the pieces lost / gained
            long whitePiecesOnTheBoard = gipfBoardState.getPieceMap().values().stream().filter(p -> p.getPieceColor() == WHITE).count();
            long blackPiecesOnTheBoard = gipfBoardState.getPieceMap().values().stream().filter(p -> p.getPieceColor() == BLACK).count();

            long whitePiecesLost = 15 - whitePiecesOnTheBoard - whitePiecesInReserve;
            long blackPiecesLost = 15 - blackPiecesOnTheBoard - blackPiecesInReserve;

            value += 100 * blackPiecesLost;
            value -= 100 * whitePiecesLost;

            if (gipfBoardState.players.winner() != null) {
                if (gipfBoardState.players.winner().pieceColor == WHITE) value -= 100_000;
                if (gipfBoardState.players.winner().pieceColor == BLACK) value += 100_000;
            }
        }

        return value;
    }

    private Set<Position> neighborsOf(Position p) {
        return Arrays.asList(
                new Position(p.posId - 1),
                new Position(p.posId - 10),
                new Position(p.posId - 11),
                new Position(p.posId + 1),
                new Position(p.posId + 10),
                new Position(p.posId + 11)
                ).stream().collect(toSet());
    }
}

package AI;

import GameLogic.GipfBoardState;
import GameLogic.Piece;
import GameLogic.PlayersInGame;
import GameLogic.Position;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

/**
 * Created by frans on 8-12-2015.
 */
public class AssignBlobValue implements Function<GipfBoardState, Long> {
    @Override
    public Long apply(GipfBoardState gipfBoardState) {
        Map<Position, Piece> pieceMap = gipfBoardState.getPieceMap();
        PlayersInGame players = gipfBoardState.players;

        long value = 0;
        for (Map.Entry<Position, Piece> pieceEntry : pieceMap.entrySet()) {
            Position position = pieceEntry.getKey();
            Piece piece = pieceEntry.getValue();

            // Only consider the current player's pieces for addition
            if (piece.getPieceColor() == gipfBoardState.players.current().pieceColor) {
                value += neighborsOf(position).stream()
                        .filter(neighborPosition -> pieceMap.containsKey(neighborPosition))
                        .filter(neighborPosition -> pieceMap.get(neighborPosition).getPieceColor() == gipfBoardState.players.current().pieceColor)
                        .count();
            }
            // Else, subtract the values
            else {
                value -= neighborsOf(position).stream()
                        .filter(neighborPosition -> pieceMap.containsKey(neighborPosition))
                        .filter(neighborPosition -> pieceMap.get(neighborPosition).getPieceColor() != gipfBoardState.players.current().pieceColor)
                        .count();
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

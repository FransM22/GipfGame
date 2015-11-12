package GameLogic;

import java.io.Serializable;
import java.util.HashMap;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

/**
 * Created by frans on 11-10-2015.
 */
public class PlayersInGame extends HashMap<PieceColor, PlayersInGame.Player> implements Serializable {
    // These two Players are pointers to the winning player and the current player
    private Player winningPlayer = null;
    private Player currentPlayer = null;

    public PlayersInGame() {
        super();

        put(PieceColor.WHITE, new Player(PieceColor.WHITE));
        put(PieceColor.BLACK, new Player(PieceColor.BLACK));
    }

    public PlayersInGame(PlayersInGame other) {
        super(other);

        other.entrySet().stream()
                .forEach(otherEntry -> put(otherEntry.getKey(), new Player(otherEntry.getValue())));

        this.currentPlayer = other.currentPlayer == other.get(WHITE) ? get(WHITE) : get(BLACK);
        this.winningPlayer = null;
    }

    public void setStartingPlayer(Player startingPlayer) {
        currentPlayer = startingPlayer;
    }

    public void updateCurrent() {
        currentPlayer = ((currentPlayer == get(WHITE)) ? get(BLACK) : get(WHITE));
    }

    public void setCurrent(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player current() {
        return currentPlayer;
    }

    public Player winner() {
        return winningPlayer;
    }

    public void makeCurrentPlayerWinner() {
        this.winningPlayer = current();
    }

    /**
     * Created by frans on 5-10-2015.
     */
    public static class Player implements Serializable {
        public final PieceColor pieceColor;
        public int reserve = 18;         // Default for standard and tournament games
        public boolean hasPlacedNormalPieces = false;
        public boolean isPlacingGipfPieces = true;
        public boolean hasPlacedGipfPieces = false;
        public boolean mustStartWithGipfPieces = false;
        public Player(PieceColor pieceColor) {
            this.pieceColor = pieceColor;
        }

        public Player(Player other) {
            this.pieceColor = other.pieceColor;
            this.reserve = other.reserve;
            this.hasPlacedGipfPieces = other.hasPlacedGipfPieces;
            this.isPlacingGipfPieces = other.isPlacingGipfPieces;
            this.hasPlacedNormalPieces = other.hasPlacedNormalPieces;
            this.mustStartWithGipfPieces = other.mustStartWithGipfPieces;
        }

        public void setMustStartWithGipfPieces(boolean mustStartWithGipfPieces) {
            this.mustStartWithGipfPieces = mustStartWithGipfPieces;
        }

        public void setHasPlacedNormalPieces(boolean hasPlacedNormalPieces) {
            this.hasPlacedNormalPieces = hasPlacedNormalPieces;
        }

        public void setHasPlacedGipfPieces(boolean hasPlacedGipfPieces) {
            this.hasPlacedGipfPieces = hasPlacedGipfPieces;
        }

        public void toggleIsPlacingGipfPieces() {
            if (mustStartWithGipfPieces && !hasPlacedGipfPieces) {
                isPlacingGipfPieces = true;
            } else {
                isPlacingGipfPieces = !hasPlacedNormalPieces && !isPlacingGipfPieces;
            }
        }

        public boolean getIsPlacingGipfPieces() {
            return isPlacingGipfPieces;
        }

        public void setIsPlacingGipfPieces(boolean isPlacingGipfPieces) {
            this.isPlacingGipfPieces = isPlacingGipfPieces;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayersInGame)) return false;
        if (!(this.keySet().size() == ((PlayersInGame) o).keySet().size() && this.values().containsAll(((PlayersInGame) o).values()))) return false;

        PlayersInGame that = (PlayersInGame) o;

        if (winningPlayer != null ? !winningPlayer.equals(that.winningPlayer) : that.winningPlayer != null)
            return false;
        return !(currentPlayer != null ? !currentPlayer.equals(that.currentPlayer) : that.currentPlayer != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (winningPlayer != null ? winningPlayer.hashCode() : 0);
        result = 31 * result + (currentPlayer != null ? currentPlayer.hashCode() : 0);
        return result;
    }
}

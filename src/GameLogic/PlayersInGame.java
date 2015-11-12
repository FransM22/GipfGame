package GameLogic;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static GameLogic.PieceColor.WHITE;

/**
 * Created by frans on 11-10-2015.
 */
public class PlayersInGame implements Serializable, Iterable<PlayersInGame.Player> {
    // These two Players are pointers to the winning player and the current player
    private Player winningPlayer = null;
    private Player currentPlayer = null;
    public Player white = new Player(PieceColor.WHITE);
    private Player black = new Player(PieceColor.BLACK);

    public PlayersInGame() {
    }

    public Player get(PieceColor pieceColor) {
        if (pieceColor == WHITE) { return white; }
        else return black;
    }

    public PlayersInGame(PlayersInGame other) {
        this.white = new Player(other.white);
        this.black = new Player(other.black);

        this.currentPlayer = other.currentPlayer == other.white ? white : black;
        this.winningPlayer = null;
    }

    public void setStartingPlayer(Player startingPlayer) {
        currentPlayer = startingPlayer;
    }

    public void updateCurrent() {
        currentPlayer = ((currentPlayer == white) ? black : white);
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

    @Override
    public Iterator<Player> iterator() {
        return new PlayerIterator();
    }

    private class PlayerIterator implements Iterator<Player>{
        private Player cursor = null;

        @Override
        public boolean hasNext() {
            if (cursor == null || cursor == white) return true;
            return false;
        }

        @Override
        public Player next() {
            if (cursor == black) throw new NoSuchElementException();

            if (cursor == white) cursor = black;
            else if (cursor == null) cursor = white;

            return cursor;
        }
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

        PlayersInGame players = (PlayersInGame) o;

        if (winningPlayer != null ? !winningPlayer.equals(players.winningPlayer) : players.winningPlayer != null)
            return false;
        if (currentPlayer != null ? !currentPlayer.equals(players.currentPlayer) : players.currentPlayer != null)
            return false;
        if (!white.equals(players.white)) return false;
        return black.equals(players.black);

    }

    @Override
    public int hashCode() {
        int result = winningPlayer != null ? winningPlayer.hashCode() : 0;
        result = 31 * result + (currentPlayer != null ? currentPlayer.hashCode() : 0);
        result = 31 * result + white.hashCode();
        result = 31 * result + black.hashCode();
        return result;
    }
}

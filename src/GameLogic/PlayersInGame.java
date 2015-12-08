package GameLogic;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static GameLogic.PieceColor.BLACK;
import static GameLogic.PieceColor.WHITE;

/**
 * Created by frans on 11-10-2015.
 */
public class PlayersInGame implements Serializable, Iterable<PlayersInGame.Player> {
    public Player white = new Player(WHITE);
    public Player black = new Player(BLACK);
    // The following two Players are pointers to the winning player and the current player
    private final Player winningPlayer;
    private final Player currentPlayer;

    public PlayersInGame() {
        currentPlayer = white;
        winningPlayer = null;
    }

    public PlayersInGame(PlayersInGame oldPlayers) {
        this.white = new Player(oldPlayers.white);
        this.black = new Player(oldPlayers.black);

        if (oldPlayers.current().pieceColor == WHITE) {
            this.currentPlayer = this.white;
        }
        else {
            this.currentPlayer = this.black;
        }

        if (oldPlayers.winner() == null) {
            this.winningPlayer = null;
        }
        else if (oldPlayers.winner().pieceColor == WHITE) {
            this.winningPlayer = this.white;
        }
        else {
            this.winningPlayer = this.black;
        }
    }

    public PlayersInGame(PlayersInGame oldPlayers, PieceColor newCurrentPlayer, PieceColor winningPlayer) {
        this.white = new Player(oldPlayers.white);
        this.black = new Player(oldPlayers.black);

        // currentPlayer can't be null
        currentPlayer = (newCurrentPlayer == WHITE) ? this.white : this.black;

        if (winningPlayer == null) this.winningPlayer = null;
        else this.winningPlayer = (winningPlayer == WHITE) ? this.white : this.black;
    }

    public Player get(PieceColor pieceColor) {
        if (pieceColor == WHITE) {
            return white;
        } else return black;
    }

    public PlayersInGame updateCurrent() {
        PieceColor newCurrentPlayer = ((this.currentPlayer == white) ? BLACK : WHITE);
        return new PlayersInGame(this, newCurrentPlayer, null);
    }

    public Player current() {
        return currentPlayer;
    }

    public Player winner() {
        return winningPlayer;
    }

    public PlayersInGame makeCurrentPlayerWinner() {
        return new PlayersInGame(this, this.currentPlayer.pieceColor, this.currentPlayer.pieceColor);
    }

    @Override
    public Iterator<Player> iterator() {
        return new PlayerIterator();
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

    private class PlayerIterator implements Iterator<Player> {
        private Player cursor = null;

        @Override
        public boolean hasNext() {
            return cursor == null || cursor == white;
        }

        @Override
        public Player next() {
            if (cursor == black) throw new NoSuchElementException();

            if (cursor == white) cursor = black;
            else if (cursor == null) cursor = white;

            return cursor;
        }
    }
}

package GameLogic;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Is still room for optimization, but should be done only if this code seems
 * to be a bottleneck.
 * <p/>
 * Created by frans on 21-9-2015.
 */
public class Game {
    public LinkedList<String> debugMessages;
    public Player whitePlayer;
    public Player blackPlayer;
    Player currentPlayer;
    Player winningPlayer;
    public boolean isGameOver = false;
    private GipfBoard gipfBoard;

    public Game() {
        gipfBoard = new GipfBoard();
        whitePlayer = new Player(PieceColor.WHITE);
        blackPlayer = new Player(PieceColor.BLACK);

        currentPlayer = whitePlayer;
        debugMessages = new LinkedList<>();
    }

    public static PieceColor getPieceColor(Piece piece) {
        switch (piece) {
            case WHITE_SINGLE:
            case WHITE_GIPF:
                return PieceColor.WHITE;
        }
        return PieceColor.BLACK;
    }

    public static PieceType getPieceType(Piece piece) {
        switch (piece) {
            case WHITE_SINGLE:
            case BLACK_SINGLE:
                return PieceType.NORMAL;
        }
        return PieceType.GIPF;
    }

    /**
     * Checks whether the position is located on the board
     *
     * @param p the position of which should be determined whether it is empty
     */
    public boolean isPositionOnBoard(Position p) {
        int col = p.getColName() - 'a' + 1;
        int row = p.getRowNumber();

        // See google doc for explanation of the formula
        return !(row <= 0 ||
                col >= 10 ||
                row + col >= 15 ||
                col - row <= -5 ||
                col <= 0
        );
    }

    /**
     * By Leroy
     *
     * @param p
     * @return
     */
    //This method check valid Position
    public boolean isValidPosition(Position p) {
        int col = p.getColName() - 'a' + 1;
        int row = p.getRowNumber();

        // See google doc for explanation of the formula
        return !(row <= 1 ||
                col >= 9 ||
                row + col >= 14 ||
                col - row <= -4 ||
                col <= 1
        );
    }

    private boolean isPositionEmpty(Position p) {
        return !gipfBoard.getPieceMap().containsKey(p);
    }

    private void movePiece(Position currentPosition, int deltaPos) throws Exception {
        Position nextPosition = new Position(currentPosition.posId + deltaPos);

        if (!isValidPosition(nextPosition)) {
            throw new InvalidMoveException();
        } else {
            try {
                if (!isPositionEmpty(nextPosition)) {
                    movePiece(nextPosition, deltaPos);
                }

                gipfBoard.getPieceMap().put(nextPosition, gipfBoard.getPieceMap().remove(currentPosition));
            } catch (InvalidMoveException e) {
                debugOutput("Moving to " + nextPosition.getName() + " is not allowed");
                throw new InvalidMoveException();
            }
        }
    }

    private void movePiecesTowards(Position startPos, Direction direction) throws InvalidMoveException {
        int deltaPos = direction.getDeltaPos();

        Position currentPosition = new Position(startPos);

        try {
            movePiece(currentPosition, deltaPos);
        } catch (Exception e) {
            throw new InvalidMoveException();
        }
    }

    /**
     * applyMove applies the given move to the board.
     * First, the new piece is added to the startPos
     * Then the pieces are moved in the direction of the move,
     * and finally pieces that need to be removed are removed from the board
     *
     * @param move the move that is applied
     */
    public void applyMove(Move move) {
        if (currentPlayer.piecesLeft >= 1) {
            // Add the piece to the new pieces
            setPiece(move.startPos, move.addedPiece);

            try {
                movePiecesTowards(move.startPos, move.direction);

                // Remove the pieces that need to be removed
                // Java 8 solution (performs the remove operation on each of the pieces that should be removed)
                move.removedPiecePositions.forEach(gipfBoard.getPieceMap()::remove);

                // TODO Add pieces retrieved by removing pieces here

                try {
                    detectFourPieces();
                } catch (ArrayIndexOutOfBoundsException e) {
                    debugOutput("Caught an ArrayIndexOutOfBoundsException");
                    e.printStackTrace();
                }

                debugOutput(move.toString());
                currentPlayer.piecesLeft--;

                if (currentPlayer.piecesLeft == 0) {
                    updateCurrentPlayer();
                    isGameOver = true;
                    winningPlayer = currentPlayer;

                    debugOutput("Game over! " + winningPlayer.pieceColor + " won!");
                }

                else {
                    updateCurrentPlayer();
                }

            } catch (InvalidMoveException e) {
                gipfBoard.getPieceMap().remove(move.startPos);
                System.out.println("Move not applied");
            }
        } else {
            debugOutput("No pieces left");
        }
    }

    public void setPiece(Position pos, Game.Piece piece) {
        gipfBoard.getPieceMap().put(pos, piece);
    }

    public GipfBoard getGipfBoard() {
        return gipfBoard;
    }

    /**
     * This method is currently a placeholder. Currently statically returns all potential candidates for allowed moves,
     * but it should be checked which ones are actually allowed.
     *
     * @return
     */
    public Set<Move> getAllowedMoves() {
        return new HashSet<>(Arrays.asList(
                new Move(getCurrentPiece(), new Position('a', 1), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 2), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 2), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 3), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 3), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 4), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 4), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 5), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('b', 6), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('b', 6), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('c', 7), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('c', 7), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('d', 8), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('d', 8), Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('e', 9), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('f', 8), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('f', 8), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('g', 7), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('g', 7), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('h', 6), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('h', 6), Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('i', 5), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 4), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 4), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 3), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 3), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 2), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 2), Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 1), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('h', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('h', 1), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('g', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('g', 1), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('f', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('f', 1), Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('e', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('d', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('d', 1), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('c', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('c', 1), Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('b', 1), Direction.NORTH),
                new Move(getCurrentPiece(), new Position('b', 1), Direction.NORTH_EAST)
        ));
    }

    private void updateCurrentPlayer() {
        currentPlayer = ((currentPlayer == whitePlayer) ? blackPlayer : whitePlayer);
    }

    public Piece getCurrentPiece() {
        if (currentPlayer.pieceColor == PieceColor.WHITE) return Piece.WHITE_SINGLE;
        if (currentPlayer.pieceColor == PieceColor.BLACK) return Piece.BLACK_SINGLE;

        return null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Set<Position> getBorderPositions() {
        // TODO
        return null;
    }

    /**
     * By Dingding
     */
    public void detectFourPieces() {
        //position i, j, colour, count, row
        ArrayList<int[]> fourPieces = new ArrayList<int[]>(); //Arraylist to contain all rows of at least 4 pieces

        int count = 0;
        int colour = 0;
        int startBoardj = 2;
        int endBoardj = 9; //Testing purposes
        int startBoardi = 2 * 10; //Testing purposes
        int endBoardi = 90;
        //Direction: South to North
        for (int i = startBoardi; i < endBoardi; i += 10) {
            for (int j = startBoardj; j < endBoardj; j++) //End = top current column
            {
                count = 0;
                Position p = new Position(i + j);
                System.out.println("i: " + i + "j: " + j + "on board: " + isPositionOnBoard(p));

                if (!isPositionOnBoard(p)) {
                    //Empty intentionally
                } else {
                    //i = column of piece
                    Piece piece = gipfBoard.getPieceMap().get(p);
                    //First piece encountered
                    if ((piece != null) && count == 0) {
                        //System.out.println("Detected first piece");
                        count++;
                        int[] temp = {i, j, getColour(piece), count, 0};
                        fourPieces.add(temp);
                    }
                    //Pieces encountered after first piece consecutive
                    else if ((piece != null) && fourPieces.get(fourPieces.size() - 1)[2] == getColour(piece) && j == fourPieces.get(fourPieces.size() - 1)[1] + 1) {
                        //System.out.println("Detected other piece");
                        count++;

                        if (count == 4) {
                            //Found a row of 4, change [4] to 1 to indicate part of row
                            for (int a = fourPieces.size() - 1; a > fourPieces.size() - 5; a--) {
                                fourPieces.get(a)[4] = 1;
                            }

                            int[] temp = {i + j, getColour(piece), count, 1};
                            fourPieces.add(temp);
                        } else if (count > 4) //Add new piece as part of row hence ID = 1
                        {
                            int[] temp = {i + j, getColour(piece), count, 1};
                            fourPieces.add(temp);
                        } else //Not yet part of row -> ID = 0
                        {
                            int[] temp = {i + j, getColour(piece), count, 0};
                            fourPieces.add(temp);
                        }
                    }
                    //Found a piece not consecutive
                    else if ((piece != null)) {
                        //Have to empty ArrayList, but keep rows found so far looking at rowID (fourPieces[4])
                        count = 1;
                        ArrayList<int[]> tempList = new ArrayList();
                        for (int k = 0; k < fourPieces.size() - 1; k++) {
                            if (fourPieces.get(k)[4] == 1) //Copy all pieces that were part of a row
                            {
                                int[] temp = {fourPieces.get(k)[0], fourPieces.get(k)[1], fourPieces.get(k)[2], fourPieces.get(k)[3], fourPieces.get(k)[4]};
                                tempList.add(temp);
                            } else {
                                //Go to next part
                            }
                        }
                        int[] temp = {i, j, getColour(piece), count};
                        tempList.add(temp);
                        fourPieces = tempList;
                    }
                }
            }

            System.out.println("Checked for four pieces");
        }
        //Have to reset count per direction
        count = 0;
        //Direction: SW -> NE
        for (int i = startBoardi; i < endBoardi; i += 10) {
            for (int j = startBoardj; j < endBoardj; j++) {

            }
        }
        //Reset count per direction
        count = 0;
        //Direction: NW -> SE
        for (int i = startBoardi; i < endBoardi; i++) {
            int j; //Same as j starting position until e
        }

    }

    /**
     * By Dingding
     *
     * @param piece
     * @return
     */
    public int getColour(Piece piece) {
        int colour = 0;
        if (piece.equals("BLACK_SINGLE")) {
            colour = 0;
        } else if (piece.equals("WHITE_SINGLE")) {
            colour = 1;
        }
        return colour;
    }

    public void debugOutput(String debug) {
        debugMessages.add(debug);
    }

    public Set<Position> getStartPositionsForMoves() {
        return getAllowedMoves()
                .stream()
                .map(Move::getStartingPosition)
                .collect(Collectors.toSet());
    }

    public Set<Position> getMoveToPositionsForStartPosition(Position position) {
        return getAllowedMoves()
                .stream()
                .filter(m -> m.getStartingPosition().equals(position))
                .map(move -> new Position(
                        move.getStartingPosition().getPosId() + move.getDirection().getDeltaPos()))
                .collect(Collectors.toSet());
    }

    public enum PieceType {
        GIPF,
        NORMAL,
    }

    /**
     * There are four types of pieces. Gipf pieces consist of two stacked normal pieces of the same pieceColor.
     */
    public enum Piece {
        WHITE_SINGLE,
        WHITE_GIPF,
        BLACK_SINGLE,
        BLACK_GIPF;

        @Override
        public String toString() {
            switch (super.name()) {
                case "WHITE_SINGLE":
                    return "White Single";
                case "WHITE_GIPF":
                    return "White Gipf";
                case "BLACK_SINGLE":
                    return "Black Single";
                case "BLACK_GIPF":
                    return "Black Gipf";
                default:
                    return "[Piece type not known]";
            }
        }
    }

    public enum PieceColor {
        WHITE,
        BLACK
    }

    public class Player {
        public PieceColor pieceColor;
        public int piecesLeft = 2;    // Each player starts with 18 pieces
        boolean isPlacingGipfPieces = true;

        Player(PieceColor pieceColor) {
            this.pieceColor = pieceColor;
        }
    }
}

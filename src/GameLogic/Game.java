package GameLogic;

import GameLogic.Move.Direction;

import java.util.*;

/**
 * Is still room for optimization, but should be done only if this code seems
 * to be a bottleneck.
 * <p/>
 * Created by frans on 21-9-2015.
 */
public class Game {
    public LinkedList<String> debugMessages;
    Player currentPlayer;
    private GipfBoard gipfBoard;

    public Game() {
        gipfBoard = new GipfBoard();
        currentPlayer = Player.WHITE;
        debugMessages = new LinkedList<>();
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
                row >= 10 ||
                row - col >= 5 ||
                col <= 0);
    }

    private boolean isPositionEmpty(Position p) {
        return !gipfBoard.getPieceMap().containsKey(p);
    }

    private void movePiece(Position currentPosition, int deltaPos) throws Exception {
        Position nextPosition = new Position(currentPosition.posId + deltaPos);

        if (!isPositionOnBoard(nextPosition)) {
            throw new InvalidMoveException();
        } else {
            try {
                if (!isPositionEmpty(nextPosition)) {
                    movePiece(nextPosition, deltaPos);
                }

                gipfBoard.getPieceMap().put(nextPosition, gipfBoard.getPieceMap().remove(currentPosition));
            } catch (InvalidMoveException e) {
                debugOutput("Moving to " + nextPosition + " is not allowed");
                throw new InvalidMoveException();
            }
        }
    }

    private void movePiecesTowards(Position startPos, Move.Direction direction) throws InvalidMoveException {
        int deltaPos = getDeltaPosFromDirection(direction);

        Position currentPosition = new Position(startPos);

        try {
            movePiece(currentPosition, deltaPos);
        } catch (Exception e) {
            throw new InvalidMoveException();
        }
    }

    public int getDeltaPosFromDirection(Move.Direction direction) {
        // Determine the deltaPos value based on the direction
        switch (direction) {
            case NORTH:
                return 1;
            case NORTH_EAST:
                return 11;
            case SOUTH_EAST:
                return 10;
            case SOUTH:
                return -1;
            case SOUTH_WEST:
                return -11;
            case NORTH_WEST:
                return -10;
        }

        return -1;
    }

    public Direction getDirectionFromDeltaPos(int deltaPos) {
        switch (deltaPos) {
            case 1:
                return Direction.NORTH;
            case 11:
                return Direction.NORTH_EAST;
            case 10:
                return Direction.SOUTH_EAST;
            case -1:
                return Direction.SOUTH;
            case -11:
                return Direction.SOUTH_WEST;
            case -10:
                return Direction.NORTH_WEST;

            default:
                debugOutput("invalid deltaPos '" + deltaPos + "'");
                return null;
        }
    }

    /**
     * applyMove applies the given move to the board.
     * First, the new piece is added to the startPos
     * Then the pieces are moved in the direction of the move,
     * and finally pieces that need to be removed are removed from the board
     *
     * @param m the move that is applied
     */
    public void applyMove(Move m) {
        if (currentPlayer.piecesLeft >= 1) {
            // Add the piece to the new pieces
            setPiece(m.startPos, m.addedPiece);

            try {
                movePiecesTowards(m.startPos, m.direction);

                // Remove the pieces that need to be removed
                // Java 8 solution (performs the remove operation on each of the pieces that should be removed)
                m.removedPiecePositions.forEach(gipfBoard.getPieceMap()::remove);

                try {
                    detectFourPieces();
                } catch (ArrayIndexOutOfBoundsException e) {
                    debugOutput("Caught an ArrayIndexOutOfBoundsException");     // TODO Fix this
                    e.printStackTrace();
                }

                currentPlayer.piecesLeft--;

                updateCurrentPlayer();
            } catch (InvalidMoveException e) {
                debugOutput("Move not applied");
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
                new Move(getCurrentPiece(), new Position('a', 1), Move.Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 2), Move.Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 2), Move.Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 3), Move.Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 3), Move.Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 4), Move.Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 4), Move.Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('a', 5), Move.Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('b', 6), Move.Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('b', 6), Move.Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('c', 7), Move.Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('c', 7), Move.Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('d', 8), Move.Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('d', 8), Move.Direction.SOUTH_EAST),
                new Move(getCurrentPiece(), new Position('e', 9), Move.Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('f', 8), Move.Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('f', 8), Move.Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('g', 7), Move.Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('g', 7), Move.Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('h', 6), Move.Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('h', 6), Move.Direction.SOUTH),
                new Move(getCurrentPiece(), new Position('i', 5), Move.Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 4), Move.Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 4), Move.Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 3), Move.Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 3), Move.Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 2), Move.Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 2), Move.Direction.SOUTH_WEST),
                new Move(getCurrentPiece(), new Position('i', 1), Move.Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('h', 1), Move.Direction.NORTH),
                new Move(getCurrentPiece(), new Position('h', 1), Move.Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('g', 1), Move.Direction.NORTH),
                new Move(getCurrentPiece(), new Position('g', 1), Move.Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('f', 1), Move.Direction.NORTH),
                new Move(getCurrentPiece(), new Position('f', 1), Move.Direction.NORTH_WEST),
                new Move(getCurrentPiece(), new Position('e', 1), Move.Direction.NORTH),
                new Move(getCurrentPiece(), new Position('d', 1), Move.Direction.NORTH),
                new Move(getCurrentPiece(), new Position('d', 1), Move.Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('c', 1), Move.Direction.NORTH),
                new Move(getCurrentPiece(), new Position('c', 1), Move.Direction.NORTH_EAST),
                new Move(getCurrentPiece(), new Position('b', 1), Move.Direction.NORTH),
                new Move(getCurrentPiece(), new Position('b', 1), Move.Direction.NORTH_EAST)
        ));
    }

    private void updateCurrentPlayer() {
        currentPlayer = ((currentPlayer == Player.WHITE) ? Player.BLACK : Player.WHITE);
    }

    public Piece getCurrentPiece() {
        if (currentPlayer == Player.WHITE) return Piece.WHITE_SINGLE;
        if (currentPlayer == Player.BLACK) return Piece.BLACK_SINGLE;

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
                Position p = new Position(i + j);
                debugOutput("i: " + i + "j: " + j + "on board: " + isPositionOnBoard(p));

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
                            for (int a = j; a > j - 4; a--) {
                                fourPieces.get(j)[4] = 1;
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
                        for (int k = 0; k < fourPieces.size(); k++) {
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

    /**
     * There are four types of pieces. Gipf pieces consist of two stacked normal pieces of the same color.
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

    public enum Player {
        WHITE,
        BLACK;

        public int piecesLeft = 18;    // Each player starts with 18 pieces
        boolean isPlacingGipfPieces = true;
    }
}

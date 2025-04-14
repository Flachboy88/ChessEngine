package Pieces;

public class Pawn extends Piece {
    private boolean justMadeDoubleMove; // Pour en passant

    public Pawn(PieceColor color, Position position){
        super(color, position);
        justMadeDoubleMove = false;
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        int forwardDirection = color == PieceColor.WHITE ? -1 : 1;
        int rowDiff = (newPosition.getRow() - position.getRow()) * forwardDirection;
        int colDiff = newPosition.getColumn() - position.getColumn();

        //forward move
        if(colDiff == 0 && rowDiff == 1 && board[newPosition.getRow()][newPosition.getColumn()] == null){
            return true;
        }

        // Initial two-square move
        boolean isStartingPosition = !hasMoved;
        if (colDiff == 0 && rowDiff == 2 && isStartingPosition
                && board[newPosition.getRow()][newPosition.getColumn()] == null) {
            // Check the square in between for blocking pieces
            int middleRow = position.getRow() + forwardDirection;
            if (board[middleRow][position.getColumn()] == null) {
                return true; // Move forward two squares
            }
        }

        //diagonal capture
        if (Math.abs(colDiff) == 1 && rowDiff == 1) {
            // Capture normale
            if (board[newPosition.getRow()][newPosition.getColumn()] != null &&
                    board[newPosition.getRow()][newPosition.getColumn()].color != this.color) {
                return true; // Capture an opponent's piece
            }

            // En Passant
            int enPassantRow = position.getRow();
            if (board[enPassantRow][newPosition.getColumn()] != null &&
                    board[enPassantRow][newPosition.getColumn()] instanceof Pawn &&
                    board[enPassantRow][newPosition.getColumn()].getColor() != this.color) {

                Pawn opponentPawn = (Pawn) board[enPassantRow][newPosition.getColumn()];
                if (opponentPawn.isJustMadeDoubleMove()) {
                    return true; // En passant is valid
                }
            }
        }
        return false;
    }

    public void setJustMadeDoubleMove(boolean value) {
        this.justMadeDoubleMove = value;
    }

    public boolean isJustMadeDoubleMove() {
        return justMadeDoubleMove;
    }

    // Cette méthode reset justMadeDoubleMove de tous les pions de la couleur opposée
    public static void resetEnPassantFlags(Piece[][] board, PieceColor currentPlayerColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] instanceof Pawn && board[row][col].getColor() != currentPlayerColor) {
                    ((Pawn) board[row][col]).setJustMadeDoubleMove(false);
                }
            }
        }
    }

    // Vérifie si ce mouvement est un en passant
    public boolean isEnPassantMove(Position newPosition, Piece[][] board) {
        int colDiff = newPosition.getColumn() - position.getColumn();
        if (Math.abs(colDiff) == 1) {
            int enPassantRow = position.getRow();
            if (board[enPassantRow][newPosition.getColumn()] != null &&
                    board[enPassantRow][newPosition.getColumn()] instanceof Pawn &&
                    board[enPassantRow][newPosition.getColumn()].getColor() != this.color) {

                Pawn opponentPawn = (Pawn) board[enPassantRow][newPosition.getColumn()];
                return opponentPawn.isJustMadeDoubleMove();
            }
        }
        return false;
    }
}
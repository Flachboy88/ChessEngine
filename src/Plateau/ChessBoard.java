package Plateau;

import Pieces.*;

public class ChessBoard {
    private Piece[][] board;

    public ChessBoard() {
        this.board = new Piece[8][8]; // Chessboard is 8x8
        setupPieces();
    }

    public Piece[][] getBoard() {
        return board;
    }

    public Piece getPiece(int row, int column) {
        return board[row][column];
    }

    public void setPiece(int row, int column, Piece piece) {
        board[row][column] = piece;
        if (piece != null) {
            piece.setPosition(new Position(row, column));
        }
    }

    private void setupPieces() {
        // Place Rooks
        board[0][0] = new Rook(PieceColor.BLACK, new Position(0, 0));
        board[0][7] = new Rook(PieceColor.BLACK, new Position(0, 7));
        board[7][0] = new Rook(PieceColor.WHITE, new Position(7, 0));
        board[7][7] = new Rook(PieceColor.WHITE, new Position(7, 7));
        // Place Knights
        board[0][1] = new Knight(PieceColor.BLACK, new Position(0, 1));
        board[0][6] = new Knight(PieceColor.BLACK, new Position(0, 6));
        board[7][1] = new Knight(PieceColor.WHITE, new Position(7, 1));
        board[7][6] = new Knight(PieceColor.WHITE, new Position(7, 6));
        // Place Bishops
        board[0][2] = new Bishop(PieceColor.BLACK, new Position(0, 2));
        board[0][5] = new Bishop(PieceColor.BLACK, new Position(0, 5));
        board[7][2] = new Bishop(PieceColor.WHITE, new Position(7, 2));
        board[7][5] = new Bishop(PieceColor.WHITE, new Position(7, 5));
        // Place Queens
        board[0][3] = new Queen(PieceColor.BLACK, new Position(0, 3));
        board[7][3] = new Queen(PieceColor.WHITE, new Position(7, 3));
        // Place Kings
        board[0][4] = new King(PieceColor.BLACK, new Position(0, 4));
        board[7][4] = new King(PieceColor.WHITE, new Position(7, 4));
        // Place Pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(PieceColor.BLACK, new Position(1, i));
            board[6][i] = new Pawn(PieceColor.WHITE, new Position(6, i));
        }
    }

    public void movePiece(Position start, Position end) {
        Piece movingPiece = board[start.getRow()][start.getColumn()];

        if (movingPiece != null) {
            // Gestion spéciale pour le roque
            if (movingPiece instanceof King && ((King) movingPiece).isCastling(end)) {
                King king = (King) movingPiece;
                Position rookSource = king.getRookCastlingSource(end);  // Modifié pour passer la destination du roi
                Position rookDest = king.getRookCastlingDestination(end);

                // Déplacer la tour
                Piece rook = board[rookSource.getRow()][rookSource.getColumn()];
                board[rookDest.getRow()][rookDest.getColumn()] = rook;
                rook.setPosition(rookDest);
                rook.setMoved();
                board[rookSource.getRow()][rookSource.getColumn()] = null;
            }

            // Gestion spéciale pour l'en passant
            if (movingPiece instanceof Pawn) {
                Pawn pawn = (Pawn) movingPiece;

                // Vérifier s'il s'agit d'un double mouvement de pion
                int rowDiff = Math.abs(end.getRow() - start.getRow());
                if (rowDiff == 2) {
                    pawn.setJustMadeDoubleMove(true);
                } else {
                    pawn.setJustMadeDoubleMove(false);
                }

                // Si c'est un mouvement en passant, capturer le pion opposé
                if (pawn.isEnPassantMove(end, board)) {
                    // Supprimer le pion capturé en passant
                    board[start.getRow()][end.getColumn()] = null;
                }
            }

            // Déplacer la pièce
            board[end.getRow()][end.getColumn()] = movingPiece;
            movingPiece.setPosition(end);
            movingPiece.setMoved();
            board[start.getRow()][start.getColumn()] = null;
        }
    }

    public void promotePawn(Position pawnPosition, String promotionType) {
        Piece pawn = board[pawnPosition.getRow()][pawnPosition.getColumn()];
        if (!(pawn instanceof Pawn)) {
            return;
        }

        PieceColor color = pawn.getColor();
        Piece newPiece = null;

        switch(promotionType) {
            case "Queen":
                newPiece = new Queen(color, pawnPosition);
                break;
            case "Rook":
                newPiece = new Rook(color, pawnPosition);
                break;
            case "Bishop":
                newPiece = new Bishop(color, pawnPosition);
                break;
            case "Knight":
                newPiece = new Knight(color, pawnPosition);
                break;
        }

        if (newPiece != null) {
            board[pawnPosition.getRow()][pawnPosition.getColumn()] = newPiece;
        }
    }
}
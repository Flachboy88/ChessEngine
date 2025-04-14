package Pieces;

public class King extends Piece {
    public King(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        int rowDiff = Math.abs(position.getRow() - newPosition.getRow());
        int colDiff = Math.abs(position.getColumn() - newPosition.getColumn());

        // Kings can move one square in any direction.
        boolean isOneSquareMove = rowDiff <= 1 && colDiff <= 1 && !(rowDiff == 0 && colDiff == 0);

        // Vérifier castling/roque
        boolean isCastling = !hasMoved && rowDiff == 0 && colDiff == 2;

        if (!isOneSquareMove && !isCastling) {
            return false; // Ni un déplacement d'une case ni un roque
        }

        // Vérification du roque
        if (isCastling) {
            return validateCastling(newPosition, board);
        }

        Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
        // Le déplacement est valide si la destination est vide ou contient une pièce adverse
        return destinationPiece == null || destinationPiece.getColor() != this.getColor();
    }

    private boolean validateCastling(Position newPosition, Piece[][] board) {
        int row = position.getRow();
        int kingCol = position.getColumn(); // Devrait être 4 en position initiale
        int newCol = newPosition.getColumn();

        // Direction du roque (droite = petit roque, gauche = grand roque)
        boolean isKingSideCastling = newCol > kingCol;
        int rookCol = isKingSideCastling ? 7 : 0;

        // Vérifier si la tour est en place et n'a pas bougé
        Piece rookPiece = board[row][rookCol];
        if (rookPiece == null || !(rookPiece instanceof Rook) || rookPiece.hasMoved()) {
            return false;
        }

        // Vérifier que le chemin est dégagé entre le roi et la tour
        int startCol = Math.min(kingCol, rookCol) + 1;
        int endCol = Math.max(kingCol, rookCol);
        for (int col = startCol; col < endCol; col++) {
            if (board[row][col] != null) {
                return false; // Pièce sur le chemin
            }
        }

        // La validation de l'échec sera faite dans ChessGame
        return true;
    }

    // Renvoie la position finale de la tour après le roque
    public Position getRookCastlingDestination(Position kingDestination) {
        int row = position.getRow();
        boolean isKingSideCastling = kingDestination.getColumn() > position.getColumn();

        // Si roque côté roi (petit roque), la tour va à la gauche du roi
        // Si roque côté dame (grand roque), la tour va à la droite du roi
        int newRookCol = isKingSideCastling ? kingDestination.getColumn() - 1 : kingDestination.getColumn() + 1;

        return new Position(row, newRookCol);
    }

    // Renvoie la position initiale de la tour impliquée dans le roque
    public Position getRookCastlingSource(Position kingDestination) {
        int row = position.getRow();
        boolean isKingSideCastling = kingDestination.getColumn() > position.getColumn();
        int rookCol = isKingSideCastling ? 7 : 0; // Selon la direction du roque

        return new Position(row, rookCol);
    }

    // Détermine si ce mouvement est un roque
    public boolean isCastling(Position newPosition) {
        return !hasMoved &&
                position.getRow() == newPosition.getRow() &&
                Math.abs(position.getColumn() - newPosition.getColumn()) == 2;
    }
}
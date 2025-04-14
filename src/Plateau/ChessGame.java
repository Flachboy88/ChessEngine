package Plateau;

import Pieces.*;
import Vues.*;

import java.util.ArrayList;
import java.util.List;
import Vues.VuePromotion;

public class ChessGame {
    private ChessBoard board;
    private boolean whiteTurn = true; // White starts the game
    private boolean checkDisplayed = false; // Pour éviter de montrer le popup à chaque clic
    private Position selectedPosition;
    private Historique historique;

    public ChessGame() {
        this.board = new ChessBoard();
        this.historique = new Historique();
    }

    public Historique getHistorique() {
        return this.historique;
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    public void resetGame() {
        this.board = new ChessBoard();
        this.whiteTurn = true;
        this.checkDisplayed = false;
        this.historique = new Historique();
    }

    public PieceColor getCurrentPlayerColor() {
        return whiteTurn ? PieceColor.WHITE : PieceColor.BLACK;
    }


    public boolean isPieceSelected() {
        return selectedPosition != null;
    }

    public boolean handleSquareSelection(int row, int col) {
        if (selectedPosition == null) {
            // Première sélection - vérifier si la case contient une pièce et si c'est au tour du joueur
            Piece selectedPiece = board.getPiece(row, col);
            if (selectedPiece != null
                    && selectedPiece.getColor() == (whiteTurn ? PieceColor.WHITE : PieceColor.BLACK)) {
                selectedPosition = new Position(row, col);
                return false; // Pas encore de mouvement effectué, juste une sélection
            }
        } else {
            // Deuxième sélection - tenter de déplacer la pièce
            Position targetPosition = new Position(row, col);
            Piece selectedPiece = board.getPiece(selectedPosition.getRow(), selectedPosition.getColumn());

            // Vérifier si la case cible contient une pièce du même joueur
            Piece targetPiece = board.getPiece(row, col);
            if (targetPiece != null && targetPiece.getColor() == selectedPiece.getColor()) {
                // Le joueur sélectionne une autre de ses pièces, donc changer la sélection
                selectedPosition = new Position(row, col);
                return false;
            }

            // D'abord, vérifier si le roi actuel est en échec
            PieceColor currentColor = selectedPiece.getColor();
            boolean kingInCheck = isInCheck(currentColor);

            // Vérifier si le mouvement est valide selon les règles de la pièce
            if (selectedPiece != null && selectedPiece.isValidMove(targetPosition, board.getBoard())) {
                // Vérifier si ce mouvement mettrait notre propre roi en échec
                if (wouldBeInCheckAfterMove(selectedPiece.getColor(), selectedPosition, targetPosition)) {
                    if (kingInCheck) {
                        // Le roi est déjà en échec, message spécifique
                        VueAlerte.afficherRoiEnDanger();
                    } else {
                        // Ce mouvement mettrait le roi en échec
                        VueAlerte.afficherMouvementImpossible();
                    }
                    selectedPosition = null;
                    return false;
                }

                // Exécuter le mouvement
                board.movePiece(selectedPosition, targetPosition);

                // Vérifier si un pion doit être promu
                checkPawnPromotion(targetPosition);

                // Changer de tour
                whiteTurn = !whiteTurn;

                // Reset la position sélectionnée
                selectedPosition = null;

                // Après le mouvement, vérifier si l'adversaire est en échec
                PieceColor opponentColor = currentColor == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;

                // Vérifier échec
                if (isInCheck(opponentColor)) {
                    checkDisplayed = true;
                    VueAlerte.afficherEchec();

                    // Vérifier échec et mat
                    if (isCheckmate(opponentColor)) {
                        String gagnant = currentColor == PieceColor.WHITE ? "blancs" : "noirs";
                        VueAlerte.afficherEchecEtMat(gagnant);
                    }
                } else {
                    checkDisplayed = false;
                }

                return true; // Mouvement effectué avec succès
            } else {
                // Mouvement invalide
                selectedPosition = null;
                return false;
            }
        }
        return false;
    }

    public boolean makeMove(Position start, Position end) {
        Piece movingPiece = board.getPiece(start.getRow(), start.getColumn());
        if (movingPiece == null || movingPiece.getColor() != (whiteTurn ? PieceColor.WHITE : PieceColor.BLACK)) {
            return false;
        }

        if (movingPiece.isValidMove(end, board.getBoard())) {
            board.movePiece(start, end);

            // Vérifier si un pion doit être promu
            checkPawnPromotion(end);

            whiteTurn = !whiteTurn;
            return true;
        }
        return false;
    }


    public boolean isInCheck(PieceColor kingColor) {
        Position kingPosition = findKingPosition(kingColor);
        for (int row = 0; row < board.getBoard().length; row++) {
            for (int col = 0; col < board.getBoard()[row].length; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getColor() != kingColor) {
                    if (piece.isValidMove(kingPosition, board.getBoard())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Position findKingPosition(PieceColor color) {
        for (int row = 0; row < board.getBoard().length; row++) {
            for (int col = 0; col < board.getBoard()[row].length; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece instanceof King && piece.getColor() == color) {
                    return new Position(row, col);
                }
            }
        }
        throw new RuntimeException("King not found, which should never happen.");
    }

    public boolean isCheckmate(PieceColor kingColor) {
        if (!isInCheck(kingColor)) {
            return false;
        }

        // Vérifier tous les mouvements possibles pour chaque pièce
        for (int row = 0; row < board.getBoard().length; row++) {
            for (int col = 0; col < board.getBoard()[row].length; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getColor() == kingColor) {
                    // Vérifier tous les mouvements légaux pour cette pièce
                    List<Position> legalMoves = getLegalMovesForPieceAt(new Position(row, col));
                    for (Position move : legalMoves) {
                        if (!wouldBeInCheckAfterMove(kingColor, new Position(row, col), move)) {
                            return false; // Il existe un mouvement qui annule l'échec
                        }
                    }
                }
            }
        }
        return true; // Aucun mouvement ne peut annuler l'échec => échec et mat
    }

    private boolean isPositionOnBoard(Position position) {
        return position.getRow() >= 0 && position.getRow() < board.getBoard().length &&
                position.getColumn() >= 0 && position.getColumn() < board.getBoard()[0].length;
    }

    public boolean wouldBeInCheckAfterMove(PieceColor kingColor, Position from, Position to) {
        // Sauvegarde de la position du roi avant simulation
        Position kingPosition = null;
        if (board.getPiece(from.getRow(), from.getColumn()) instanceof King) {
            kingPosition = from;
        } else {
            kingPosition = findKingPosition(kingColor);
        }

        // Sauvegarde de l'état actuel
        Piece movingPiece = board.getPiece(from.getRow(), from.getColumn());
        Piece targetPiece = board.getPiece(to.getRow(), to.getColumn());
        Position originalPosition = null;

        if (movingPiece != null) {
            originalPosition = movingPiece.getPosition(); // Sauvegarde de la position originale
        }

        // Simulation du mouvement
        board.setPiece(to.getRow(), to.getColumn(), movingPiece);
        board.setPiece(from.getRow(), from.getColumn(), null);

        // Mettre à jour temporairement la position de la pièce déplacée
        if (movingPiece != null) {
            movingPiece.setPosition(to);
        }

        // Cas spécial pour le roque (déplacement de la tour)
        boolean isKingCastling = movingPiece instanceof King && ((King) movingPiece).isCastling(to);
        Position rookFrom = null;
        Position rookTo = null;
        Piece rook = null;
        Position originalRookPosition = null;

        if (isKingCastling) {
            King king = (King) movingPiece;
            rookFrom = king.getRookCastlingSource(to);  // Modifié pour passer la destination du roi
            rookTo = king.getRookCastlingDestination(to);
            rook = board.getPiece(rookFrom.getRow(), rookFrom.getColumn());

            if (rook != null) {
                originalRookPosition = rook.getPosition(); // Sauvegarde de la position originale

                // Déplacer la tour pour la simulation
                board.setPiece(rookTo.getRow(), rookTo.getColumn(), rook);
                board.setPiece(rookFrom.getRow(), rookFrom.getColumn(), null);

                // Mettre à jour temporairement la position de la tour
                rook.setPosition(rookTo);
            }
        }

        // Vérification de l'échec après le mouvement simulé
        boolean inCheck = false;
        // Vérifier si une pièce adverse peut atteindre le roi
        Position currentKingPosition = movingPiece instanceof King ? to : findKingPosition(kingColor);

        for (int row = 0; row < board.getBoard().length; row++) {
            for (int col = 0; col < board.getBoard()[row].length; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null && piece.getColor() != kingColor) {
                    if (piece.isValidMove(currentKingPosition, board.getBoard())) {
                        inCheck = true;
                        break;
                    }
                }
            }
            if (inCheck) break;
        }

        // Restauration de l'état initial
        board.setPiece(from.getRow(), from.getColumn(), movingPiece);
        board.setPiece(to.getRow(), to.getColumn(), targetPiece);

        // Restaurer la position originale de la pièce
        if (movingPiece != null && originalPosition != null) {
            movingPiece.setPosition(originalPosition);
        }

        // Restaurer la tour si c'était un roque
        if (isKingCastling && rook != null) {
            board.setPiece(rookFrom.getRow(), rookFrom.getColumn(), rook);
            board.setPiece(rookTo.getRow(), rookTo.getColumn(), null);

            // Restaurer la position originale de la tour
            if (originalRookPosition != null) {
                rook.setPosition(originalRookPosition);
            }
        }

        return inCheck;
    }

    private void checkPawnPromotion(Position position) {
        Piece piece = board.getPiece(position.getRow(), position.getColumn());
        if (piece instanceof Pawn) {
            // Un pion atteint la dernière rangée
            if ((piece.getColor() == PieceColor.WHITE && position.getRow() == 0) ||
                    (piece.getColor() == PieceColor.BLACK && position.getRow() == 7)) {

                // Demander à l'utilisateur quelle pièce il veut
                String promotionType = VuePromotion.afficherChoixPromotion(piece.getColor());

                // Promouvoir le pion
                board.promotePawn(position, promotionType);
            }
        }
    }

    public List<Position> getLegalMovesForPieceAt(Position position) {
        Piece selectedPiece = board.getPiece(position.getRow(), position.getColumn());
        if (selectedPiece == null)
            return new ArrayList<>();

        List<Position> legalMoves = new ArrayList<>();
        switch (selectedPiece.getClass().getSimpleName()) {
            case "Pawn":
                addPawnMoves(position, selectedPiece.getColor(), legalMoves);
                break;
            case "Rook":
                addLineMoves(position, new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }, legalMoves);
                break;
            case "Knight":
                addSingleMoves(position, new int[][] { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { -1, 2 },
                        { 1, -2 }, { -1, -2 } }, legalMoves);
                break;
            case "Bishop":
                addLineMoves(position, new int[][] { { 1, 1 }, { -1, -1 }, { 1, -1 }, { -1, 1 } }, legalMoves);
                break;
            case "Queen":
                addLineMoves(position, new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { -1, -1 },
                        { 1, -1 }, { -1, 1 } }, legalMoves);
                break;
            case "King":
                addSingleMoves(position, new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { -1, -1 },
                        { 1, -1 }, { -1, 1 } }, legalMoves);
                King king = (King) selectedPiece;
                // Ajouter les mouvements de roque si le roi n'a pas bougé et n'est pas en échec
                if (!king.hasMoved() && !isInCheck(king.getColor())) {
                    addCastlingMoves(position, king.getColor(), legalMoves);
                }
                break;
        }
        return legalMoves;
    }

    private void addCastlingMoves(Position kingPosition, PieceColor color, List<Position> legalMoves) {
        int row = kingPosition.getRow();
        int col = kingPosition.getColumn();

        // Petit roque (côté roi)
        if (canCastle(row, col, row, col + 2, true)) {
            legalMoves.add(new Position(row, col + 2));
        }

        // Grand roque (côté dame)
        if (canCastle(row, col, row, col - 2, false)) {
            legalMoves.add(new Position(row, col - 2));
        }
    }

    private boolean canCastle(int kingRow, int kingCol, int targetRow, int targetCol, boolean kingSide) {
        // Vérifier que le roi n'a pas bougé
        Piece king = board.getPiece(kingRow, kingCol);
        if (king == null || !(king instanceof King) || king.hasMoved()) {
            return false;
        }

        // Vérifier que la tour n'a pas bougé
        int rookCol = kingSide ? 7 : 0;
        Piece rook = board.getPiece(kingRow, rookCol);
        if (rook == null || !(rook instanceof Rook) || rook.hasMoved()) {
            return false;
        }

        // Vérifier que les cases entre le roi et la tour sont vides
        int step = kingSide ? 1 : -1;
        for (int c = kingCol + step; kingSide ? (c < rookCol) : (c > rookCol); c += step) {
            if (board.getPiece(kingRow, c) != null) {
                return false;
            }
        }

        // Vérifier que le roi ne traverse pas une case en échec
        for (int c = kingCol; kingSide ? (c <= kingCol + 1) : (c >= kingCol - 1); c += step) {
            if (wouldBeInCheckAfterMove(king.getColor(), new Position(kingRow, kingCol), new Position(kingRow, c))) {
                return false;
            }
        }

        return true;
    }

    private void addLineMoves(Position position, int[][] directions, List<Position> legalMoves) {
        for (int[] d : directions) {
            Position newPos = new Position(position.getRow() + d[0], position.getColumn() + d[1]);
            while (isPositionOnBoard(newPos)) {
                if (board.getPiece(newPos.getRow(), newPos.getColumn()) == null) {
                    legalMoves.add(new Position(newPos.getRow(), newPos.getColumn()));
                    newPos = new Position(newPos.getRow() + d[0], newPos.getColumn() + d[1]);
                } else {
                    if (board.getPiece(newPos.getRow(), newPos.getColumn()).getColor() != board
                            .getPiece(position.getRow(), position.getColumn()).getColor()) {
                        legalMoves.add(newPos);
                    }
                    break;
                }
            }
        }
    }

    private void addSingleMoves(Position position, int[][] moves, List<Position> legalMoves) {
        for (int[] move : moves) {
            Position newPos = new Position(position.getRow() + move[0], position.getColumn() + move[1]);
            if (isPositionOnBoard(newPos) && (board.getPiece(newPos.getRow(), newPos.getColumn()) == null ||
                    board.getPiece(newPos.getRow(), newPos.getColumn()).getColor() != board
                            .getPiece(position.getRow(), position.getColumn()).getColor())) {
                legalMoves.add(newPos);
            }
        }
    }

    private void addPawnMoves(Position position, PieceColor color, List<Position> legalMoves) {
        int direction = color == PieceColor.WHITE ? -1 : 1;
        Position newPos = new Position(position.getRow() + direction, position.getColumn());
        if (isPositionOnBoard(newPos) && board.getPiece(newPos.getRow(), newPos.getColumn()) == null) {
            legalMoves.add(newPos);
        }

        if ((color == PieceColor.WHITE && position.getRow() == 6)
                || (color == PieceColor.BLACK && position.getRow() == 1)) {
            newPos = new Position(position.getRow() + 2 * direction, position.getColumn());
            Position intermediatePos = new Position(position.getRow() + direction, position.getColumn());
            if (isPositionOnBoard(newPos) && board.getPiece(newPos.getRow(), newPos.getColumn()) == null
                    && board.getPiece(intermediatePos.getRow(), intermediatePos.getColumn()) == null) {
                legalMoves.add(newPos);
            }
        }

        int[] captureCols = { position.getColumn() - 1, position.getColumn() + 1 };
        for (int col : captureCols) {
            // Capture normale diagonale
            newPos = new Position(position.getRow() + direction, col);
            if (isPositionOnBoard(newPos) && board.getPiece(newPos.getRow(), newPos.getColumn()) != null &&
                    board.getPiece(newPos.getRow(), newPos.getColumn()).getColor() != color) {
                legalMoves.add(newPos);
            }

            // En passant
            if (isPositionOnBoard(newPos) && board.getPiece(newPos.getRow(), newPos.getColumn()) == null) {
                Position adjacentPos = new Position(position.getRow(), col);
                if (isPositionOnBoard(adjacentPos) && board.getPiece(adjacentPos.getRow(), adjacentPos.getColumn()) != null) {
                    Piece adjacentPiece = board.getPiece(adjacentPos.getRow(), adjacentPos.getColumn());
                    if (adjacentPiece instanceof Pawn && adjacentPiece.getColor() != color) {
                        Pawn opponentPawn = (Pawn) adjacentPiece;
                        if (opponentPawn.isJustMadeDoubleMove()) {
                            legalMoves.add(newPos);
                        }
                    }
                }
            }
        }
    }
}
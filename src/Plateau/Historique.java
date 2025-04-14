package Plateau;

import java.util.ArrayList;
import java.util.List;
import Pieces.*;

public class Historique {
    private List<CoupJoue> coups;
    private int positionActuelle;

    public Historique() {
        this.coups = new ArrayList<>();
        this.positionActuelle = -1;
    }

    public void ajouterCoup(Position from, Position to, Piece piece) {
        String notation = genererNotation(from, to, piece);
        CoupJoue coup = new CoupJoue(from, to, piece, notation);

        // Si nous sommes au milieu de l'historique, supprimer tout ce qui suit
        if (positionActuelle < coups.size() - 1) {
            coups = coups.subList(0, positionActuelle + 1);
        }

        coups.add(coup);
        positionActuelle++;
    }

    public CoupJoue getCoupActuel() {
        if (positionActuelle >= 0 && positionActuelle < coups.size()) {
            return coups.get(positionActuelle);
        }
        return null;
    }

    public boolean peutReculer() {
        return positionActuelle > -1;
    }

    public boolean peutAvancer() {
        return positionActuelle < coups.size() - 1;
    }

    public CoupJoue reculer() {
        if (peutReculer()) {
            positionActuelle--;
            return getCoupActuel();
        }
        return null;
    }

    public CoupJoue avancer() {
        if (peutAvancer()) {
            positionActuelle++;
            return getCoupActuel();
        }
        return null;
    }

    public List<CoupJoue> getCoups() {
        return coups;
    }

    public int getPositionActuelle() {
        return positionActuelle;
    }

    private String genererNotation(Position from, Position to, Piece piece) {
        StringBuilder notation = new StringBuilder();

        // Ajouter le symbole de la pièce
        if (!(piece instanceof Pawn)) {
            if (piece instanceof King) notation.append("R");
            else if (piece instanceof Queen) notation.append("D");
            else if (piece instanceof Rook) notation.append("T");
            else if (piece instanceof Bishop) notation.append("F");
            else if (piece instanceof Knight) notation.append("C");
        }

        // Position de départ
        notation.append(positionToString(from));

        // Séparateur
        notation.append("-");

        // Position d'arrivée
        notation.append(positionToString(to));

        return notation.toString();
    }

    private String positionToString(Position pos) {
        char colonne = (char) ('a' + pos.getColumn());
        int ligne = 8 - pos.getRow();
        return "" + colonne + ligne;
    }

    public static class CoupJoue {
        private Position from;
        private Position to;
        private Piece piece;
        private String notation;

        public CoupJoue(Position from, Position to, Piece piece, String notation) {
            this.from = from;
            this.to = to;
            this.piece = piece;
            this.notation = notation;
        }

        public Position getFrom() {
            return from;
        }

        public Position getTo() {
            return to;
        }

        public Piece getPiece() {
            return piece;
        }

        public String getNotation() {
            return notation;
        }

        public PieceColor getColor() {
            return piece.getColor();
        }

        @Override
        public String toString() {
            return notation;
        }
    }
}
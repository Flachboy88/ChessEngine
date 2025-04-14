package Vues;

import Plateau.ChessGame;
import Plateau.Historique.CoupJoue;
import Pieces.PieceColor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class VueHistorique extends BorderPane {

    private ChessGame game;
    private ListView<String> listeCoups;
    private ObservableList<String> coupsObservables;
    private Button btnPrecedent;
    private Button btnSuivant;
    private VuePlateau vuePlateau;

    public VueHistorique(ChessGame game, VuePlateau vuePlateau) {
        this.game = game;
        this.vuePlateau = vuePlateau;

        // Initialiser la liste observable
        coupsObservables = FXCollections.observableArrayList();

        // Configurer l'affichage
        this.setPadding(new Insets(10));
        this.setMinWidth(200);

        // Titre de l'historique
        Label lblTitre = new Label("Historique des coups");
        lblTitre.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Liste des coups
        listeCoups = new ListView<>(coupsObservables);
        listeCoups.setPrefHeight(400);

        // Boutons de navigation
        btnPrecedent = new Button("←");
        btnPrecedent.setOnAction(e -> reculerDansHistorique());
        btnPrecedent.setDisable(true);

        btnSuivant = new Button("→");
        btnSuivant.setOnAction(e -> avancerDansHistorique());
        btnSuivant.setDisable(true);

        HBox boutonsNav = new HBox(20, btnPrecedent, btnSuivant);
        boutonsNav.setAlignment(Pos.CENTER);
        boutonsNav.setPadding(new Insets(10, 0, 0, 0));

        // Organisation du contenu
        VBox contenu = new VBox(10, lblTitre, listeCoups, boutonsNav);
        this.setCenter(contenu);

        // Écouter les événements de coup joué
        vuePlateau.addEventHandler(VuePlateau.MoveEvent.MOVE_MADE, e -> {
            mettreAJourHistorique();
        });

        // Écouter les événements de réinitialisation
        this.addEventHandler(VueOutils.GameResetEvent.RESET, e -> {
            resetHistorique();
        });
    }

    public void mettreAJourHistorique() {
        // Convertir les coups en liste affichable
        coupsObservables.clear();
        int i = 1;
        boolean estBlanc = true;
        StringBuilder ligne = new StringBuilder();

        for (CoupJoue coup : game.getHistorique().getCoups()) {
            if (estBlanc) {
                ligne = new StringBuilder(i + ". " + coup.getNotation());
                estBlanc = false;
            } else {
                ligne.append(" " + coup.getNotation());
                coupsObservables.add(ligne.toString());
                estBlanc = true;
                i++;
            }
        }

        // Si le dernier coup est blanc, l'ajouter seul
        if (!estBlanc) {
            coupsObservables.add(ligne.toString());
        }

        // Défiler jusqu'au dernier coup
        if (!coupsObservables.isEmpty()) {
            listeCoups.scrollTo(coupsObservables.size() - 1);
        }

        // Mettre à jour les boutons
        btnPrecedent.setDisable(!game.getHistorique().peutReculer());
        btnSuivant.setDisable(!game.getHistorique().peutAvancer());
    }

    private void reculerDansHistorique() {
        CoupJoue coup = game.getHistorique().reculer();
        if (coup != null) {
            // Surligner les cases du coup précédent
            vuePlateau.resetLastMove();
            vuePlateau.afficherEtatHistorique(coup.getFrom(), coup.getTo());

            // Mettre à jour les boutons
            btnPrecedent.setDisable(!game.getHistorique().peutReculer());
            btnSuivant.setDisable(!game.getHistorique().peutAvancer());

            // Mettre à jour la sélection dans la liste
            updateListSelection();
        }
    }

    private void avancerDansHistorique() {
        CoupJoue coup = game.getHistorique().avancer();
        if (coup != null) {
            // Surligner les cases du coup suivant
            vuePlateau.resetLastMove();
            vuePlateau.afficherEtatHistorique(coup.getFrom(), coup.getTo());

            // Mettre à jour les boutons
            btnPrecedent.setDisable(!game.getHistorique().peutReculer());
            btnSuivant.setDisable(!game.getHistorique().peutAvancer());

            // Mettre à jour la sélection dans la liste
            updateListSelection();
        }
    }

    private void updateListSelection() {
        int position = game.getHistorique().getPositionActuelle();
        if (position >= 0) {
            // Calculer l'index dans la ListView
            int index = position / 2;
            if (index < coupsObservables.size()) {
                listeCoups.getSelectionModel().select(index);
                listeCoups.scrollTo(index);
            }
        } else {
            listeCoups.getSelectionModel().clearSelection();
        }
    }

    private void resetHistorique() {
        coupsObservables.clear();
        btnPrecedent.setDisable(true);
        btnSuivant.setDisable(true);
    }
}
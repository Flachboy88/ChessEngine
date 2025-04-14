package Vues;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import Plateau.ChessGame;

public class VueOutils extends MenuBar {

    private ChessGame game;

    public VueOutils(ChessGame game) {
        this.game = game;

        // Création du menu "Paramètres"
        Menu menuParametres = new Menu("Paramètres");

        // Création de l'option "Recommencer"
        MenuItem itemRecommencer = new MenuItem("Recommencer");
        itemRecommencer.setOnAction(e -> {
            game.resetGame();
            // Notification pour mettre à jour la vue du plateau
            fireEvent(new GameResetEvent());
        });

        // Ajout de l'option au menu
        menuParametres.getItems().add(itemRecommencer);

        // Ajout du menu à la barre de menus
        this.getMenus().add(menuParametres);
    }

    // Événement personnalisé pour la réinitialisation du jeu
    public static class GameResetEvent extends javafx.event.Event {
        public static final javafx.event.EventType<GameResetEvent> RESET =
                new javafx.event.EventType<>(javafx.event.Event.ANY, "RESET");

        public GameResetEvent() {
            super(RESET);
        }
    }
}
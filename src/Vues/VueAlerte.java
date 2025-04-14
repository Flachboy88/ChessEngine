package Vues;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class VueAlerte {

    public static void afficherEchec() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Échec");
        alert.setHeaderText("Le roi est en échec!");
        alert.setContentText("Vous devez protéger votre roi.");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    public static void afficherMouvementImpossible() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Mouvement impossible");
        alert.setHeaderText("Mouvement invalide");
        alert.setContentText("Ce mouvement mettrait votre roi en échec.");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    public static void afficherRoiEnDanger() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Roi en danger");
        alert.setHeaderText("Votre roi est en échec!");
        alert.setContentText("Vous devez d'abord protéger votre roi avant de déplacer d'autres pièces.");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    public static void afficherEchecEtMat(String gagnant) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Échec et mat");
        alert.setHeaderText("Fin de la partie");
        alert.setContentText("Échec et mat! Les " + gagnant + " ont gagné!");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
}
package Vues;

import Pieces.PieceColor;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VuePromotion {

    private static String selectedPiece = "Queen"; // Par défaut, promouvoir en Dame

    public static String afficherChoixPromotion(PieceColor couleur) {
        selectedPiece = "Queen"; // Réinitialiser à Dame par défaut

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Promotion du pion");
        dialog.setResizable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        HBox hbox = new HBox(20);
        hbox.setPadding(new Insets(10));

        // Préfixe de couleur pour les images
        String colorPrefix = couleur == PieceColor.WHITE ? "b" : "n";

        // Créer des boutons pour chaque pièce disponible pour la promotion
        Button queenButton = createPieceButton("D" + colorPrefix + ".png", "Queen", dialog);
        Button rookButton = createPieceButton("T" + colorPrefix + ".png", "Rook", dialog);
        Button bishopButton = createPieceButton("F" + colorPrefix + ".png", "Bishop", dialog);
        Button knightButton = createPieceButton("C" + colorPrefix + ".png", "Knight", dialog);

        // Ajouter les boutons au conteneur
        hbox.getChildren().addAll(queenButton, rookButton, bishopButton, knightButton);
        vbox.getChildren().add(hbox);

        Scene scene = new Scene(vbox);
        dialog.setScene(scene);
        dialog.showAndWait();

        return selectedPiece;
    }

    private static Button createPieceButton(String imageName, String pieceType, Stage dialog) {
        Button button = new Button();
        try {
            Image image = new Image("file:resources/images/" + imageName);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(60);
            imageView.setFitWidth(60);
            button.setGraphic(imageView);
        } catch(Exception e) {
            button.setText(pieceType);
        }

        button.setOnAction(e -> {
            selectedPiece = pieceType;
            dialog.close();
        });

        return button;
    }
}
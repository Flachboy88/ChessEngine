import Plateau.ChessGame;
import Vues.VueOutils;
import Vues.VuePlateau;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private ChessGame game;
    private VuePlateau vuePlateau;

    @Override
    public void start(Stage primaryStage) {
        // Initialisation du jeu
        game = new ChessGame();

        // Création du conteneur principal
        BorderPane root = new BorderPane();

        // Création des vues
        VueOutils vueOutils = new VueOutils(game);
        vuePlateau = new VuePlateau(game);

        // Gestion de l'événement de réinitialisation
        vueOutils.addEventHandler(VueOutils.GameResetEvent.RESET, e -> vuePlateau.update());

        // Placement des vues dans le conteneur
        root.setTop(vueOutils);
        root.setCenter(vuePlateau);

        // Création de la scène
        Scene scene = new Scene(root, 800, 850);

        // Configuration de la fenêtre
        primaryStage.setTitle("Jeu d'Échecs");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package Vues;

import Pieces.*;
import Plateau.ChessBoard;
import Plateau.ChessGame;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VuePlateau extends BorderPane {

    private static final int SQUARE_SIZE = 80;
    private static final int BOARD_SIZE = 8 * SQUARE_SIZE;

    private ChessGame game;
    private Canvas boardCanvas;
    private GraphicsContext gc;

    // Maps pour stocker les images des pièces
    private Map<String, Image> pieceImages;

    // Label pour afficher le statut du jeu
    private Label statusLabel;

    // Positions légales pour la pièce sélectionnée
    private List<Position> legalMoves;

    public VuePlateau(ChessGame game) {
        this.game = game;

        // Initialisation du canvas
        boardCanvas = new Canvas(BOARD_SIZE, BOARD_SIZE);
        gc = boardCanvas.getGraphicsContext2D();

        // Initialisation du label de statut
        statusLabel = new Label("Au tour des blancs");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        statusLabel.setPadding(new Insets(10));

        // Placement des composants dans le BorderPane
        this.setCenter(boardCanvas);
        this.setBottom(statusLabel);

        // Configuration de l'arrière-plan
        this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        // Chargement des images des pièces
        loadPieceImages();

        // Gestionnaire d'événements pour les clics sur l'échiquier
        boardCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleBoardClick);

        // Gestionnaire pour l'événement de réinitialisation
        this.addEventHandler(VueOutils.GameResetEvent.RESET, e -> {
            legalMoves = null;
            update();
        });

        // Affichage initial du plateau
        drawBoard();
    }

    private void loadPieceImages() {
        pieceImages = new HashMap<>();

        // Chargement des images depuis le dossier resources/images
        // Roi (King)
        pieceImages.put("whiteKing", new Image("file:resources/images/Rb.png"));
        pieceImages.put("blackKing", new Image("file:resources/images/Rn.png"));

        // Dame (Queen)
        pieceImages.put("whiteQueen", new Image("file:resources/images/Db.png"));
        pieceImages.put("blackQueen", new Image("file:resources/images/Dn.png"));

        // Fou (Bishop)
        pieceImages.put("whiteBishop", new Image("file:resources/images/Fb.png"));
        pieceImages.put("blackBishop", new Image("file:resources/images/Fn.png"));

        // Cavalier (Knight)
        pieceImages.put("whiteKnight", new Image("file:resources/images/Cb.png"));
        pieceImages.put("blackKnight", new Image("file:resources/images/Cn.png"));

        // Tour (Rook)
        pieceImages.put("whiteRook", new Image("file:resources/images/Tb.png"));
        pieceImages.put("blackRook", new Image("file:resources/images/Tn.png"));

        // Pion (Pawn)
        pieceImages.put("whitePawn", new Image("file:resources/images/Pb.png"));
        pieceImages.put("blackPawn", new Image("file:resources/images/Pn.png"));
    }

    public void update() {
        drawBoard();
        updateStatus();
    }

    private void drawBoard() {
        ChessBoard board = game.getBoard();

        // Dessin des cases de l'échiquier
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    gc.setFill(Color.BEIGE);
                } else {
                    gc.setFill(Color.DARKGREEN);
                }
                gc.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

                // Affichage des positions légales si une pièce est sélectionnée
                if (legalMoves != null) {
                    for (Position pos : legalMoves) {
                        if (pos.getRow() == row && pos.getColumn() == col) {
                            gc.setFill(Color.rgb(255, 255, 0, 0.5)); // Jaune semi-transparent
                            gc.fillOval(col * SQUARE_SIZE + SQUARE_SIZE / 3,
                                    row * SQUARE_SIZE + SQUARE_SIZE / 3,
                                    SQUARE_SIZE / 3, SQUARE_SIZE / 3);
                        }
                    }
                }
            }
        }

        // Dessin des pièces
        drawPieces(board);

        // Dessiner les coordonnées
        drawCoordinates();
    }

    private void drawPieces(ChessBoard board) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null) {
                    String imageKey = getPieceImageKey(piece);
                    Image pieceImage = pieceImages.get(imageKey);

                    if (pieceImage != null) {
                        // Dessiner l'image au centre de la case
                        gc.drawImage(pieceImage,
                                col * SQUARE_SIZE,
                                row * SQUARE_SIZE,
                                SQUARE_SIZE,
                                SQUARE_SIZE);
                    }
                }
            }
        }
    }

    private void drawCoordinates() {
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        gc.setFill(Color.BLACK);

        // Lettres pour les colonnes (A-H)
        for (int col = 0; col < 8; col++) {
            gc.fillText(String.valueOf((char)('A' + col)),
                    col * SQUARE_SIZE + SQUARE_SIZE - 15,
                    BOARD_SIZE - 5);
        }

        // Chiffres pour les rangées (1-8) (inversé pour correspondre à la notation d'échecs)
        for (int row = 0; row < 8; row++) {
            gc.fillText(String.valueOf(8 - row),
                    5,
                    row * SQUARE_SIZE + 15);
        }
    }

    private String getPieceImageKey(Piece piece) {
        String colorPrefix = piece.getColor() == PieceColor.WHITE ? "white" : "black";

        if (piece instanceof Pawn) {
            return colorPrefix + "Pawn";
        } else if (piece instanceof Rook) {
            return colorPrefix + "Rook";
        } else if (piece instanceof Knight) {
            return colorPrefix + "Knight";
        } else if (piece instanceof Bishop) {
            return colorPrefix + "Bishop";
        } else if (piece instanceof Queen) {
            return colorPrefix + "Queen";
        } else if (piece instanceof King) {
            return colorPrefix + "King";
        }
        return "";
    }

    private void updateStatus() {
        PieceColor currentPlayer = game.getCurrentPlayerColor();
        String playerTurn = currentPlayer == PieceColor.WHITE ? "blancs" : "noirs";

        if (game.isInCheck(currentPlayer)) {
            if (game.isCheckmate(currentPlayer)) {
                statusLabel.setText("Échec et mat ! Les " +
                        (currentPlayer == PieceColor.WHITE ? "noirs" : "blancs") +
                        " ont gagné !");
            } else {
                statusLabel.setText("Échec ! Au tour des " + playerTurn);
            }
        } else {
            statusLabel.setText("Au tour des " + playerTurn);
        }
    }

    private void handleBoardClick(MouseEvent event) {
        int col = (int) (event.getX() / SQUARE_SIZE);
        int row = (int) (event.getY() / SQUARE_SIZE);

        if (col >= 0 && col < 8 && row >= 0 && row < 8) {
            boolean moveMade = game.handleSquareSelection(row, col);

            // Si une pièce est sélectionnée, afficher les mouvements légaux
            if (game.isPieceSelected()) {
                Position selectedPos = new Position(row, col);
                legalMoves = game.getLegalMovesForPieceAt(selectedPos);
            } else {
                legalMoves = null;
            }

            // Si un mouvement a été effectué, mettre à jour le statut
            if (moveMade) {
                updateStatus();
            }

            // Redessiner le plateau
            drawBoard();
        }
    }
}
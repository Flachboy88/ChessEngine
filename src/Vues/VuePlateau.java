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

    private Position selectedSquare;
    private Position lastMoveFrom;
    private Position lastMoveTo;


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

        // Dessin des cases de l'échiquier avec un vert moins foncé
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    gc.setFill(Color.BEIGE);
                } else {
                    gc.setFill(Color.rgb(76, 153, 76)); // Vert moins foncé
                }
                gc.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

                // Surligner la dernière case de départ et d'arrivée
                if (lastMoveFrom != null && lastMoveTo != null) {
                    if ((row == lastMoveFrom.getRow() && col == lastMoveFrom.getColumn()) ||
                            (row == lastMoveTo.getRow() && col == lastMoveTo.getColumn())) {
                        gc.setFill(Color.rgb(255, 255, 153, 0.7)); // Jaune clair
                        gc.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                    }
                }

                // Surligner la case actuellement sélectionnée
                if (selectedSquare != null && row == selectedSquare.getRow() && col == selectedSquare.getColumn()) {
                    gc.setFill(Color.rgb(255, 255, 153, 0.7)); // Jaune clair
                    gc.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }

                // Affichage des positions légales si une pièce est sélectionnée
                if (legalMoves != null) {
                    for (Position pos : legalMoves) {
                        if (pos.getRow() == row && pos.getColumn() == col) {
                            gc.setFill(Color.rgb(50, 50, 50, 0.5)); // Noir clair semi-transparent
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
            Position clickedPos = new Position(row, col);

            // Si une pièce est déjà sélectionnée
            if (selectedSquare != null) {
                // Tenter de déplacer la pièce
                boolean moveMade = game.handleSquareSelection(row, col);

                if (moveMade) {
                    // Le mouvement a été effectué
                    lastMoveFrom = selectedSquare;
                    lastMoveTo = clickedPos;

                    // Enregistrer le coup dans l'historique
                    game.getHistorique().ajouterCoup(selectedSquare, clickedPos,
                            game.getBoard().getPiece(clickedPos.getRow(), clickedPos.getColumn()));

                    // Mettre à jour la vue de l'historique
                    fireEvent(new MoveEvent(selectedSquare, clickedPos));

                    // Réinitialiser la sélection
                    selectedSquare = null;
                    legalMoves = null;

                    // Mettre à jour le statut
                    updateStatus();
                } else {
                    // Si le mouvement n'est pas valide, vérifier si on clique sur une autre pièce amie
                    Piece clickedPiece = game.getBoard().getPiece(row, col);
                    if (clickedPiece != null &&
                            clickedPiece.getColor() == game.getCurrentPlayerColor()) {
                        // Sélectionner la nouvelle pièce
                        selectedSquare = clickedPos;
                        legalMoves = game.getLegalMovesForPieceAt(selectedSquare);
                    } else {
                        // Clic invalide, réinitialiser la sélection
                        selectedSquare = null;
                        legalMoves = null;
                    }
                }
            }
            // Si aucune pièce n'est sélectionnée
            else {
                Piece clickedPiece = game.getBoard().getPiece(row, col);

                // Vérifier si la case contient une pièce et si elle appartient au joueur actuel
                if (clickedPiece != null &&
                        clickedPiece.getColor() == game.getCurrentPlayerColor()) {
                    // Sélectionner la pièce
                    selectedSquare = clickedPos;
                    legalMoves = game.getLegalMovesForPieceAt(selectedSquare);

                    // Utiliser handleSquareSelection pour mettre à jour l'état du jeu
                    game.handleSquareSelection(row, col);
                }
            }

            // Redessiner le plateau
            drawBoard();
        }
    }

    public void resetLastMove() {
        lastMoveFrom = null;
        lastMoveTo = null;
        drawBoard();
    }

    public static class MoveEvent extends javafx.event.Event {
        public static final javafx.event.EventType<MoveEvent> MOVE_MADE =
                new javafx.event.EventType<>(javafx.event.Event.ANY, "MOVE_MADE");

        private Position from;
        private Position to;

        public MoveEvent(Position from, Position to) {
            super(MOVE_MADE);
            this.from = from;
            this.to = to;
        }

        public Position getFrom() {
            return from;
        }

        public Position getTo() {
            return to;
        }
    }

    public void afficherEtatHistorique(Position from, Position to) {
        // Surligner les cases du coup
        lastMoveFrom = from;
        lastMoveTo = to;
        drawBoard();
    }

}
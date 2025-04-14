package Pieces;

public abstract class Piece {

    protected Position position;
    protected PieceColor color;
    protected boolean hasMoved; // Pour suivre si la pièce a déjà bougé

    public Piece(PieceColor color, Position position){
        this.color = color;
        this.position = position;
        this.hasMoved = false;
    }

    public PieceColor getColor(){
        return this.color;
    }

    public Position getPosition(){
        return this.position;
    }

    public void setPosition(Position position){
        this.position = position;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved() {
        this.hasMoved = true;
    }

    public abstract boolean isValidMove(Position newPosition, Piece[][] board);
}
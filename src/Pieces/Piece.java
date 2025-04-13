package Pieces;

public abstract class Piece {

    protected Position position;
    protected PieceColor color;

    public Piece(PieceColor color, Position position){
        this.color = color;
        this.position = position;
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

    public abstract boolean isValidMove(Position newPosition, Piece[][] board);

}

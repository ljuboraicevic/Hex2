package tictactoe;

/**
 * Class Coordinate represents row and column coordinates of a single field on
 * the board.
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public final class Coordinate {
    public int row;
    public int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    @Override
    public String toString() {
        return row + ", " + col;
    }
}

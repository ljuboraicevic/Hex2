package hex2;

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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coordinate other = (Coordinate) obj;
        if (this.row != other.row) {
            return false;
        }
        if (this.col != other.col) {
            return false;
        }
        return true;
    }
    
    
    
    @Override
    public String toString() {
        return row + ", " + col;
    }
}

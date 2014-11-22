package tictactoe;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class TicTacToe {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Board b = new Board(3);
//        b.putMark(new Coordinate(0, 0), (byte)1);
//        b.putMark(new Coordinate(1, 1), (byte)2);
//        b.putMark(new Coordinate(2, 2), (byte)1);
//        b.putMark(new Coordinate(1, 2), (byte)2);
        MonteCarlo mc = new MonteCarlo(10000, 1, true);
        MCSimulationMove[] evaluateBoard = mc.evaluateBoard(b);
        System.out.println("");
    }
    
}

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
        
        //MCSimulationMove[] evaluateBoard = MonteCarlo.evaluateBoard(b, 5000, 1);
        System.out.println("");
        
//        PlayerHuman ph1 = new PlayerHuman();
//        PlayerHuman ph2 = new PlayerHuman();
//        PlayerMonteCarlo pmc1 = new PlayerMonteCarlo(50000, 1, true);
//        PlayerMonteCarlo pmc2 = new PlayerMonteCarlo(50000, 1, true);
//        
//        Game g = new Game(b, pmc1, pmc2);
//        g.play();
        
        for (int iCount = 0; iCount < 100; iCount++) {
            System.out.println(RandomBoardGenerator.makeUpARandomBoard(3));
        }
    }
    
}

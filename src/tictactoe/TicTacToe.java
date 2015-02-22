package tictactoe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class TicTacToe {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
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
        
        StringBuilder sb = new StringBuilder();
        
        for (int iCount = 0; iCount < 100; iCount++) {
            //System.out.println(RandomBoardGenerator.makeUpARandomBoard(3));
            Board board = RandomBoardGenerator.makeUpARandomBoard(3);
            MonteCarlo.evaluateBoardNoParallel(board, 1000, sb);
        }
        
        try (FileWriter fw = new FileWriter(new File("abcd"))) {
            fw.write(sb.toString());
        }
    }
    
}

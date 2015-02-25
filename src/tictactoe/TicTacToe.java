package tictactoe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class TicTacToe {

    /* CONSTANTS */
    private static final int boardSize = 3;
    private static final int MCRepetitions = 50000;
    private static final String NNFileName = "fannnetworks/fifth.net";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* INITIALIZE THE PLAYERS */
        PlayerHuman ph1 = new PlayerHuman();
        PlayerMonteCarlo pmc1 = new PlayerMonteCarlo(MCRepetitions);
        PlayerMonteCarlo pmc2 = new PlayerMonteCarlo(MCRepetitions);
        PlayerNeuralNetwork pnn1 = new PlayerNeuralNetwork(NNFileName);
        
        multipleGames(pnn1, pmc2, 10);
    }
        
    public static void singleGame(Player p1, Player p2) {
        Board b = new Board(boardSize);
        Game g = new Game(b, p1, p2);
        g.play();
    }
    
    public static void multipleGames(Player p1, Player p2, int repetitions) {
        int[] wins = new int[boardSize];
        
        for (int iCount = 0; iCount < repetitions; iCount++) {
            Board b = new Board(boardSize);
            Game g = new Game(b, p1, p2);
            int winner = g.play();
            wins[winner]++;
        }
        
        System.out.println(Arrays.toString(wins));
    }
    
    public static void dataGeneration(int repetitions, String fileName) 
            throws IOException {
        
        StringBuilder sb = new StringBuilder();
        
        for (int iCount = 0; iCount < repetitions; iCount++) {
            Board board = RandomBoardGenerator.makeUpARandomBoard(boardSize);
            MonteCarlo.evaluateBoardNoParallel(board, repetitions, sb);
            if (iCount % repetitions == 0) { System.out.println(iCount); }
        }
        
        try (FileWriter fw = new FileWriter(new File(fileName))) {
            fw.write(sb.toString());
        }
    }
}

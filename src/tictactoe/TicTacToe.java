package tictactoe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class TicTacToe {

    /* CONSTANTS */
    private static final int boardSize = 3;
    private static final int MCRepetitions = 1000;
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
        
        multipleGames(pmc1, pmc2, 100);
        //dataGeneration(10000, "datasets/newdata");
    }

    /**
     * Plays a single game.
     * 
     * @param p1 First player
     * @param p2 Second player
     */
    public static void singleGame(Player p1, Player p2) {
        Board b = new Board(boardSize);
        Game g = new Game(b, p1, p2);
        g.play();
    }
    
    /**
     * Plays multiple games and reports the winners.
     * 
     * @param p1 First player
     * @param p2 Second player
     * @param repetitions How many games
     */
    public static void multipleGames(Player p1, Player p2, int repetitions) {
        int[] wins = new int[3];
        
        for (int iCount = 0; iCount < repetitions; iCount++) {
            Board b = new Board(boardSize);
            Game g = new Game(b, p1, p2);
            int winner = g.play();
            wins[winner]++;
        }
        
        System.out.println(Arrays.toString(wins));
    }
    
    /**
     * Generates data from Monte Carlo simulations by evaluating random boards.
     * 
     * @param repetitions How many random boards should be evaluated
     * @param fileName Filename where data will be saved 
     */
    public static void dataGeneration(int repetitions, String fileName) {
        
        StringBuilder sb = new StringBuilder();
        
        for (int iCount = 0; iCount < repetitions; iCount++) {
            Board board = RandomBoardGenerator.makeUpARandomBoard(boardSize);
            MonteCarlo.evaluateBoard(board, repetitions, sb);
            if (iCount % 1000 == 0) { System.out.println(iCount); }
        }
        
        try (FileWriter fw = new FileWriter(new File(fileName))) {
            fw.write(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

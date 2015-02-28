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
    private static final int MCRepetitions = 10000;
    private static final String NNFileName = "fannnetworks/newdata-2layers.net";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* INITIALIZE THE PLAYERS */
        PlayerHuman h = new PlayerHuman();
        PlayerMonteCarlo mc = new PlayerMonteCarlo(MCRepetitions);
        PlayerNeuralNetwork nn = new PlayerNeuralNetwork(NNFileName);
        
        singleGame(nn, h);
        //multipleGames(nn, mc, 300);
        //randomDataGeneration(50000, "datasets/datawithzeros.dat");
        //monteCarloDataGeneration(MCRepetitions, MCRepetitions, 200, "datasets/montecarlodata");
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
    public static void randomDataGeneration(int repetitions, String fileName) {
        
        StringBuilder sb = new StringBuilder();
        
        for (int iCount = 0; iCount < repetitions; iCount++) {
            Board board = RandomBoardGenerator.makeUpARandomBoard(boardSize);
            MonteCarlo.evaluateBoard(board, MCRepetitions, sb);
            if (iCount % 1000 == 0) { System.out.println(iCount); }
        }
        
        try (FileWriter fw = new FileWriter(new File(fileName))) {
            fw.write(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Plays games between two MonteCarlo players and records them.
     * 
     * @param p1MCRepetitions Number of MonteCarlo repetitions for player 1
     * @param p2MCRepetitions Number of MonteCarlo repetitions for player 2
     * @param noOfGames How many games will be played between the two players
     * @param fileName  File to which the data is saved
     */
    public static void monteCarloDataGeneration(
            int p1MCRepetitions, 
            int p2MCRepetitions,
            int noOfGames,
            String fileName) {
        
        StringBuilder sb = new StringBuilder();
        //player one's moves get recorded in the StringBuilder
        PlayerMonteCarlo p1 = new PlayerMonteCarlo(p1MCRepetitions, sb);
        //player two's moves don't get recorded
        PlayerMonteCarlo p2 = new PlayerMonteCarlo(p2MCRepetitions);
        
        //play the games
        multipleGames(p1, p2, noOfGames);
        
        //save data to file
        try (FileWriter fw = new FileWriter(new File(fileName))) {
            fw.write(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
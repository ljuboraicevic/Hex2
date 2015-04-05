package hex2;

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
public class Hex2 {

    /* CONSTANTS */
    private static final int boardSize = 5;
    private static final int MCRepetitions = 3000;
    private static final String NNFileName = "fannnetworks/series2/cuniform-2layers.net";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* INITIALIZE THE PLAYERS */
        //StringBuilder sb = new StringBuilder();
        PlayerHuman h = new PlayerHuman();
        PlayerMonteCarlo mc = new PlayerMonteCarlo(MCRepetitions);
        PlayerMonteCarlo mc2 = new PlayerMonteCarlo(MCRepetitions);
        PlayerBranchingMonteCarlo bmc = new PlayerBranchingMonteCarlo(MCRepetitions, 2, 2);
        //PlayerNeuralNetwork nn = new PlayerNeuralNetwork(NNFileName);
        
        compareMCwithBranchingMC();
        //singleGame(bmc, mc2);
        //multipleGames(bmc, mc, 10);
        //randomDataGeneration(1, "test");
        //monteCarloDataGeneration(10000, 3, 30, "datasets/series2/cuniform");
        
//        try (FileWriter fw = new FileWriter(new File("test"))) {
//            fw.write(sb.toString());
//        } catch (IOException ex) {
//            Logger.getLogger(Hex2.class.getName()).log(Level.SEVERE, null, ex);
//        }
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
    
    public static void compareMCwithBranchingMC() {
        
        int noOfBoards = 100;
        int plies = 2;
        int best = 2;
        int repetitions = 100;
        
        MCSimulationMove[] classicMoves = new MCSimulationMove[noOfBoards];
        MCSimulationMove[] branchingMoves = new MCSimulationMove[noOfBoards];
        
        int[] noOfSwitchedBoards = new int[noOfBoards];
        int totalSwitches = 0;
        double[] averageSwitchProbabilityDiff = new double[noOfBoards];
        double totalAverageProbabilityDiff = 0;
        
        //generate array of random boards (e.g. 1000)
        Board[] boards = new Board[noOfBoards];
        for (int iCount = 0; iCount < noOfBoards; iCount++) {
            boards[iCount] = RandomBoardGenerator.makeUpARandomBoard(boardSize);
        }
        
        //repeat the whole experiment "repetition" times
        for (int repCount = 0; repCount < repetitions; repCount++) {
            System.out.println(repCount);
            //for each board do x100
            //for each of the boards PlayerBranching do a makeMove
            for (int boardCount = 0; boardCount < noOfBoards; boardCount++) {
                BranchingLogic logic = new BranchingLogic(boards[boardCount], MCRepetitions, plies, best);
                MCSimulationMove classicMove   = logic.getMCMove();
                MCSimulationMove branchingMove = logic.getMCBranchingMove();
                classicMoves[boardCount]   = classicMove;
                branchingMoves[boardCount] = branchingMove;
                
                //if branching and classicMC have chosen different boards
                if (!classicMove.getCoordinates().equals(branchingMove.getCoordinates())) {
                    noOfSwitchedBoards[boardCount]++;
                    averageSwitchProbabilityDiff[boardCount] += 
                            classicMove.getProbability() - branchingMove.getProbability();
                    totalSwitches++;
                    totalAverageProbabilityDiff += 
                            classicMove.getProbability() - branchingMove.getProbability();
                }
            }
        }
        
        //divide averageSwitchProbabilityDiffs to get the average
        for (int iCount = 0; iCount < noOfBoards; iCount++) {
            averageSwitchProbabilityDiff[iCount] /= repetitions * MCRepetitions;
        }
        
        totalAverageProbabilityDiff /= repetitions * MCRepetitions * noOfBoards;
        
        System.out.println(Arrays.toString(noOfSwitchedBoards));
        System.out.println(Arrays.toString(averageSwitchProbabilityDiff));
        System.out.println((totalSwitches * 1.0) / (noOfBoards * repetitions));
        System.out.println(totalAverageProbabilityDiff);
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
            byte winner = g.play();
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
            Logger.getLogger(Hex2.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Hex2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
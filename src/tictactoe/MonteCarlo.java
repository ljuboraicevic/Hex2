package tictactoe;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class MonteCarlo {

    /**
     * Should not be instantiated.
     */
    public MonteCarlo() {}
    
    /**
     * Returns array of MCSimulationMove that contains all possible moves and 
     * their probabilities. First element in array is best move, chosen by MonteCarlo
     * @param b
     * @param repetitions
     * @param threads
     * @return 
     */
    public static MCSimulationMove[] evaluateBoard(
            Board b, 
            int repetitions, 
            int threads) {
        //make a deep copy of the board for each thread
        Board[] boardCopies = new Board[threads];
        for (int iCount = 0; iCount < threads; iCount++) {
            boardCopies[iCount] = b.deepCopy();
        }
        
        int noOfEmptyFields = b.getNoOfEmptyFields();
        int movesPlayed = b.getSize() * b.getSize() - noOfEmptyFields;
        byte player = b.whosOnTheMove();
        
        //create simulation threads
        MonteCarloThread[] simArray = new MonteCarloThread[threads];
        int fields = noOfEmptyFields / threads;
        int iCount;
        for (iCount = 0; iCount < threads - 1; iCount++) {
            simArray[iCount] = new MonteCarloThread(
                    boardCopies[iCount], 
                    b,
                    iCount * fields, 
                    (iCount + 1) * fields , 
                    repetitions, 
                    movesPlayed, 
                    player);
        }
        
        //last simulation
        simArray[threads - 1] = new MonteCarloThread(
                boardCopies[threads - 1], 
                b,
                iCount * fields, 
                noOfEmptyFields, 
                repetitions, 
                movesPlayed, 
                player);
        
        //start all threads
        for (MonteCarloThread mct : simArray) {
            mct.start();
        }
        
        //join, so that everything bellow has to wait until they're done
        for (MonteCarloThread mct : simArray) {
            try {
                mct.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(MonteCarlo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //creating array of all moves
        MCSimulationMove[] allMoves = new MCSimulationMove[noOfEmptyFields];
        
        int counter = 0;
        //copying moves from thread - simulation to one array
        for (int jCount = 0; jCount < threads; jCount++) {
            MCSimulationMove[] help = simArray[jCount].getAllMoves();
            for (iCount = 0; iCount < help.length; iCount++) {
                allMoves[counter++] = help[iCount];
            }
        }
        
        Arrays.sort(allMoves, Comparator.reverseOrder());
        
        return allMoves;
    }
    
    /**
     * Makes a random sequence of moves.
     * 
     * @param movesPlayed
     * @param boardSize
     * @return 
     */
    public static byte[] getRandomSequence(int movesPlayed, int boardSize) {
        byte[] result = getSequence(movesPlayed, boardSize);
        shuffleArray(result);
        return result;
    }

    /**
     * Makes a non-random sequence of moves.
     * 
     * @param movesPlayed
     * @param boardSize
     * @return 
     */
    private static byte[] getSequence(int movesPlayed, int boardSize) {
        byte[] result = new byte[boardSize - movesPlayed - 1];
        int ones = getNumberOfFirstPlayersMoves(movesPlayed, boardSize);
        
        //fill in the ones and twos
        for (int iCount = 0; iCount < result.length; iCount++) {
            result[iCount] = iCount < ones ? (byte) 1 : (byte) 2;
        }
        
        return result;
    }
    
    /**
     * Returns how many ones or first player's moves should be in the random
     * sequence.
     * 
     * @param movesPlayed Moved played so far in the game
     * @param boardSize Size of the board
     * @return Number of first players moves
     */
    private static int getNumberOfFirstPlayersMoves(int movesPlayed, int boardSize) {
        int length = boardSize - movesPlayed - 1;
        int result = (int) Math.floor(length / 2);
        
        if (movesPlayed % 2 == 1) {
            if (length % 2 == 1) { result += 1; }
        }
       
        return result;
    }
    
    /**
     * Shuffle the byte array
     * 
     * @param ar Array of bytes
     */
    private static void shuffleArray(byte[] ar) {
        Random random = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            byte a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
    
    /**
     * Check if player won on the board b.
     * 
     * @param b
     * @param player
     * @return True if player won, false otherwise.
     */
    public static boolean didPlayerWin(Board b, byte player) {
        int wonSum = player * b.getSize();

        //check rows
        for (int row = 0; row < b.getSize(); row++) {
            int rowSum = 0;
            for (int col = 0; col < b.getSize(); col++) {
                rowSum += b.getFieldMark(new Coordinate(row, col));
            }
            
            if (rowSum == wonSum) { return true; }
        }

        //check columns
        for (int col = 0; col < b.getSize(); col++) {
            int colSum = 0;
            for (int row = 0; row < b.getSize(); row++) {
                colSum += b.getFieldMark(new Coordinate(row, col));
            }
            
            if (colSum == wonSum) { return true; }
        }
        
        int diag1Sum = 0;
        int diag2Sum = 0;
        
        //check diagonals
        for (int row = 0; row < b.getSize(); row++) {
            diag1Sum += b.getFieldMark(new Coordinate(row, row));
            diag2Sum += b.getFieldMark(new Coordinate(row, (b.getSize() - row - 1)));
        }
        
        if (diag1Sum == wonSum || diag2Sum == wonSum) { return true; }
        
        return false;
    }
}
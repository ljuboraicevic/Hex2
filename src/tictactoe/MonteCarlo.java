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
     * Determines how many times Monte Carlo simulation is run.
     */
    private final int repetitions;
    
    /**
     * How many threads should be used when doing the simulation.
     */
    private final int threads;
    
    /**
     * if there are two or more results with best result, should return first one
     * or randomly chosen
     */
    private final boolean randomizeBest;
    
    /**
     * Initializes a new PlayerMonteCarlo.
     * 
     * @param repetitions Monte Carlo simulation repetitions
     * @param threads How many threads to use during the simulation
     * @param randomizeBest if there are two or more results with best result, 
     * should return first one or randomly chose among them
     */
    public MonteCarlo(int repetitions, int threads, boolean randomizeBest) {
        this.repetitions = repetitions;
        this.threads = threads;
        this.randomizeBest = randomizeBest;
    }
    
    public int getNumberOfRepetitions(){
        return this.repetitions;
    }
    
    /**
     * Returns array of MCSimulationMove that contains all possible moves and 
     * their probabilities. First element in array is best move, chosen by MonteCarlo
     * @param b
     * @return 
     */
    public MCSimulationMove[] evaluateBoard(Board b) {
        //make a deep copy of the board for each thread
        Board[] boardCopies = new Board[threads];
        for (int iCount = 0; iCount < threads; iCount++) {
            boardCopies[iCount] = b.deepCopy();
        }
        
        //get coordinates of empty fields in the board
//        Coordinate[] emptyFields = b.getEmptyFields();
        
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
//                allMoves[counter++] = new MCSimulationMove(
//                                help[iCount].getCoordinates(), 
//                                help[iCount].getProbability());
            }
        }
        
        Arrays.sort(allMoves, Comparator.reverseOrder());
        
//        if (this.randomizeBest && allMoves.length > 1){
//            int numberOfBestResults = 0;
//            //determine how many best results exists
//            while (numberOfBestResults!= allMoves.length - 1 && 
//            allMoves[numberOfBestResults].compareTo(allMoves[numberOfBestResults + 1]) == 0){
//                numberOfBestResults++;
//            }
//            //if more then one best result
//            if(numberOfBestResults > 1){
//                //chose randomly one
//                int randomIndex = (int) Math.floor(Math.random() * numberOfBestResults);
//                MCSimulationMove temp = new MCSimulationMove(allMoves[randomIndex].getCoordinates(), 
//                allMoves[randomIndex].getProbability());
//                allMoves[randomIndex] = new MCSimulationMove(
//                allMoves[0].getCoordinates(), allMoves[0].getProbability());
//                allMoves[0] = temp;
//            }                        
//        }
        
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
    static void shuffleArray(byte[] ar) {
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
        
        //check diagonals
        for (int row = 0; row < b.getSize(); row++) {
            int diag1Sum = 0;
            int diag2Sum = 0;
            diag1Sum += b.getFieldMark(new Coordinate(row, row));
            diag2Sum += b.getFieldMark(new Coordinate(row, (b.getSize() - row - 1)));
            if (diag1Sum == wonSum || diag1Sum == wonSum) { return true; }
        }
        
        return false;
    }
    
    
//    /**
//     * Checks if player won game. Checks from top to bottom in each row if there is vertical player
//     * fields (1) that are somehow connected to active vertical players fields from previous row
//     * If it doesn't find any active vertical field in row, then halt => player two won
//     * 
//     * @param b
//     * @param player == 0 => vertical player's move, player == 1 => horizontal player's move
//     * @return 
//     */
//    public static boolean didIWin(Board b, byte player) {
//        //booleans that indicate that on [i] position in currentRow there is 
//        //active vertical field field that's marked by player0
//        boolean[] activeVerticalFieldsInRow = new boolean[b.size];
//        
//        //for first row all "previous" row fields are active 
//        for (int i = 0; i < b.size; i++) {
//            activeVerticalFieldsInRow[i] = true;
//        }
//        
//        int currentRow = 0;
//        boolean winnerUndetermined = true;
//        byte playerWon = 0;
//        
//        while (winnerUndetermined && currentRow < b.size) {
//            activeVerticalFieldsInRow = getPotentialsInRow(b, currentRow, activeVerticalFieldsInRow);
//            checkForMissedActiveFields(b, currentRow, activeVerticalFieldsInRow);
//            if (anyVerticalPlayerOnPotentialFieldInRow(b, currentRow, activeVerticalFieldsInRow)) {
//                currentRow++;
//            } else {
//                winnerUndetermined = false;
//                playerWon = 1;
//            }
//        }
//        return playerWon == player;
//    }
    
//    /**
//     * checks if there is any active field in currentRow
//     * @param b
//     * @param row
//     * @param activeFields
//     * @return 
//     */
//    private static boolean anyVerticalPlayerOnPotentialFieldInRow(
//            Board b, 
//            int row, 
//            boolean[] activeFields) {
//        
//        for (int i = 0; i < b.size; i++) {
//            if (activeFields[i] && b.isFieldVertical(new Coordinate(row, i))) {
//                return true;
//            }
//        }
//        return false;
//
//    }
//    /**
//     * marks all fields that are active based on connection with active fields from previous row
//     * this doesn't find all active fields in row, only ones that are connected with previous row
//     * ex: t = 
//     * 1 2 2
//     * 1 1 1
//     * 2 2 1
//     * t[1][1] won't be marked as active, because fields from previous row that t[1][1]is connected to
//     * (t[0][1],t[0][2]) are not active
//     * @param b
//     * @param row
//     * @param previousRowActiveFields
//     * @return 
//     */
//    private static boolean[] getPotentialsInRow(Board b, int row, boolean[] previousRowActiveFields) {
//        boolean potentials[] = new boolean[b.size];
//        for (int i = 0; i < b.size - 1; i++) {
//            potentials[i] = b.isFieldVertical(new Coordinate(row, i)) && (previousRowActiveFields[i] || previousRowActiveFields[i + 1]);
//        }
//        potentials[b.size - 1] = b.isFieldVertical(new Coordinate(row, b.size - 1))
//                && previousRowActiveFields[b.size - 1];
//        return potentials;
//    }
//    /**
//     * finds active fields in row that are horizontally connected to some active field
//     * It has to be started after getPotentialsInRow()
//     * This function marks t[1][1] and t[1][2] from previous example as active
//     * based on connection with t[1][0] that is marked as active by getPotentialsInRow()
//     * @param b
//     * @param currentRow
//     * @param activeVerticalFieldsInRow 
//     */
//    private static void checkForMissedActiveFields(Board b, int currentRow, boolean[] activeVerticalFieldsInRow) {
//        for (int i = 1; i < b.size; i++) {
//            if (!activeVerticalFieldsInRow[i] && b.isFieldVertical(new Coordinate(currentRow, i))) {
//                activeVerticalFieldsInRow[i] = activeVerticalFieldsInRow[i - 1];
//            }
//        }
//        for (int i = b.size - 2; i > 0; i--) {
//            if (!activeVerticalFieldsInRow[i] && b.isFieldVertical(new Coordinate(currentRow, i))) {
//                activeVerticalFieldsInRow[i] = activeVerticalFieldsInRow[i + 1];
//            }
//        }
//    }
//
}
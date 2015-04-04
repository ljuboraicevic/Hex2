package hex2;

import java.util.Arrays;
import java.util.Comparator;

/**
 * MonteCarlo class contains static methods that are used by PlayerMonteCarlo
 * and randomDataGeneration in TitTacToe.java.
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class MonteCarlo {

    /**
     * Returns array of MCSimulationMove that contains all possible moves and 
     * their probabilities. First element in array is best move, chosen by 
     * MonteCarlo.
     * 
     * @param b
     * @param repetitions
     * @param sb 
     * @return 
     */
    public static MCSimulationMove[] evaluateBoard(
            Board b, 
            int repetitions,
            StringBuilder sb) {
        
        Board boardCopy = b.deepCopy();
        int noOfEmptyFields = b.getNoOfEmptyFields();
        int movesPlayed = b.getSize() * b.getSize() - noOfEmptyFields;
        byte player = b.whosOnTheMove();
        Coordinate[] emptyFields = b.getEmptyFields();
        MCSimulationMove[] moves = new MCSimulationMove[noOfEmptyFields];

        //for each of the empty fields on the board
        for (int field = 0; field < noOfEmptyFields; field++) {

            int thisFieldWinSum = 0;
            
            //mark current "empty" field as this player's and then run the
            //simulation on the rest of the empty fields
            boardCopy.putMarkHard(
                    new Coordinate(emptyFields[field].row, emptyFields[field].col), 
                    player
            );
            
            //make repetitions
            for (int repetition = 0; repetition < repetitions; repetition++) {

                byte[] sequence = MonteCarlo.getRandomSequence(
                        movesPlayed, 
                        boardCopy.getSize() * boardCopy.getSize());

                //overlay the random sequence on top of the boardCopy 
                //skipping the field that's being tested
                int seqCount = 0;
                for (int iCount = 0; iCount < emptyFields.length; iCount++) {
                    if (iCount != field) {
                        Coordinate c = emptyFields[iCount];
                        boardCopy.putMarkHard(
                                new Coordinate(c.row, c.col), sequence[seqCount]);
                    }
                    seqCount++;
                }

                //check if current player won
                if (MonteCarlo.didPlayerWin(boardCopy, player)) {
                    thisFieldWinSum++;
                }
            }
            
            //write down the result for this empty field
            moves[field] = new MCSimulationMove(emptyFields[field], thisFieldWinSum * 1.0);
        }

        //sort the moves array so that the first item has the highes probability
        Arrays.sort(moves, Comparator.reverseOrder());
        
        //add data to StringBuilder
        if (sb != null) {
            Board boardRecord = b.deepCopy();
            
            //for each of the fields in moves
            for (int iCount = 0; iCount < noOfEmptyFields; iCount++) {
                //add players mark
                boardRecord.putMarkHard(moves[iCount].getCoordinates(), player);
                
                //add a new line
                sb.append(boardRecord.toSingleRowString(false));
                sb.append(iCount == 0 ? 
                        moves[iCount].getProbability() / repetitions : 
                        (moves[iCount].getProbability() / repetitions) * 1.0);
                sb.append(System.lineSeparator());

                //remove players mark
                boardRecord.putMarkHard(moves[iCount].getCoordinates(), (byte)0);
            }
        }
        
        return moves;
    }
    
    /**
     * Makes a random sequence of moves.
     * 
     * @param movesPlayed
     * @param boardSize
     * @return 
     */
    private static byte[] getRandomSequence(int movesPlayed, int boardSize) {
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
        byte[] result = new byte[boardSize - movesPlayed];
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
    private static int getNumberOfFirstPlayersMoves(int movesPlayed, 
            int boardSize) {
        
        int length = boardSize - movesPlayed - 1;
        int result = (int) Math.floor(length / 2);
        
        if (movesPlayed % 2 == 1) {
            if (length % 2 == 1) { result += 1; }
        }
       
        return result;
    }
    
    /**
     * Shuffles the byte array
     * 
     * @param ar Array of bytes
     */
    public static void shuffleArray(byte[] ar) {
        int N = ar.length;
        for (int i = 0; i < N; i++) {
            // choose index uniformly in [i, N-1]
            int r = i + (int) (Math.random() * (N - i));
            byte swap = ar[r];
            ar[r] = ar[i];
            ar[i] = swap;
        }
    }
    
    /**
     * Checks if player won on the board b. Checks from top to bottom in each 
     * row if there is vertical player fields (1) that are somehow connected to 
     * active vertical players fields from previous row. If it doesn't find any 
     * active vertical field in row, then halt => player two won.
     * 
     * @param b
     * @param player
     * @return True if player won, false otherwise.
     */
    public static boolean didPlayerWin(Board b, byte player) {
        //booleans that indicate that on [i] position in currentRow there is 
        //active vertical field field that's marked by player0
        boolean[] activeVerticalFieldsInRow = new boolean[b.getSize()];
        
        //for first row all "previous" row fields are active 
        for (int i = 0; i < b.getSize(); i++) {
            activeVerticalFieldsInRow[i] = true;
        }
        
        int currentRow = 0;
        boolean winnerUndetermined = true;
        byte playerWon = 1;
        
        while (winnerUndetermined && currentRow < b.getSize()) {
            activeVerticalFieldsInRow = getPotentialsInRow(b, currentRow, activeVerticalFieldsInRow);
            checkForMissedActiveFields(b, currentRow, activeVerticalFieldsInRow);
            if (anyVerticalPlayerOnPotentialFieldInRow(b, currentRow, activeVerticalFieldsInRow)) {
                currentRow++;
            } else {
                winnerUndetermined = false;
                playerWon = 2;
            }
        }
        return playerWon == player;
    }    
    
    /**
     * checks if there is any active field in currentRow
     * @param b
     * @param row
     * @param activeFields
     * @return 
     */
    private static boolean anyVerticalPlayerOnPotentialFieldInRow(
            Board b, 
            int row, 
            boolean[] activeFields) {
        
        for (int i = 0; i < b.getSize(); i++) {
            if (activeFields[i] && b.isFieldVertical(new Coordinate(row, i))) {
                return true;
            }
        }
        return false;

    }
    /**
     * marks all fields that are active based on connection with active fields from previous row
     * this doesn't find all active fields in row, only ones that are connected with previous row
     * ex: t = 
     * 1 2 2
     * 1 1 1
     * 2 2 1
     * t[1][1] won't be marked as active, because fields from previous row that t[1][1]is connected to
     * (t[0][1],t[0][2]) are not active
     * @param b
     * @param row
     * @param previousRowActiveFields
     * @return 
     */
    private static boolean[] getPotentialsInRow(Board b, int row, boolean[] previousRowActiveFields) {
        boolean potentials[] = new boolean[b.getSize()];
        for (int i = 0; i < b.getSize() - 1; i++) {
            potentials[i] = b.isFieldVertical(new Coordinate(row, i)) && (previousRowActiveFields[i] || previousRowActiveFields[i + 1]);
        }
        potentials[b.getSize() - 1] = b.isFieldVertical(new Coordinate(row, b.getSize() - 1))
                && previousRowActiveFields[b.getSize() - 1];
        return potentials;
    }
    /**
     * finds active fields in row that are horizontally connected to some active field
     * It has to be started after getPotentialsInRow()
     * This function marks t[1][1] and t[1][2] from previous example as active
     * based on connection with t[1][0] that is marked as active by getPotentialsInRow()
     * @param b
     * @param currentRow
     * @param activeVerticalFieldsInRow 
     */
    private static void checkForMissedActiveFields(Board b, int currentRow, boolean[] activeVerticalFieldsInRow) {
        for (int i = 1; i < b.getSize(); i++) {
            if (!activeVerticalFieldsInRow[i] && b.isFieldVertical(new Coordinate(currentRow, i))) {
                activeVerticalFieldsInRow[i] = activeVerticalFieldsInRow[i - 1];
            }
        }
        for (int i = b.getSize() - 2; i > 0; i--) {
            if (!activeVerticalFieldsInRow[i] && b.isFieldVertical(new Coordinate(currentRow, i))) {
                activeVerticalFieldsInRow[i] = activeVerticalFieldsInRow[i + 1];
            }
        }
    }
}
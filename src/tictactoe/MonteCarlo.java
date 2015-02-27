package tictactoe;

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
                
                //get random sequence
                byte[] sequence = MonteCarlo.getRandomSequence(
                        movesPlayed, 
                        boardCopy.getSize() * boardCopy.getSize());
                
                //overlay the random sequence on top of the boardCopy
                int seqCount = 0;
                for (int iCount = 0; iCount < emptyFields.length; iCount++) {
                    if (iCount != field) {
                        Coordinate c = emptyFields[iCount];
                        boardCopy.putMarkHard(
                                new Coordinate(c.row, c.col), sequence[seqCount]);
                    }
                    seqCount++;
                }

                //check if current player won and the other player didn't win
                //if (MonteCarlo.didPlayerWin(boardCopy, player) && 
                        if (!MonteCarlo.didPlayerWin(boardCopy, 
                                Board.calculateNextPlayer(player))) {
                    thisFieldWinSum++;
                }
            }
            
            //write down the result for this empty field
            moves[field] = new MCSimulationMove(emptyFields[field], thisFieldWinSum * 1.0);
        }

        //add data to StringBuilder
        if (sb != null) {
            Board boardRecord = b.deepCopy();
            for (int iCount = 0; iCount < noOfEmptyFields; iCount++) {
                //add players mark
                boardRecord.putMarkHard(emptyFields[iCount], player);

                //add a new line
                sb.append(boardRecord.toSingleRowString(false));
                sb.append(moves[iCount].getProbability() / repetitions);
                sb.append(System.lineSeparator());

                //remove players mark
                boardRecord.putMarkHard(emptyFields[iCount], (byte)0);
            }
        }
        
        Arrays.sort(moves, Comparator.reverseOrder());
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
     * Checks if player won on the board b.
     * 
     * @param b
     * @param player
     * @return True if player won, false otherwise.
     */
    public static boolean didPlayerWin(Board b, byte player) {
        //check rows
        for (int row = 0; row < b.getSize(); row++) {
            int col = 0;
            while (col < b.getSize()
                    && b.getFieldMark(new Coordinate(row, col)) == player) {
                col++;
            }
            
            if (col == b.getSize()) { return true; }
        }

        //check columns
        for (int col = 0; col < b.getSize(); col++) {
            int row = 0;
            while (row < b.getSize()
                    && b.getFieldMark(new Coordinate(row, col)) == player) {
                row++;
            }
            if (row == b.getSize()) { return true; }
        }
        
        //check diagonals
        int diag1Sum = 0;
        int diag2Sum = 0;
        
        for (int diag1 = 0; diag1 < b.getSize(); diag1++) {
            diag1Sum += b.getFieldMark(new Coordinate(diag1, diag1)) == player ? 
                    1 : 0;
        }
        
        for (int diag2 = 0; diag2 < b.getSize(); diag2++) {
            diag2Sum += b.getFieldMark(new Coordinate(diag2, b.getSize() - diag2 - 1)) 
                    == player ? 1 : 0;
        }
        
        return diag1Sum == b.getSize() || diag2Sum == b.getSize();
    }
}
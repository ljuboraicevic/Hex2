package hex2;

import java.util.ArrayList;
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
    * Evaluates given board for player that made last move. 
    * @param board
    * @param repetitions
    * @return 
    */
    public static double evaluateBoard(Board board, int repetitions) {

        byte whoMadeLastMove = board.whoMadeLastMove();
        int numberOfWins = 0;
        Coordinate[] emptyFields = board.getEmptyFields();
        byte[] arrayForFillingEmtyFields;

        for (int repetition = 0; repetition < repetitions; repetition++) {

            arrayForFillingEmtyFields = board.createLegalRandomArrayForFillingEmtyFields();

            int noOfFields = board.getNoOfEmptyFields();
            for (int i = 0; i < noOfFields; i++) {
                board.putMark(emptyFields[i], arrayForFillingEmtyFields[i]);
            }

            //check if current player won
            if (MonteCarlo.didPlayerWin(board, whoMadeLastMove)) {
                numberOfWins++;
            }
            for (Coordinate coordinate : emptyFields) {
                board.removeMark(coordinate);
            }
        }

        return (numberOfWins * 1.0) / repetitions;
    }
    /**
     * Evaluates given board for player that made last move. 
     * @param boards
     * @param repetitions
     * @return array of probabilities 
     */
    public static double[] evaluateBoards(ArrayList<Board> boards, int repetitions) {
        double[] probabilities = new double[boards.size()];
        int index = 0;
        for(Board board : boards){
            probabilities[index++] = evaluateBoard(board, repetitions);
        }
        return probabilities;
    }
    
    /**
     * Calculates best moves for player on move to play. 
     * @param board
     * @param repetitions
     * @param noOfBestMoves
     * @return sorted descending array of best moves 
     */
    public static MCSimulationMove[] getBestMoves(Board board, int repetitions, int noOfBestMoves) {
        Coordinate[] emptyFields = board.getEmptyFields();
        int noOfEmptyFields = board.getNoOfEmptyFields();
        MCSimulationMove[] bestMoves = new MCSimulationMove[noOfEmptyFields];
        for(int i = 0; i < noOfEmptyFields; i++){
            board.putMark(emptyFields[i], board.whosOnTheMove());
            double probability = evaluateBoard(board, repetitions);
            bestMoves[i] = new MCSimulationMove(emptyFields[i], probability);
            board.removeMark(emptyFields[i]);       
        }
        Arrays.sort(bestMoves, Comparator.reverseOrder());
        return Arrays.copyOfRange(bestMoves, 0, noOfBestMoves);
    }
    
    /**
     * get single best move for player that is on move
     * @param board
     * @param repetitions
     * @return 
     */
    public static MCSimulationMove getBestMove(Board board, int repetitions){
        MCSimulationMove move = MonteCarlo.getBestMoves(board, repetitions, 1)[0];
        return move;
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

    private static boolean anyVerticalPlayerOnPotentialFieldInRow(
            Board b,
            int row,
            boolean[] activeFields) {
        for (int i = 0; i < b.getSize(); i++) {
            if (activeFields[i] && b.isFieldMarkedByFirstPlayer(new Coordinate(row, i))) {
                return true;
            }
        }
        return false;
    }

    /*
    marks all fields that are active based on connection with active fields
    from previous row this doesn't find all active fields in row, only ones
    that are connected with previous row ex: t = 1 2 2 1 1 1 2 2 1 t[1][1]
    won't be marked as active, because fields from previous row that
    t[1][1]is connected to (t[0][1],t[0][2]) are not active
    */    
    private static boolean[] getPotentialsInRow(Board b, int row, boolean[] previousRowActiveFields) {
        boolean potentials[] = new boolean[b.getSize()];
        for (int i = 0; i < b.getSize() - 1; i++) {
            potentials[i] = b.isFieldMarkedByFirstPlayer(new Coordinate(row, i)) && (previousRowActiveFields[i] || previousRowActiveFields[i + 1]);
        }
        potentials[b.getSize() - 1] = b.isFieldMarkedByFirstPlayer(new Coordinate(row, b.getSize() - 1))
                && previousRowActiveFields[b.getSize() - 1];
        return potentials;
    }

    /*
    finds active fields in row that are horizontally connected to some active
    field It has to be started after getPotentialsInRow() This function marks
    t[1][1] and t[1][2] from previous example as active based on connection
    with t[1][0] that is marked as active by getPotentialsInRow()
    */
    private static void checkForMissedActiveFields(Board b, int currentRow, boolean[] activeVerticalFieldsInRow) {
        for (int i = 1; i < b.getSize(); i++) {
            if (!activeVerticalFieldsInRow[i] && b.isFieldMarkedByFirstPlayer(new Coordinate(currentRow, i))) {
                activeVerticalFieldsInRow[i] = activeVerticalFieldsInRow[i - 1];
            }
        }
        for (int i = b.getSize() - 2; i > 0; i--) {
            if (!activeVerticalFieldsInRow[i] && b.isFieldMarkedByFirstPlayer(new Coordinate(currentRow, i))) {
                activeVerticalFieldsInRow[i] = activeVerticalFieldsInRow[i + 1];
            }
        }
    }  
}

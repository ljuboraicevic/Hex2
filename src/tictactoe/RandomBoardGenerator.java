package tictactoe;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class RandomBoardGenerator {
    
    /**
     * Makes a random board, and the number of moves played is also random.
     * 
     * @param boardSize
     * @return 
     */
    public static Board makeUpARandomBoard(int boardSize) {
        int n = boardSize * boardSize;
        int movesPlayed = (int) Math.ceil(Math.random() * n);
        return makeRandomBoard(movesPlayed, boardSize);
    }
    
    /**
     * Makes a random board, when given movesPlayed and boardSize.
     * 
     * @param movesPlayed
     * @param boardSize
     * @return 
     */
    public static Board makeRandomBoard(int movesPlayed, int boardSize) {
        byte[] sequence = getRandomSequence(movesPlayed, boardSize * boardSize);
        Board b = new Board(boardSize, sequence, movesPlayed);
        return b;
    }
    
    /**
     * Makes a random sequence which get overlayed over the board.
     * 
     * @param movesPlayed
     * @param boardLength
     * @return 
     */
    private static byte[] getRandomSequence(int movesPlayed, int boardLength) {
        byte[] result = getSequence(movesPlayed, boardLength);
        MonteCarlo.shuffleArray(result);
        return result;
    }
    
    /**
     * Creates sequence of 0,1 and 2 which becomes the sequence which gets
     * overlayed over the board when making a random board.
     * 
     * @param movesPlayed
     * @param boardLength
     * @return 
     */
    private static byte[] getSequence(int movesPlayed, int boardLength) {
        byte[] result = new byte[boardLength];
        int noOfOnes = (int)Math.ceil((movesPlayed * 1.0) / 2);
        
        for (int iCount = 0; iCount < noOfOnes; iCount++) {
            result[iCount] = (byte)1;
        }
        
        for (int iCount = noOfOnes; iCount < movesPlayed; iCount++) {
            result[iCount] = (byte)2;
        }
        
        return result;
    }
}

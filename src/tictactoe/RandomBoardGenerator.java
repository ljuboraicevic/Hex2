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
        return makeRandomBoard(getRandomMovesPlayed(), boardSize);
    }
    
    /**
     * Makes a random board, when given movesPlayed and boardSize. Only returns
     * random boards that don't already have a winning combination on them.
     * 
     * @param movesPlayed
     * @param boardSize
     * @return 
     */
    public static Board makeRandomBoard(int movesPlayed, int boardSize) {
        Board b = null;
        boolean boardNotWon = false;
        while (!boardNotWon) {
            byte[] sequence = getRandomSequence(movesPlayed, boardSize * boardSize);
            b = new Board(boardSize, sequence, movesPlayed);
            if (!MonteCarlo.didPlayerWin(b, (byte)1)
                    && !MonteCarlo.didPlayerWin(b, (byte)2)) {
                boardNotWon = true;
            }
        }
        return b;
    }

    /**
     * Random number of moves played can't be uniformly distributed, since there
     * are more combinations for moves later in the game.
     * 
     * @return 
     */
    private static int getRandomMovesPlayed() {
        int random = (int) Math.ceil(Math.random() * 362880); // 362880 = 9!
        if      (random <= 9)      { return 0; }
        else if (random <= 504)    { return 2; }
        else if (random <= 15120)  { return 4; }
        else if (random <= 181440) { return 6; }
        return 8;
    }
    
    /**
     * Makes a random sequence which get overlaid over the board.
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
     * overlaid over the board when making a random board.
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

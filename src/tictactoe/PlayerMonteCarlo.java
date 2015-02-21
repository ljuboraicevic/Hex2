package tictactoe;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerMonteCarlo implements Player {

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
    public PlayerMonteCarlo(int repetitions, int threads, boolean randomizeBest) {
        this.repetitions = repetitions;
        this.threads = threads;
        this.randomizeBest = randomizeBest;
    }
    
    public int getNumberOfRepetitions(){
        return this.repetitions;
    }
    
    @Override
    public Coordinate makeMove(Board t) {
        //return makeMoveWithProbability(t)[0].getCoordinates();
        //MCSimulationMove[] c = MonteCarlo.evaluateBoardNoParallel(t, repetitions);
        return MonteCarlo.evaluateBoardNoParallel(t, repetitions)[0].getCoordinates();
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
        MonteCarlo.shuffleArray(result);
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
}

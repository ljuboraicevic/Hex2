package tictactoe;

/**
 * A Monte Carlo player. Uses Monte Carlo simulation from MonteCarlo.java to
 * make moves.
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerMonteCarlo implements Player {

    /**
     * How many times Monte Carlo simulation is run.
     */
    private final int repetitions;
    
    /**
     * Used for collecting and saving the data about the moves played
     */
    private final StringBuilder sb;
    
    
    /**
     * Initializes a new PlayerMonteCarlo without a StringBuilder.
     * 
     * @param repetitions Monte Carlo simulation repetitions
     */
    public PlayerMonteCarlo(int repetitions) {
        this(repetitions, null);
    }
    
    /**
     * Initializes a new PlayerMonteCarlo with a StringBuilder.
     * 
     * @param repetitions Monte Carlo simulation repetitions
     * @param sb StringBuilder used for collecting data about the moves
     */
    public PlayerMonteCarlo(int repetitions, StringBuilder sb) {
        this.repetitions = repetitions;
        this.sb = sb;
    }
    
    public int getNumberOfRepetitions(){
        return this.repetitions;
    }
    
    @Override
    public Coordinate makeMove(Board b) {
        return MonteCarlo.evaluateBoard(b, repetitions, sb)[0].getCoordinates();
    }
}
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
     * Initializes a new PlayerMonteCarlo.
     * 
     * @param repetitions Monte Carlo simulation repetitions
     */
    public PlayerMonteCarlo(int repetitions) {
        this.repetitions = repetitions;
    }
    
    public int getNumberOfRepetitions(){
        return this.repetitions;
    }
    
    @Override
    public Coordinate makeMove(Board b) {
        return MonteCarlo.evaluateBoard(b, repetitions, null)[0].getCoordinates();
    }
}
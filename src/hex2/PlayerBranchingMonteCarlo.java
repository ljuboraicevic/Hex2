package hex2;

import java.util.ArrayList;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerBranchingMonteCarlo implements Player {

    /**
     * How many times Monte Carlo simulation is run.
     */
    private final int repetitions;
    
    /**
     * Used for collecting and saving the data about the moves played
     */
    private final StringBuilder sb;
    
    private final int plies;
    
    private final int best;
    
    
    /**
     * Initializes a new PlayerMonteCarlo without a StringBuilder.
     * 
     * @param repetitions Monte Carlo simulation repetitions
     */
    public PlayerBranchingMonteCarlo(int repetitions, int plies, int best) {
        this(repetitions, null, plies, best);
    }
    
    /**
     * Initializes a new PlayerMonteCarlo with a StringBuilder.
     * 
     * @param repetitions Monte Carlo simulation repetitions
     * @param sb StringBuilder used for collecting data about the moves
     */
    public PlayerBranchingMonteCarlo(int repetitions, StringBuilder sb, 
            int plies, int best) {
        this.repetitions = repetitions;
        this.sb = sb;
        this.plies = plies;
        this.best = best;
    }
    
    public int getNumberOfRepetitions(){
        return this.repetitions;
    }
    
    @Override
    public Coordinate makeMove(Board b) {
        ArrayList<Board>[] buffers = new ArrayList[2];
        buffers[0] = new ArrayList<>();
        buffers[1] = new ArrayList<>();
        
        MCSimulationMove[] cOriginal = MonteCarlo.evaluateBoard(b, repetitions, sb);
        
        System.out.print(cOriginal[0]);
        
        MCSimulationMove[] c;
        
        double bestProb = -1;
        int bestIndex = -1;
        int boardCounter = 0;
        
        
        buffers[0].add(b);
        
        
        for (int ply = 0; ply <= plies; ply++) {
            //values of these three variables record the best response in each
            //ply, but only the best response from the last ply is used
            //(after this for loop has finished)
            bestProb = -1;
            bestIndex = -1;
            boardCounter = 0;
            
            //toggle between two buffers; first clear the buffer that will be
            //used to store this ply's output boards
            buffers[(ply + 1) % 2].clear();
            for (Board board: buffers[ply % 2]) {
                boardCounter++;
                c = MonteCarlo.evaluateBoard(board, repetitions, sb);

                for (int iCount = 0; iCount < best; iCount++) {
                    Board newB = board.deepCopy();
                    newB.putMark(c[iCount].getCoordinates(), newB.whosOnTheMove());
                    buffers[(ply + 1) % 2].add(newB);
                }

                if (c[0].getProbability() > bestProb) { 
                    bestProb = c[0].getProbability(); 
                    bestIndex = boardCounter;
                }
            }
        }
        
        //calculate what's the best move (in the first ply)
        int pieceSize = buffers[0].size() / best;
        int currentPiece = pieceSize;
        int counter = 0;
        
        while (currentPiece < bestIndex) {
            currentPiece += pieceSize;
            counter++;
        }
        
        System.out.println(cOriginal[counter]);
        
        return cOriginal[counter].getCoordinates();
    }
    
}

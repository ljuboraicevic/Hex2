package tictactoe;

/**
 * Class MonteCarloSimulation represents a part of the whole simulation. 
 * Each Monte Carlo simulation works on a subset of all of the empty fields on 
 * the board.
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class MonteCarloThread extends Thread {

    /**
     * Board that will be used for simulation.
     */
    private final Board boardCopy;
    
    /**
     * The original board that should not be altered.
     */
    private final Board originalBoard;
    
    /**
     * List of empty fields on the board.
     */
    private final Coordinate[] emptyFields;
    
    /**
     * This simulation should only take some empty fields into account. 
     * Variable 'from' represents the start of range of empty fields that this 
     * simulation will work with.
     */
    private final int from;
    
    /**
     * End of range of empty fields that this simulation will work with.
     */
    private final int to;
    
    /**
     * Number of repetitions of the simulation.
     */
    private final int repetitions;
    
    /**
     * Stores move with highest number of won simulations.
     */
    private MCSimulationMove bestMove;
    
    /**
     * Used to store coordinates of all possible moves and results for 
     * Monte Carlo simulations.
     */
    private MCSimulationMove[] possibleMoves;
    
    /**
     * How many moves have been played so far.
     */
    private final int movesPlayed;
    
    /**
     * Who is the player, number 1 or 2.
     */
    private final byte player;

    /**
     * Initializes a new Monte Carlo simulation.
     * 
     * @param b Copy of the board that this simulation is going to work with
     * @param originalBoard Original board
     * @param from Start of the subset of empty fields
     * @param to End of the subset of empty fields
     * @param repetitions Number of repetitions
     * @param movesPlayed How many moves have been played so far
     * @param player For which player is simulation being run
     */
    public MonteCarloThread(
            Board b,
            Board originalBoard,
            int from, 
            int to, 
            int repetitions,
            int movesPlayed,
            byte player) {
        
        this.boardCopy = b;
        this.originalBoard = originalBoard;
        this.emptyFields = this.originalBoard.getEmptyFields();
        this.from = from;
        this.to = to;
        this.repetitions = repetitions;
        this.bestMove = new MCSimulationMove(null, -1.0);
        this.movesPlayed = movesPlayed;
        this.player = player;
        this.possibleMoves = new MCSimulationMove[to - from];
    }
    
    public MCSimulationMove[] getAllMoves(){
        return this.possibleMoves;
    }
    
    public MCSimulationMove getBestMove() {
        return this.bestMove;
    }

    @Override
    public void run() {
        //for each of the empty fields on the board that was assigned to 
        //this thread
        for (int field = from; field < to; field++) {

            int thisFieldWinSum = 0;
            
            //mark current "empty" field as this player's and then run the
            //simulation on the rest of the empty fields
            boardCopy.putMarkHard(
                    new Coordinate(emptyFields[field].row, emptyFields[field].col), 
                    player
            );
            //boardCopy.matrix[emptyFields[field].row][emptyFields[field].col] = (byte) (player + 1);
            
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
                        seqCount++;
                    }
                }

                //check if current player won and the other player didn't win
                if (MonteCarlo.didPlayerWin(boardCopy, player) && 
                        !MonteCarlo.didPlayerWin(boardCopy, 
                                Board.calculateNextPlayer(player))) {
                    thisFieldWinSum++;
                }
            }
            
            this.possibleMoves[field - from] = new MCSimulationMove(emptyFields[field],
                1.0 * thisFieldWinSum);
        
            //if this field is the best so far
            if (thisFieldWinSum * 1.0 > bestMove.getProbability()) {
                bestMove = new MCSimulationMove(
                        emptyFields[field], 
                        thisFieldWinSum * 1.0);
            }
        }
    }
}
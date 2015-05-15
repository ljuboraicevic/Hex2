package hex2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author nikola
 */
public class MCSearchTreeLogic {

    private final int repetitions;
    private final int plies;
    private final int noOfBestMoves;

    private MCSimulationMove bestMove;

    public MCSearchTreeLogic(int repetitions, int plies, int noOfBestMoves) {
        this.repetitions = repetitions;
        this.plies = plies;
        this.noOfBestMoves = noOfBestMoves;
        this.bestMove = new MCSimulationMove(null, 0.);
    }

    public MCSimulationMove getBestMove(Board board) {
        calculateMove(board);
        return bestMove;
    }

    private void calculateMove(Board board) {
        ArrayList<Board> previousPlyBoards = new ArrayList<>();
        ArrayList<Board> thisPlyBoards = new ArrayList<>();
        previousPlyBoards.add(board);

        //setup
        MCSimulationMove[] firstBestMoves = MonteCarlo.getBestMoves(
                board, repetitions, noOfBestMoves);
        
        //create boards from starting board, with added best moves (one per board)
        for(MCSimulationMove move : firstBestMoves){
            Board temp = board.deepCopy();
            temp.putMark(move.getCoordinates(), temp.whosOnTheMove());
            thisPlyBoards.add(temp);
        }
        
        //for each ply
        for (int plyCounter = 0; plyCounter < plies; plyCounter++) {
            previousPlyBoards = thisPlyBoards;
            thisPlyBoards = new ArrayList<>();
            
            //for each board from previous ply
            for (Board previousPlyBoard : previousPlyBoards) {
                
                //add to board opponent's best move
                MCSimulationMove bestOpponentMove = MonteCarlo.getBestMoves(previousPlyBoard, repetitions, 1)[0];
                previousPlyBoard.putMark(bestOpponentMove.getCoordinates(), previousPlyBoard.whosOnTheMove());
                
                //create best moves in respond to opponent's best move
                MCSimulationMove[] bestMoves = MonteCarlo.getBestMoves(
                previousPlyBoard, repetitions, noOfBestMoves);
                
                //create board for each such move and add move to board
                for(MCSimulationMove move : bestMoves){
                    Board temp = previousPlyBoard.deepCopy();
                    temp.putMark(move.getCoordinates(), temp.whosOnTheMove());
                    thisPlyBoards.add(temp);
                }
            }
        }
        
        double[] probabilitiesInLastPly = evaluateBoardsFromLastPly(thisPlyBoards.size(), thisPlyBoards);
        
        //int indexOfMinMaxInFirstPly = getIndexViaMinMax(numberOfLeaves, probabilitiesInLastPly, noOfBestMoves);
        int indexOfLeaveWithMaxProbabilityInLastLevel = findIndexOfMaxElementInArray( probabilitiesInLastPly);
        int indexOfMoveInFirstLevel = translateIndexFromLastLevelIntoFirstLevelIndex(
                indexOfLeaveWithMaxProbabilityInLastLevel, probabilitiesInLastPly.length);
        
        bestMove = new MCSimulationMove( firstBestMoves[indexOfMoveInFirstLevel].getCoordinates(), 
            probabilitiesInLastPly[indexOfLeaveWithMaxProbabilityInLastLevel]);
        
    }
    
    
    private void calculateMove2(Board board) {

        int numberOfLevels = 2 * plies;

        MCSimulationMove[] firstLevelBestMoves = MonteCarlo.getBestMoves(board, repetitions, noOfBestMoves);
    }

    private int findIndexOfMaxElementInArray(double[] array) {
        int indexOfMaximum = 0;
        for(int i = 1; i < array.length; i++){
            if(array[i] > array[indexOfMaximum]){
                indexOfMaximum = i;
            }
        }
        return indexOfMaximum;
    }
    
    private int getIndexViaMinMax(int numberOfLeaves, double[] probabilitiesInLastPly, int numberOfBestMoves){
        int chunkSize = numberOfLeaves / numberOfBestMoves;
        int minIndex = numberOfBestMoves;
        double minValue = -1.0d;
        for(int i = 0; i < numberOfBestMoves; i++){
            double[] leavesProbabilitiesCorrespondingToSameBranch = Arrays.copyOfRange(
                    probabilitiesInLastPly, i * chunkSize,  (i+1) * chunkSize);
            Arrays.sort(leavesProbabilitiesCorrespondingToSameBranch);
            if(leavesProbabilitiesCorrespondingToSameBranch[0] > minValue){
                minIndex = i;
                minValue = leavesProbabilitiesCorrespondingToSameBranch[0];
            }
        }
        if(minIndex == numberOfBestMoves){
            System.out.println("Nelogicno");
            minIndex = 0;
        }
        return minIndex;
    }
    
    private int findIndexOfMinMaxInLastPly(int numberOfLeaves, double[] probabilitiesInLastPly, int noOfBestMoves, int indexInFirstPly) {
        int chunkSize = numberOfLeaves / noOfBestMoves;
        double minValue = 1.01d;
        int minIndex = -1;
        for(int i = indexInFirstPly * chunkSize; i < (indexInFirstPly + 1) * chunkSize; i++ ){
            if(probabilitiesInLastPly[i] < minValue){
                minValue = probabilitiesInLastPly[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
    
    private double[] evaluateBoardsFromLastPly(int numberOfLeaves, ArrayList<Board> thisPlyBoards) {
        double[] probabilitiesInLastPly = new double[numberOfLeaves];
        int index = 0;
        //thisPlyBoards consists of last ply leaves
        for(Board boardFromLastPly : thisPlyBoards){
            probabilitiesInLastPly[index++] = MonteCarlo.evaluateBoard(
                    boardFromLastPly, repetitions);
        }
        return probabilitiesInLastPly;
    }
    
    private int translateIndexFromLastLevelIntoFirstLevelIndex(int indexInLastPly, int numberOfLeavesInLastLevel) {
        int chunkSize = numberOfLeavesInLastLevel / noOfBestMoves;
        return indexInLastPly / chunkSize;
    }

    
}

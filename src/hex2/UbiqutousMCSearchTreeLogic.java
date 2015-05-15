/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hex2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author nikola
 */
public class UbiqutousMCSearchTreeLogic {
    /**
     * number of Monte Carlo repetitions
     */
    private final int repetitions;
    /**
     * number of plies. One ply consists from two tree levels - first level consists moves
     * from "player on move" 's moves, second level consists of opponent's (other player) moves.
     */
    private final int plies;
    /**
     * number of best moves calculated in each level
     */
    private final int noOfBestMoves;
    
    public boolean didChangeMove;


    public UbiqutousMCSearchTreeLogic(int repetitions, int plies, int noOfBestMoves) {
        this.repetitions = repetitions;
        this.plies = plies;
        this.noOfBestMoves = noOfBestMoves;
    }
    
    /**
     * Best move calculated by Monte Carlo search tree logic. This logic simulates couple of moves in advance.
     * For given board search tree is created - best moves are calculated for player on move and than for other player 
     * one best move is added in response. These steps are repeated ply times. 
     * Tree grows exponentially - number of leaves in last level = best moves ^ plies
     * Best move is calculated by min max principle i.e. we choose first level move that maximize minimal probability in last level
     * 
     * @param board - input board 
     * @return calculated best move 
     */    
    public MCSimulationMove calculateMoveWithoutOpponentsBranching(Board board) {
        int numberOfLevels = 2 * plies;

        MCSimulationMove[] firstLevelBestMoves = MonteCarlo.getBestMoves(board, repetitions, noOfBestMoves);
        ArrayList<Board> firstLevelBoards = createNextLevelBoardsByAddingMovesOnPreviousLevelBoard(firstLevelBestMoves, board);
        
        ArrayList<Board> lastLevelBoards = createLastLevelBoardsFromFirstLevelBoardsWithoutOpponentsMoveBranching(numberOfLevels, firstLevelBoards);
        
        double[] probabilitiesInLastLevel = MonteCarlo.evaluateBoards(lastLevelBoards, repetitions);

        return chooseFirstLevelBestMoveWithHighestLastLevelProbability(probabilitiesInLastLevel, firstLevelBestMoves);
    }
    
    private ArrayList<Board> createNextLevelBoardsByAddingMovesOnPreviousLevelBoard(MCSimulationMove[] moves, Board board) {
        
        ArrayList<Board> nextLevelBoards = new ArrayList<>();
        for(MCSimulationMove move : moves){
            Board temp = board.deepCopy();
            temp.putMark(move.getCoordinates(), temp.whosOnTheMove());
            nextLevelBoards.add(temp);
        }
        return nextLevelBoards;
    }
    
    private ArrayList<Board> createLastLevelBoardsFromFirstLevelBoardsWithoutOpponentsMoveBranching(int numberOfLevels, ArrayList<Board> firstLevelBoards) {
        ArrayList<Board> previousLevelBoards = firstLevelBoards;
        ArrayList<Board> nextLevelBoards = new ArrayList<>();

        for (int levelCounter = 1; levelCounter < numberOfLevels; ) {
            
            addOpponentsBestMoveOnEachBoard(previousLevelBoards);
            levelCounter++;
            
            if(levelCounter == numberOfLevels){
                break;
            }    

            nextLevelBoards = createNextLevelBoardsFromPreviousLevelBoards(previousLevelBoards);
            previousLevelBoards = (ArrayList<Board>) nextLevelBoards.clone();
            levelCounter++;
        }
        if(numberOfLevels == 2){
            return previousLevelBoards;
        }
        return nextLevelBoards;
    }
    
    private void addOpponentsBestMoveOnEachBoard(ArrayList<Board> previousLevelBoards) {
        for (Board previousLevelBoard : previousLevelBoards){
            MCSimulationMove bestMove = MonteCarlo.getBestMove(previousLevelBoard, repetitions);
            previousLevelBoard.putMark(bestMove.getCoordinates(), previousLevelBoard.whosOnTheMove());
        }
    }

    private MCSimulationMove chooseFirstLevelBestMoveWithHighestLastLevelProbability(
            double[] probabilitiesInLastLevel, MCSimulationMove[] firstLevelBestMoves) {
        
        int indexOfLeaveWithMaxProbabilityInLastLevel = findIndexOfMaxElementInArray(probabilitiesInLastLevel);
        int indexOfMoveInFirstLevel = translateIndexFromLastLevelIntoFirstLevelIndex(
                indexOfLeaveWithMaxProbabilityInLastLevel, probabilitiesInLastLevel.length);
        didChangeMove = indexOfLeaveWithMaxProbabilityInLastLevel == 0;

        return new MCSimulationMove(firstLevelBestMoves[indexOfMoveInFirstLevel].getCoordinates(),
                probabilitiesInLastLevel[indexOfLeaveWithMaxProbabilityInLastLevel]);
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
    
    private int translateIndexFromLastLevelIntoFirstLevelIndex(int indexInLastPly, int numberOfLeavesInLastLevel) {
        int chunkSize = numberOfLeavesInLastLevel / noOfBestMoves;
        return indexInLastPly / chunkSize;
    }
    /**
     * Evaluates given board by calculating highest probability after couple of steps in future.
     * Search tree is calculated without opponents move branching (see calculateMoveWithoutOpponentsBranching() javadoc)
     * @param board
     * @return probability that player who made last move will win game if plays moves from best moves
     */
    public double evaluateBoardWithoutOpponentsMoveBranching(Board board){
        int numberOfLevels = 2 * plies;
        
        ArrayList<Board> firstLevelBoards = new ArrayList<>();
        firstLevelBoards.add(board);
        
        ArrayList<Board> lastLevelBoards = createLastLevelBoardsFromFirstLevelBoardsWithOpponentsMovesBranching(numberOfLevels, firstLevelBoards);
        
        double[] probabilitiesInLastLevel = MonteCarlo.evaluateBoards(lastLevelBoards, repetitions);
        
        return findProbabilityByMinMaxPrinciple(probabilitiesInLastLevel);
    }
    
    /**
     * Best move calculated by Monte Carlo search tree logic. This logic simulates couple of moves in advance.
     * For given board search tree is created - best moves are calculated for player on move and than for other player 
     * in response. These steps are repeated ply times. Tree grows exponentially - number of leaves in last level = best moves ^ (2 * plies)
     * Best move is calculated by min max principle i.e. we choose first level move that maximize minimal probability in last level
     * 
     * @param board - input board 
     * @return calculated best move 
     */    
    public MCSimulationMove calculateMoveWithOpponentsBranchingViaMinMax(Board board) {
        
        int numberOfLevels = 2 * plies;

        MCSimulationMove[] firstLevelBestMoves = MonteCarlo.getBestMoves(board, repetitions, noOfBestMoves);
        
        ArrayList<Board> firstLevelBoards = createNextLevelBoardsByAddingMovesOnPreviousLevelBoard(firstLevelBestMoves, board);
        
        ArrayList<Board> lastLevelBoards = createLastLevelBoardsFromFirstLevelBoardsWithOpponentsMovesBranching(numberOfLevels, firstLevelBoards);
        
        double[] probabilitiesInLastLevel = MonteCarlo.evaluateBoards(lastLevelBoards, repetitions);
        
        return chooseFirstLevelBestMoveViaMinMaxPrinciple(probabilitiesInLastLevel, firstLevelBestMoves);
        
    }
    
    private ArrayList<Board> createLastLevelBoardsFromFirstLevelBoardsWithOpponentsMovesBranching(int numberOfLevels, ArrayList<Board> firstLevelBoards) {
        ArrayList<Board> previousLevelBoards = firstLevelBoards;
        ArrayList<Board> nextLevelBoards = new ArrayList<>();

        for (int levelCounter = 1; levelCounter < numberOfLevels; levelCounter++) {
            nextLevelBoards.clear();

            nextLevelBoards = createNextLevelBoardsFromPreviousLevelBoards(previousLevelBoards);
            previousLevelBoards = (ArrayList<Board>) nextLevelBoards.clone();
        }
        return nextLevelBoards;
    }

    private ArrayList<Board> createNextLevelBoardsFromPreviousLevelBoards(ArrayList<Board> previousLevelBoards) {
        ArrayList<Board> nextLevelBoards = new ArrayList<>();
        for (Board previousLevelBoard : previousLevelBoards){
            MCSimulationMove[] bestMoves = MonteCarlo.getBestMoves(previousLevelBoard, repetitions, noOfBestMoves);
            
            ArrayList<Board> nextLevelBoardsGeneratedFromOneBoard =
                    createNextLevelBoardsByAddingMovesOnPreviousLevelBoard(bestMoves, previousLevelBoard);
            
            nextLevelBoards.addAll(nextLevelBoardsGeneratedFromOneBoard);
        }
        return nextLevelBoards;
    }
    
    private MCSimulationMove chooseFirstLevelBestMoveViaMinMaxPrinciple(double[] probabilitiesInLastLevel, MCSimulationMove[] firstLevelBestMoves) {
        int indexOfMinMaxInFirstPly = getIndexOfFirstLevelBestMoveViaMinMax(probabilitiesInLastLevel, noOfBestMoves);
        int indexOfMinMaxInLastPly = findIndexWithMinMaxProbabilityInLastLevel(probabilitiesInLastLevel, noOfBestMoves, indexOfMinMaxInFirstPly);
        
        return new MCSimulationMove( firstLevelBestMoves[indexOfMinMaxInFirstPly].getCoordinates(),
                probabilitiesInLastLevel[indexOfMinMaxInLastPly]);
    }

    private int getIndexOfFirstLevelBestMoveViaMinMax(double[] probabilitiesInLastPly, int numberOfBestMoves){
        int numberOfLeaves = probabilitiesInLastPly.length;
        int chunkSize = numberOfLeaves / numberOfBestMoves;
        int indexOfFirstLevelBestMove = numberOfBestMoves;
        double minMaxProbability = -1.0d;
        for(int i = 0; i < numberOfBestMoves; i++){
            double[] leavesProbabilitiesDerivedFromOneFirstLevelBoard = Arrays.copyOfRange(
                    probabilitiesInLastPly, i * chunkSize,  (i + 1) * chunkSize);
            Arrays.sort(leavesProbabilitiesDerivedFromOneFirstLevelBoard);
            
            if(leavesProbabilitiesDerivedFromOneFirstLevelBoard[0] > minMaxProbability){
                indexOfFirstLevelBestMove = i;
                minMaxProbability = leavesProbabilitiesDerivedFromOneFirstLevelBoard[0];
            }
        }
        return indexOfFirstLevelBestMove;
    }
    
    private int findIndexWithMinMaxProbabilityInLastLevel(double[] probabilitiesInLastPly, int noOfBestMoves, int indexInFirstPly) {
        int numberOfLeaves = probabilitiesInLastPly.length;
        int noOfLeavesDerivedFromSameFirstLevelBoard = numberOfLeaves / noOfBestMoves;
        final int beginOfInterval = indexInFirstPly * noOfLeavesDerivedFromSameFirstLevelBoard;
        final int endOfInterval = beginOfInterval + noOfLeavesDerivedFromSameFirstLevelBoard;
        
        double minProbability = 1.01d;
        int indexWithMinMaxProbability = -1;
        
        for(int i = beginOfInterval; i < endOfInterval; i++ ){
            if(probabilitiesInLastPly[i] < minProbability){
                minProbability = probabilitiesInLastPly[i];
                indexWithMinMaxProbability = i;
            }
        }
        return indexWithMinMaxProbability;
    }

    /**
     * Evaluates given board using min max principle. Search tree is calculated with opponents move branching (see calculateMoveViaMinMax() javadoc)
     * @param board
     * @return probability that player who made last move won't lose game if plays moves from best moves
     */
    public double evaluateBoardWithOpponentsMoveBranchingViaMinMaxPrinciple(Board board){
        int numberOfLevels = 2 * plies;
        
        ArrayList<Board> firstLevelBoards = new ArrayList<>();
        firstLevelBoards.add(board);
        
        ArrayList<Board> lastLevelBoards = createLastLevelBoardsFromFirstLevelBoardsWithOpponentsMovesBranching(numberOfLevels, firstLevelBoards);
        
        double[] probabilitiesInLastLevel = MonteCarlo.evaluateBoards(lastLevelBoards, repetitions);
        
        return findProbabilityByMinMaxPrinciple(probabilitiesInLastLevel);
    }

    private double findProbabilityByMinMaxPrinciple(double[] probabilitiesInLastLevel) {
        int numberOfLeaves = probabilitiesInLastLevel.length;
        int chunkSize = numberOfLeaves / noOfBestMoves;
        double minMaxProbability = -1.0d;
        for(int i = 0; i < noOfBestMoves; i++){
            double[] leavesProbabilitiesDerivedFromOneFirstLevelBoard = Arrays.copyOfRange(
                    probabilitiesInLastLevel, i * chunkSize,  (i + 1) * chunkSize);
            Arrays.sort(leavesProbabilitiesDerivedFromOneFirstLevelBoard);
            
            if(leavesProbabilitiesDerivedFromOneFirstLevelBoard[0] > minMaxProbability){
                minMaxProbability = leavesProbabilitiesDerivedFromOneFirstLevelBoard[0];
            }
        }
        return minMaxProbability;
    }
}

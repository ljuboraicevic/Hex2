/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hex2;

/**
 *
 * @author nikola
 */
public class PlayerMonteCarloSearch implements Player{
    private UbiqutousMCSearchTreeLogic logic;
    private int noOfRepetitions;
    
    public PlayerMonteCarloSearch(int noOfRepetitions, int noOfPlies, int noOfBestMoves){
        this.noOfRepetitions = noOfRepetitions;
        logic = new UbiqutousMCSearchTreeLogic(noOfRepetitions, noOfPlies, noOfBestMoves);
    }
    
    public int getNumberOfRepetitions(){
        return noOfRepetitions;
    }
    
    @Override
    public Coordinate makeMove(Board b) {
        return logic.calculateMoveWithoutOpponentsBranching(b).getCoordinates();
    }
    
}

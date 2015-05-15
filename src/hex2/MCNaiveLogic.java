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
public class MCNaiveLogic {
    private final int repetitions;

    public MCNaiveLogic(int repetitions) {
        this.repetitions = repetitions;
    }
    
    public MCSimulationMove getBestMove(Board board) {
        return MonteCarlo.getBestMoves(board, repetitions, 1)[0];
    }
}

package hex2;

import com.googlecode.fannj.Fann;

/**
 * A neural network player. Uses jfann library (which in turn uses fann library
 * written for C and C++) to make moves.
 * 
 * IMPORTANT NOTE ON NEURAL NETWORKS:
 * Neural network needs to be trained from an outside (Documents/FANNTraining)
 * C++ program in order for this to work (if you use fantool you'll get
 * "Wrong version" error. After creating the neural network this way all the
 * dots need to be replaced with commas (. => ,) except in the first line (
 * FANN_FLO_2.1) and then it should hopefully work.
 * 
 * Fannj-0.7.jar and jna-3.2.2.jar need to be added. libfann libraries need to
 * be installed (it can be found in synaptic).
 * 
 * It seems that the problem with fanntool is that it uses double, and the C++
 * program I wrote uses float.
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerNeuralNetwork implements Player{

    /**
     * A neural network that decides if the move is good or bad.
     */
    private final Fann nnFan;
    
    /**
     * Initializes a PlayerNeuralNetwork with neural network loaded from file f.
     * 
     * @param f Neural network file
     */
    public PlayerNeuralNetwork(String f) {
        nnFan = new Fann(f);
    }
    
    @Override
    public Coordinate makeMove(Board b) {
        //make a deep copy of the board
        Board boardCopy = b.deepCopy();
        
        //get coordinates of empty fields in the board
        Coordinate[] emptyFields = b.getEmptyFields();

        double bestResult = -1;
        Coordinate bestField  = null;
        int noOfEmptyFields = b.getNoOfEmptyFields();
        byte player = b.whosOnTheMove();
        
        //for each of the empty fields
        for (int field = 0; field < noOfEmptyFields; field++) {
            //set previously checked field to zero
            if (field > 0) { 
                Coordinate prev = emptyFields[field - 1];
                boardCopy.putMarkHard(prev, (byte)0);
            }
            
            //put mark on the field
            boardCopy.putMark(emptyFields[field], (byte)(player + 1));
            
            //ask the neural network if it likes the board
            float[] input = transformBoardToNNInput(boardCopy, player);
            float[] result = nnFan.run(input);
            
            //check if this is the best result so far
            if (result[0] > bestResult) {
                bestResult = result[0];
                bestField = emptyFields[field];
            }
        }
        
        return bestField;
    }
    
    /**
     * Transforms the board to neural network input.
     * 
     * @param b Board
     * @param player Which player is neural network, one or two
     * @return Neural network input
     */
    private static float[] transformBoardToNNInput(Board b, byte player) {
        float[] result = new float[b.getSize() * b.getSize()];
        
        //copy matrix to result and apply transformations
        for (int row = 0; row < b.getSize(); row++) {
            for (int col = 0; col < b.getSize(); col++) {
                Coordinate c = new Coordinate(row, col);
                result[row * b.getSize() + col] = 
                        (float)(b.getFieldMark(c) + f(player, (byte) b.getFieldMark(c)));
            }
        }        
        
        return result;
    }
    
    /**
     * Helper function which does a transformation of input.
     * 
     * @param player
     * @param field
     * @return 
     */
    private static byte f(byte player, byte field) {
        if      (player == 0 && field == 2) { return -3; }
        else if (player == 1 && field == 1) { return -2; }
        else if (player == 1 && field == 2) { return -1; }
        else                                { return  0; }
    }
}
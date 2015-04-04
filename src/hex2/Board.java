package hex2;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class Board {
    
    public static final byte FIRST_PLAYERS_MARK = 1;
    
    public static final byte SECOND_PLAYERS_MARK = 2;
    /**
     * Matrix of bytes representing past players' past moves.
     */
    private byte[][] matrix;

    /**
     * Number of empty fields left on the board.
     */
    private int noOfEmptyFields;

    /**
     * Board side size.
     */
    private int size;
    
    /**
     * Player who has the next move. First player = 1, second = 2.
     */
    private byte nextMovePlayer;
    
    /**
     * Initializes an empty Board.
     *
     * @param size board size
     */
    public Board(int size) {
        this.matrix = new byte[size][size];
        this.size = size;
        this.noOfEmptyFields = size * size;
        this.nextMovePlayer = 1;
    }
    
    /**
     * Makes a new board from a byte array.
     * 
     * @param size Board size
     * @param sequence Byte array (positions of moves)
     * @param movesPlayed How many moves have been played so far
     */
    public Board(int size, byte[] sequence, int movesPlayed) {
        this(size);
        
        //overlay the sequence over the board
        for (int iCount = 0; iCount < sequence.length; iCount++) {
            Coordinate c = this.intToCoordinate(iCount);
            this.matrix[c.row][c.col] = sequence[iCount];
        }
        
        this.noOfEmptyFields = size * size - movesPlayed;
        this.nextMovePlayer = (byte) (movesPlayed % 2 + 1);
    }
    
    public boolean isFieldVertical(Coordinate c) {
        return matrix[c.row][c.col] == 1;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public int getNoOfEmptyFields() {
        return this.noOfEmptyFields;
    }
    
    public int getNoOfMovesPlayed(){
        return size * size - this.noOfEmptyFields;
    }
    
    public byte whoMadeLastMove(){
        if(nextMovePlayer == FIRST_PLAYERS_MARK){
            return SECOND_PLAYERS_MARK;
        }
        return FIRST_PLAYERS_MARK;
    }
    
    
    
    public byte getFieldMark(Coordinate c) {
        return this.matrix[c.row][c.col];
    }
    
    /**
     * Checks if a field has already been marked.
     *
     * @param c Coordinates of the field.
     * @return true if field has been marked, false otherwise
     */
    public boolean isFieldMarked(Coordinate c) {
        return matrix[c.row][c.col] != 0;
    }
    
    /**
     * Who is the next player, 1 or 2?
     * 
     * @return 
     */
    public byte whosOnTheMove() {
        return nextMovePlayer;
    }
    
    /**
     * Checks if the move is legal.
     * 
     * @param c Coordinates of the field played
     * @return true if move is legal, false otherwise
     */
    public boolean isMoveLegal(Coordinate c) {
        return c.row >= 0 && c.row < size && c.col >= 0 
                && c.col < size && !isFieldMarked(c);
    }
    
    /**
     * Puts a "mark" on a field
     *
     * @param c Coordinates of the field
     * @param mark of the player - 1 or 2
     * @return true if move is legal, false otherwise
     */
    public boolean putMark(Coordinate c, byte mark) {
        //if the field is empty
        if (!isFieldMarked(c)) {
            noOfEmptyFields--;
            putMarkHard(c, mark);
            return true;
        }

        //if the field has already been marked return false
        return false;
    }
    
    /**
     * Puts a "mark" on a field without checking if the field has been marked.
     * Also, it doesn't lower noOfEmptyFields. 
     * THIS METHOD SHOULD ONLY BE USED FROM MonteCarlo.java AND 
     * PlayerNeuralNetwork, WHEN EVALUATING THE BOARD.
     *
     * @param c Coordinates of the field
     * @param mark of the player - 1 or 2
     */
    public void putMarkHard(Coordinate c, byte mark) {
        matrix[c.row][c.col] = mark;  //mark it
        nextMovePlayer = calculateNextPlayer(nextMovePlayer);
    }
    
    public static byte calculateNextPlayer(byte player) {
        return (byte) ((player + 1) - (2 * ((player + 1) % 2)));
    }
    
    /**
     * Does the opposite of putMark
     * 
     * @param c Coordinate from which the mark should be removed
     * @return true if mark was removed (if the field has been marked
     * previously), false otherwise.
     */
    public boolean removeMark(Coordinate c){
        if(!isFieldMarked(c)){
            return false;
        } else {
            matrix[c.row][c.col] = 0;
            noOfEmptyFields++;
            nextMovePlayer = (byte) ((nextMovePlayer + 1) % 2);
            return true;
        }
    }
    
    /**
     * Returns an array with coordinates of empty fields on the board.
     * O(size^2)
     *
     * @return Array of coordinates of empty fields
     */
    public Coordinate[] getEmptyFields() {
        Coordinate[] result = new Coordinate[noOfEmptyFields];

        int count = 0;
        //for each field on the board
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                //if the field is empty, add it
                if (matrix[row][col] == 0) {
                    result[count] = new Coordinate(row, col);
                    count++;
                }
            }
        }

        return result;
    }
    
    public Board[] getAllPossibleBoardsAfterNextPlayerMove(){
        Board [] possibleMoves = new Board[noOfEmptyFields];
        int i = 0;
        for(Coordinate emptyField : getEmptyFields()){
            possibleMoves[i] = deepCopy();
            possibleMoves[i].putMarkHard(emptyField, nextMovePlayer);
            i++;
        }
        return possibleMoves;
    }
    
    private byte[] createLegalRandomArrayForFillingEmtyFields(){
        byte [] result = new byte[noOfEmptyFields];
        int noOfFirstPlayerMovesRemaining = getNoOfRemainingMovesForFirstPlayer();
        int i;
        for(i = 0; i < noOfFirstPlayerMovesRemaining; i++){
            result[i] = FIRST_PLAYERS_MARK;
        }
        for(; i < noOfEmptyFields; i++){
            result[i] = SECOND_PLAYERS_MARK;
        }
        //todo izdvojiti u neki util
        MonteCarlo.shuffleArray(result);
        return result;
    }

    private int getNoOfRemainingMovesForFirstPlayer() {
        int noOfFirstPlayerMovesRemaining;
        noOfFirstPlayerMovesRemaining = noOfEmptyFields / 2;
        if(noOfEmptyFields % 2 != 0 && nextMovePlayer == FIRST_PLAYERS_MARK){
            noOfFirstPlayerMovesRemaining ++;
        }
        return noOfFirstPlayerMovesRemaining;
    }
    
    public  Board createPossibleOutcomeAfterAllFieldsMarked() {
        
        Board possibleOutcomeWithAllFiedsMarked = this.deepCopy();
        Coordinate[] emptyFields = this.getEmptyFields();
        byte[] sequence = this.createLegalRandomArrayForFillingEmtyFields();
        
        int seqCount = 0;
        for (Coordinate c : emptyFields) {
            possibleOutcomeWithAllFiedsMarked.putMarkHard(
                    new Coordinate(c.row, c.col), sequence[seqCount]);
            
            seqCount++;
        }
        return possibleOutcomeWithAllFiedsMarked;
    }
    
    /**
     * Makes a deep copy of itself.
     * 
     * @return A deep copy of itself
     */
    public Board deepCopy() {
        Board result = new Board(this.size);
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Coordinate c = new Coordinate(row, col);
                result.putMark(c, this.matrix[c.row][c.col]);
            }
        }
        
        result.noOfEmptyFields = this.noOfEmptyFields;
        result.nextMovePlayer = this.nextMovePlayer;
        
        return result;
    }
    
    /**
     * Calculates the coordinate from an absolute position on the table. E.g.
     * on a 3x3 board i = 5 will produce (1,1) - second row, second column.
     * 
     * @param i
     * @return Coordinate of i
     * @throws IndexOutOfBoundsException 
     */
    public final Coordinate intToCoordinate(int i) throws IndexOutOfBoundsException {
        if (size*size <= i) { 
            throw new IndexOutOfBoundsException(
                    "i is greater than the size of the board."); 
        }
        
        int row = i / size;
        int col = i % size;
        Coordinate result = new Coordinate(row, col);
        return result;
    }
    
    /**
     * Converts the board to a single row String of -1s, 0s and 1s. Used for
     * input to learning models and statistics, not human readable.
     * 
     * @param reversePlayers Should player 1 and 2 be exchanged
     * @return 
     */
    public String toSingleRowString(boolean reversePlayers) {
        StringBuilder sb = new StringBuilder();
        
        for (byte[] row : matrix) {
            for (byte field : row) {
                byte b = field;
                if (field > 0 && reversePlayers) {
                    b = (byte)(1 + Math.abs(field - 2));
                }
                if (b == 2) { b = -1; }
                sb.append(b);
                sb.append(" ");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Converts the table to a human readable String that looks like a
     * Tic-Tac-Toe board.
     * 
     * @return Human readable representation of the Tic-Tac-Toe board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < size; row++) {

            //add empty spaces to make romboid shape
            for (int iCount = 0; iCount < row; iCount++) {
                sb.append(" ");
            }

            for (int col = 0; col < size; col++) {
                sb.append(matrix[row][col]).append(" ");
            }

            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }
}
package tictactoe;

import java.util.Scanner;

/**
 * Class PlayerHuman implements Player interface. It is used to input moves from
 * a human player.
 * 
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class PlayerHuman implements Player{

    /**
     * Asks player to input the coordinates of her move and returns that as her
     * move.
     * 
     * @param b Hex board
     * @return Coordinates of the player's move
     */
    @Override
    public Coordinate makeMove(Board b) {
        Scanner scan = new Scanner(System.in);
        System.out.println(b);
        Coordinate move = null;
        boolean legal = false;
        
        while (!legal) {
            System.out.print("Row: ");
            int row = scan.nextInt();
            System.out.print("Col: ");
            int col = scan.nextInt();
            move = new Coordinate(--row, --col);
            legal = b.isMoveLegal(move);
            if (!legal) { System.out.println("Illegal move!"); }
        }
        
        return move;
    }
}

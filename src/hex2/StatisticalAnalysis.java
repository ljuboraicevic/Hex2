/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hex2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nikola
 */
public class StatisticalAnalysis {

    private static final int[] noOfMoves = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29};
    private static final int START_OF_MIDDLE = 5;
    private static final int START_OF_BEGIN = 0;
    private static final int START_OF_END = 10;
    private static final int BOARD_SIZE = 7;

    private static int getNoOfMoves() {
        double random = Math.random();
        int index;
        if (random < 0.5) {
            index = START_OF_MIDDLE + randomInRangeInclusive(0, 4);
        } else if (random < 0.75) {
            index = START_OF_BEGIN + randomInRangeInclusive(0, 4);
        } else {
            index = START_OF_END + randomInRangeInclusive(0, 4);
        }
        return noOfMoves[index];
    }

    private static int randomInRangeInclusive(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    private static Board[] getRandomBoards(int numberOfBoards, int boardSize) {
        Board[] boards = new Board[numberOfBoards];
        HashSet<String> set = new HashSet();
        for (int i = 0; i < numberOfBoards; i++) {
            int numberOfMoves = getNoOfMoves();
            Board b = RandomBoardGenerator.makeRandomBoard(numberOfMoves, boardSize);
            while (set.contains(b.toSingleRowString(false))) {
                b = RandomBoardGenerator.makeRandomBoard(numberOfMoves, boardSize);
            }
            boards[i] = b;
            set.add(boards[i].toSingleRowString(false));
        }
        return boards;
    }

    private static int[] getNumberOfRepetitions(int totalNumber, int startingNumberOfRepetitions,
            int percentOfIncrease) {
        int[] repetitions = new int[totalNumber];
        repetitions[0] = startingNumberOfRepetitions;
        for (int i = 1; i < totalNumber; i++) {
            repetitions[i] = increaseNumberForGivenPercent(repetitions[i - 1], percentOfIncrease);
        }
        return repetitions;
    }

    private static int increaseNumberForGivenPercent(int number, int percent) {
        return (int) Math.round(number * (1 + percent / 100.0));
    }

    public static void main(String args[]) throws Exception {
        Board[] randomBoards = getRandomBoards(100, BOARD_SIZE);
        int[] repetitions = getNumberOfRepetitions(62, 500, 5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repetitions.length; i++) {
            sb.append("********************");
            sb.append("\n");
            sb.append(repetitions[i]);
            sb.append("\n");

            for (Board board : randomBoards) {
                double winProbability = MonteCarlo.getProbabilityPlayerThatMadeLastMoveWins(board, repetitions[i]);
                String boardAsString = board.toSingleRowString(false);
                sb.append(boardAsString).append(" ").append(winProbability).append("\n");
            }
        }

        try (FileWriter fw = new FileWriter(new File("statistics2"))) {
            fw.write(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(Hex2.class.getName()).log(Level.SEVERE, null, ex);
        }
//        try {
//            Scanner scan = new Scanner(new File("statistics"));
//            String[] repetitions = new String[62];
//            String[] ids = new String[100];
//            int k = 0;
//            String[][] probById = new String[62][100];
//            while(scan.hasNextLine()){
//                String nextLine = scan.nextLine();
//                if(nextLine.startsWith("*")){
//                    nextLine = scan.nextLine();
//                } else {
//                    throw new Exception("wtf");
//                }
//                repetitions[k] = Integer.valueOf(nextLine).toString();
//                for(int i = 0; i < 100; i++){
//                    nextLine = scan.nextLine();
//                    String[] chunks = nextLine.split(" ");
//                    String id = "";
//                    for(int j = 0; j < chunks.length - 1; j++){
//                        id = id + chunks[j];
//                    }
//                    ids[i] = id;
//                    String prob = chunks[chunks.length - 1];
//                    probById[k][i] = prob;
//                }
//                k++;
//            }
//            for(int i = 0; i < ids.length; i++){
//                FileWriter writer = new FileWriter(new File ("statistic/" + ids[i]));
//                for(int j = 0; j < 62; j++){
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(repetitions[j]);
//                    sb.append(", ");
//                    sb.append(probById[j][i]);
//                    sb.append("\n");
//                    writer.write(sb.toString());
//                }
//                writer.close();
//            }
//                for(int i = 0; i < 62; i++){
//                FileWriter writer = new FileWriter(new File ("out/" + repetitions[i]));
//                    for(int j = 0; j < ids.length; j++){
//                        StringBuilder sb = new StringBuilder();
//                        sb.append(ids[j]);
//                        sb.append(", ");
//                        sb.append(probById[i][j]);
//                        sb.append("\n");
//                        writer.write(sb.toString());
//                }
//                writer.close();
//            }
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(StatisticalAnalysis.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }
}

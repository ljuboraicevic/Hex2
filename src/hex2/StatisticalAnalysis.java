/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hex2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author nikola
 */
public class StatisticalAnalysis {
    private static final int BOARD_SIZE = 7;

    
    public static void main(String args[]) throws Exception {
        compareNaiveMCLogicWithSearchTreeMCLogic();
    }
   
    public static void compareNaiveMCLogicWithSearchTreeMCLogic() throws IOException {
        FileWriter fw = new FileWriter(new File("naiveVSsearch4.txt"));
        Board board = RandomBoardGenerator.makeRandomBoard(7, 7);
        System.out.println(board.toString());

        for (int plyCounter = 2; plyCounter < 4; plyCounter++) {
            for (int noOfBestMoves = 2; noOfBestMoves < 4; noOfBestMoves++) {
                int differentChoiceCounter = 0;
                ArrayList<MCSimulationMove> naiveMCMoves = new ArrayList<>();
                ArrayList<MCSimulationMove> searchMCMoves = new ArrayList<>();
                for(int i = 0; i < 100; i++ ){
                    UbiqutousMCSearchTreeLogic mcSearchLogic = new UbiqutousMCSearchTreeLogic(1000, plyCounter, noOfBestMoves);
//                    MCNaiveLogic mcNaiveLogic = new MCNaiveLogic(1000);
//                    
//                    MCSimulationMove naiveMove = mcNaiveLogic.getBestMove(board);
                    MCSimulationMove searchMove = mcSearchLogic.calculateMoveWithoutOpponentsBranching(board);
                    
//                    Coordinate naiveMoveCoordinate = naiveMove.getCoordinates();
                    Coordinate searchMoveCoordinate = searchMove.getCoordinates();
//                    naiveMCMoves.add(naiveMove);
                    searchMCMoves.add(searchMove);
                    if (mcSearchLogic.didChangeMove) {
                        differentChoiceCounter++;
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Best moves: ").append(noOfBestMoves).append("\n");
                sb.append("Plies: ").append(plyCounter).append("\n");
                sb.append("Number of different choices (of 100): ").append(differentChoiceCounter).append("\n");
                sb.append("All choices (naive - branching):").append("\n");
                for(int i = 0; i < searchMCMoves.size(); i++ ){
                    sb.append(searchMCMoves.get(i).toString()).append("\n");
               //     sb.append(naiveMCMoves.get(i).toString()).append(" - ").append(searchMCMoves.get(i).toString()).append("\n");
                }
                sb.append("\n");
                fw.write(sb.toString());                
            }
        }
        fw.close();
    }

    private static int getNoOfMoves() {
        final int[] noOfMoves = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29};
        final int START_OF_MIDDLE = 5;
        final int START_OF_BEGIN = 0;
        final int START_OF_END = 10;
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

    private static void testRandomBoards() {
        Board[] randomBoards = getRandomBoards(100, BOARD_SIZE);
        int[] repetitions = getNumberOfRepetitions(62, 500, 5);
        for (int i = 0; i < repetitions.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("********************");
            sb.append("\n");
            sb.append(repetitions[i]);
            sb.append("\n");
            for (Board board : randomBoards) {
                for(int j = 0; j < 100; j++){
                    double winProbability = MonteCarlo.evaluateBoard(board, repetitions[i]);
                    String boardAsString = board.toSingleRowString(false);
                    sb.append(boardAsString).append(" ").append(winProbability).append("\n");
                }
            }
            try (FileWriter fw = new FileWriter(new File("statistics/statistics_for_repetitions_"+repetitions[i] ))) {
                fw.write(sb.toString());
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Hex2.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("repetitions "+repetitions[i] + " done");
        }
        
    }
       
    private static void createCsvFromSums() throws FileNotFoundException, IOException{
        int[] repetitions = getNumberOfRepetitions(62, 500, 5);
        int ll = 0;
    //    FileWriter writer = new FileWriter(new File("summ/summaryOfSummary"));
        HashMap<String, ArrayList> map = new HashMap();
        for(int i = 0; i<62; i++){
            Scanner scan = new Scanner(new File("summ/summary"+repetitions[i]));
            while(scan.hasNextLine()){
                String nextLine = scan.nextLine();
                String[] chunks = nextLine.split(" ");
                String id = "";
                for (int j = 0; j < 49; j++) {
                    id = id + chunks[j];
                }
                double sum = 0.;
                for(int j = 50; j< chunks.length; j++){
                    try{
                        sum += Double.parseDouble(chunks[j]);
                    } catch (NumberFormatException e){
                      ll++;
                    }
                }
                double avg = sum / 100;
                System.out.println(chunks.length - 49);
                if(map.containsKey(id)){
                    map.get(id).add(new Pair<Integer, Double>(repetitions[i], avg));
                } else {
                    ArrayList<Pair> list = new ArrayList();
                    list.add(new Pair<Integer, Double>(repetitions[i], avg));
                    map.put(id, list);
                }
            }
        }
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Entry entry = (Entry) it.next();
            FileWriter writer = new FileWriter(new File("summ3/" + (String)entry.getKey() +".csv"));
            for(Pair pair : (ArrayList<Pair>) entry.getValue()){
                StringBuilder sb = new StringBuilder();
                sb.append(pair.getKey()).append(",").append(pair.getValue()).append("\n");
                writer.write(sb.toString());   
                
            }
            writer.close();
        }
        System.out.println(ll);
        
    }
    
    
    private static void parseStatistics() throws FileNotFoundException, Exception{
        int[] repetitions = getNumberOfRepetitions(62, 500, 5);
        ArrayList<HashMap<String, ArrayList<Double>> >list;
        list = new ArrayList<>();
        
        for(int i = 0; i<62; i++){
            Scanner scan = new Scanner(new File("statistics/statistics_for_repetitions_"+repetitions[i]));
            list.add(new HashMap<>());
            
            String nextLine = scan.nextLine();
            if (nextLine.startsWith("*")) {
                nextLine = scan.nextLine();
            } else {
                throw new Exception("wtf");
            }
            scan.nextLine();
            while(scan.hasNextLine()){
                nextLine = scan.nextLine();
                String[] chunks = nextLine.split(" ");
                String id = "";
                for (int j = 0; j < chunks.length - 1; j++) {
                    id = id + chunks[j];
                }
                int size = (int) Math.sqrt(chunks.length - 1);
                Board b = new Board(id, size);
                String boardAsString = b.toSingleRowString(false);
                Double probability = Double.parseDouble(chunks[chunks.length - 1]);
                if(!list.get(i).containsKey(boardAsString)){
                    ArrayList<Double> a = new ArrayList<>();
                    a.add(probability);
                    list.get(i).put(boardAsString, a);
                } else {
                    list.get(i).get(boardAsString).add(probability);
                }
            }
        }
        for(int i = 0; i<62; i++){
            FileWriter fw = new FileWriter(new File("summ/summary"+repetitions[i]), false);
            
            StringBuilder sb = new StringBuilder();
            for (Iterator it = list.get(i).entrySet().iterator(); it.hasNext();) {
                Entry entry = (Entry) it.next();
                sb.append(entry.getKey()).append(" ");
                for(Double d : (ArrayList<Double>) entry.getValue()){
                    sb.append(d).append(" ");
                }
                sb.append("\n");
            }
            fw.write(sb.toString());                
                        
            
            fw.close();
        }
    
        
    }

    private static ArrayList <Board> readBoardsFromStatisticsFile(String filename) throws Exception, FileNotFoundException {
        Scanner scan = new Scanner(new File(filename));
        ArrayList <Board> list = new ArrayList<Board>();
        HashSet<String> set = new HashSet<>();
        String nextLine = scan.nextLine();
        if (nextLine.startsWith("*")) {
            nextLine = scan.nextLine();
        } else {
            throw new Exception("unsuported file pattern. file should start with" + 
                    "**************************\n numberOfRepetitions");
        }
        //skip line with number of repetitions
        scan.nextLine();
        while (scan.hasNextLine()) {
            
            nextLine = scan.nextLine();
            String[] chunks = nextLine.split(" ");
            String id = "";
            for (int j = 0; j < chunks.length - 1; j++) {
                id = id + chunks[j];
            }
            int size = (int) Math.sqrt(chunks.length - 1);
            Board b = new Board(id, size);
            if(!set.contains(b.toSingleRowString(false))){
                list.add(b);
                set.add(b.toSingleRowString(false));
            }
        }
        return list;
    }
}

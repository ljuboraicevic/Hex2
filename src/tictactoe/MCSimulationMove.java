package tictactoe;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class MCSimulationMove implements Comparable<MCSimulationMove> {
    private final Coordinate coordinate;
    private final Double probability;

    public MCSimulationMove(Coordinate coordinate, Double simulationsWon) {
        this.coordinate = coordinate;
        this.probability = simulationsWon;
    }

    public Coordinate getCoordinates() {
        return coordinate;
    }

    public Double getProbability() {
        return probability;
    }

    @Override
    public String toString() {
        return "MCSimulationMove{" + "coordinate=" + coordinate + ", probability=" + probability + '}';
    }

    @Override
    /**
     * used in sorting array of unplayed moves
     */
    public int compareTo(MCSimulationMove other) {
        double result = this.probability - other.probability;
        if(this.probability - other.probability < 0){
            return -1;
        } else if (result > 0){
            return 1;
        }
        return 0;
    }      
}

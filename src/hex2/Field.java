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
public class Field {

    private Coordinate coordinate;
    private byte mark;
    
    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public byte getMark() {
        return mark;
    }

    public void setMark(byte mark) {
        this.mark = mark;
    }

    public Field(Coordinate coordinate, byte mark) {
        this.coordinate = coordinate;
        this.mark = mark;
    }
    

}

package olyapps.sathv.fbla2020;

/**
 * Created by sathv on 8/15/2018.
 */

public class ColourItem {
    private String displayString;
    private int colour;

    public ColourItem (int colour) {
        this(colour, "");
    }
    public ColourItem (int colour, String displayString) {
        this.colour = colour;
        this.displayString = displayString;
    }

    @Override
    public String toString() {
        return displayString;
    }
    public void setDisplayString(String s){
        this.displayString = s;
    }

    public int getColour(){
        return colour;
    }
    public void setColour(int i){
        this.colour = i;
    }
}

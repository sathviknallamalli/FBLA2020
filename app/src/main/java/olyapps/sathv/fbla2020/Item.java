package olyapps.sathv.fbla2020;

/**
 * Created by sathv on 9/29/2018.
 */

public class Item{
    public final String text;
    public final int icon;
    public Item(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }
    @Override
    public String toString() {
        return text;
    }
}
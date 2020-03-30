package olyapps.sathv.fbla2020;

/**
 * Created by sathv on 7/3/2018.
 */

public class CurrentEvent {

    String rownameevent;
    boolean isTeam;

    public CurrentEvent(String rownameevent, boolean isTeam) {
        this.rownameevent = rownameevent;
        this.isTeam = isTeam;
    }

    public String getRownameevent() {
        return rownameevent;
    }

    public void setRownameevent(String rownameevent) {
        this.rownameevent = rownameevent;
    }

    public boolean isTeam() {
        return isTeam;
    }

    public void setTeam(boolean team) {
        isTeam = team;
    }
}

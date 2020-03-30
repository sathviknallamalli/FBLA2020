package olyapps.sathv.fbla2020;

/**
 * Created by sathv on 6/4/2018.
 */

public class Events {
    String eventname, eventtype, eventcategory;

    public Events(String eventname, String eventtype, String eventcategory) {
        this.eventname = eventname;
        this.eventtype = eventtype;
        this.eventcategory = eventcategory;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    public String getEventcategory() {
        return eventcategory;
    }

    public void setEventcategory(String eventcategory) {
        this.eventcategory = eventcategory;
    }
}

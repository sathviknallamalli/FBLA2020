package olyapps.sathv.fbla2020;

/**
 * Created by sathv on 6/4/2018.
 */

public class CalendarEvent {
    String caltitle, caldate;

    public CalendarEvent(String caltitle, String caldate) {
        this.caltitle = caltitle;
        this.caldate = caldate;
    }

    public String getCaltitle() {
        return caltitle;
    }

    public void setCaltitle(String caltitle) {
        this.caltitle = caltitle;
    }

    public String getCaldate() {
        return caldate;
    }

    public void setCaldate(String caldate) {
        this.caldate = caldate;
    }
}

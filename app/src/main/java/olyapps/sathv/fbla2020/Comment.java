package olyapps.sathv.fbla2020;

/**
 * Created by sathv on 6/8/2018.
 */

public class Comment {

    String text,uid,timestemp;

    public Comment(String text, String uid, String timestemp) {
        this.text = text;
        this.uid = uid;
        this.timestemp = timestemp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String name) {
        this.uid = uid;
    }

    public String getTimestemp() {
        return timestemp;
    }

    public void setTimestemp(String timestemp) {
        this.timestemp = timestemp;
    }
}


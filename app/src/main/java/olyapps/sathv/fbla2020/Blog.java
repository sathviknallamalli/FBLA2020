package olyapps.sathv.fbla2020;

/**
 * Created by sathv on 7/9/2018.
 */

public class Blog {
    String desc, title, imageurl, username, timestamp, uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Blog() {
    }

    public Blog(String desc, String title, String imageurl, String username, String timestamp, String uid) {
        this.desc = desc;
        this.title = title;
        this.imageurl = imageurl;
        this.username = username;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

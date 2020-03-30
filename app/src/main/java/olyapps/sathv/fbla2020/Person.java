package olyapps.sathv.fbla2020;

/**
 * Created by sathv on 6/8/2018.
 */

public class Person {

    String personname;
    String isGroup;
    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Person(String personname, String isGroup, String uid) {
        this.personname = personname;
        this.isGroup = isGroup;
        this.uid = uid;
    }

    public String isGroup() {
        return isGroup;
    }

    public void setGroup(String group) {
        isGroup = group;
    }

    public String getPersonname() {
        return personname;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }

}


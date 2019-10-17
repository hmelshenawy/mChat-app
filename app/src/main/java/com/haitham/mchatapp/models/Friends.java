package com.haitham.mchatapp.models;

public class Friends {

    String NAME, STATUS, IMAGE, USERID;


    public Friends() {

        //Empty constartuctor is required.

    }

    public Friends(String NAME, String STATUS, String IMAGE, String USERID) {
        this.NAME = NAME;
        this.STATUS = STATUS;
        this.IMAGE = IMAGE;
        this.USERID = USERID;
    }

    public Friends(String NAME, String STATUS, String USERID) {
        this.NAME = NAME;
        this.STATUS = STATUS;
        this.USERID = USERID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public void setIMAGE(String IMAGE) {
        this.IMAGE = IMAGE;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }
}

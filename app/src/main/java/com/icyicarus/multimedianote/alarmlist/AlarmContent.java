package com.icyicarus.multimedianote.alarmlist;

public class AlarmContent {
    private int id = -1;
    private String title = "";
    private String time = "";
    private int noteID = -1;

    public AlarmContent(int id, String title, String time, int noteID) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.noteID = noteID;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public int getNoteID() {
        return noteID;
    }
}

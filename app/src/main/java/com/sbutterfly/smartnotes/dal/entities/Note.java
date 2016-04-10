package com.sbutterfly.smartnotes.dal.entities;

public class Note {

    private int id;
    private String title;
    private String body;
    private int importance;

    public static abstract class Importance {
        public final static int NONE = 0;
        public final static int LOW = 1;
        public final static int MIDDLE = 2;
        public final static int HIGH = 3;
    }

    public Note(int id, String title, String body, int importance) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.importance = importance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }
}

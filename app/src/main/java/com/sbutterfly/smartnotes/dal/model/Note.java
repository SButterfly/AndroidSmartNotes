package com.sbutterfly.smartnotes.dal.model;

import android.os.Bundle;

public class Note extends BaseModel {

    private final static String KEY_TITLE = "title";
    private final static String KEY_BODY = "body";
    private final static String KEY_IMPORTANCE = "importance";

    private String title = "";
    private String body = "";
    private int importance;

    public static abstract class Importance {
        public final static int NONE = 0;
        public final static int LOW = 1;
        public final static int MIDDLE = 2;
        public final static int HIGH = 3;
    }

    public Note() {
        importance = Importance.NONE;
    }

    public Note(int id, String title, String body, int importance) {
        super(id);
        this.title = title;
        this.body = body;
        this.importance = importance;
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

    @Override
    public void save(Bundle bundle) {
        super.save(bundle);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_BODY, body);
        bundle.putInt(KEY_IMPORTANCE, importance);
    }

    @Override
    public void populate(Bundle bundle) {
        super.populate(bundle);
        title = bundle.getString(KEY_TITLE);
        body = bundle.getString(KEY_BODY);
        importance = bundle.getInt(KEY_IMPORTANCE);
    }

    @Override
    public String toString() {
        return "Note{" +
                "id='" + getId() + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", importance=" + importance +
                '}';
    }
}

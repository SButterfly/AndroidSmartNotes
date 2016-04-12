package com.sbutterfly.smartnotes.dal.model;

import android.os.Bundle;

public class BaseModel {

    public final static int INVALID_ID = -1;

    private static final String ID_KEY = "id";

    private int id = INVALID_ID;

    public BaseModel() { }

    public BaseModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        save(bundle);
        return bundle;
    }

    public void save(Bundle bundle) {
        bundle.putInt(ID_KEY, id);
    }

    public void populate(Bundle bundle) {
        id = bundle.getInt(ID_KEY);
    }
}

package chuang.karote.actionslistgenerator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by karot.chuang on 2016/11/29.
 */

public class SavedActionList {
    private final static String KEY_ID = "_id";
    private final static String KEY_TIME_STAMP = "timeStamp";
    private final static String KEY_LIST_NAME = "listName";
    private final static String KEY_JSON_STRING = "jsonString";

    @Expose
    @SerializedName(KEY_ID)
    private long id;

    @Expose
    @SerializedName(KEY_TIME_STAMP)
    private long timeStamp;

    @Expose
    @SerializedName(KEY_LIST_NAME)
    private String listName;

    @Expose
    @SerializedName(KEY_JSON_STRING)
    private String jsonString;

    private SavedActionList(long id, long timeStamp, String listName, String jsonString) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.listName = listName;
        this.jsonString = jsonString;
    }

    public static class Builder {
        private long id;
        private long timeStamp;
        private String listName;
        private String jsonString;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder setListName(String listName) {
            this.listName = listName;
            return this;
        }

        public Builder setJsonString(String jsonString) {
            this.jsonString = jsonString;
            return this;
        }

        public SavedActionList create() {
            return new SavedActionList(id, timeStamp, listName, jsonString);
        }
    }

    @Override
    protected SavedActionList clone() throws CloneNotSupportedException {
        try {
            return (SavedActionList) super.clone();
        } catch (CloneNotSupportedException e) {
            return new SavedActionList.Builder().create();
        }
    }

    public long getId() {
        return id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getListName() {
        return listName;
    }

    public String getJsonString() {
        return jsonString;
    }

    public SavedActionList setId(long id) {
        this.id = id;
        return this;
    }

    public SavedActionList setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public SavedActionList setListName(String listName) {
        this.listName = listName;
        return this;
    }

    public SavedActionList setJsonString(String jsonString) {
        this.jsonString = jsonString;
        return this;
    }
}

package chuang.karote.actionslistgenerator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by karot.chuang on 2016/11/8.
 */

public class TabataAction {
    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("description")
    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

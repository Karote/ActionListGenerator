package chuang.karote.actionslistgenerator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by karot.chuang on 2016/11/16.
 */

public class TabataConfig {
    @Expose
    @SerializedName("music")
    String music;

    @Expose
    @SerializedName("actions")
    List<TabataAction> actions;

    public TabataConfig(String music, List<TabataAction> actions) {
        this.music = music;
        this.actions = actions;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public void setActions(List<TabataAction> actions) {
        this.actions = actions;
    }

    public String getMusic() {
        return music;
    }

    public List<TabataAction> getActions() {
        return actions;
    }
}

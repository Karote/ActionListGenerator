package chuang.karote.actionslistgenerator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by karot.chuang on 2016/11/8.
 */

public class TabataAction implements Parcelable {
    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("pictures")
    private List<String> pictures;

    public TabataAction(String name, List<String> pictures) {
        this.name = name;
        this.pictures = pictures;
    }

    protected TabataAction(Parcel in) {
        name = in.readString();
        pictures = in.createStringArrayList();
    }

    public static final Creator<TabataAction> CREATOR = new Creator<TabataAction>() {
        @Override
        public TabataAction createFromParcel(Parcel in) {
            return new TabataAction(in);
        }

        @Override
        public TabataAction[] newArray(int size) {
            return new TabataAction[size];
        }
    };

    public String getName() {
        return name;
    }

    public List<String> getPictures() {
        return pictures;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(pictures);
    }
}

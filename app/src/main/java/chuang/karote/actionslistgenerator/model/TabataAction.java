package chuang.karote.actionslistgenerator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by karot.chuang on 2016/11/8.
 */

public class TabataAction implements Parcelable{
    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("description")
    private String description;

    public TabataAction(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected TabataAction(Parcel in) {
        name = in.readString();
        description = in.readString();
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

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
    }
}

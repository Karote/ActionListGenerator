package chuang.karote.actionslistgenerator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Karote on 2016/12/18.
 */

public class CalenderLog {
    private final static String KEY_ID = "_id";
    private final static String KEY_CALENDER_DATE = "calenderDate";
    private final static String KEY_COUNTER = "counter";

    @Expose
    @SerializedName(KEY_ID)
    private long id;

    @Expose
    @SerializedName(KEY_CALENDER_DATE)
    private Date calenderDate;

    @Expose
    @SerializedName(KEY_COUNTER)
    private int counter;

    private CalenderLog(long id, Date calenderDate, int counter) {
        this.id = id;
        this.calenderDate = calenderDate;
        this.counter = counter;
    }

    public static class Builder {
        private long id;
        private Date calenderDate;
        private int counter;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setCalenderDate(Date calenderDate) {
            this.calenderDate = calenderDate;
            return this;
        }

        public Builder setCounter(int counter) {
            this.counter = counter;
            return this;
        }

        public CalenderLog create() {
            return new CalenderLog(id, calenderDate, counter);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        try {
            return (CalenderLog) super.clone();
        } catch (CloneNotSupportedException e) {
            return new CalenderLog.Builder().create();
        }
    }

    public long getId() {
        return id;
    }

    public CalenderLog setId(long id) {
        this.id = id;
        return this;
    }

    public Date getCalenderDate() {
        return calenderDate;
    }

    public CalenderLog setCalenderDate(Date calenderDate) {
        this.calenderDate = calenderDate;
        return this;
    }

    public int getCounter() {
        return counter;
    }

    public CalenderLog setCounter(int counter) {
        this.counter = counter;
        return this;
    }
}

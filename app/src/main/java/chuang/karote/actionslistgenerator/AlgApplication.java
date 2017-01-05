package chuang.karote.actionslistgenerator;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by karot.chuang on 2017/1/4.
 */

public class AlgApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());
    }
}

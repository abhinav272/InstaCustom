package com.abhinav.instacropperpicker;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static App instance;

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FileUtils.init();
    }

    public static App getInstance() {
        return instance;
    }
}

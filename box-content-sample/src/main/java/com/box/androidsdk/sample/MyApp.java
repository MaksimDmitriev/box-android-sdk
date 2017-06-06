package com.box.androidsdk.sample;

// Copyright (С) ABBYY (BIT Software), 1993 - 2017. All rights reserved.
// Автор: Максим Дмитриев

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }
}

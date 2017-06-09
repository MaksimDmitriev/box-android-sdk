// Copyright (С) ABBYY (BIT Software), 1993 - 2017. All rights reserved.
// Автор: Максим Дмитриев

package com.box.androidsdk.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class SecondActivity extends ActionBarActivity implements View.OnClickListener {

    private TaskHolder mTaskHolder;

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.log_out:
                mTaskHolder.logout();
                break;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mTaskHolder = (TaskHolder) fragmentManager.findFragmentByTag(TaskHolder.FRAGMENT_TAG);
        if (mTaskHolder == null) {
            fragmentManager.beginTransaction().add(mTaskHolder = TaskHolder.newInstance(getClass().getSimpleName()),
                                                   TaskHolder.FRAGMENT_TAG
            ).commit();
        }
        findViewById(R.id.log_out).setOnClickListener(this);
    }
}

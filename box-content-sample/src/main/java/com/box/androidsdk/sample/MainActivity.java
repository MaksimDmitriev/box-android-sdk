package com.box.androidsdk.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.box.androidsdk.content.auth.BoxAuthentication;

/**
 * Sample content app that demonstrates session creation, and use of file api.
 */
public class MainActivity extends ActionBarActivity {

    private TaskHolder mTaskHolder;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mTaskHolder = (TaskHolder) fragmentManager.findFragmentByTag(TaskHolder.FRAGMENT_TAG);
        if (mTaskHolder == null) {
            fragmentManager.beginTransaction().add(mTaskHolder = new TaskHolder(),
                                                   TaskHolder.FRAGMENT_TAG
            ).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int numAccounts = BoxAuthentication.getInstance().getStoredAuthInfo(this).keySet().size();
        menu.findItem(R.id.logoutAll).setVisible(numAccounts > 1);
        menu.findItem(R.id.logout).setVisible(numAccounts > 0);
        menu.findItem(R.id.switch_accounts).setTitle(
                numAccounts > 0 ? R.string.switch_accounts : R.string.login);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.upload) {
            mTaskHolder.uploadSampleFile();
            return true;
        } else if (id == R.id.switch_accounts) {
            mTaskHolder.switchAccounts();
            return true;
        } else if (id == R.id.logout) {
            mTaskHolder.logout();
            return true;
        } else if (id == R.id.logoutAll) {
            new Thread() {
                @Override
                public void run() {
                    BoxAuthentication.getInstance().logoutAllUsers(getApplicationContext());
                }
            }.start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

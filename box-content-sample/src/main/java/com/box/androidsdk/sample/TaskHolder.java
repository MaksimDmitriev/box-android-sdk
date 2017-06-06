package com.box.androidsdk.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxEntity;
import com.box.androidsdk.content.models.BoxError;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsFile;
import com.box.androidsdk.content.requests.BoxResponse;

import org.apache.http.HttpStatus;

public class TaskHolder extends Fragment implements BoxAuthentication.AuthListener {

    private static final String TAG = "TaskHolder";
    public static final String FRAGMENT_TAG = "TaskHolder";

    BoxSession mSession = null;
    BoxSession mOldSession = null;

    private BoxApiFolder mFolderApi;
    private BoxApiFile mFileApi;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, "onCreate");
        BoxConfig.IS_LOG_ENABLED = true;
        configureClient();
        initSession();
    }

    /**
     * Set required config parameters. Use values from your application settings in the box
     * developer console.
     */
    private void configureClient() {
        // Test 500
        BoxConfig.CLIENT_ID = "";
        BoxConfig.CLIENT_SECRET = "";

        // needs to match redirect uri in developer settings if set.
        BoxConfig.REDIRECT_URL = "";
    }

    /**
     * Create a BoxSession and authenticate.
     */
    private void initSession() {
        mSession = new BoxSession(MyApp.getContext());
        mSession.setSessionAuthListener(this);
        mSession.authenticate(MyApp.getContext());
    }

    @Override
    public void onRefreshed(final BoxAuthentication.BoxAuthenticationInfo info) {
        Log.d(TAG, "onRefreshed");
    }

    @Override
    public void onAuthCreated(final BoxAuthentication.BoxAuthenticationInfo info) {
        Log.d(TAG, "onAuthCreated");
        mFolderApi = new BoxApiFolder(mSession);
        mFileApi = new BoxApiFile(mSession);
    }

    @Override
    public void onAuthFailure(
            final BoxAuthentication.BoxAuthenticationInfo info, final Exception ex) {
        Log.d(TAG, "onAuthFailure");
        if (ex != null) {
            Log.d(TAG, "onAuthFailure", ex);
        } else if (info == null && mOldSession != null) {
            mSession = mOldSession;
            mSession.setSessionAuthListener(this);
            mOldSession = null;
            onAuthCreated(mSession.getAuthInfo());
        }
    }

    @Override
    public void onLoggedOut(
            final BoxAuthentication.BoxAuthenticationInfo info, final Exception ex) {
        Log.d(TAG, "onLoggedOut");
    }

    public void switchAccounts() {
        mOldSession = mSession;
        // when switching accounts we don't care about events for the old session.
        mOldSession.setSessionAuthListener(null);
        mSession = new BoxSession(MyApp.getContext());
        mSession.setSessionAuthListener(this);
        mSession.authenticate(MyApp.getContext())
                .addOnCompletedListener(new BoxFutureTask.OnCompletedListener<BoxSession>() {
                    @Override
                    public void onCompleted(BoxResponse<BoxSession> response) {
                        if (response.isSuccess()) {

                        }
                    }
                });
    }

    public void logout() {
        BoxAuthentication.getInstance().addListener(new BoxAuthentication.AuthListener() {
            @Override
            public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {

            }

            @Override
            public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {

            }

            @Override
            public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {

            }

            @Override
            public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
                Log.d(TAG, "BoxAuthentication onLoggedOut");
            }
        });
        mSession.logout();

    }

    public void uploadSampleFile() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String uploadFileName = "box_logo.png";
                    InputStream uploadStream = getResources().getAssets().open(uploadFileName);
                    String destinationFolderId = "0";
                    String uploadName = "BoxSDKUpload.png";
                    BoxRequestsFile.UploadFile request = mFileApi.getUploadRequest(uploadStream, uploadName, destinationFolderId);
                    final BoxFile uploadFileInfo = request.send();
                    Log.d(TAG, "Uploaded " + uploadFileInfo.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BoxException e) {
                    e.printStackTrace();
                    BoxError error = e.getAsBoxError();
                    if (error != null && error.getStatus() == HttpStatus.SC_CONFLICT) {
                        ArrayList<BoxEntity> conflicts = error.getContextInfo().getConflicts();
                        if (conflicts != null && conflicts.size() == 1 && conflicts.get(0) instanceof BoxFile) {
                            uploadNewVersion((BoxFile) conflicts.get(0));
                            return;
                        }
                    }
                    Log.d(TAG, "Upload failed");
                }
            }
        }.start();

    }

    /**
     * Method demonstrates a new version of a file being uploaded using the file api
     * @param file
     */
    private void uploadNewVersion(final BoxFile file) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String uploadFileName = "box_logo.png";
                    InputStream uploadStream = getResources().getAssets().open(uploadFileName);
                    BoxRequestsFile.UploadNewVersion request = mFileApi.getUploadNewVersionRequest(uploadStream, file.getId());
                    final BoxFile uploadFileVersionInfo = request.send();
                    Log.d(TAG, "Uploaded new version of " + uploadFileVersionInfo.getName());
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                } catch (BoxException e) {
                    Log.e(TAG, "", e);
                }
            }
        }.start();
    }
}

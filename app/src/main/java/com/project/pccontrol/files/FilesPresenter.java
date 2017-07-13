package com.project.pccontrol.files;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.project.pccontrol.control.ActionControl;
import com.project.pccontrol.main.MainActivity;
import com.project.pccontrol.mouse.MouseContractor;
import com.project.pccontrol.mouse.MousePresenter;
import com.project.pccontrol.protocol.RemoteItActionReceiver;
import com.project.pccontrol.protocol.action.FileExploreRequestAction;
import com.project.pccontrol.protocol.action.FileExploreResponseAction;
import com.project.pccontrol.protocol.action.RemoteItAction;

import java.util.Arrays;

/**
 * Created by Elbehiry on 5/30/17.
 */

public class FilesPresenter implements FilesContractor.Presenter, ActionControl.actionMessageBack,MainActivity.onRecieveRemoteAction {
    private ActionControl mActionControl;
    private static FilesPresenter Instance = null;
    private String directory;
    private SharedPreferences preferences;
    private Handler handler;
    private FilesContractor.View mView;

    public static FilesPresenter getInstance(FilesContractor.View view,ActionControl actionControl) {
        if (Instance == null) {
            Instance = new FilesPresenter(view,actionControl);
        }
        return Instance;
    }


    public FilesPresenter(FilesContractor.View view,ActionControl actionControl){
        mView = view;
        mActionControl = actionControl;
        handler= new Handler();
        preferences = mActionControl.getPreferences();
        directory = preferences.getString("fileExplore_directory", "");

    }

    @Override
    public void start() {
        refresh();
    }


    private void refresh() {
        sendFileExploreRequest("");
    }

    @Override
    public void sendFileExploreRequest(String fileString) {
        if (mActionControl != null)
        mActionControl.sendAction(new FileExploreRequestAction(directory, fileString));
    }


    @Override
    public void onMessage(@Nullable final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mView.showMessage(message);
            }
        });

    }



    @Override
    public void receiveAction(RemoteItAction action) {
        if (action instanceof FileExploreResponseAction) {
            FileExploreResponseAction fera = (FileExploreResponseAction) action;
            directory = fera.directory;
            mView.returnedFiles(Arrays.asList(fera.files),directory);

        }
    }
}

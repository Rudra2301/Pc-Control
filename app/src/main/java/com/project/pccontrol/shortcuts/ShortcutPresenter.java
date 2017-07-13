package com.project.pccontrol.shortcuts;

import android.os.Handler;
import android.support.annotation.Nullable;

import com.project.pccontrol.control.ActionControl;
import com.project.pccontrol.main.MainActivity;
import com.project.pccontrol.protocol.action.Combination;
import com.project.pccontrol.protocol.action.FileExploreRequestAction;
import com.project.pccontrol.protocol.action.RemoteItAction;

/**
 * Created by MacBookPro on 5/31/17.
 */

public class ShortcutPresenter implements ShortcutContractor.Presenter ,ActionControl.actionMessageBack,MainActivity.onRecieveRemoteAction{
    ShortcutContractor.View mView;
    ActionControl mActionControl;
    private static ShortcutPresenter Instance = null;
    Handler mHandler;
    public static ShortcutPresenter getInstance(ShortcutContractor.View view,ActionControl actionControl){
        if (Instance == null){
            Instance = new ShortcutPresenter(view,actionControl);
        }
        return Instance;
    }

    private ShortcutPresenter(ShortcutContractor.View view,ActionControl actionControl){
        mActionControl = actionControl;
        mView = view;
        mHandler = new Handler();
    }

    @Override
    public void start() {

    }

    @Override
    public void sendAction(int actionNum) {
        if (mActionControl != null)
            mActionControl.sendAction(new Combination(actionNum));

    }

    @Override
    public void receiveAction(RemoteItAction action) {

    }

    @Override
    public void onMessage(@Nullable final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.showMessage(message);
            }
        });


    }
}

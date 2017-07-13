package com.project.pccontrol.mouse;

import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.project.pccontrol.BasePresenter;
import com.project.pccontrol.BaseView;

/**
 * Created by MacBookPro on 5/30/17.
 */

public interface MouseContractor {

    interface View extends BaseView<Presenter>{

        void showMessage(@Nullable String message);

        boolean ifWorking();


    }

    interface Presenter extends BasePresenter{

        void onTouchDown(@Nullable MotionEvent event,@Nullable byte whichButton);

        void onTouchMove(@Nullable MotionEvent event);

        void onTouchUp(@Nullable MotionEvent event,@Nullable byte whichButton);

        void onMouseClick(@Nullable boolean state,@Nullable byte whichButton);

        void playSound(@Nullable MediaPlayer mediaPlayer);

        void onVibrate(@Nullable int time);

        void sendMessage(@Nullable String s);

        void onKeyBoardSend(@Nullable String message);

        void onKeyBoardDelete();

        void onMouseTouchMove(MotionEvent event);

        void onMouseTouchDown(MotionEvent event,int width);

        void onMouseTouchUp(MotionEvent event);



    }
}

package com.project.pccontrol.mouse;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.project.pccontrol.R;
import com.project.pccontrol.control.ActionControl;
import com.project.pccontrol.main.MainActivity;
import com.project.pccontrol.protocol.RemoteItActionReceiver;
import com.project.pccontrol.protocol.action.KeyboardAction;
import com.project.pccontrol.protocol.action.MouseClickAction;
import com.project.pccontrol.protocol.action.MouseMoveAction;
import com.project.pccontrol.protocol.action.MouseWheelAction;
import com.project.pccontrol.protocol.action.RemoteItAction;
import com.project.pccontrol.protocol.action.ScreenCaptureResponseAction;

/**
 * Created by MacBookPro on 5/30/17.
 */

public class MousePresenter implements MouseContractor.Presenter, ActionControl.actionMessageBack, MainActivity.onRecieveRemoteAction {
    private static final String TAG = MousePresenter.class.getSimpleName();
    private MediaPlayer mpClickOn;
    private MediaPlayer mpClickOff;
    private boolean feedbackSound;
    private SharedPreferences preferences;
//    private Context mContext;
    private Handler handler;
    private MouseContractor.View mView;
    private boolean hold;
    private long holdDelay;
    private long clickDelay;


    private boolean mouseMoveOrWheel;
    private float wheelSensitivity;
    private float wheelAcceleration;
    private float wheelPrevious;
    private float wheelResult;
    private float wheelBarWidth;

    private boolean holdPossible;

    private float moveSensitivity;
    private float moveAcceleration;
    private float moveDownX;
    private float moveDownY;
    private float movePreviousX;
    private float movePreviousY;
    private float moveResultX;
    private float moveResultY;
    private float immobileDistance;
    private float screenDensity;
    private static final float MOUSE_WHEEL_SENSITIVITY_FACTOR = 10;

    private static MousePresenter Instance = null;
    private ActionControl mActionControl;

    public static MousePresenter getInstance(Context context, MouseContractor.View view,ActionControl actionControl) {
        if (Instance == null) {
            Instance = new MousePresenter(context, view,actionControl);
        }
        return Instance;
    }


    private MousePresenter(Context context, MouseContractor.View view,ActionControl actionControl) {
        mView = view;
        mActionControl = actionControl;
        handler = new Handler();
        mpClickOn = MediaPlayer.create(context, R.raw.clickon);
        mpClickOff = MediaPlayer.create(context, R.raw.clickoff);
        preferences = mActionControl.getPreferences();
        hold = false;
        feedbackSound = preferences.getBoolean("feedback_sound", false);
        holdDelay = Long.parseLong(preferences.getString("control_hold_delay", null));
        this.clickDelay = Long.parseLong(this.preferences.getString("control_click_delay", null));
        this.holdDelay = Long.parseLong(this.preferences.getString("control_hold_delay", null));
        screenDensity = context.getResources().getDisplayMetrics().density;
        this.immobileDistance = Float.parseFloat(this.preferences.getString("control_immobile_distance", null));
        this.immobileDistance *= screenDensity;
        this.moveSensitivity = Float.parseFloat(this.preferences.getString("control_sensitivity", null));
        this.moveSensitivity /= screenDensity;
        this.moveAcceleration = Float.parseFloat(this.preferences.getString("control_acceleration", null));
        this.wheelSensitivity = this.moveSensitivity / MOUSE_WHEEL_SENSITIVITY_FACTOR;
        this.wheelAcceleration = this.moveAcceleration;

    }


    @Override
    public void start() {

    }

    @Override
    public void onTouchDown(MotionEvent event, byte whichButton) {
        if (!this.hold) {
            onMouseClick(MouseClickAction.STATE_DOWN, whichButton);
            onVibrate(50);
        } else {
            hold = false;
        }
    }

    @Override
    public void onTouchMove(MotionEvent event) {
        if (!hold && event.getEventTime() - event.getDownTime() >= holdDelay) {
            hold = true;
            onVibrate(100);
        }
    }

    @Override
    public void onTouchUp(MotionEvent event, byte whichButton) {
        if (!this.hold) {
            onMouseClick(MouseClickAction.STATE_UP, whichButton);
        }

    }

    @Override
    public void onMouseClick(boolean state, byte whichButton) {
        mActionControl.sendAction(new MouseClickAction(whichButton, state));
        if (this.feedbackSound) {
            if (state) {
                this.playSound(this.mpClickOn);
            } else {
                this.playSound(this.mpClickOff);
            }
        }
    }

    @Override
    public void playSound(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    @Override
    public void onVibrate(int time) {
        mActionControl.vibrate(time);
    }

    @Override
    public void sendMessage(String s) {
        for (int i = 0; i < s.length(); i++)
            mActionControl.sendAction(new KeyboardAction(s.charAt(i)));
    }


    @Override
    public void onKeyBoardSend(@Nullable String message) {
        sendMessage(message);

    }

    @Override
    public void onKeyBoardDelete() {
        mActionControl.sendAction(new KeyboardAction(-1));
    }


    @Override
    public void onMouseTouchMove(MotionEvent event) {

        if (mouseMoveOrWheel) {
            if (event.getPointerCount() == 2) {
                mouseMoveOrWheel = false;
                onTouchDownMouseWheel(event);
                holdPossible = false;
            } else
                onTouchMoveMouseMove(event);
        } else {
            onTouchMoveMouseWheel(event);
        }
    }

    @Override
    public void onMouseTouchDown(MotionEvent event,int width) {
        mouseMoveOrWheel = event.getX() < width - wheelBarWidth;

        if (mouseMoveOrWheel) {
            onTouchDownMouseMove(event);
        }
        else {
            onTouchDownMouseWheel(event);
        }
    }

    @Override
    public void onMouseTouchUp(MotionEvent event) {
//        onMouseClick( MouseClickAction.STATE_UP,MouseClickAction.BUTTON_LEFT);
//        onVibrate(100);
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
        //received from mActionControl

        if (action instanceof ScreenCaptureResponseAction) {
            Log.i(TAG, "ScreenCaptureResponseAction");
        }


    }

    private void onTouchDownMouseWheel(MotionEvent event) {
        wheelPrevious = event.getRawY();
        wheelResult = 0;
    }

    private boolean isHold() {
        return hold;
    }

    private void setHold(boolean hold) {
        this.hold = hold;
    }

    private void onTouchMoveMouseMove(MotionEvent event) {
        if (holdPossible) {
            if (getDistanceFromDown(event) > immobileDistance) {
                holdPossible = false;
            } else if (event.getEventTime() - event.getDownTime() > this.holdDelay) {
                onMouseClick(MouseClickAction.STATE_DOWN, MouseClickAction.BUTTON_LEFT);
                holdPossible = false;
                setHold(true);
                onVibrate(100);
            }
        }

        float moveRawX = event.getRawX() - movePreviousX;
        float moveRawY = event.getRawY() - movePreviousY;

        moveRawX *= moveSensitivity;
        moveRawY *= moveSensitivity;

        moveRawX = (float) ((Math.pow(Math.abs(moveRawX), moveAcceleration) * Math.signum(moveRawX)));
        moveRawY = (float) ((Math.pow(Math.abs(moveRawY), moveAcceleration) * Math.signum(moveRawY)));

        moveRawX += moveResultX;
        moveRawY += moveResultY;

        int moveXFinal = Math.round(moveRawX);
        int moveYFinal = Math.round(moveRawY);

        if (moveXFinal != 0 || moveYFinal != 0) {
            mouseMove(moveXFinal, moveYFinal);
        }

        moveResultX = moveRawX - moveXFinal;
        moveResultY = moveRawY - moveYFinal;

        movePreviousX = event.getRawX();
        movePreviousY = event.getRawY();
    }


    private void onTouchMoveMouseWheel(MotionEvent event) {
        float wheelRaw = event.getRawY() - this.wheelPrevious;
        wheelRaw *= this.wheelSensitivity;
        wheelRaw = (float) ((Math.pow(Math.abs(wheelRaw), this.wheelAcceleration) * Math.signum(wheelRaw)));
        wheelRaw += this.wheelResult;
        int wheelFinal = Math.round(wheelRaw);

        if (wheelFinal != 0) {
            mouseWheel(wheelFinal);
        }

        this.wheelResult = wheelRaw - wheelFinal;
        this.wheelPrevious = event.getRawY();
    }

    private double getDistanceFromDown(MotionEvent event) {
        return Math.sqrt(Math.pow(event.getRawX() - this.moveDownX, 2) + Math.pow(event.getRawY() - this.moveDownY, 2));
    }

    private void mouseMove(int moveX, int moveY) {
        mActionControl.sendAction(new MouseMoveAction((short) moveX, (short) moveY));
    }
    private void onTouchUpMouseWheel(MotionEvent event) {

    }


    private void mouseWheel(int amount) {
        mActionControl.sendAction(new MouseWheelAction((byte) amount));
    }


    private void onTouchDownMouseMove(MotionEvent event) {
        moveDownX = this.movePreviousX = event.getRawX();
        moveDownY = this.movePreviousY = event.getRawY();

        moveResultX = 0;
        moveResultY = 0;

        holdPossible = true;
    }













}

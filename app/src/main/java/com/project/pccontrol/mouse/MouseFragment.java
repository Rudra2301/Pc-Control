package com.project.pccontrol.mouse;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.project.pccontrol.R;
import com.project.pccontrol.protocol.action.MouseClickAction;


/**
 * Created by MacBookPro on 5/28/17.bhjd•ئksenj╩nss
 */
public class MouseFragment extends Fragment implements MouseContractor.View {

    private static MouseFragment Instance = null;
    private MouseContractor.Presenter mPresenter;

    private ImageView mControlView;
    private Button right,left,delete,send;
    private EditText keyText;
    private FloatingActionButton keyBoard;




    public static MouseFragment getInstance(){
        if (Instance == null){
            Instance = new MouseFragment();
        }
        return Instance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mouse,container,false);

        mControlView = (ImageView) rootView.findViewById(R.id.controlView);
        keyText = (EditText) rootView.findViewById(R.id.textline);
        delete = (Button) rootView.findViewById(R.id.inputBackspace);
        send = (Button) rootView.findViewById(R.id.inputSend);
        keyBoard = (FloatingActionButton) rootView.findViewById(R.id.down);
        right = (Button) rootView.findViewById(R.id.rightClickView);
        left = (Button) rootView.findViewById(R.id.leftClickView);

        keyBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleKeyboard();
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        mPresenter.onTouchMove(event);
                        break;
                    }

                    case MotionEvent.ACTION_DOWN:
                    {
                        mPresenter.onTouchDown(event,MouseClickAction.BUTTON_RIGHT);
                        break;
                    }

                    case MotionEvent.ACTION_UP:
                    {
                        mPresenter.onTouchUp(event,MouseClickAction.BUTTON_RIGHT);
                        break;
                    }

                    default:
                        break;
                }
                return true;
            }
        });
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        mPresenter.onTouchMove(event);
                        break;
                    }

                    case MotionEvent.ACTION_DOWN:
                    {
                        mPresenter.onTouchDown(event,MouseClickAction.BUTTON_LEFT);
                        break;
                    }

                    case MotionEvent.ACTION_UP:
                    {
                        mPresenter.onTouchUp(event,MouseClickAction.BUTTON_LEFT);
                        break;
                    }

                    default:
                        break;
                }
                return true;
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onKeyBoardDelete();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = keyText.getText().toString();
                if(!message.isEmpty()){
                    mPresenter.onKeyBoardSend(message);
                    keyText.setText("");
                }

            }
        });



        mControlView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE: {
                        mPresenter.onMouseTouchMove(event);
                        break;
                    }

                    case MotionEvent.ACTION_DOWN: {
                        mPresenter.onMouseTouchDown(event,screenWidth());
                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                        mPresenter.onMouseTouchUp(event);
                        break;
                    }

                    default:
                        break;
                }

                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        isAdded();
        mPresenter.start();
    }



    private void toggleKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);

    }

    @Override
    public void setPresenter(MouseContractor.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void showMessage(@Nullable String message) {
        if (isAdded())
        Toast.makeText(getActivity(), ""+message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean ifWorking() {
        return isAdded();
    }


    private int screenWidth(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;


        return width;
    }
}

package com.project.pccontrol.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.project.pccontrol.R;
import com.project.pccontrol.connection.ConnectionActivity;
import com.project.pccontrol.control.ActionControl;
import com.project.pccontrol.files.FilesFragment;
import com.project.pccontrol.files.FilesPresenter;
import com.project.pccontrol.mouse.MouseFragment;
import com.project.pccontrol.mouse.MousePresenter;
import com.project.pccontrol.protocol.RemoteItActionReceiver;
import com.project.pccontrol.protocol.action.RemoteItAction;
import com.project.pccontrol.settings.SettingsActivity;
import com.project.pccontrol.shortcuts.ShortcutPresenter;
import com.project.pccontrol.shortcuts.ShortcutsFragment;
import com.project.pccontrol.utils.ActivityUtils;

public class MainActivity extends AppCompatActivity implements RemoteItActionReceiver {
    private SpaceNavigationView spaceNavigationView;
    private ActionControl mActionControl;
    private MainActivity.onRecieveRemoteAction lisRemoteAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionControl = new ActionControl(this);
//        mActionControl.setListener(this);

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.addSpaceItem(new SpaceItem("Mouse", R.drawable.ic_mouse));
        spaceNavigationView.addSpaceItem(new SpaceItem("Files", R.drawable.files));
        spaceNavigationView.addSpaceItem(new SpaceItem("shortcuts", R.drawable.shortcuts));
        spaceNavigationView.addSpaceItem(new SpaceItem("Settings", R.drawable.ic_settings));
        spaceNavigationView.showIconOnly();
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                startActivity(new Intent(MainActivity.this, ConnectionActivity.class));

            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                display(itemIndex);

            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });



        display(0);



    }

    @Override
    protected void onResume() {
        super.onResume();
        mActionControl.registerActionReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActionControl.unregisterActionReceiver(this);
    }

    private void display(int position) {
        switch (position){
            case 0:
                MouseFragment mouseFragment = MouseFragment.getInstance();
                MousePresenter mousePresenter = MousePresenter.getInstance(MainActivity.this,mouseFragment,mActionControl);
                mActionControl.setListener(mousePresenter);
                setListener(mousePresenter);
                if (mouseFragment != null) {
                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mouseFragment, R.id.contentFrame);
                    mouseFragment.setPresenter(mousePresenter);
                }
                break;
            case 1:
                FilesFragment filesFragment = FilesFragment.getInstance();
                FilesPresenter filesPresenter = FilesPresenter.getInstance(filesFragment,mActionControl);
                mActionControl.setListener(filesPresenter);
                setListener(filesPresenter);
                if (filesFragment != null) {
                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), filesFragment, R.id.contentFrame);
                    filesFragment.setPresenter(filesPresenter);
                }
                break;
            case 2:
                ShortcutsFragment shortcutsFragment = ShortcutsFragment.getInstance();
                ShortcutPresenter shortcutPresenter = ShortcutPresenter.getInstance(shortcutsFragment,mActionControl);
                setListener(shortcutPresenter);
                if (shortcutsFragment != null) {
                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), shortcutsFragment, R.id.contentFrame);
                     shortcutsFragment.setPresenter(shortcutPresenter);
                }
                break;

            case 3:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;
        }
    }


    @Override
    public void receiveAction(RemoteItAction action) {
        lisRemoteAction.receiveAction(action);
    }
    private void setListener(MainActivity.onRecieveRemoteAction action){
        lisRemoteAction = action;

    }
    public interface onRecieveRemoteAction{
        void receiveAction(RemoteItAction action);
    }
}

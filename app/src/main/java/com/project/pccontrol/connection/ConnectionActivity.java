package com.project.pccontrol.connection;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.project.pccontrol.R;
import com.project.pccontrol.model.Connection;
import com.project.pccontrol.utils.ActivityUtils;

public class ConnectionActivity extends AppCompatActivity {

    private ConnectionPresenter mPresenter;
    private ConnectionFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        mFragment = (ConnectionFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(mFragment == null){
            mFragment = ConnectionFragment.getInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),mFragment,R.id.contentFrame);
        }
        mPresenter = new ConnectionPresenter(mFragment);
        mFragment.setPresenter(mPresenter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ConnectionFragment.ADDRESS_REQUEST_CODE) {
                String address = data.getStringExtra("address");
                mFragment.setAdress(address);

            }
        }
    }
}

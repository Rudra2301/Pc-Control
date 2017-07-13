package com.project.pccontrol.connection;

import android.support.annotation.Nullable;

import com.project.pccontrol.BasePresenter;
import com.project.pccontrol.BaseView;
import com.project.pccontrol.model.Connection;
import com.project.pccontrol.utils.BluetoothAvailable;

import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by MacBookPro on 5/28/17.
 */

public class ConnectionPresenter implements ConnectionContractor.Presenter{
    ConnectionContractor.View mView;
    private Realm realm;

    public ConnectionPresenter(ConnectionContractor.View view){
        mView = view;
    }

    @Override
    public void start() {
        realm = Realm.getInstance(mView.getRealmConfig());

    }

    @Override
    public void addConnection(int position) {
        if(position == 1) {
            if (!BluetoothAvailable.isBluetoohAvailable()) {
                mView.showMessage("Bluetooth is not available on your phone");

            }
            else {
                mView.showBluetoothAdd();
            }
        }
        else {
            mView.showWifiAdd();

        }
    }

    @Override
    public void addConnectionToDb(@Nullable Connection connection) {

        realm.beginTransaction();
        Connection mConnection = realm.createObject(Connection.class,System.currentTimeMillis());
        mConnection.setName(connection.getName());
        mConnection.setType(connection.getType());

        if(connection.getType() == Connection.ConType.bluetooth) {
            mConnection.setAddress(connection.getAddress());

        }
        else {
            mConnection.setHost(connection.getHost());
            mConnection.setPort(connection.getPort());
        }
        mConnection.setPassword(connection.getPassword());
        realm.commitTransaction();
        if(connection.getType() == Connection.ConType.bluetooth) {
            mView.dismissBlueDialog();
        }
        else {

            mView.dismissWifiDialog();
        }
    }


}

package com.project.pccontrol.connection;

import android.support.annotation.Nullable;

import com.project.pccontrol.BasePresenter;
import com.project.pccontrol.BaseView;
import com.project.pccontrol.model.Connection;

import io.realm.RealmConfiguration;

/**
 * Created by MacBookPro on 5/28/17.
 */

public interface ConnectionContractor {
    interface View extends BaseView<Presenter> {

        void showAddDialog();

        void showMessage(@Nullable String message);

        void showBluetoothAdd();


        void showWifiAdd();

        RealmConfiguration getRealmConfig();

        void setAdress(@Nullable String adress);

        void dismissBlueDialog();

        void dismissWifiDialog();

    }

    interface Presenter extends BasePresenter {

        void addConnection(int position);

        void addConnectionToDb(@Nullable Connection connection);

//        void getAllConnections();
    }

}

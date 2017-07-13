package com.project.pccontrol.connection;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.project.pccontrol.R;
import com.project.pccontrol.model.Connection;

import java.util.ArrayList;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import static com.google.common.base.Preconditions.checkNotNull;
/**
 * Created by MacBookPro on 5/28/17.
 */

public class ConnectionFragment extends Fragment implements ConnectionContractor.View,Connection_Adapter.onConnection{

    public final static int ADDRESS_REQUEST_CODE = 2;
    private static ConnectionFragment Instance;
    private ConnectionContractor.Presenter mPresenter;
    private RealmRecyclerView connection_list;
    private Connection_Adapter mAdapter;
    RealmResults<Connection> connections ;
    private FloatingActionButton add;
    private Realm realm;
    private RealmConfiguration realmConfiguration;
    private Connection mConnection;
    private EditText name,password,ipAdress,port;
    private Dialog bluetoothDialog,wifiDialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor mEditor;


    String address = "";



    public static ConnectionFragment getInstance(){
        if (Instance == null){
            Instance = new ConnectionFragment();
        }
        return Instance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_con,container,false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = preferences.edit();
        mConnection = new Connection();

        Realm.init(getActivity());
        realm = Realm.getInstance(getRealmConfig());
        connections = realm
                .where(Connection.class)
                .findAllSorted("name", Sort.ASCENDING);



        //recycler view
        connection_list = (RealmRecyclerView) rootView.findViewById(R.id.realm_recycler_view);
        mAdapter = new Connection_Adapter(getActivity(),connections,false,false);
        mAdapter.setListiner(this);
        connection_list.setAdapter(mAdapter);

        //add button
        add = (FloatingActionButton) getActivity().findViewById(R.id.add_new_conn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    @Override
    public void setPresenter(ConnectionContractor.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showAddDialog() {
        String[] connectionTypeName = {
                this.getResources().getString(R.string.text_wifi)
                , this.getResources().getString(R.string.text_bluetooth)
        };

        new MaterialDialog.Builder(getActivity())
                .title(R.string.text_connection_type)
                .items(R.array.connection)
                .titleColorRes(R.color.colorAccent)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mPresenter.addConnection(which);
                    }
                })
                .show();
    }

    @Override
    public void showMessage(@Nullable String message) {
        Toast.makeText(getActivity(), ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBluetoothAdd() {
         bluetoothDialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.bluetooth_dialog, false)
                .show();

        Button scan = (Button) bluetoothDialog.findViewById(R.id.scan_but);
        Button add = (Button) bluetoothDialog.findViewById(R.id.add_bluetooth);
        name  = (EditText) bluetoothDialog.findViewById(R.id.bluetooth_name);
        password = (EditText) bluetoothDialog.findViewById(R.id.dialog_pass);
        ipAdress = (EditText) bluetoothDialog.findViewById(R.id.scan_address);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().startActivityForResult(new Intent(getActivity(), BluetoothDevicesActivity.class), ADDRESS_REQUEST_CODE);

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = name.getText().toString();
                String passText = password.getText().toString();

                if(!nameText.isEmpty() && !passText.isEmpty()){
                    if(!address.equals("")){
                        mConnection.setName(nameText);
                        mConnection.setPassword(passText);
                        mConnection.setType(Connection.ConType.bluetooth);
                        mPresenter.addConnectionToDb(checkNotNull(mConnection));
                    }
                    else {
                     showMessage("please scan for your pc");
                    }
                }
                else {
                    showMessage("name and password can't be empty");
                }


            }
        });

    }


    @Override
    public void showWifiAdd() {
        wifiDialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.wifi_dialog, false)
                .show();

        Button add = (Button) wifiDialog.findViewById(R.id.add_wifi);
        name  = (EditText) wifiDialog.findViewById(R.id.wifi_name);
        port  = (EditText) wifiDialog.findViewById(R.id.port_address);
        password = (EditText) wifiDialog.findViewById(R.id.dialog_pass_wifi);
        ipAdress = (EditText) wifiDialog.findViewById(R.id.scan_address_wifi);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = name.getText().toString();
                String passText = password.getText().toString();
                String portText = port.getText().toString();
                String host = ipAdress.getText().toString();



                    if (!nameText.isEmpty() && !passText.isEmpty() && !host.isEmpty()&& !portText.isEmpty()) {
                        int portnum = Integer.parseInt(portText);
                        mConnection.setName(nameText);
                        mConnection.setPassword(passText);
                        mConnection.setHost(host);
                        mConnection.setPort(portnum);
                        mConnection.setType(Connection.ConType.wifi);

                        mPresenter.addConnectionToDb(checkNotNull(mConnection));

                    } else {
                        showMessage("name and password can't be empty");
                    }


            }
        });


    }

    @Override
    public RealmConfiguration getRealmConfig() {
        if (realmConfiguration == null) {
            realmConfiguration = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
        }
        return realmConfiguration;

    }

    @Override
    public void setAdress(@Nullable String adress) {
        address = checkNotNull(adress);
        mConnection.setAddress(address);
        mConnection.setType(Connection.ConType.bluetooth);
        ipAdress.setText(""+address);
    }

    @Override
    public void dismissBlueDialog() {
        if(bluetoothDialog.isShowing()){
            bluetoothDialog.dismiss();
        }
    }

    @Override
    public void dismissWifiDialog() {
        if(wifiDialog.isShowing()){
            wifiDialog.dismiss();
        }
    }

    private String mType;


    @Override
    public void onConnectionClick(int position) {
        Connection connection = connections.get(position);
        if(connection != null){
            Connection.ConType type = connection.getType();
            String name = connection.getName();
            String password = connection.getPassword();
            mEditor.putString("name",name);
            mEditor.putString("pass",password);
            if(type == Connection.ConType.bluetooth){
                String address = connection.getAddress();
                mEditor.putInt("type",1);
                mEditor.putString("address",address);
                mEditor.putInt("port",0);
                mEditor.putString("host","");
            }
            else {
                int port = connection.getPort();
                String host = connection.getHost();
                mEditor.putInt("type",2);
                mEditor.putString("address","");
                mEditor.putInt("port",port);
                mEditor.putString("host",host);
            }
            mEditor.commit();

        }
    }
}


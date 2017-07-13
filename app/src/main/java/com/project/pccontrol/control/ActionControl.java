package com.project.pccontrol.control;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.project.pccontrol.R;
import com.project.pccontrol.model.Connection;
import com.project.pccontrol.protocol.Bluetooth.RemoteItConnectionBluetooth;
import com.project.pccontrol.protocol.RemoteItActionReceiver;
import com.project.pccontrol.protocol.RemoteItConnection;
import com.project.pccontrol.protocol.action.AuthentificationAction;
import com.project.pccontrol.protocol.action.AuthentificationResponseAction;
import com.project.pccontrol.protocol.action.RemoteItAction;
import com.project.pccontrol.protocol.tcp.RemoteItConnectionTcp;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.HashSet;

/**
 * Created by MacBookPro on 5/29/17.
 */

public class ActionControl implements Runnable {

    public static final String TAG = ActionControl.class.getSimpleName();
    private ActionControl.actionMessageBack mActionMessageBack;
    private Context mContext;
    private static final long CONNECTION_CLOSE_DELAY = 3000;

    private SharedPreferences preferences;
    private Vibrator vibrator;
//    private Reference<Activity> context;

    private RemoteItConnection[] connection;

    private HashSet<RemoteItActionReceiver> actionReceivers;



    private CloseConnectionScheduler closeConnectionScheduler;


    private boolean requestEnableBluetooth;

    public ActionControl(Context context){
        mContext =context;
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        PreferenceManager.setDefaultValues(mContext, R.xml.settings, true);

        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        actionReceivers = new HashSet<RemoteItActionReceiver>();


        connection = new RemoteItConnection[1];

        closeConnectionScheduler = new CloseConnectionScheduler();

        requestEnableBluetooth = true;
    }

    public SharedPreferences getPreferences()
    {
        return this.preferences;
    }
    public void vibrate(long l)
    {
        if (this.preferences.getBoolean("feedback_vibration", true))
        {
            this.vibrator.vibrate(l);
        }
    }

    public boolean requestEnableBluetooth()
    {
        boolean b = this.requestEnableBluetooth;

        this.requestEnableBluetooth = false;

        return b;
    }

    public synchronized void run() {
        Connection co = getUsedConnection();
        if (co != null) {
            RemoteItConnection c = null;
            try {
                if(co.getType() == Connection.ConType.bluetooth){
                    c = RemoteItConnectionBluetooth.create(ActionControl.this,mContext, co.getAddress());
                }
                else {
                    c = RemoteItConnectionTcp.create(co.getHost(), co.getPort());
                }
                synchronized (this.connection) {
                    this.connection[0] = c;
                }
                try {
                    mActionMessageBack.onMessage(mContext.getResources().getString(R.string.text_connection_established));
                    String password = co.getPassword();
                    this.sendAction(new AuthentificationAction(password));
                    while (true) {
                        RemoteItAction action = c.receiveAction();
                        this.receiveAction(action);
                    }
                }
                finally {
                    synchronized (this.connection) {
                        this.connection[0] = null;}
                    c.close();}}
            catch (IOException e) {
                if (c == null) {
                    mActionMessageBack.onMessage(mContext.getResources().getString(R.string.text_connection_refused));}
                else {
                    mActionMessageBack.onMessage(mContext.getResources().getString(R.string.text_connection_closed));}}
            catch (IllegalArgumentException e) {
                mActionMessageBack.onMessage(mContext.getResources().getString(R.string.text_illegal_connection_parameter));}
        }
        else
        {
            mActionMessageBack.onMessage(mContext.getResources().getString(R.string.text_no_connection_selected));
        }
    }



    public void sendAction(RemoteItAction Action) {
        synchronized (this.connection) {
            if (this.connection[0] != null) {
                try {
                    Log.i(TAG,"send  "+connection[0]);
                    this.connection[0].sendAction(Action);
                }
                catch (IOException e) {

                    Log.i(TAG,e.getMessage());
                }}}
    }

    private void receiveAction(RemoteItAction action) {
        synchronized (this.actionReceivers) {
            for (RemoteItActionReceiver actionReceiver : this.actionReceivers) {
                actionReceiver.receiveAction(action);}}

        if (action instanceof AuthentificationResponseAction) {
            this.receiveAuthentificationResponseAction((AuthentificationResponseAction) action);
        }
    }

    private void receiveAuthentificationResponseAction(AuthentificationResponseAction action)
    {
        if (action.authentificated)
        {
            mActionMessageBack.onMessage(mContext.getResources().getString(R.string.text_authentificated));


        }
        else
        {
            mActionMessageBack.onMessage(mContext.getResources().getString(R.string.text_not_authentificated));
        }
    }

    public void registerActionReceiver(RemoteItActionReceiver actionReceiver)
    {
        synchronized (this.actionReceivers)
        {
            this.actionReceivers.add(actionReceiver);

            if (this.actionReceivers.size() > 0)
            {
                synchronized (this.connection)
                {
                    if (this.connection[0] == null)
                    {
                        (new Thread(this)).start();
                    }
                }
            }
        }
    }

    public void unregisterActionReceiver(RemoteItActionReceiver actionReceiver)
    {
        synchronized (this.actionReceivers)
        {
            this.actionReceivers.remove(actionReceiver);

            if (this.actionReceivers.size() == 0)
            {
                this.closeConnectionScheduler.schedule();
            }
        }
    }


    private class CloseConnectionScheduler implements Runnable
    {
        private Thread currentThread;

        public synchronized void run()
        {
            try
            {
                this.wait(CONNECTION_CLOSE_DELAY);

                synchronized (ActionControl.this.actionReceivers)
                {
                    if (ActionControl.this.actionReceivers.size() == 0)
                    {
                        synchronized (ActionControl.this.connection)
                        {
                            if (ActionControl.this.connection[0] != null)
                            {
                                ActionControl.this.connection[0].close();

                                ActionControl.this.connection[0] = null;
                            }
                        }
                    }
                }

                this.currentThread = null;
            }
            catch (InterruptedException e)
            {
            }
            catch (IOException e)
            {
            }
        }

        public synchronized void schedule()
        {
            if (this.currentThread != null)
            {
                this.currentThread.interrupt();
            }

            this.currentThread = new Thread(this);

            this.currentThread.start();
        }
    }
    //get shared used connection from shared preference
    private Connection getUsedConnection() {
        Connection connection = new Connection();
        String name = preferences.getString("name","");
        String pass = preferences.getString("pass","");
        connection.setName(name);
        connection.setPassword(pass);
        int type = preferences.getInt("type",0);
        if(type == 1){
            //bluetooth
            String address = preferences.getString("address","");
            connection.setAddress(address);
            connection.setType(Connection.ConType.bluetooth);
            Log.i("CONNECTION_DATA   ",""+address);

        }
        else {
            //wifi
            int port = preferences.getInt("port",0);
            String host = preferences.getString("host","");
            connection.setType(Connection.ConType.wifi);
            connection.setPort(port);
            connection.setHost(host);
            Log.i("CONNECTION_DATA   ","port "+port);
            Log.i("CONNECTION_DATA   ","host "+host);


        }
Log.i("CONNECTION_DATA   ",name);
Log.i("CONNECTION_DATA   ",pass);
Log.i("CONNECTION_DATA   ",""+type);
        return connection;
    }
    public void setListener(ActionControl.actionMessageBack listener){
        mActionMessageBack = listener;
    }

    public interface actionMessageBack{
        void onMessage(@Nullable String message);
    }

}


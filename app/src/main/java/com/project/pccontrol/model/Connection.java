package com.project.pccontrol.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by MacBookPro on 5/28/17.
 */

public class Connection extends RealmObject{
    @PrimaryKey
    private long id;


    @Ignore
    private static final long serialVersionUID = 1L;


    public enum ConType{
        wifi,bluetooth
    }
    private String mType;
    //both
    private String name;
    private String password;
    //bluetooth
    private String address;

    //wifi
    private String host;
    private int port;


    public Connection(){

    }

    public ConType getType() {
        return ConType.valueOf(mType);
    }

    public void setType(ConType type) {
        mType = type.toString();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

package com.project.pccontrol.utils;

/**
 * Created by MacBookPro on 5/28/17.
 */

public class BluetoothAvailable {
    private static boolean available;

    static
    {
        try
        {
            Class.forName("android.bluetooth.BluetoothAdapter");

            available = true;
        }
        catch (ClassNotFoundException e)
        {
            available = false;
        }
    }

    public static boolean isBluetoohAvailable()
    {
        return available;
    }

}

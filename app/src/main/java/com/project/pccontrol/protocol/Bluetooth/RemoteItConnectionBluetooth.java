package com.project.pccontrol.protocol.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import com.project.pccontrol.control.ActionControl;
import com.project.pccontrol.protocol.RemoteItConnection;

import java.io.IOException;
import java.util.UUID;

public class RemoteItConnectionBluetooth extends RemoteItConnection {
	private BluetoothSocket socket;
	
	public RemoteItConnectionBluetooth(BluetoothSocket socket) throws IOException
	{
		super(socket.getInputStream(), socket.getOutputStream());
		
		this.socket = socket;
	}
	
	public static RemoteItConnectionBluetooth create(ActionControl application, Context mContext, String address) throws IOException
	{
		Looper.prepare();
		
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		
		if (adapter != null)
		{
			if (adapter.isEnabled())
			{
				try
				{
					BluetoothDevice device = adapter.getRemoteDevice(address);
					
					if (device != null)
					{
						BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(RemoteItConnection.BLUETOOTH_UUID));
						socket.connect();
						
						RemoteItConnectionBluetooth connection = new RemoteItConnectionBluetooth(socket);
						
						return connection;
					}
				}
				catch (IllegalArgumentException e)
				{
					throw new IOException();
				}
			}
			else
			{
				if (application.requestEnableBluetooth())
				{
					Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
				}
			}
		}
		
		throw new IOException();
	}
	
	public void close() throws IOException
	{
		this.socket.close();
		super.close();
	}
}

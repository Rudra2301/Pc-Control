package com.project.pccontrol.connection;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.project.pccontrol.R;

import java.util.ArrayList;
import java.util.Set;


public class BluetoothDevicesActivity extends ListActivity implements OnItemClickListener{
	public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 0;
	

	private ArrayList<BluetoothDevice> deviceList;
	private BluetoothDevicesAdapter deviceListAdapter;
	
	private BluetoothDevicesBroadcastReceiver broadcastReceiver;
	
	private BluetoothAdapter bluetoothAdapter;
	

	private boolean requestEnableBluetooth;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		

		this.deviceList = new ArrayList<BluetoothDevice>();
		
		this.deviceListAdapter = new BluetoothDevicesAdapter(this, R.layout.bluetoothdevice, this.deviceList);
		this.getListView().setAdapter(this.deviceListAdapter);
		this.broadcastReceiver = new BluetoothDevicesBroadcastReceiver();
		
		this.requestEnableBluetooth = true;
		
		this.getListView().setOnItemClickListener(this);
	}
	
	protected void onResume()
	{
		super.onResume();
		
		this.registerReceiver(this.broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		this.registerReceiver(this.broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		this.registerReceiver(this.broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (this.bluetoothAdapter != null)
		{
			if (this.bluetoothAdapter.isEnabled())
			{
				Set<BluetoothDevice> deviceSet = this.bluetoothAdapter.getBondedDevices();
				this.deviceList.addAll(deviceSet);
				this.deviceListAdapter.notifyDataSetChanged();
				
				this.bluetoothAdapter.startDiscovery();
			}
			else
			{
				if (this.requestEnableBluetooth)
				{
					this.startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
					
					this.requestEnableBluetooth = false;
				}
				else
				{
					this.finish();
				}
			}
		}
		else
		{
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void onPause()
	{
		super.onPause();
		
		if (this.bluetoothAdapter != null && bluetoothAdapter.isDiscovering())
		{
			this.bluetoothAdapter.cancelDiscovery();
		}
		
		this.unregisterReceiver(this.broadcastReceiver);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		BluetoothDevice bluetoothDevice = this.deviceList.get(position);
		
		if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED)
		{
			Toast.makeText(this, "Bluetooth device not paired", Toast.LENGTH_SHORT).show();

		}
		
		String address = bluetoothDevice.getAddress();
		
		Intent intent = new Intent();
		intent.putExtra("address", address);
		
		this.setResult(RESULT_OK, intent);
		
		this.finish();
	}
	
	private class BluetoothDevicesAdapter extends ArrayAdapter<BluetoothDevice>
	{
		private ArrayList<BluetoothDevice> bluetoothDevices;
		private LayoutInflater layoutInflater;
		
		public BluetoothDevicesAdapter(Context context, int textViewResourceId, ArrayList<BluetoothDevice> bluetoothDevices)
		{
			super(context, textViewResourceId, bluetoothDevices);
			
			this.bluetoothDevices = bluetoothDevices;
			this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			BluetoothDevicesHolder holder;
			
			if (convertView == null)
			{
				convertView = this.layoutInflater.inflate(R.layout.bluetoothdevice, null);
				
				holder = new BluetoothDevicesHolder();
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.address = (TextView) convertView.findViewById(R.id.address);
				
				convertView.setTag(holder);
			}
			else
			{
				holder = (BluetoothDevicesHolder) convertView.getTag();
			}
			
			BluetoothDevice bluetoothDevice = this.bluetoothDevices.get(position);
			holder.name.setText(bluetoothDevice.getName());
			holder.address.setText(bluetoothDevice.getAddress());
			
			return convertView;
		}
		
		private class BluetoothDevicesHolder
		{
			public TextView name;
			public TextView address;
		}
	}
	
	private class BluetoothDevicesBroadcastReceiver extends BroadcastReceiver
	{
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			
			if (action.equals(BluetoothDevice.ACTION_FOUND))
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				if (!BluetoothDevicesActivity.this.deviceList.contains(device))
				{
					BluetoothDevicesActivity.this.deviceList.add(device);
					BluetoothDevicesActivity.this.deviceListAdapter.notifyDataSetChanged();
				}
			}
			else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
			{
				Toast.makeText(context, "Bluetooth discovery started", Toast.LENGTH_SHORT).show();

			}
			else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
			{
				Toast.makeText(context, "Bluetooth discovery finished", Toast.LENGTH_SHORT).show();

			}
		}
	}
}

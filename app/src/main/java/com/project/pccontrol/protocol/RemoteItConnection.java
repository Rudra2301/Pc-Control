package com.project.pccontrol.protocol;

import com.project.pccontrol.protocol.action.RemoteItAction;
import com.project.pccontrol.protocol.action.ScreenCaptureResponseAction;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public abstract class RemoteItConnection
{
	public static final String BLUETOOTH_UUID = "300ad0a7-059d-4d97-b9a3-eabe5f6af813";
	public static final String DEFAULT_PASSWORD = "remote";
	
	private DataInputStream dataInputStream;
	private OutputStream outputStream;
	private RemoteItAction capAction = new ScreenCaptureResponseAction(new byte[3000000]);
	public boolean active = true;
	
	public RemoteItConnection(InputStream inputStream, OutputStream outputStream)
	{
		this.dataInputStream = new DataInputStream(inputStream);
		this.outputStream = outputStream;
	}
	
	public RemoteItAction receiveAction() throws IOException {
		synchronized (this.dataInputStream) {
			try {
				byte type = this.dataInputStream.readByte();
				if (type == 7) {
					return ((ScreenCaptureResponseAction) capAction).parse_(dataInputStream);
				}
				else {
					RemoteItAction action = RemoteItAction.parse(this.dataInputStream, type);
					return action;
				}
			}
			catch (IOException e)
			{
				// Problem with connection (Usually the device disconnected
				active = false;
				throw e;
			}
			
		}
	}
	
	public void sendAction(RemoteItAction action) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		action.toDataOutputStream(new DataOutputStream(baos));
		
		synchronized (this.outputStream)
		{
			this.outputStream.write(baos.toByteArray());
			this.outputStream.flush();
		}
	}
	
	public void close() throws IOException
	{
		this.dataInputStream.close();
		this.outputStream.close();
	}
}

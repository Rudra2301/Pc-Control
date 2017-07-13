package com.project.pccontrol.protocol;


import com.project.pccontrol.protocol.action.RemoteItAction;

public interface RemoteItActionReceiver
{
	public void receiveAction(RemoteItAction action);
}

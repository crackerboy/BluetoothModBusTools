package com.bluetooth.modbus.snrtools.thread;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bluetooth.modbus.snrtools.Constans;
import com.bluetooth.modbus.snrtools.manager.AppStaticVar;

public class ConnectThread extends Thread { 		
	private BluetoothDevice mDevice;
	private Handler mHanlder;
	
	public ConnectThread(BluetoothDevice device,Handler hanlder) {
		this.mDevice = device;
		this.mHanlder = hanlder;
	}
	
	public void run() {
		try {
			//����һ��Socket���ӣ�ֻ��Ҫ��������ע��ʱ��UUID��
			if(mHanlder == null){
				return ;
			}
			if(mDevice == null){
				return;
			}
			AppStaticVar.mSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			//����
			Message msg2 = new Message();
			msg2.obj = "���Ժ��������ӷ�����:"+AppStaticVar.mCurrentName;
			msg2.what = Constans.CONNECTING_DEVICE;
			mHanlder.sendMessage(msg2);
			AppStaticVar.mSocket .connect();
			Message msg = new Message();
			msg.obj = "�Ѿ������Ϸ���ˣ�";
			msg.what = Constans.CONNECT_DEVICE_SUCCESS;
			mHanlder.sendMessage(msg);
		} 
		catch (IOException e) 
		{
			Log.e("connect", "", e);
			AppStaticVar.mSocket = null;
			Message msg = new Message();
			msg.obj = "���豸����ʧ��";
			msg.what = Constans.CONNECT_DEVICE_FAILED;
			mHanlder.sendMessage(msg);
		} 
	}
}

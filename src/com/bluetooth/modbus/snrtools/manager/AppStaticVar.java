package com.bluetooth.modbus.snrtools.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

public class AppStaticVar {
	/** ȡ��Ĭ�ϵ����������� */
	public static BluetoothAdapter mBtAdapter;
	/** ��ǰ���ӵ�������ַ*/
	public static String mCurrentAddress;
	/** ��ǰ���ӵ���������*/
	public static String mCurrentName;
	/** ����socket*/
	public static BluetoothSocket mSocket;
	/** ����ȼ�*/
	public static int PASSWORD_LEVEAL = -1;
	
}

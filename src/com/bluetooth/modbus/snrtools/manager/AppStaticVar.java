package com.bluetooth.modbus.snrtools.manager;

import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

import com.bluetooth.modbus.snrtools.bean.Parameter;

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
	/** ����ȼ�1�鿴��������*/
	public static int PASSWORD_LEVEAL1_COUNT = 0;
	/** ����ȼ�2�鿴��������*/
	public static int PASSWORD_LEVEAL2_COUNT = 0;
	/** ����ȼ�3�鿴��������*/
	public static int PASSWORD_LEVEAL3_COUNT = 0;
	/** ����ȼ�4�鿴��������*/
	public static int PASSWORD_LEVEAL4_COUNT= 0;
	/** ����ȼ�5�鿴��������*/
	public static int PASSWORD_LEVEAL5_COUNT = 0;
	/** �����б�*/
	public static List<Parameter> mParamList;
	/** ������������λ��*/
	public static int ZXZLPosition = -1;
	/** ������������λ��*/
	public static int FXZLPosition = -1;
	/** �Ƿ��������Ͽ�*/
	public static boolean isExit = false;
}

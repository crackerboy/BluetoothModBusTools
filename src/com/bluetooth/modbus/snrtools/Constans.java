package com.bluetooth.modbus.snrtools;

public class Constans {
	
	/** ָ���ͺ�������ʼ�ֶ�*/
	public final static String DEVICE_NAME_START = "Sinier";

	/** ѭ����ȡ�Ĳ�������Ŀ*/
	public final static int READ_PARAM_COUNT = 6;

	/** �����豸��*/
	public final static int CONNECTING_DEVICE = 0X10001;
	/** �豸���ӳɹ�*/
	public final static int CONNECT_DEVICE_SUCCESS = 0X10002;
	/** �豸����ʧ��*/
	public final static int CONNECT_DEVICE_FAILED = 0X10003;
	/** �豸δ����*/
	public final static int NO_DEVICE_CONNECTED = 0X10004;
	/** �豸������Ϣ*/
	public final static int DEVICE_RETURN_MSG = 0X10005;
	/** �����Ѿ��ر�*/
	public final static int CONNECT_IS_CLOSED = 0X10006;
	/** ���Ӷ���*/
	public final static int CONNECT_IS_JIM = 0X10007;
	/** ͨѶ��ʼ*/
	public final static int CONTACT_START = 0X10008;
	/** ���Ϸ��ķ�����Ϣ*/
	public final static int ERROR_START = 0X10009;
	/** ���ӳ�ʱ*/
	public final static int TIME_OUT = 0X1000A;
	
	public static class PasswordLevel{
		/** �ȼ�1�������������� ������1-17��������������521*/
		public static long LEVEL_1 = 521;
		/** �ȼ�2���߼��������� ������1-32��������������3210*/
		public static long LEVEL_2 = 3210;
		/** �ȼ�3���������������� ������1-55��������������6108*/
		public static long LEVEL_3 = 6108;
		/** �ȼ�4��ת�������� ������1-60��������������97206*/
		public static long LEVEL_4 = 97206;
		/** �ȼ�5���������� ������1-60�������̶�ֵ270427*/
		public static long LEVEL_5 = 270427;
		/** �ȼ�6��������������,��������5210*/
		public static long LEVEL_6 = 5210;
	}
	
	public static class Directory{
		public static final String DOWNLOAD = "/sdcard/Sinier/update/";
	}
}

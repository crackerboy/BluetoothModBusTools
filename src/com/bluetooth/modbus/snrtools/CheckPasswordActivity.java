package com.bluetooth.modbus.snrtools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bluetooth.modbus.snrtools.bean.Parameter;
import com.bluetooth.modbus.snrtools.bean.Selector;
import com.bluetooth.modbus.snrtools.manager.AppStaticVar;
import com.bluetooth.modbus.snrtools.uitls.ModbusUtils;
import com.bluetooth.modbus.snrtools.uitls.NumberBytes;

public class CheckPasswordActivity extends BaseActivity {
	private Handler mHandler;
	private Thread mThread;
	private EditText editText1;
	private List<Parameter> mList;
	private int reconnectCount = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mList = new ArrayList<Parameter>();
		setContentView(R.layout.check_pass_activity);
		setTitleContent("����У��");
		hideRightView(R.id.btnRight1);
		hideRightView(R.id.view2);
		initHandler();
		editText1 = (EditText) findViewById(R.id.editText1);
		startReadParam();
	}

	@Override
	public void BtnRight(View v) {
		switch (v.getId()) {
			case R.id.btnRight1 :
				Intent intent = new Intent(mContext, ParamSettingActivity.class);
				startActivity(intent);
				break;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button2 :
				checkPsw(Long.parseLong(editText1.getText().toString()));
				if (AppStaticVar.PASSWORD_LEVEAL != -1) {
					Intent intent = new Intent(mContext,
							ParamSettingActivity.class);
					intent.putExtra("list", (Serializable) mList);
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(mContext, "���벻��ȷ!", Toast.LENGTH_SHORT)
							.show();
				}
				break;
		}
	}

	private void startReadParam() {
		mThread = new Thread(new Runnable() {

			@Override
			public void run() {
				ModbusUtils.readParameter(mContext.getClass().getSimpleName(),mHandler);
			}
		});
		mThread.start();
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case Constans.CONTACT_START :
						showProgressDialog("���豸ͨѶ��...");
						System.out.println("=====������ʼ��ȡ����");
						break;
					case Constans.NO_DEVICE_CONNECTED :
						System.out.println("=====����û���豸����");
						break;
					case Constans.DEVICE_RETURN_MSG :
						hideProgressDialog();
						System.out.println("�����յ�������=====" + msg.obj.toString());
						dealReturnMsg(msg.obj.toString());
						break;
					case Constans.CONNECT_IS_CLOSED :
						System.out.println("=====�������ӶϿ�");
						showConnectDevice();
						break;
					case Constans.ERROR_START :
						System.out.println("=====�������մ���");
						if (reconnectCount > 0) {
							if (mThread != null && !mThread.isInterrupted()) {
								mThread.interrupt();
							}
							startReadParam();
							reconnectCount--;
						} else {
							hideProgressDialog();
							showToast("��ȡ���ݳ�ʱ��");
						}
						break;
					case Constans.TIME_OUT :
						System.out.println("��ҳ�����ӳ�ʱ=====");
						if (reconnectCount > 0) {
							if (mThread != null && !mThread.isInterrupted()) {
								mThread.interrupt();
							}
							startReadParam();
							reconnectCount--;
						} else {
							hideProgressDialog();
							showToast("��ȡ���ݳ�ʱ��");
						}
						break;
				}
			}
		};
	}

	private void dealReturnMsg(String msg) {
		if (msg.length() != 290) {
			return;
		}
		mList.clear();
		Parameter parameter = null;
		ArrayList<Selector> selectorList = null;
		Selector selector = null;
		/********************************** �������� **************************************/
		/********************************** ����1--���� **************************************/
		mList.add(new Parameter(true, "��������"));
		String param = msg.substring(6, 10);
		System.out.println("����1--����==" + Integer.parseInt(param, 16));
		parameter = new Parameter();
		parameter.address = "0000";
		parameter.count = "0001";
		parameter.name = "����";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "��������";
				break;
			case 1 :
				parameter.value = "English";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "��������";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "English";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����2--������λ **************************************/
		param = msg.substring(10, 14);
		System.out.println("����2--������λ ==" + Integer.parseInt(param, 16));
		parameter = new Parameter();
		parameter.address = "0001";
		parameter.count = "0001";
		parameter.name = "������λ";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "L/h";
				break;
			case 1 :
				parameter.value = "L/mim";
				break;
			case 2 :
				parameter.value = "L/s";
				break;
			case 3 :
				parameter.value = "m3/h";
				break;
			case 4 :
				parameter.value = "m3/min";
				break;
			case 5 :
				parameter.value = "m3/s";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "L/h";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "L/min";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "L/s";
		selector.value = "0002";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "m3/h";
		selector.value = "0003";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "m3/min";
		selector.value = "0004";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "m3/s";
		selector.value = "0005";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);

		/********************************** ����3--�Ǳ��������� **************************************/
		param = msg.substring(14, 22);
		System.out.println("�Ǳ���������========" + param);
		System.out.println("����3--�Ǳ���������=="
				+ NumberBytes.hexStrToLong(msg.substring(18, 22)
						+ msg.substring(14, 18)));
		parameter = new Parameter();
		parameter.address = "0002";
		parameter.count = "0002";
		parameter.name = "�Ǳ���������";
		parameter.type = 3;
		parameter.maxValue = 99999;
		parameter.minValue = 0;
		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(18, 22)
				+ msg.substring(14, 18));
		parameter.value = NumberBytes.hexStrToLong(msg.substring(18, 22)
				+ msg.substring(14, 18))
				+ "";
		mList.add(parameter);
		/********************************** ����4--������������ **************************************/
		param = msg.substring(22, 26);
		System.out.println("����4--������������==" + Integer.parseInt(param, 16));
		parameter = new Parameter();
		parameter.address = "0004";
		parameter.count = "0001";
		parameter.name = "������������";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "����";
				break;
			case 1 :
				parameter.value = "����";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "����";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "����";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����5--����������� **************************************/
		param = msg.substring(26, 30);
		System.out.println("����13--�����������==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0005";
		parameter.count = "0001";
		parameter.name = "�����������";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "����";
				break;
			case 1 :
				parameter.value = "��ֹ";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "����";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "��ֹ";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);

		/********************************** ����6--�������㵥λ **************************************/
		param = msg.substring(30, 34);
		System.out.println("����6--�������㵥λ==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0006";
		parameter.count = "0001";
		parameter.name = "�������㵥λ";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "0.001m3";
				break;
			case 1 :
				parameter.value = "0.01m3";
				break;
			case 2 :
				parameter.value = "0.1m3";
				break;
			case 3 :
				parameter.value = "1m3";
				break;
			case 4 :
				parameter.value = "0.001L";
				break;
			case 5 :
				parameter.value = "0.01L";
				break;
			case 6 :
				parameter.value = "0.1L";
				break;
			case 7 :
				parameter.value = "1L";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "0.001m3";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.01m3";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.1m3";
		selector.value = "0002";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1m3";
		selector.value = "0003";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.001L";
		selector.value = "0004";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.01L";
		selector.value = "0005";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.1L";
		selector.value = "0006";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1L";
		selector.value = "0007";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����7--��������ʱ��(s) **************************************/
		param = msg.substring(34, 38);
		System.out.println("����7--��������ʱ��(s) ==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0007";
		parameter.count = "0001";
		parameter.name = "��������ʱ��(s)";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "1s";
				break;
			case 1 :
				parameter.value = "2s";
				break;
			case 2 :
				parameter.value = "3s";
				break;
			case 3 :
				parameter.value = "4s";
				break;
			case 4 :
				parameter.value = "6s";
				break;
			case 5 :
				parameter.value = "8s";
				break;
			case 6 :
				parameter.value = "10s";
				break;
			case 7 :
				parameter.value = "15s";
				break;
			case 8 :
				parameter.value = "30s";
				break;
			case 9 :
				parameter.value = "50s";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "1s";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "2s";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "3s";
		selector.value = "0002";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "4s";
		selector.value = "0003";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "6s";
		selector.value = "0004";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "8s";
		selector.value = "0005";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "10s";
		selector.value = "0006";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "15s";
		selector.value = "0007";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "30s";
		selector.value = "0008";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "50s";
		selector.value = "0009";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����8--С�ź��г���(%) **************************************/
		param = msg.substring(38, 42);
		System.out.println("����8--С�ź��г���(%)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0008";
		parameter.count = "0001";
		parameter.name = "С�ź��г���(%)";
		parameter.type = 2;
		parameter.point = 2;
		parameter.maxValue = 99.99;
		parameter.minValue = 0.0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"%";
		mList.add(parameter);
		/********************************** ����9--���������ʽ **************************************/
		param = msg.substring(42, 46);
		System.out.println("����9--���������ʽ==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0009";
		parameter.count = "0001";
		parameter.name = "���������ʽ";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "Ƶ��";
				break;
			case 1 :
				parameter.value = "����";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "Ƶ��";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "����";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����10--���嵥λ���� **************************************/
		param = msg.substring(46, 50);
		System.out.println("����10--���嵥λ����==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "000A";
		parameter.count = "0001";
		parameter.name = "���嵥λ����";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "1.0m3/cp";
				break;
			case 1 :
				parameter.value = "0.1m3/cp";
				break;
			case 2 :
				parameter.value = "0.01m3/cp";
				break;
			case 3 :
				parameter.value = "0.001m3/cp";
				break;
			case 4 :
				parameter.value = "1.0L/cp";
				break;
			case 5 :
				parameter.value = "0.1L/cp";
				break;
			case 6 :
				parameter.value = "0.01L/cp";
				break;
			case 7 :
				parameter.value = "0.001L/cp";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "1.0m3/cp";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.1m3/cp";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.01m3/cp";
		selector.value = "0002";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.001m3/cp";
		selector.value = "0003";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1.0L/cp";
		selector.value = "0004";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.1L/cp";
		selector.value = "0005";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.01L/cp";
		selector.value = "0006";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.001L/cp";
		selector.value = "0007";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����11--������ʱ�� **************************************/
		param = msg.substring(50, 54);
		System.out.println("����11--������ʱ��==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "000B";
		parameter.count = "0001";
		parameter.name = "������ʱ��";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "4ms";
				break;
			case 1 :
				parameter.value = "8ms";
				break;
			case 2 :
				parameter.value = "20ms";
				break;
			case 3 :
				parameter.value = "30ms";
				break;
			case 4 :
				parameter.value = "40ms";
				break;
			case 5 :
				parameter.value = "80ms";
				break;
			case 6 :
				parameter.value = "100ms";
				break;
			case 7 :
				parameter.value = "150ms";
				break;
			case 8 :
				parameter.value = "200ms";
				break;
			case 9 :
				parameter.value = "400ms";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "4ms";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "8ms";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "20ms";
		selector.value = "0002";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "30ms";
		selector.value = "0003";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "40ms";
		selector.value = "0004";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "80ms";
		selector.value = "0005";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "100ms";
		selector.value = "0006";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "150ms";
		selector.value = "0007";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "200ms";
		selector.value = "0008";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "400ms";
		selector.value = "0009";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����12--Ƶ�������Χ(Hz) **************************************/
		param = msg.substring(54, 58);
		System.out.println("����12--Ƶ�������Χ(Hz)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "000C";
		parameter.count = "0001";
		parameter.name = "Ƶ�������Χ(Hz)";
		parameter.type = 1;
		parameter.maxValue = 5999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16);
		parameter.value = Long.parseLong(param, 16) + "Hz";
		mList.add(parameter);
		/********************************** ����13--����������� **************************************/
		param = msg.substring(58, 62);
		System.out.println("����13--�����������==" + Integer.parseInt(param, 16));
		parameter = new Parameter();
		parameter.address = "000D";
		parameter.count = "0001";
		parameter.name = "�����������";
		parameter.type = 4;
		parameter.maxValue = 9999;
		parameter.minValue = -9999;
		parameter.valueIn = Long.parseLong(param, 16) < 0x8000 ? Long
				.parseLong(param, 16) : Long.parseLong(param, 16) - 65536;
		parameter.value = (Long.parseLong(param, 16) < 0x8000 ? Long.parseLong(
				param, 16) : Long.parseLong(param, 16) - 65536) + "";
		mList.add(parameter);
		/********************************** ����14--���Ᵽ��ʱ�� **************************************/
		param = msg.substring(62, 66);
		System.out.println("����14--���Ᵽ��ʱ��==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "000E";
		parameter.count = "0001";
		parameter.name = "���Ᵽ��ʱ��";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "15s";
				break;
			case 1 :
				parameter.value = "30s";
				break;
			case 2 :
				parameter.value = "60s";
				break;
			case 3 :
				parameter.value = "120s";
				break;
			case 4 :
				parameter.value = "180s";
				break;
			case 5 :
				parameter.value = "300s";
				break;
			case 6 :
				parameter.value = "����";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "15s";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "30s";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "60s";
		selector.value = "0002";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "120s";
		selector.value = "0003";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "180s";
		selector.value = "0004";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "300s";
		selector.value = "0005";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "����";
		selector.value = "0006";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����15--ͨѶ��ַ **************************************/
		param = msg.substring(66, 70);
		System.out.println("����15--ͨѶ��ַ==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "000F";
		parameter.count = "0001";
		parameter.name = "ͨѶ��ַ";
		parameter.type = 1;
		parameter.maxValue = 247;
		parameter.minValue = 1;
		parameter.valueIn = Long.parseLong(param, 16);
		parameter.value = Long.parseLong(param, 16) + "";
		mList.add(parameter);
		/********************************** ����16--ͨѶ���� **************************************/
		param = msg.substring(70, 74);
		System.out.println("����16--ͨѶ����==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0010";
		parameter.count = "0001";
		parameter.name = "ͨѶ����";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		selectorList = new ArrayList<Selector>();
		for (int i = 0; i < 8; i++) {
			if ((Integer) parameter.valueIn == i) {
				parameter.value = 300 * (int) Math.pow(2, i) + "bps";
			}
			selector = new Selector();
			selector.name = 300 * (int) Math.pow(2, i) + "bps";
			selector.value = "000" + i;
			selectorList.add(selector);
		}
		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����17--�豸λ�� **************************************/
		param = msg.substring(74, 78);
		System.out.println("����15--�豸λ��==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0011";
		parameter.count = "0001";
		parameter.name = "�豸λ��";
		parameter.type = 1;
		parameter.maxValue = 9999;
		parameter.minValue = 1;
		parameter.valueIn = Long.parseLong(param, 16);
		parameter.value = Long.parseLong(param, 16) + "";
		mList.add(parameter);
		/********************************** �߼����� **************************************/
		/********************************** ����18--�����ܵ��ھ� **************************************/
		mList.add(new Parameter(true, "�߼�����"));
		param = msg.substring(78, 82);
		System.out.println("����18--�����ܵ��ھ�==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0012";
		parameter.count = "0001";
		parameter.name = "�����ܵ��ھ�";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "3mm";
				break;
			case 1 :
				parameter.value = "6mm";
				break;
			case 2 :
				parameter.value = "10mm";
				break;
			case 3 :
				parameter.value = "15mm";
				break;
			case 4 :
				parameter.value = "20mm";
				break;
			case 5 :
				parameter.value = "25mm";
				break;
			case 6 :
				parameter.value = "32mm";
				break;
			case 7 :
				parameter.value = "40mm";
				break;
			case 8 :
				parameter.value = "50mm";
				break;
			case 9 :
				parameter.value = "65mm";
				break;
			case 10 :
				parameter.value = "80mm";
				break;
			case 11 :
				parameter.value = "100mm";
				break;
			case 12 :
				parameter.value = "125mm";
				break;
			case 13 :
				parameter.value = "150mm";
				break;
			case 14 :
				parameter.value = "200mm";
				break;
			case 15 :
				parameter.value = "250mm";
				break;
			case 16 :
				parameter.value = "300mm";
				break;
			case 17 :
				parameter.value = "350mm";
				break;
			case 18 :
				parameter.value = "400mm";
				break;
			case 19 :
				parameter.value = "450mm";
				break;
			case 20 :
				parameter.value = "500mm";
				break;
			case 21 :
				parameter.value = "600mm";
				break;
			case 22 :
				parameter.value = "700mm";
				break;
			case 23 :
				parameter.value = "800mm";
				break;
			case 24 :
				parameter.value = "900mm";
				break;
			case 25 :
				parameter.value = "1000mm";
				break;
			case 26 :
				parameter.value = "1200mm";
				break;
			case 27 :
				parameter.value = "1400mm";
				break;
			case 28 :
				parameter.value = "1600mm";
				break;
			case 29 :
				parameter.value = "1800mm";
				break;
			case 30 :
				parameter.value = "2000mm";
				break;
			case 31 :
				parameter.value = "2200mm";
				break;
			case 32 :
				parameter.value = "2400mm";
				break;
			case 33 :
				parameter.value = "2500mm";
				break;
			case 34 :
				parameter.value = "2600mm";
				break;
			case 35 :
				parameter.value = "2800mm";
				break;
			case 36 :
				parameter.value = "3000mm";
				break;
		}
		selectorList = new ArrayList<Selector>();
		selector = new Selector();
		selector.name = "3mm";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "6mm";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "10mm";
		selector.value = "0002";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "15mm";
		selector.value = "0003";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "20mm";
		selector.value = "0004";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "25mm";
		selector.value = "0005";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "32mm";
		selector.value = "0006";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "40mm";
		selector.value = "0007";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "50mm";
		selector.value = "0008";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "65mm";
		selector.value = "0009";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "80mm";
		selector.value = "000A";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "100mm";
		selector.value = "000B";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "125mm";
		selector.value = "000C";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "150mm";
		selector.value = "000D";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "200mm";
		selector.value = "000E";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "250mm";
		selector.value = "000F";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "300mm";
		selector.value = "0010";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "350mm";
		selector.value = "0011";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "400mm";
		selector.value = "0012";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "450mm";
		selector.value = "0013";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "500mm";
		selector.value = "0014";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "600mm";
		selector.value = "0015";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "700mm";
		selector.value = "0016";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "800mm";
		selector.value = "0017";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "900mm";
		selector.value = "0018";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1000mm";
		selector.value = "0019";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1200mm";
		selector.value = "001A";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1400mm";
		selector.value = "001B";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1600mm";
		selector.value = "001C";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1800mm";
		selector.value = "001D";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "2000mm";
		selector.value = "001E";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "2200mm";
		selector.value = "001F";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "2400mm";
		selector.value = "0020";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "2500mm";
		selector.value = "0021";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "2600mm";
		selector.value = "0022";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "2800mm";
		selector.value = "0023";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "3000mm";
		selector.value = "0024";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����19--�����г���ʾ **************************************/
		param = msg.substring(82, 86);
		System.out.println("����19--�����г���ʾ==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0013";
		parameter.count = "0001";
		parameter.name = "�����г���ʾ";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "����";
				break;
			case 1 :
				parameter.value = "��ֹ";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "����";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "��ֹ";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����20--������ϵ��ֵ **************************************/
		param = msg.substring(86, 90);
		System.out.println("����20--������ϵ��ֵ==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0014";
		parameter.count = "0001";
		parameter.name = "������ϵ��ֵ";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 5.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"%";
		mList.add(parameter);
		/********************************** ����21--�չܱ������� **************************************/
		param = msg.substring(90, 94);
		System.out.println("����21--�չܱ�������==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0015";
		parameter.count = "0001";
		parameter.name = "�չܱ�������";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "����";
				break;
			case 1 :
				parameter.value = "��ֹ";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "����";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "��ֹ";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����22--�չܱ�����ֵ(%) **************************************/
		param = msg.substring(94, 98);
		System.out.println("����22--�չܱ�����ֵ(%)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0016";
		parameter.count = "0001";
		parameter.name = "�չܱ�����ֵ(%)";
		parameter.type = 2;
		parameter.point = 2;
		parameter.maxValue = 599.99;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"%";
		mList.add(parameter);
		/********************************** ����23--�������ޱ������� **************************************/
		param = msg.substring(98, 102);
		System.out.println("����23--�������ޱ�������==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0017";
		parameter.count = "0001";
		parameter.name = "�������ޱ�������";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "����";
				break;
			case 1 :
				parameter.value = "��ֹ";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "����";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "��ֹ";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����24--�������ޱ�����ֵ(%) **************************************/
		param = msg.substring(102, 106);
		System.out.println("����24--�������ޱ�����ֵ(%)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0018";
		parameter.count = "0001";
		parameter.name = "�������ޱ�����ֵ(%)";
		parameter.type = 2;
		parameter.point = 2;
		parameter.maxValue = 599.99;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"%";
		mList.add(parameter);
		/********************************** ����25--�������ޱ������� **************************************/
		param = msg.substring(106, 110);
		System.out.println("����25--�������ޱ�������==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0019";
		parameter.count = "0001";
		parameter.name = "�������ޱ�������";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "����";
				break;
			case 1 :
				parameter.value = "��ֹ";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "����";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "��ֹ";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����26--�������ޱ�����ֵ(%) **************************************/
		param = msg.substring(110, 114);
		System.out.println("����26--�������ޱ�����ֵ(%)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "001A";
		parameter.count = "0001";
		parameter.name = "�������ޱ�����ֵ(%)";
		parameter.type = 2;
		parameter.point = 2;
		parameter.maxValue = 599.99;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"%";
		mList.add(parameter);
		/********************************** ����27--���ű������� **************************************/
		param = msg.substring(114, 118);
		System.out.println("����27--���ű�������==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "001B";
		parameter.count = "0001";
		parameter.name = "���ű�������";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "����";
				break;
			case 1 :
				parameter.value = "��ֹ";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "����";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "��ֹ";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����28--�������� **************************************/
		param = msg.substring(118, 126);
		System.out.println("����28--��������=="
				+ NumberBytes.hexStrToLong(msg.substring(122, 126)
						+ msg.substring(118, 122)));
		parameter = new Parameter();
		parameter.address = "001C";
		parameter.count = "0002";
		parameter.name = "��������";
		parameter.type = 3;
		parameter.maxValue = 999999999;
		parameter.minValue = 0;
		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(122, 126)
				+ msg.substring(118, 122));
		parameter.value = NumberBytes.hexStrToLong(msg.substring(122, 126)
				+ msg.substring(118, 122))
				+ "";
		mList.add(parameter);
		/********************************** ����29--�������� **************************************/
		param = msg.substring(126, 134);
		System.out.println("����29--��������=="
				+ NumberBytes.hexStrToLong(msg.substring(130, 134)
						+ msg.substring(126, 130)));
		parameter = new Parameter();
		parameter.address = "001E";
		parameter.count = "0002";
		parameter.name = "��������";
		parameter.type = 3;
		parameter.maxValue = 999999999;
		parameter.minValue = 0;
		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(130, 134)
				+ msg.substring(126, 130));
		parameter.value = NumberBytes.hexStrToLong(msg.substring(130, 134)
				+ msg.substring(126, 130))
				+ "";
		mList.add(parameter);
		/********************************** ����30--������������ **************************************/
		param = msg.substring(134, 142);
		System.out.println("����30--������������=="
				+ NumberBytes.hexStrToLong(msg.substring(138, 142)
						+ msg.substring(134, 138)));
		parameter = new Parameter();
		parameter.address = "0020";
		parameter.count = "0002";
		parameter.name = "������������";
		parameter.type = 3;
		parameter.maxValue = 999999;
		parameter.minValue = 0;
		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(138, 142)
				+ msg.substring(134, 138));
		parameter.value = NumberBytes.hexStrToLong(msg.substring(138, 142)
				+ msg.substring(134, 138))
				+ "";
		Constans.PasswordLevel.LEVEL_1 = NumberBytes.hexStrToLong(msg
				.substring(138, 142) + msg.substring(134, 138));
		mList.add(parameter);
		/********************************** ����31--�߼��������� **************************************/
		param = msg.substring(142, 150);
		System.out.println("����31--�߼���������=="
				+ NumberBytes.hexStrToLong(msg.substring(146, 150)
						+ msg.substring(142, 146)));
		parameter = new Parameter();
		parameter.address = "0022";
		parameter.count = "0002";
		parameter.name = "�߼���������";
		parameter.maxValue = 999999;
		parameter.minValue = 0;
		parameter.type = 3;
		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(146, 150)
				+ msg.substring(142, 146));
		parameter.value = NumberBytes.hexStrToLong(msg.substring(146, 150)
				+ msg.substring(142, 146))
				+ "";
		Constans.PasswordLevel.LEVEL_2 = NumberBytes.hexStrToLong(msg
				.substring(146, 150) + msg.substring(142, 146));
		mList.add(parameter);
		/********************************** ����32--������������ **************************************/
		param = msg.substring(150, 158);
		System.out.println("����32--������������=="
				+ NumberBytes.hexStrToLong(msg.substring(154, 158)
						+ msg.substring(150, 154)));
		parameter = new Parameter();
		parameter.address = "0024";
		parameter.count = "0002";
		parameter.maxValue = 999999;
		parameter.minValue = 0;
		parameter.name = "������������";
		parameter.type = 3;
		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(154, 158)
				+ msg.substring(150, 154));
		parameter.value = NumberBytes.hexStrToLong(msg.substring(154, 158)
				+ msg.substring(150, 154))
				+ "";
		Constans.PasswordLevel.LEVEL_6 = NumberBytes.hexStrToLong(msg
				.substring(154, 158) + msg.substring(150, 154));
		mList.add(parameter);
		/********************************** ���������� **************************************/
		/********************************** ����33--������������ **************************************/
		mList.add(new Parameter(true, "����������"));
		param = msg.substring(158, 162);
		System.out.println("����33--������������==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0026";
		parameter.count = "0001";
		parameter.name = "������������";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "����";
				break;
			case 1 :
				parameter.value = "��ֹ";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "����";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "��ֹ";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����34--����������1(m/s) **************************************/
		param = msg.substring(162, 166);
		System.out.println("����34--����������1(m/s)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0027";
		parameter.count = "0001";
		parameter.name = "����������1(m/s)";
		parameter.type = 2;
		parameter.point = 3;
		parameter.maxValue = 15;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"m/s";
		mList.add(parameter);
		/********************************** ����35--��������ֵ1 **************************************/
		param = msg.substring(166, 170);
		System.out.println("����35--��������ֵ1==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0028";
		parameter.count = "0001";
		parameter.name = "��������ֵ1";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 1.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn);
		mList.add(parameter);
		/********************************** ����36--����������2(m/s) **************************************/
		param = msg.substring(170, 174);
		System.out.println("����36--����������2(m/s)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0029";
		parameter.count = "0001";
		parameter.name = "����������2(m/s)";
		parameter.type = 2;
		parameter.point = 3;
		parameter.maxValue = 15;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"m/s";
		mList.add(parameter);
		/********************************** ����37--��������ֵ2 **************************************/
		param = msg.substring(174, 178);
		System.out.println("����37--��������ֵ2==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "002A";
		parameter.count = "0001";
		parameter.name = "��������ֵ2";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 1.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn);
		mList.add(parameter);
		/********************************** ����38--����������3(m/s) **************************************/
		param = msg.substring(178, 182);
		System.out.println("����38--����������3(m/s)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "002B";
		parameter.count = "0001";
		parameter.name = "����������3(m/s)";
		parameter.type = 2;
		parameter.point = 3;
		parameter.maxValue = 15;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"m/s";
		mList.add(parameter);
		/********************************** ����39--��������ֵ3 **************************************/
		param = msg.substring(182, 186);
		System.out.println("����39--��������ֵ3==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "002C";
		parameter.count = "0001";
		parameter.name = "��������ֵ3";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 1.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn);
		mList.add(parameter);
		/********************************** ����40--����������4(m/s) **************************************/
		param = msg.substring(186, 190);
		System.out.println("����40--����������4(m/s)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "002D";
		parameter.count = "0001";
		parameter.name = "����������4(m/s)";
		parameter.type = 2;
		parameter.point = 3;
		parameter.maxValue = 15;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"m/s";
		mList.add(parameter);
		/********************************** ����41--��������ֵ4 **************************************/
		param = msg.substring(190, 194);
		System.out.println("����41--��������ֵ4==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "002E";
		parameter.count = "0001";
		parameter.name = "��������ֵ4";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 1.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn);
		mList.add(parameter);
		/********************************** ����42--����������5(m/s) **************************************/
		param = msg.substring(194, 198);
		System.out.println("����42--����������5(m/s)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "002F";
		parameter.count = "0001";
		parameter.name = "����������5(m/s)";
		parameter.type = 2;
		parameter.point = 3;
		parameter.maxValue = 15;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"m/s";
		mList.add(parameter);
		/********************************** ����43--��������ֵ5 **************************************/
		param = msg.substring(198, 202);
		System.out.println("����43--��������ֵ5==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0030";
		parameter.count = "0001";
		parameter.name = "��������ֵ5";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 1.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn);
		mList.add(parameter);
		/********************************** ����44--����������6(m/s) **************************************/
		param = msg.substring(202, 206);
		System.out.println("����44--����������6(m/s)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0031";
		parameter.count = "0001";
		parameter.name = "����������6(m/s)";
		parameter.type = 2;
		parameter.point = 3;
		parameter.maxValue = 15;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"m/s";
		mList.add(parameter);
		/********************************** ����45--��������ֵ6 **************************************/
		param = msg.substring(206, 210);
		System.out.println("����45--��������ֵ6==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0032";
		parameter.count = "0001";
		parameter.name = "��������ֵ6";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 1.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn);
		mList.add(parameter);
		/********************************** ����46--����������7(m/s) **************************************/
		param = msg.substring(210, 214);
		System.out.println("����46--����������7(m/s)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0033";
		parameter.count = "0001";
		parameter.name = "����������7(m/s)";
		parameter.type = 2;
		parameter.point = 3;
		parameter.maxValue = 15;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"m/s";
		mList.add(parameter);
		/********************************** ����47--��������ֵ7 **************************************/
		param = msg.substring(214, 218);
		System.out.println("����47--��������ֵ7==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0034";
		parameter.count = "0001";
		parameter.name = "��������ֵ7";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 1.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn);
		mList.add(parameter);
		/********************************** ����48--����������8(m/s) **************************************/
		param = msg.substring(218, 222);
		System.out.println("����48--����������8(m/s)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0035";
		parameter.count = "0001";
		parameter.name = "����������8(m/s)";
		parameter.type = 2;
		parameter.point = 3;
		parameter.maxValue = 15;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn)+"m/s";
		mList.add(parameter);
		/********************************** ����49--���ŷ�ʽѡ�� **************************************/
		param = msg.substring(222, 226);
		System.out.println("����49--���ŷ�ʽѡ��==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0036";
		parameter.count = "0001";
		parameter.name = "���ŷ�ʽѡ��";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "1/16��Ƶ";
				break;
			case 1 :
				parameter.value = "1/20��Ƶ";
				break;
			case 2 :
				parameter.value = "1/25��Ƶ";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "1/16��Ƶ";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1/20��Ƶ";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1/25��Ƶ";
		selector.value = "0002";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����50--���ŵ��� **************************************/
		param = msg.substring(226, 230);
		System.out.println("����50--���ŵ���==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0037";
		parameter.count = "0001";
		parameter.name = "���ŵ���";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "100mA";
				break;
			case 1 :
				parameter.value = "250mA";
				break;
			case 2 :
				parameter.value = "300mA";
				break;
			case 3 :
				parameter.value = "500mA";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "100mA";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "250mA";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "350mA";
		selector.value = "0002";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "500mA";
		selector.value = "0003";
		selectorList.add(selector);
		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����51--����������� **************************************/
		param = msg.substring(230, 234);
		System.out.println("����51--�����������==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0038";
		parameter.count = "0001";
		parameter.name = "�����������";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "����";
				break;
			case 1 :
				parameter.value = "��ֹ";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "����";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "��ֹ";
		selector.value = "0001";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����52--�������ϵ�� **************************************/
		param = msg.substring(234, 238);
		System.out.println("����52--�������ϵ��==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0039";
		parameter.count = "0001";
		parameter.name = "�������ϵ��";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "0.010m/s";
				break;
			case 1 :
				parameter.value = "0.020m/s";
				break;
			case 2 :
				parameter.value = "0.030m/s";
				break;
			case 3 :
				parameter.value = "0.050m/s";
				break;
			case 4 :
				parameter.value = "0.080m/s";
				break;
			case 5 :
				parameter.value = "0.100m/s";
				break;
			case 6 :
				parameter.value = "0.200m/s";
				break;
			case 7 :
				parameter.value = "0.300m/s";
				break;
			case 8 :
				parameter.value = "0.500m/s";
				break;
			case 9 :
				parameter.value = "0.800m/s";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "0.010m/s";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.020m/s";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.030m/s";
		selector.value = "0002";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.050m/s";
		selector.value = "0003";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.080m/s";
		selector.value = "0004";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.100m/s";
		selector.value = "0005";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.200m/s";
		selector.value = "0006";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.300m/s";
		selector.value = "0007";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.500m/s";
		selector.value = "0008";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "0.800m/s";
		selector.value = "0009";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����53--�������ʱ��(ms) **************************************/
		param = msg.substring(238, 242);
		System.out.println("����53--�������ʱ��(ms)==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "003A";
		parameter.count = "0001";
		parameter.name = "�������ʱ��(ms)";
		parameter.type = 1;
		parameter.valueIn = Integer.parseInt(param, 16);
		switch ((Integer) parameter.valueIn) {
			case 0 :
				parameter.value = "400ms";
				break;
			case 1 :
				parameter.value = "600ms";
				break;
			case 2 :
				parameter.value = "800ms";
				break;
			case 3 :
				parameter.value = "1000ms";
				break;
			case 4 :
				parameter.value = "2500ms";
				break;
		}
		selectorList = new ArrayList<Selector>();

		selector = new Selector();
		selector.name = "400ms";
		selector.value = "0000";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "600ms";
		selector.value = "0001";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "800ms";
		selector.value = "0002";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "1000ms";
		selector.value = "0003";
		selectorList.add(selector);

		selector = new Selector();
		selector.name = "2500ms";
		selector.value = "0004";
		selectorList.add(selector);

		parameter.selectors = selectorList;
		mList.add(parameter);
		/********************************** ����54--���������� **************************************/
		param = msg.substring(242, 250);
		System.out.println("����54--����������=="
				+ NumberBytes.hexStrToLong(msg.substring(246, 250)
						+ msg.substring(242, 246)));
		parameter = new Parameter();
		parameter.address = "003B";
		parameter.count = "0002";
		parameter.name = "����������";
		parameter.type = 3;
		parameter.maxValue = 99999999;
		parameter.minValue = 0;
		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(246, 250)
				+ msg.substring(242, 246));
		parameter.value = NumberBytes.hexStrToLong(msg.substring(246, 250)
				+ msg.substring(242, 246))
				+ "";
		mList.add(parameter);
		/********************************** ����55--�������������� **************************************/
		param = msg.substring(250, 258);
		System.out.println("����55--��������������=="
				+ NumberBytes.hexStrToLong(msg.substring(254, 258)
						+ msg.substring(250, 254)));
		parameter = new Parameter();
		parameter.address = "003D";
		parameter.count = "0002";
		parameter.name = "��������������";
		parameter.type = 3;
		parameter.maxValue = 999999;
		parameter.minValue = 0;
		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(254, 258)
				+ msg.substring(250, 254));
		parameter.value = NumberBytes.hexStrToLong(msg.substring(254, 258)
				+ msg.substring(250, 254))
				+ "";
		Constans.PasswordLevel.LEVEL_3 = NumberBytes.hexStrToLong(msg
				.substring(254, 258) + msg.substring(250, 254));
		mList.add(parameter);
		/********************************** ת�������� **************************************/
		/********************************** ����56--ת�����궨ϵ�� **************************************/
		mList.add(new Parameter(true, "ת��������"));
		param = msg.substring(258, 262);
		System.out.println("����56--ת�����궨ϵ��==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "003F";
		parameter.count = "0001";
		parameter.name = "ת�����궨ϵ��";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 5.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn);
		mList.add(parameter);
		/********************************** ����57--����������� **************************************/
		param = msg.substring(262, 266);
		System.out.println("����57--�����������==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0040";
		parameter.count = "0001";
		parameter.name = "�����������";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 1.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn);
		mList.add(parameter);
		/********************************** ����58--������������ **************************************/
		param = msg.substring(266, 270);
		System.out.println("����58--������������==" + Long.parseLong(param, 16));
		parameter = new Parameter();
		parameter.address = "0041";
		parameter.count = "0001";
		parameter.name = "������������";
		parameter.type = 2;
		parameter.point = 4;
		parameter.maxValue = 3.9999;
		parameter.minValue = 0;
		parameter.valueIn = Long.parseLong(param, 16)
				/ Math.pow(10, parameter.point);
		parameter.value = String.format("%." + parameter.point + "f",
				(Double) parameter.valueIn);
		mList.add(parameter);
		/********************************** ����59--�Ǳ���� **************************************/
		param = msg.substring(270, 278);
		System.out.println("����59--�Ǳ����=="
				+ NumberBytes.hexStrToLong(msg.substring(274, 278)
						+ msg.substring(270, 274)));
		parameter = new Parameter();
		parameter.address = "0042";
		parameter.count = "0002";
		parameter.name = "�Ǳ����";
		parameter.type = 3;
		parameter.maxValue = 99999999;
		parameter.minValue = 0;
		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(274, 278)
				+ msg.substring(270, 274));
		parameter.value = NumberBytes.hexStrToLong(msg.substring(274, 278)
				+ msg.substring(270, 274))
				+ "";
		mList.add(parameter);
		/********************************** ����60--ת�������� **************************************/
		param = msg.substring(278, 286);
		System.out.println("����60--ת��������=="
				+ NumberBytes.hexStrToLong(msg.substring(282, 286)
						+ msg.substring(278, 282)));
		parameter = new Parameter();
		parameter.address = "0044";
		parameter.count = "0002";
		parameter.name = "ת��������";
		parameter.type = 3;
		parameter.maxValue = 999999;
		parameter.minValue = 0;
		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(282, 286)
				+ msg.substring(278, 282));
		parameter.value = NumberBytes.hexStrToLong(msg.substring(282, 286)
				+ msg.substring(278, 282))
				+ "";
		Constans.PasswordLevel.LEVEL_4 = NumberBytes.hexStrToLong(msg
				.substring(282, 286) + msg.substring(278, 282));
		mList.add(parameter);

	}

	private void checkPsw(long psw) {
		if (Constans.PasswordLevel.LEVEL_1 == psw) {
			AppStaticVar.PASSWORD_LEVEAL = 1;
		} else if (Constans.PasswordLevel.LEVEL_2 == psw) {
			AppStaticVar.PASSWORD_LEVEAL = 2;
		} else if (Constans.PasswordLevel.LEVEL_3 == psw) {
			AppStaticVar.PASSWORD_LEVEAL = 3;
		} else if (Constans.PasswordLevel.LEVEL_4 == psw) {
			AppStaticVar.PASSWORD_LEVEAL = 4;
		} else if (Constans.PasswordLevel.LEVEL_5 == psw) {
			AppStaticVar.PASSWORD_LEVEAL = 5;
		}
	}

	@Override
	public void reconnectSuccss() {
		reconnectCount = 3;
		startReadParam();
	}

}

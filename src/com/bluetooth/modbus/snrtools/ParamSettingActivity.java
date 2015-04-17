package com.bluetooth.modbus.snrtools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bluetooth.modbus.snrtools.adapter.ParameterAdapter;
import com.bluetooth.modbus.snrtools.bean.Parameter;
import com.bluetooth.modbus.snrtools.bean.Selector;
import com.bluetooth.modbus.snrtools.manager.AppStaticVar;

public class ParamSettingActivity extends BaseActivity {

	private ListView mListview;
	private ParameterAdapter mAdapter;
	private List<Parameter> mList;
	private List<Parameter> mDataList;
//	private ReadThread mReadThread;
//	private Thread mThread;
	private final static int READ_TIMEOUT = 10000;
	private final static int SELECT_PARAM = 0x100001;
	private final static int INPUT_PARAM = 0x100002;
//	private Timer mTimer;
	/** 显示参数的数量 */
	private int mCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_param_activity);
		if (AppStaticVar.PASSWORD_LEVEAL == 0) {
			setTitleContent("查看参数");
		} else {
			setTitleContent("设置参数");
		}
		mList = (List<Parameter>) getIntent().getSerializableExtra("list");
		hideRightView(R.id.btnRight1);
		hideRightView(R.id.view2);
		initUI();
		setListeners();
//		getData();
	}

	@Override
	public void reconnectSuccss() {
	}

	private void setListeners() {
		switch (AppStaticVar.PASSWORD_LEVEAL) {
		case 1 :// 可以设置1-24
			mCount = 17;
			break;
		case 2 :// 可以设置1-25
			mCount = 32;
			break;
		case 3 :// 可以设置1-38
			mCount = 55;
			break;
		case 4 :// 可以设置1-60

			mCount = 60;
			break;
		case 5 :// // 超级密码
			mCount = mList.size();
			break;
	}
		mDataList = new ArrayList<Parameter>();
		mDataList.addAll(mList.subList(0, mCount));
		mAdapter = new ParameterAdapter(mContext, mDataList);
		mListview.setAdapter(mAdapter);
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				if (mAdapter.getItem(position).selectors != null) {
					intent.setClass(mContext, SelectActivity.class);
					intent.putExtra("position", position);
					intent.putExtra("title", mAdapter.getItem(position).name);
					intent.putExtra("value", mAdapter.getItem(position).value);
					intent.putExtra("valueIn",
							mAdapter.getItem(position).valueIn.toString());
					intent.putExtra("list",
							(Serializable) mAdapter.getItem(position).selectors);
					intent.putExtra("param", mAdapter.getItem(position));
					startActivityForResult(intent, SELECT_PARAM);
				} else {
					intent.setClass(mContext, InputParamActivity.class);
					intent.putExtra("position", position);
					intent.putExtra("title", mAdapter.getItem(position).name);
					intent.putExtra("value", mAdapter.getItem(position).value);
					intent.putExtra("valueIn",
							mAdapter.getItem(position).valueIn.toString());
					intent.putExtra("param", mAdapter.getItem(position));
					startActivityForResult(intent, INPUT_PARAM);
				}
			}
		});
	}

	private void initUI() {
		mListview = (ListView) findViewById(R.id.listView1);
	}

//	private void startTimer() {
//		if (mTimer == null) {
//			mTimer = new Timer();
//		}
//		mTimer.schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				startReadParam();
//			}
//		}, READ_TIMEOUT);
//	}
//
//	private void stopTimer() {
//		if (mTimer != null) {
//			mTimer.cancel();
//			mTimer = null;
//		}
//	}
//	private void startReadParam() {
//
//		mThread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
////				ModbusUtils.readParameter(mHandler);
//			}
//		});
//		mThread.start();
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PARAM) {
				int position = data.getIntExtra("position", -1);
				Selector selector = (Selector) data
						.getSerializableExtra("selector");
				if (position != -1) {
					mAdapter.getItem(position).valueIn = selector.value;
					mAdapter.getItem(position).value = selector.name;
					mAdapter.notifyDataSetChanged();
				}
			} else if (requestCode == INPUT_PARAM) {
				int position = data.getIntExtra("position", -1);
				String value = data.getStringExtra("value");
				if (position != -1) {
					mAdapter.getItem(position).valueIn = value;
					mAdapter.getItem(position).value = value;
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

//	private void dealReturnMsg(String msg) {
//		if (msg.length() != 282) {
//			return;
//		}
//		mList.clear();
//		Parameter parameter = null;
//		ArrayList<Selector> selectorList = null;
//		Selector selector = null;
//		/**********************************基本参数 **************************************/
//		/********************************** 参数1--语言 **************************************/
//		String param = msg.substring(6, 10);
//		System.out.println("参数1--语言==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0000";
//		parameter.count = "0001";
//		parameter.name = "语言";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "简体中文";
//				break;
//			case 1 :
//				parameter.value = "English";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "简体中文";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "English";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数2--流量单位 **************************************/
//		param = msg.substring(10, 14);
//		System.out.println("参数2--流量单位 ==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0001";
//		parameter.count = "0001";
//		parameter.name = "流量单位";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "L/h";
//				break;
//			case 1 :
//				parameter.value = "L/mim";
//				break;
//			case 2 :
//				parameter.value = "L/s";
//				break;
//			case 3 :
//				parameter.value = "m3/h";
//				break;
//			case 4 :
//				parameter.value = "m3/min";
//				break;
//			case 5 :
//				parameter.value = "m3/s";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "L/h";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "L/min";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "L/s";
//		selector.value = "0002";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "m3/h";
//		selector.value = "0003";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "m3/min";
//		selector.value = "0004";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "m3/s";
//		selector.value = "0005";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		
//		/********************************** 参数3--仪表量程设置 **************************************/
//		param = msg.substring(14, 22);
//		System.out.println("参数3--仪表量程设置==" + NumberBytes.hexStrToLong(msg.substring(18, 22)+msg.substring(14, 18)));
//		parameter = new Parameter();
//		parameter.address = "0002";
//		parameter.count = "0002";
//		parameter.name = "仪表量程设置";
//		parameter.type = 3;
//		parameter.valueIn =NumberBytes.hexStrToLong(msg.substring(18, 22)+msg.substring(14, 18));
//		parameter.value = NumberBytes.hexStrToLong(msg.substring(18, 22)+msg.substring(14, 18))+"";
//		mList.add(parameter);
//		/********************************** 参数4--流量方向择项 **************************************/
//		param = msg.substring(22, 26);
//		System.out.println("参数4--流量方向择项==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0004";
//		parameter.count = "0001";
//		parameter.name = "流量方向择项";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "正向";
//				break;
//			case 1 :
//				parameter.value = "反向";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "正向";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "反向";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数5--反向输出允许 **************************************/
//		param = msg.substring(26, 30);
//		System.out.println("参数13--反向输出允许==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0005";
//		parameter.count = "0001";
//		parameter.name = "反向输出允许";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "允许";
//				break;
//			case 1 :
//				parameter.value = "禁止";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "允许";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "禁止";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		
//		/********************************** 参数6--流量积算单位 **************************************/
//		param = msg.substring(30, 34);
//		System.out.println("参数6--流量积算单位==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0006";
//		parameter.count = "0001";
//		parameter.name = "流量积算单位";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "0.001m3";
//				break;
//			case 1 :
//				parameter.value = "0.01m3";
//				break;
//			case 2 :
//				parameter.value = "0.1m3";
//				break;
//			case 3 :
//				parameter.value = "1m3";
//				break;
//			case 4 :
//				parameter.value = "0.001L";
//				break;
//			case 5 :
//				parameter.value = "0.01L";
//				break;
//			case 6 :
//				parameter.value = "0.1L";
//				break;
//			case 7 :
//				parameter.value = "1L";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "0.001m3";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.01m3";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.1m3";
//		selector.value = "0002";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1m3";
//		selector.value = "0003";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.001L";
//		selector.value = "0004";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.01L";
//		selector.value = "0005";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.1L";
//		selector.value = "0006";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1L";
//		selector.value = "0007";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数7--测量阻尼时间(s) **************************************/
//		param = msg.substring(34, 38);
//		System.out.println("参数7--测量阻尼时间(s) ==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0007";
//		parameter.count = "0001";
//		parameter.name = "测量阻尼时间(s)";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "1s";
//				break;
//			case 1 :
//				parameter.value = "2s";
//				break;
//			case 2 :
//				parameter.value = "3s";
//				break;
//			case 3 :
//				parameter.value = "4s";
//				break;
//			case 4 :
//				parameter.value = "6s";
//				break;
//			case 5 :
//				parameter.value = "8s";
//				break;
//			case 6 :
//				parameter.value = "10s";
//				break;
//			case 7 :
//				parameter.value = "15s";
//				break;
//			case 8 :
//				parameter.value = "30s";
//				break;
//			case 9 :
//				parameter.value = "50s";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "1s";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "2s";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "3s";
//		selector.value = "0002";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "4s";
//		selector.value = "0003";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "6s";
//		selector.value = "0004";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "8s";
//		selector.value = "0005";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "10s";
//		selector.value = "0006";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "15s";
//		selector.value = "0007";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "30s";
//		selector.value = "0008";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "50s";
//		selector.value = "0009";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数8--小信号切除点(%) **************************************/
//		param = msg.substring(38, 42);
//		System.out.println("参数8--小信号切除点(%)==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0008";
//		parameter.count = "0001";
//		parameter.name = "小信号切除点(%)";
//		parameter.type = 2;
//		parameter.point = 2;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数9--脉冲输出方式 **************************************/
//		param = msg.substring(42, 46);
//		System.out.println("参数9--脉冲输出方式==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0009";
//		parameter.count = "0001";
//		parameter.name = "脉冲输出方式";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "频率";
//				break;
//			case 1 :
//				parameter.value = "脉冲";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "频率";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "脉冲";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数10--脉冲单位当量 **************************************/
//		param = msg.substring(46, 50);
//		System.out.println("参数10--脉冲单位当量==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "000A";
//		parameter.count = "0001";
//		parameter.name = "脉冲单位当量";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "1.0m3/cp";
//				break;
//			case 1 :
//				parameter.value = "0.1m3/cp";
//				break;
//			case 2 :
//				parameter.value = "0.01m3/cp";
//				break;
//			case 3 :
//				parameter.value = "0.001m3/cp";
//				break;
//			case 4 :
//				parameter.value = "1.0L/cp";
//				break;
//			case 5 :
//				parameter.value = "0.1L/cp";
//				break;
//			case 6 :
//				parameter.value = "0.01L/cp";
//				break;
//			case 7 :
//				parameter.value = "0.001L/cp";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "1.0m3/cp";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.1m3/cp";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.01m3/cp";
//		selector.value = "0002";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.001m3/cp";
//		selector.value = "0003";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1.0L/cp";
//		selector.value = "0004";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.1L/cp";
//		selector.value = "0005";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.01L/cp";
//		selector.value = "0006";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.001L/cp";
//		selector.value = "0007";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数11--脉冲宽度时间 **************************************/
//		param = msg.substring(50, 54);
//		System.out.println("参数11--脉冲宽度时间==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "000B";
//		parameter.count = "0001";
//		parameter.name = "脉冲宽度时间";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "4ms";
//				break;
//			case 1 :
//				parameter.value = "8ms";
//				break;
//			case 2 :
//				parameter.value = "20ms";
//				break;
//			case 3 :
//				parameter.value = "30ms";
//				break;
//			case 4 :
//				parameter.value = "40ms";
//				break;
//			case 5 :
//				parameter.value = "80ms";
//				break;
//			case 6 :
//				parameter.value = "100ms";
//				break;
//			case 7 :
//				parameter.value = "150ms";
//				break;
//			case 8 :
//				parameter.value = "200ms";
//				break;
//			case 9 :
//				parameter.value = "400ms";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "4ms";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "8ms";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "20ms";
//		selector.value = "0002";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "30ms";
//		selector.value = "0003";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "40ms";
//		selector.value = "0004";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "80ms";
//		selector.value = "0005";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "100ms";
//		selector.value = "0006";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "150ms";
//		selector.value = "0007";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "200ms";
//		selector.value = "0008";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "400ms";
//		selector.value = "0009";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数12--频率输出范围(Hz) **************************************/
//		param = msg.substring(54, 58);
//		System.out
//				.println("参数12--频率输出范围(Hz)==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "000C";
//		parameter.count = "0001";
//		parameter.name = "频率输出范围(Hz)";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		parameter.value = Integer.parseInt(param, 16) + "";
//		mList.add(parameter);
//		/********************************** 参数13--流量零点修正 **************************************/
//		param = msg.substring(58, 62);
//		System.out.println("参数13--流量零点修正==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "000D";
//		parameter.count = "0001";
//		parameter.name = "流量零点修正";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		parameter.value = Integer.parseInt(param, 16) + "";
//		mList.add(parameter);
//		/********************************** 参数14--背光保持时间 **************************************/
//		param = msg.substring(62, 66);
//		System.out.println("参数14--背光保持时间==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "000E";
//		parameter.count = "0001";
//		parameter.name = "背光保持时间";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "15s";
//				break;
//			case 1 :
//				parameter.value = "30s";
//				break;
//			case 2 :
//				parameter.value = "60s";
//				break;
//			case 3 :
//				parameter.value = "120s";
//				break;
//			case 4 :
//				parameter.value = "180s";
//				break;
//			case 5 :
//				parameter.value = "300s";
//				break;
//			case 6 :
//				parameter.value = "常亮";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "15s";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "30s";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "60s";
//		selector.value = "0002";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "120s";
//		selector.value = "0003";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "180s";
//		selector.value = "0004";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "300s";
//		selector.value = "0005";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "常亮";
//		selector.value = "0006";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数15--通讯地址 **************************************/
//		param = msg.substring(66, 70);
//		System.out.println("参数15--通讯地址==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "000F";
//		parameter.count = "0001";
//		parameter.name = "通讯地址";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		parameter.value = Integer.parseInt(param, 16) + "";
//		mList.add(parameter);
//		/********************************** 参数16--通讯速率 **************************************/
//		param = msg.substring(70, 74);
//		System.out.println("参数16--通讯速率==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0010";
//		parameter.count = "0001";
//		parameter.name = "通讯速率";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		selectorList = new ArrayList<>();
//		for (int i = 0; i < 8; i++) {
//			if ((int) parameter.valueIn == i) {
//				parameter.value = 300 * (int) Math.pow(2, i) + "bps";
//			}
//			selector = new Selector();
//			selector.name = 300 * (int) Math.pow(2, i) + "bps";
//			selector.value = "000" + i;
//			selectorList.add(selector);
//		}
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数17--设备位号 **************************************/
//		param = msg.substring(74, 78);
//		System.out.println("参数15--设备位号==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0011";
//		parameter.count = "0001";
//		parameter.name = "设备位号";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		parameter.value = Integer.parseInt(param, 16) + "";
//		mList.add(parameter);
//		/********************************** 高级参数 **************************************/
//		/********************************** 参数18--测量管道口径 **************************************/
//		param = msg.substring(78, 82);
//		System.out.println("参数18--测量管道口径==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0012";
//		parameter.count = "0001";
//		parameter.name = "测量管道口径";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "3mm";
//				break;
//			case 1 :
//				parameter.value = "6mm";
//				break;
//			case 2 :
//				parameter.value = "10mm";
//				break;
//			case 3 :
//				parameter.value = "15mm";
//				break;
//			case 4 :
//				parameter.value = "20mm";
//				break;
//			case 5 :
//				parameter.value = "25mm";
//				break;
//			case 6 :
//				parameter.value = "32mm";
//				break;
//			case 7 :
//				parameter.value = "40mm";
//				break;
//			case 8 :
//				parameter.value = "50mm";
//				break;
//			case 9 :
//				parameter.value = "65mm";
//				break;
//			case 10 :
//				parameter.value = "80mm";
//				break;
//			case 11 :
//				parameter.value = "100mm";
//				break;
//			case 12 :
//				parameter.value = "125mm";
//				break;
//			case 13 :
//				parameter.value = "150mm";
//				break;
//			case 14 :
//				parameter.value = "200mm";
//				break;
//			case 15 :
//				parameter.value = "250mm";
//				break;
//			case 16 :
//				parameter.value = "300mm";
//				break;
//			case 17 :
//				parameter.value = "350mm";
//				break;
//			case 18 :
//				parameter.value = "400mm";
//				break;
//			case 19 :
//				parameter.value = "450mm";
//				break;
//			case 20 :
//				parameter.value = "500mm";
//				break;
//			case 21 :
//				parameter.value = "600mm";
//				break;
//			case 22 :
//				parameter.value = "700mm";
//				break;
//			case 23 :
//				parameter.value = "800mm";
//				break;
//			case 24 :
//				parameter.value = "900mm";
//				break;
//			case 25 :
//				parameter.value = "1000mm";
//				break;
//			case 26 :
//				parameter.value = "1200mm";
//				break;
//			case 27 :
//				parameter.value = "1400mm";
//				break;
//			case 28 :
//				parameter.value = "1600mm";
//				break;
//			case 29 :
//				parameter.value = "1800mm";
//				break;
//			case 30 :
//				parameter.value = "2000mm";
//				break;
//			case 31 :
//				parameter.value = "2200mm";
//				break;
//			case 32 :
//				parameter.value = "2400mm";
//				break;
//			case 33 :
//				parameter.value = "2500mm";
//				break;
//			case 34 :
//				parameter.value = "2600mm";
//				break;
//			case 35 :
//				parameter.value = "2800mm";
//				break;
//			case 36 :
//				parameter.value = "3000mm";
//				break;
//		}
//		selectorList = new ArrayList<>();
//		selector = new Selector();
//		selector.name = "3mm";
//		selector.value = "0000";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "6mm";
//		selector.value = "0001";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "10mm";
//		selector.value = "0002";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "15mm";
//		selector.value = "0003";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "20mm";
//		selector.value = "0004";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "25mm";
//		selector.value = "0005";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "32mm";
//		selector.value = "0006";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "40mm";
//		selector.value = "0007";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "50mm";
//		selector.value = "0008";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "65mm";
//		selector.value = "0009";
//		selectorList.add(selector);
//
//		
//		selector = new Selector();
//		selector.name = "80mm";
//		selector.value = "000A";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "100mm";
//		selector.value = "000B";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "125mm";
//		selector.value = "000C";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "150mm";
//		selector.value = "000D";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "200mm";
//		selector.value = "000E";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "250mm";
//		selector.value = "000F";
//		selectorList.add(selector);
//		
//		selector = new Selector();
//		selector.name = "300mm";
//		selector.value = "0010";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "350mm";
//		selector.value = "0011";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "400mm";
//		selector.value = "0012";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "450mm";
//		selector.value = "0013";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "500mm";
//		selector.value = "0014";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "600mm";
//		selector.value = "0015";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "700mm";
//		selector.value = "0016";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "800mm";
//		selector.value = "0017";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "900mm";
//		selector.value = "0018";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1000mm";
//		selector.value = "0019";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1200mm";
//		selector.value = "001A";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1400mm";
//		selector.value = "001B";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1600mm";
//		selector.value = "001C";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1800mm";
//		selector.value = "001D";
//		selectorList.add(selector);
//
//
//		selector = new Selector();
//		selector.name = "2000mm";
//		selector.value = "001E";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "2200mm";
//		selector.value = "001F";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "2400mm";
//		selector.value = "0020";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "2500mm";
//		selector.value = "0021";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "2600mm";
//		selector.value = "0022";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "2800mm";
//		selector.value = "0023";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "3000mm";
//		selector.value = "0024";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数19--允许切除显示 **************************************/
//		param = msg.substring(82, 86);
//		System.out.println("参数19--允许切除显示==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0013";
//		parameter.count = "0001";
//		parameter.name = "允许切除显示";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "允许";
//				break;
//			case 1 :
//				parameter.value = "禁止";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "允许";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "禁止";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数20--传感器系数值 **************************************/
//		param = msg.substring(86, 90);
//		System.out.println("参数20--传感器系数值==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0014";
//		parameter.count = "0001";
//		parameter.name = "传感器系数值";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数21--空管报警允许 **************************************/
//		param = msg.substring(90, 94);
//		System.out.println("参数21--空管报警允许==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0015";
//		parameter.count = "0001";
//		parameter.name = "空管报警允许";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "允许";
//				break;
//			case 1 :
//				parameter.value = "禁止";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "允许";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "禁止";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数22--空管报警阈值(%) **************************************/
//		param = msg.substring(94, 98);
//		System.out.println("参数22--空管报警阈值(%)==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0016";
//		parameter.count = "0001";
//		parameter.name = "空管报警阈值(%)";
//		parameter.type = 2;
//		parameter.point = 2;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数23--流量上限报警允许 **************************************/
//		param = msg.substring(98, 102);
//		System.out.println("参数23--流量上限报警允许==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0017";
//		parameter.count = "0001";
//		parameter.name = "流量上限报警允许";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "允许";
//				break;
//			case 1 :
//				parameter.value = "禁止";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "允许";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "禁止";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数24--流量上限报警数值(%) **************************************/
//		param = msg.substring(102, 106);
//		System.out.println("参数24--流量上限报警数值(%)=="
//				+ Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0018";
//		parameter.count = "0001";
//		parameter.name = "流量上限报警数值(%)";
//		parameter.type = 2;
//		parameter.point = 2;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数25--流量下限报警允许 **************************************/
//		param = msg.substring(106, 110);
//		System.out.println("参数25--流量下限报警允许==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0019";
//		parameter.count = "0001";
//		parameter.name = "流量下限报警允许";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "允许";
//				break;
//			case 1 :
//				parameter.value = "禁止";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "允许";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "禁止";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数26--流量下限报警数值(%) **************************************/
//		param = msg.substring(110, 114);
//		System.out.println("参数26--流量下限报警数值(%)=="
//				+ Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "001A";
//		parameter.count = "0001";
//		parameter.name = "流量下限报警数值(%)";
//		parameter.type = 2;
//		parameter.point = 2;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数27--励磁报警允许 **************************************/
//		param = msg.substring(114, 118);
//		System.out.println("参数27--励磁报警允许==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "001B";
//		parameter.count = "0001";
//		parameter.name = "励磁报警允许";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "允许";
//				break;
//			case 1 :
//				parameter.value = "禁止";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "允许";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "禁止";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数28--正向总量 **************************************/
//		param = msg.substring(118, 126);
//		System.out.println("参数28--正向总量==" + NumberBytes.hexStrToLong(msg.substring(122, 126)+msg.substring(118, 122)));
//		parameter = new Parameter();
//		parameter.address = "001C";
//		parameter.count = "0002";
//		parameter.name = "正向总量";
//		parameter.type = 3;
//		parameter.valueIn =NumberBytes.hexStrToLong(msg.substring(122, 126)+msg.substring(118, 122));
//		parameter.value = NumberBytes.hexStrToLong(msg.substring(122, 126)+msg.substring(118, 122))+"";
//		mList.add(parameter);
//		/********************************** 参数29--反向总量 **************************************/
//		param = msg.substring(126, 134);
//		System.out.println("参数29--反向总量==" + NumberBytes.hexStrToLong(msg.substring(130, 134)+msg.substring(126, 130)));
//		parameter = new Parameter();
//		parameter.address = "001E";
//		parameter.count = "0002";
//		parameter.name = "反向总量";
//		parameter.type = 3;
//		parameter.valueIn =NumberBytes.hexStrToLong(msg.substring(130, 134)+msg.substring(126, 130));
//		parameter.value = NumberBytes.hexStrToLong(msg.substring(130, 134)+msg.substring(126, 130))+"";
//		mList.add(parameter);
//		/********************************** 参数30--基本参数密码 **************************************/
//		param = msg.substring(134, 142);
//		System.out.println("参数30--基本参数密码==" + NumberBytes.hexStrToLong(msg.substring(138, 142)+msg.substring(134, 138)));
//		parameter = new Parameter();
//		parameter.address = "0020";
//		parameter.count = "0002";
//		parameter.name = "基本参数密码";
//		parameter.type = 3;
//		parameter.valueIn =NumberBytes.hexStrToLong(msg.substring(138, 142)+msg.substring(134, 138));
//		parameter.value = NumberBytes.hexStrToLong(msg.substring(138, 142)+msg.substring(134, 138))+"";
//		mList.add(parameter);
//		/********************************** 参数31--高级参数密码 **************************************/
//		param = msg.substring(142, 150);
//		System.out.println("参数31--高级参数密码==" + NumberBytes.hexStrToLong(msg.substring(146, 150)+msg.substring(142, 146)));
//		parameter = new Parameter();
//		parameter.address = "0022";
//		parameter.count = "0002";
//		parameter.name = "高级参数密码";
//		parameter.type = 3;
//		parameter.valueIn =NumberBytes.hexStrToLong(msg.substring(146, 150)+msg.substring(142, 146));
//		parameter.value = NumberBytes.hexStrToLong(msg.substring(146, 150)+msg.substring(142, 146))+"";
//		mList.add(parameter);
//		/********************************** 参数32--总量清零密码 **************************************/
//		param = msg.substring(150, 158);
//		System.out.println("参数32--总量清零密码==" + NumberBytes.hexStrToLong(msg.substring(154, 158)+msg.substring(150, 154)));
//		parameter = new Parameter();
//		parameter.address = "0024";
//		parameter.count = "0002";
//		parameter.name = "总量清零密码";
//		parameter.type = 3;
//		parameter.valueIn =NumberBytes.hexStrToLong(msg.substring(154, 158)+msg.substring(150, 154));
//		parameter.value = NumberBytes.hexStrToLong(msg.substring(154, 158)+msg.substring(150, 154))+"";
//		mList.add(parameter);
//		/********************************** 传感器参数 **************************************/
//		/********************************** 参数33--流量修正允许 **************************************/
//		param = msg.substring(158, 162);
//		System.out.println("参数33--流量修正允许==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0026";
//		parameter.count = "0001";
//		parameter.name = "流量修正允许";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "允许";
//				break;
//			case 1 :
//				parameter.value = "禁止";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "允许";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "禁止";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数34--流量修正点1(m/s) **************************************/
//		param = msg.substring(162, 166);
//		System.out.println("参数34--流量修正点1(m/s)=="
//				+ Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0027";
//		parameter.count = "0001";
//		parameter.name = "流量修正点1(m/s)";
//		parameter.type = 2;
//		parameter.point = 3;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数35--流量修正值1 **************************************/
//		param = msg.substring(166, 170);
//		System.out.println("参数35--流量修正值1==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0028";
//		parameter.count = "0001";
//		parameter.name = "流量修正值1";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数36--流量修正点2(m/s) **************************************/
//		param = msg.substring(170, 174);
//		System.out.println("参数36--流量修正点2(m/s)=="
//				+ Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0029";
//		parameter.count = "0001";
//		parameter.name = "流量修正点2(m/s)";
//		parameter.type = 2;
//		parameter.point = 3;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数37--流量修正值2 **************************************/
//		param = msg.substring(174, 178);
//		System.out.println("参数37--流量修正值2==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "002A";
//		parameter.count = "0001";
//		parameter.name = "流量修正值2";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数38--流量修正点3(m/s) **************************************/
//		param = msg.substring(178, 182);
//		System.out.println("参数38--流量修正点3(m/s)=="
//				+ Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "002B";
//		parameter.count = "0001";
//		parameter.name = "流量修正点3(m/s)";
//		parameter.type = 2;
//		parameter.point = 3;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数39--流量修正值3 **************************************/
//		param = msg.substring(182, 186);
//		System.out.println("参数39--流量修正值3==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "002C";
//		parameter.count = "0001";
//		parameter.name = "流量修正值3";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数40--流量修正点4(m/s) **************************************/
//		param = msg.substring(186, 190);
//		System.out.println("参数40--流量修正点4(m/s)=="
//				+ Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "002D";
//		parameter.count = "0001";
//		parameter.name = "流量修正点4(m/s)";
//		parameter.type = 2;
//		parameter.point = 3;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数41--流量修正值4 **************************************/
//		param = msg.substring(190, 194);
//		System.out.println("参数41--流量修正值4==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "002E";
//		parameter.count = "0001";
//		parameter.name = "流量修正值4";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数42--流量修正点5(m/s) **************************************/
//		param = msg.substring(194, 198);
//		System.out.println("参数42--流量修正点5(m/s)=="
//				+ Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "002F";
//		parameter.count = "0001";
//		parameter.name = "流量修正点5(m/s)";
//		parameter.type = 2;
//		parameter.point = 3;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数43--流量修正值5 **************************************/
//		param = msg.substring(198, 202);
//		System.out.println("参数43--流量修正值5==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0030";
//		parameter.count = "0001";
//		parameter.name = "流量修正值5";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数44--流量修正点6(m/s) **************************************/
//		param = msg.substring(202, 206);
//		System.out.println("参数44--流量修正点6(m/s)=="
//				+ Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0031";
//		parameter.count = "0001";
//		parameter.name = "流量修正点6(m/s)";
//		parameter.type = 2;
//		parameter.point = 3;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数45--流量修正值6 **************************************/
//		param = msg.substring(206, 210);
//		System.out.println("参数45--流量修正值6==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0032";
//		parameter.count = "0001";
//		parameter.name = "流量修正值6";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数46--流量修正点7(m/s) **************************************/
//		param = msg.substring(210, 214);
//		System.out.println("参数46--流量修正点7(m/s)=="
//				+ Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0033";
//		parameter.count = "0001";
//		parameter.name = "流量修正点7(m/s)";
//		parameter.type = 2;
//		parameter.point = 3;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数47--流量修正值7 **************************************/
//		param = msg.substring(214, 218);
//		System.out.println("参数47--流量修正值7==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0034";
//		parameter.count = "0001";
//		parameter.name = "流量修正值7";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数48--流量修正点8(m/s) **************************************/
//		param = msg.substring(218, 222);
//		System.out.println("参数48--流量修正点8(m/s)=="
//				+ Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0035";
//		parameter.count = "0001";
//		parameter.name = "流量修正点8(m/s)";
//		parameter.type = 2;
//		parameter.point = 3;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数49--励磁方式选择 **************************************/
//		param = msg.substring(222, 226);
//		System.out.println("参数49--励磁方式选择==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0036";
//		parameter.count = "0001";
//		parameter.name = "励磁方式选择";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "1/16工频";
//				break;
//			case 1 :
//				parameter.value = "1/20工频";
//				break;
//			case 2 :
//				parameter.value = "1/25工频";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "1/16工频";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1/20工频";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1/25工频";
//		selector.value = "0002";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数50--励磁电流 **************************************/
//		param = msg.substring(226, 230);
//		System.out.println("参数50--励磁电流==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0037";
//		parameter.count = "0001";
//		parameter.name = "励磁电流";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "100mA";
//				break;
//			case 1 :
//				parameter.value = "250mA";
//				break;
//			case 2 :
//				parameter.value = "300mA";
//				break;
//			case 3 :
//				parameter.value = "500mA";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "100mA";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "250mA";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "350mA";
//		selector.value = "0002";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "500mA";
//		selector.value = "0003";
//		selectorList.add(selector);
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数51--尖峰抑制允许 **************************************/
//		param = msg.substring(230, 234);
//		System.out.println("参数51--尖峰抑制允许==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0038";
//		parameter.count = "0001";
//		parameter.name = "尖峰抑制允许";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "允许";
//				break;
//			case 1 :
//				parameter.value = "禁止";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "允许";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "禁止";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数52--尖峰抑制系数 **************************************/
//		param = msg.substring(234, 238);
//		System.out.println("参数52--尖峰抑制系数==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0039";
//		parameter.count = "0001";
//		parameter.name = "尖峰抑制系数";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "0.010m/s";
//				break;
//			case 1 :
//				parameter.value = "0.020m/s";
//				break;
//			case 2 :
//				parameter.value = "0.030m/s";
//				break;
//			case 3 :
//				parameter.value = "0.050m/s";
//				break;
//			case 4 :
//				parameter.value = "0.080m/s";
//				break;
//			case 5 :
//				parameter.value = "0.100m/s";
//				break;
//			case 6 :
//				parameter.value = "0.200m/s";
//				break;
//			case 7 :
//				parameter.value = "0.300m/s";
//				break;
//			case 8 :
//				parameter.value = "0.500m/s";
//				break;
//			case 9 :
//				parameter.value = "0.800m/s";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "0.010m/s";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.020m/s";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.030m/s";
//		selector.value = "0002";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.050m/s";
//		selector.value = "0003";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.080m/s";
//		selector.value = "0004";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.100m/s";
//		selector.value = "0005";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.200m/s";
//		selector.value = "0006";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.300m/s";
//		selector.value = "0007";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.500m/s";
//		selector.value = "0008";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "0.800m/s";
//		selector.value = "0009";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数53--尖峰抑制时间(ms) **************************************/
//		param = msg.substring(238, 242);
//		System.out
//				.println("参数53--尖峰抑制时间(ms)==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "003A";
//		parameter.count = "0001";
//		parameter.name = "尖峰抑制时间(ms)";
//		parameter.type = 1;
//		parameter.valueIn = Integer.parseInt(param, 16);
//		switch ((int) parameter.valueIn) {
//			case 0 :
//				parameter.value = "400ms";
//				break;
//			case 1 :
//				parameter.value = "600ms";
//				break;
//			case 2 :
//				parameter.value = "800ms";
//				break;
//			case 3 :
//				parameter.value = "1000ms";
//				break;
//			case 4 :
//				parameter.value = "2500ms";
//				break;
//		}
//		selectorList = new ArrayList<>();
//
//		selector = new Selector();
//		selector.name = "400ms";
//		selector.value = "0000";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "600ms";
//		selector.value = "0001";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "800ms";
//		selector.value = "0002";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "1000ms";
//		selector.value = "0003";
//		selectorList.add(selector);
//
//		selector = new Selector();
//		selector.name = "2500ms";
//		selector.value = "0004";
//		selectorList.add(selector);
//
//		parameter.selectors = selectorList;
//		mList.add(parameter);
//		/********************************** 参数54--传感器编码 **************************************/
//		param = msg.substring(242, 250);
//		System.out.println("参数54--传感器编码==" + NumberBytes.hexStrToLong(msg.substring(246, 250)+msg.substring(242, 246)));
//		parameter = new Parameter();
//		parameter.address = "003B";
//		parameter.count = "0002";
//		parameter.name = "传感器编码";
//		parameter.type = 3;
//		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(246, 250)+msg.substring(242, 246));
//		parameter.value = NumberBytes.hexStrToLong(msg.substring(246, 250)+msg.substring(242, 246)) + "";
//		mList.add(parameter);
//		/********************************** 参数55--传感器参数密码 **************************************/
//		param = msg.substring(250, 258);
//		System.out.println("参数55--传感器参数密码==" + NumberBytes.hexStrToLong(msg.substring(254, 258)+msg.substring(250, 254)));
//		parameter = new Parameter();
//		parameter.address = "003D";
//		parameter.count = "0002";
//		parameter.name = "传感器参数密码";
//		parameter.type = 3;
//		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(254, 258)+msg.substring(250, 254));
//		parameter.value = NumberBytes.hexStrToLong(msg.substring(254, 258)+msg.substring(250, 254)) + "";
//		mList.add(parameter);
//		/********************************** 转换器参数 **************************************/
//		/********************************** 参数56--转换器标定系数 **************************************/
//		param = msg.substring(258, 262);
//		System.out.println("参数56--转换器标定系数==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "003F";
//		parameter.count = "0001";
//		parameter.name = "转换器标定系数";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数57--电流零点修正 **************************************/
//		param = msg.substring(262, 266);
//		System.out.println("参数57--电流零点修正==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0040";
//		parameter.count = "0001";
//		parameter.name = "电流零点修正";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数58--电流满度修正 **************************************/
//		param = msg.substring(266, 270);
//		System.out.println("参数58--电流满度修正==" + Short.parseShort(param, 16));
//		parameter = new Parameter();
//		parameter.address = "0041";
//		parameter.count = "0001";
//		parameter.name = "电流满度修正";
//		parameter.type = 2;
//		parameter.point = 4;
//		parameter.valueIn = Short.parseShort(param, 16)
//				/ Math.pow(10, parameter.point);
//		parameter.value = String.format("%." + parameter.point + "f",
//				(double) parameter.valueIn);
//		mList.add(parameter);
//		/********************************** 参数59--仪表编码 **************************************/
//		param = msg.substring(270, 278);
//		System.out.println("参数59--仪表编码==" + NumberBytes.hexStrToLong(msg.substring(274, 278)+msg.substring(270, 274)));
//		parameter = new Parameter();
//		parameter.address = "0042";
//		parameter.count = "0002";
//		parameter.name = "仪表编码";
//		parameter.type = 3;
//		parameter.valueIn = NumberBytes.hexStrToLong(msg.substring(274, 278)+msg.substring(270, 274));
//		parameter.value = NumberBytes.hexStrToLong(msg.substring(274, 278)+msg.substring(270, 274)) + "";
//		mList.add(parameter);
//		
//
//		switch (AppStaticVar.PASSWORD_LEVEAL) {
//			case 0 :// 仅仅查看设置
//				mCount = mList.size();
//				break;
//			case 1 :// 可以设置1-24
//				mCount = 24;
//				break;
//			case 2 :// 可以设置1-25
//				mCount = 25;
//				break;
//			case 3 :// 可以设置1-38
//				mCount = 38;
//				break;
//			case 4 :// 可以设置1-52
//
//				mCount = 52;
//				break;
//			case 5 :// 可以设置全部
//				mCount = mList.size();
//				break;
//		}
//		mDataList.clear();
//		mDataList.addAll(mList.subList(0, mCount));
//		mAdapter.notifyDataSetChanged();
//	}
//	@Override
//	protected void onResume() {
//		// 启动接受数据
////		mReadThread = new ReadThread(mHandler, 500, getClass()
////				.getSimpleName());
////		mReadThread.start();
//		AppStaticVar.mReadThread.setHandler(mHandler);
//		super.onResume();
//	}

	@Override
	protected void onDestroy() {
//		if (mReadThread != null) {
//			mReadThread.interrupt();
//			mReadThread = null;
//		}
//		stopTimer();
		AppStaticVar.PASSWORD_LEVEAL = -1;
		super.onDestroy();
	}
}

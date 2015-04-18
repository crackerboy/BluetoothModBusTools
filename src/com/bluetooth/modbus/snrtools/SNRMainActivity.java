package com.bluetooth.modbus.snrtools;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bluetooth.modbus.snrtools.manager.AppStaticVar;
import com.bluetooth.modbus.snrtools.uitls.ModbusUtils;
import com.bluetooth.modbus.snrtools.uitls.NumberBytes;
import com.bluetooth.modbus.snrtools.view.NoFocuseTextview;

public class SNRMainActivity extends BaseActivity {

	private Handler mHandler;
	private Thread mThread;
	private TextView mParam1, mParam2, mParam3, mParam4, mParam5, mParam6,
			mParam7;
	private NoFocuseTextview mTvAlarm;
	private boolean isPause = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snr_main_activity);
		initUI();
		setTitleContent(AppStaticVar.mCurrentName);
		// hideRightView(R.id.btnRight1);
		setRightButtonContent("����", R.id.btnRight1);
		hideRightView(R.id.view2);
		initHandler();
	}

	@Override
	public void rightButtonOnClick(int id) {
		switch (id) {
			case R.id.btnRight1 :
				isPause = true;
				break;
		}
	}


	private void startReadParam() {
		mThread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (!isPause) {
					ModbusUtils.readStatus(mContext.getClass().getSimpleName(),
							mHandler);
				}
			}
		});
		mThread.start();
	}

	private void initUI() {
		mParam1 = (TextView) findViewById(R.id.param1);
		mParam2 = (TextView) findViewById(R.id.param2);
		mParam3 = (TextView) findViewById(R.id.param3);
		mParam4 = (TextView) findViewById(R.id.param4);
		mParam5 = (TextView) findViewById(R.id.param5);
		mParam6 = (TextView) findViewById(R.id.param6);
		mParam7 = (TextView) findViewById(R.id.param7);
		mTvAlarm = (NoFocuseTextview) findViewById(R.id.tvAlarm);
		mTvAlarm.setVisibility(View.GONE);
		mTvAlarm.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_alpha));
	}

	private void hasAlarm(String s) {
		if (mTvAlarm.getVisibility() != View.VISIBLE) {
			mTvAlarm.setVisibility(View.VISIBLE);
		}
		if (!mTvAlarm.getText().toString().contains(s)) {
			mTvAlarm.setText(mTvAlarm.getText() + " " + s);
		}
	}

	private void hasNoAlarm(String s) {
		mTvAlarm.setText(mTvAlarm.getText().toString().replace(" " + s, ""));
		if (TextUtils.isEmpty(mTvAlarm.getText().toString().trim())) {
			mTvAlarm.setVisibility(View.GONE);
		}
	}

	private String getSsllDw(String s) {
		System.out.println("˲ʱ������λ====" + s);
		String dw = "";
		s = s.replace("0", "");
		if ("".equals(s)) {
			dw = "L/h";
		} else if ("1".equals(s)) {
			dw = "L/m";
		} else if ("2".equals(s)) {
			dw = "L/s";
		} else if ("3".equals(s)) {
			dw = "m3/h";
		} else if ("4".equals(s)) {
			dw = "m3/m";
		} else if ("5".equals(s)) {
			dw = "m3/s";
		}
		return dw;
	}

	private String getZFDw(String s) {
		System.out.println("�����ۻ���λ====" + s);
		String dw = "";
		s = s.replace("0", "");
		if ("".equals(s)) {
			dw = "L";
		} else if ("1".equals(s)) {
			dw = "";
		} else if ("2".equals(s)) {
			dw = "";
		} else if ("3".equals(s)) {
			dw = "m3";
		}
		return dw;
	}

	private void dealReturnMsg(String msg) {
		if (msg.length() != 126) {
			return;
		}
		// ˲ʱ��������ֵ
		String ssllH = msg.substring(10, 14);
		String ssllL = msg.substring(6, 10);
		String sslldw = msg.substring(70, 74);
		System.out.println("˲ʱ����==" + NumberBytes.hexStrToFloat(ssllH + ssllL));
		System.out.println("˲ʱ������λ==" + sslldw);
		String ssllT = NumberBytes.hexStrToFloat(ssllH + ssllL) + " "
				+ getSsllDw(sslldw);
		mParam1.setText(ssllT);
		// ˲ʱ���ٸ���ֵ
		String sslsH = msg.substring(18, 22);
		String sslsL = msg.substring(14, 18);
		System.out.println("˲ʱ����==" + NumberBytes.hexStrToFloat(sslsH + sslsL));
		String sslsT = NumberBytes.hexStrToFloat(sslsH + sslsL) + " m/s";
		mParam2.setText(sslsT);
		// �����ٷֱȸ���ֵ
		String llbfbH = msg.substring(26, 30);
		String llbfbL = msg.substring(22, 26);
		System.out.println("�����ٷֱ�=="
				+ NumberBytes.hexStrToFloat(llbfbH + llbfbL));
		String llbfbT = NumberBytes.hexStrToFloat(llbfbH + llbfbL) + " %";
		mParam3.setText(llbfbT);
		// ����絼�ȸ���ֵ
		String ltddbH = msg.substring(34, 38);
		String ltddbL = msg.substring(30, 34);
		System.out.println("����絼��=="
				+ NumberBytes.hexStrToFloat(ltddbH + ltddbL));
		String ltddbT = NumberBytes.hexStrToFloat(ltddbH + ltddbL) + " %";
		mParam4.setText(ltddbT);
		// �����ۻ���ֵ����ֵ
		String zxljintH = msg.substring(42, 46);
		String zxljintL = msg.substring(38, 42);
		String zxljIntString = Long.parseLong(zxljintH + zxljintL, 16) + "";
		System.out.println("�����ۻ���ֵ����ֵ=="
				+ Long.parseLong(zxljintH + zxljintL, 16));
		// �����ۻ���ֵС��ֵ
		String zxljfloatH = msg.substring(50, 54);
		String zxljfloatL = msg.substring(46, 50);
		System.out.println("�����ۻ���ֵС��ֵ=="
				+ NumberBytes.hexStrToFloat(zxljfloatH + zxljfloatL));
		String zxljFloatString = NumberBytes.hexStrToFloat(zxljfloatH
				+ zxljfloatL)
				+ "";
		// �����ۻ���ֵ����ֵ
		String fxljintH = msg.substring(58, 62);
		String fxljintL = msg.substring(54, 58);
		System.out.println("�����ۻ���ֵ����ֵ=="
				+ Long.parseLong(fxljintH + fxljintL, 16));
		String fxljIntString = Long.parseLong(fxljintH + fxljintL, 16) + "";
		// �����ۻ���ֵС��ֵ
		String fxljfloatH = msg.substring(66, 70);
		String fxljfloatL = msg.substring(62, 66);
		System.out.println("�����ۻ���ֵС��ֵ=="
				+ NumberBytes.hexStrToFloat(fxljfloatH + fxljfloatL));
		String fxljFloatString = NumberBytes.hexStrToFloat(fxljfloatH
				+ fxljfloatL)
				+ "";
		// ���򣬷����ۻ���λ
		String ljdw = msg.substring(74, 78);
		System.out.println("���򣬷����ۻ���λ==" + ljdw);
		String zxljT = zxljIntString
				+ zxljFloatString.substring(zxljFloatString.indexOf(".")) + " "
				+ getZFDw(ljdw);
		mParam5.setText(zxljT);
		String fxljt = fxljIntString
				+ fxljFloatString.substring(fxljFloatString.indexOf(".")) + " "
				+ getZFDw(ljdw);
		mParam6.setText(fxljt);

		mParam7.setText(String.format(
				"%.3f",
				Long.parseLong(fxljintH + fxljintL, 16)
						+ NumberBytes.hexStrToFloat(fxljfloatH + fxljfloatL)
						- Long.parseLong(fxljintH + fxljintL, 16)
						- NumberBytes.hexStrToFloat(fxljfloatH + fxljfloatL))
				+ " " + getZFDw(ljdw));

		// �������ޱ���
		String llsxbj = msg.substring(78, 82);
		System.out.println("�������ޱ���==" + llsxbj);
		if (Integer.parseInt(llsxbj) == 1) {
			hasAlarm("��������");
		} else {
			hasNoAlarm("��������");
		}
		// �������ޱ���
		String llxxbj = msg.substring(82, 86);
		System.out.println("�������ޱ���==" + llxxbj);
		if (Integer.parseInt(llxxbj) == 1) {
			hasAlarm("��������");
		} else {
			hasNoAlarm("��������");
		}
		// �����쳣����
		String lcbj = msg.substring(86, 90);
		System.out.println("�����쳣����==" + lcbj);
		if (Integer.parseInt(lcbj) == 1) {
			hasAlarm("�����쳣�����쳣�����쳣");
		} else {
			hasNoAlarm("�����쳣�����쳣�����쳣");
		}
		// �չܱ���
		String kgbj = msg.substring(90, 94);
		System.out.println("�չܱ���==" + kgbj);
		if (Integer.parseInt(kgbj) == 1) {
			hasAlarm("�չܱ���");
		} else {
			hasNoAlarm("�չܱ���");
		}

	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case Constans.CONTACT_START :
						System.out.println("��ҳ�濪ʼ��ȡ����=====");
						break;
					case Constans.NO_DEVICE_CONNECTED :
						System.out.println("��ҳ������ʧ��=====");
						break;
					case Constans.DEVICE_RETURN_MSG :
						System.out.println("��ҳ���յ�����=====" + msg.obj.toString());
						dealReturnMsg(msg.obj.toString());
						if (isPause) {
							Intent setting = new Intent(mContext,
									CheckPasswordActivity.class);
							startActivity(setting);
						} else {
							startReadParam();
						}
						break;
					case Constans.CONNECT_IS_CLOSED :
						System.out.println("��ҳ�����ӹر�=====");
						isPause = true;
						showConnectDevice();
					case Constans.ERROR_START :
						System.out.println("��ҳ��������ݴ���=====");
						startReadParam();
						break;
					case Constans.TIME_OUT :
						System.out.println("��ҳ�����ӳ�ʱ=====");
						if (mThread != null && !mThread.isInterrupted()) {
							mThread.interrupt();
						}
						startReadParam();
						break;
				}
			}
		};
	}

	@Override
	public void reconnectSuccss() {
		isPause = false;
		startReadParam();
	}

	@Override
	protected void onResume() {
		isPause = false;
		startReadParam();
		super.onResume();
	}

	@Override
	protected void onPause() {
		isPause = true;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		AppStaticVar.mCurrentAddress = null;
		AppStaticVar.mCurrentName = null;
		if (AppStaticVar.mSocket != null) {
			try {
				AppStaticVar.mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			AppStaticVar.mSocket = null;
		}
		super.onDestroy();
	}
}

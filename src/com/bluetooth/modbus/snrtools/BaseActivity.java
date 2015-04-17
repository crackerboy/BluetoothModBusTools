package com.bluetooth.modbus.snrtools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetooth.modbus.snrtools.manager.ActivityManager;
import com.bluetooth.modbus.snrtools.manager.AppStaticVar;
import com.bluetooth.modbus.snrtools.thread.ConnectThread;

public abstract class BaseActivity extends Activity {

	public Context mContext;
	private ProgressDialog mPDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ActivityManager.getInstances().addActivity(this);
		mContext = this;
	}

	public void BackOnClick(View v) {
		switch (v.getId()) {
			case R.id.ivBack :
				ActivityManager.getInstances().finishActivity(this);
		}
	}

	public void BtnRight(View v) {
		rightButtonOnClick(v.getId());
	}

	/**
	 * �������Ի���dialog ��ȷ�ϣ�ȡ����.
	 *
	 * @param title �Ի����������
	 * @param view  �Ի�����ʾ����
	 * @param mOkOnClickListener  ���ȷ�ϰ�ť���¼�����
	 * @return the alert dialog
	 */
	public AlertDialog showDialog(String title,View view,DialogInterface.OnClickListener mOkOnClickListener) {
		 AlertDialog.Builder builder = new Builder(this);
		 builder.setTitle(title);
		 builder.setView(view);
		 builder.setPositiveButton("ȷ��",mOkOnClickListener);
		 builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
				   dialog.dismiss();
			   }
		 });
		 AlertDialog mAlertDialog  = builder.create();
		 mAlertDialog.show();
		 return mAlertDialog;
	}
	
	/**
	 * �������Ի���dialog ���ް�ť��.
	 *
	 * @param title �Ի����������
	 * @param view  �Ի�����ʾ����
	 * @return the alert dialog
	 */
	public AlertDialog showDialog(String title,View view) {
		 AlertDialog.Builder builder = new Builder(this);
		 builder.setTitle(title);
		 builder.setView(view);
		 builder.create();
		 AlertDialog mAlertDialog  = builder.create();
		 mAlertDialog.show();
		 return mAlertDialog;
	}
	
	public void showToast(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * �����Ҳఴť�Ĵ����¼�(xml�����ļ��б�������base_title.xml) rlMenu--�˵���ťid
	 * ��btnRight1--���ְ�ťid��view2--�Ҳ�ָ���id
	 * 
	 * @author: cchen
	 * @time: 2014-12-26 ����10:44:17
	 * @param id
	 *            ��ť��id
	 */
	protected void rightButtonOnClick(int id) {
	}

	/**
	 * �����Ҳఴť���߷ָ���(xml�����ļ��б�������base_title.xml)
	 * 
	 * @author: cchen
	 * @time: 2014-12-26 ����10:47:06
	 * @param id
	 *            ��Ҫ���ص�view��id
	 */
	public void hideRightView(int id) {
		View v = findViewById(id);
		if (v != null) {
			v.setVisibility(View.GONE);
		}
	}

	/**
	 * չʾ�Ҳఴť���߷ָ���(xml�����ļ��б�������base_title.xml)
	 * 
	 * @author: cchen
	 * @time: 2014-12-26 ����10:47:06
	 * @param id
	 *            ��Ҫ���ص�view��id
	 */
	public void showRightView(int id) {
		View v = findViewById(id);
		if (v != null) {
			v.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ����title������(xml�����ļ��б�������base_title.xml)
	 * 
	 * @author: cchen
	 * @time: 2014-12-26 ����10:49:52
	 * @param content
	 */
	public void setTitleContent(String content) {
		View v = findViewById(R.id.tvTitle);
		if (v != null && v instanceof TextView) {
			((TextView) v).setText(content);
		}
	}

	/**
	 * �����Ҳ�button������(xml�����ļ��б�������base_title.xml)
	 * 
	 * @author: cchen
	 * @time: 2014-12-26 ����10:49:52
	 * @param content
	 * @param id
	 *            ��Ҫ���õ�button��id
	 */
	public void setRightButtonContent(String content, int id) {
		View v = findViewById(id);
		if (v != null && v instanceof Button) {
			((Button) v).setText(content);
		}
	}

	public void showProgressDialog() {
		showProgressDialog(null, null);
	}

	public void showProgressDialog(String msg) {
		showProgressDialog(null, msg);
	}

	public void showProgressDialog(String title, String msg) {
		if (mPDialog == null) {
			mPDialog = new ProgressDialog(mContext);
		}
		if (!TextUtils.isEmpty(title)) {
			mPDialog.setTitle(title);
		}
		if (!TextUtils.isEmpty(msg)) {
			mPDialog.setMessage(msg);
		}
		mPDialog.show();
	}

	public abstract void reconnectSuccss();
	private AlertDialog dialog;
	
	public void showConnectDevice() {
		if(dialog == null){
			dialog = new AlertDialog.Builder(this)
			.setMessage("�豸�����ѶϿ����Ƿ��������ӣ�")
			.setPositiveButton("����", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					final BluetoothDevice device = AppStaticVar.mBtAdapter
							.getRemoteDevice(AppStaticVar.mCurrentAddress);
					ConnectThread connectThread = new ConnectThread(device,
							new Handler() {
								@Override
								public void handleMessage(Message msg) {
									switch (msg.what) {
										case Constans.CONNECTING_DEVICE :
											showProgressDialog(msg.obj
													.toString());
											break;
										case Constans.CONNECT_DEVICE_SUCCESS :
											hideProgressDialog();
											reconnectSuccss();
											break;
										case Constans.CONNECT_DEVICE_FAILED :
											hideProgressDialog();
											showToast(msg.obj.toString());
											break;
									}
								}
							});
					connectThread.start();
				}
			})
			.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).create();
		}
		if(!dialog.isShowing()){
			dialog.show();
		}
		
	}

	public void hideProgressDialog() {
		if (mPDialog != null && mPDialog.isShowing()) {
			mPDialog.dismiss();
		}
	}

	@Override
	protected void onPause() {
		hideProgressDialog();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		hideProgressDialog();
		super.onDestroy();
	}
}

package com.bluetooth.modbus.snrtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ab.http.AbFileHttpResponseListener;
import com.ab.http.AbHttpUtil;
import com.ab.view.progress.AbHorizontalProgressBar;
import com.bluetooth.modbus.snrtools.adapter.DeviceListAdapter;
import com.bluetooth.modbus.snrtools.bean.SiriListItem;
import com.bluetooth.modbus.snrtools.manager.AppStaticVar;
import com.bluetooth.modbus.snrtools.uitls.AppUtil;
import com.bluetooth.modbus.snrtools.view.MyAlertDialog.MyAlertDialogListener;

public class SelectDeviceActivity extends BaseActivity
{

	private static final String NO_DEVICE_CAN_CONNECT = "û�п������ӵ��豸";
	private ListView mListView;
	private ArrayList<SiriListItem> list;
	private DeviceListAdapter mAdapter;
	private PopupWindow mPop;
	private AbHorizontalProgressBar mAbProgressBar;
	// ���100
	private int max = 100;
	private int progress = 0;
	private TextView numberText, maxText;
	private AlertDialog mAlertDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAbHttpUtil = AbHttpUtil.getInstance(this);
		setTitleContent("ѡ���豸");
		hideRightView(R.id.view2);
		setRightButtonContent("����", R.id.btnRight1);
		init();
		showRightView(R.id.rlMenu);
	}

	@Override
	public void reconnectSuccss()
	{
		Intent intent = new Intent(mContext, SNRMainActivity.class);
		startActivity(intent);
	}

	private void init()
	{
		list = new ArrayList<SiriListItem>();
		mAdapter = new DeviceListAdapter(this, list);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setFastScrollEnabled(true);
		mListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				SiriListItem item = list.get(arg2);
				String info = item.getMessage();
				if (NO_DEVICE_CAN_CONNECT.equals(info))
				{
					return;
				}
				String address = info.substring(info.length() - 17);
				String name = info.substring(0, info.length() - 17);
				AppStaticVar.mCurrentAddress = address;
				AppStaticVar.mCurrentName = name;

				showDialog("�Ƿ�����" + item.getMessage(), new MyAlertDialogListener()
				{

					@Override
					public void onClick(View view)
					{
						switch (view.getId())
						{
							case R.id.btnCancel:
								AppStaticVar.mCurrentAddress = null;
								AppStaticVar.mCurrentName = null;
								hideDialog();
								break;
							case R.id.btnOk:
								setRightButtonContent("����", R.id.btnRight1);
								connectDevice(AppStaticVar.mCurrentAddress);
								break;
						}
					}
				});
			}
		});

		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
	}

	@Override
	protected void rightButtonOnClick(int id)
	{
		switch (id)
		{
			case R.id.btnRight1:
				searchDevice();
				break;
			case R.id.ivMenu:
				showMenu(findViewById(id));
				break;
		}
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.textView1:
				hideMenu();
				downloadXml();
				break;
		}
	}

	private void showMenu(View v)
	{
		if (mPop == null)
		{
			View contentView = View.inflate(this, R.layout.main_menu, null);
			mPop = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mPop.setBackgroundDrawable(new BitmapDrawable());
			mPop.setOutsideTouchable(true);
			mPop.setFocusable(true);
		}
		mPop.showAsDropDown(v, R.dimen.menu_x, 15);
	}

	private void hideMenu()
	{
		if (mPop != null && mPop.isShowing())
		{
			mPop.dismiss();
		}
	}

	private void downloadXml()
	{
		String url = "http://192.168.1.100:18086/snrtools/version.xml";
		mAbHttpUtil.get(url, new AbFileHttpResponseListener(url)
		{
			// ��ȡ���ݳɹ����������
			@Override
			public void onSuccess(int statusCode, File file)
			{
				int version = 0;
				String url = "";
				String md5 = "";
				XmlPullParser xpp = Xml.newPullParser();
				try
				{
					xpp.setInput(new FileInputStream(file), "utf-8");

					int eventType = xpp.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT)
					{
						switch (eventType)
						{
							case XmlPullParser.START_TAG:
								if ("version".equals(xpp.getName()))
								{
									try
									{
										version = Integer.parseInt(xpp.nextText());
									}
									catch (NumberFormatException e1)
									{
										e1.printStackTrace();
										showToast("���������°汾�ų���");
									}
								}
								if ("url".equals(xpp.getName()))
								{
									url = xpp.nextText();
								}
								if ("MD5".equals(xpp.getName()))
								{
									md5 = xpp.nextText();
								}
								break;
							default:
								break;
						}
						eventType = xpp.next();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				PackageManager manager;
				PackageInfo info = null;
				manager = getPackageManager();
				try
				{
					info = manager.getPackageInfo(getPackageName(), 0);
				}
				catch (NameNotFoundException e)
				{
					e.printStackTrace();
				}
				if (version > info.versionCode)
				{
					String fileName = url.substring(url.lastIndexOf("/") + 1);
					File apk = new File(Constans.Directory.DOWNLOAD + fileName);
					if (md5.equals(AppUtil.getFileMD5(apk)))
					{
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
						startActivity(intent);
						return;
					}
					try
					{
						if (!apk.getParentFile().exists())
						{
							apk.getParentFile().mkdirs();
						}
						apk.createNewFile();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					mAbHttpUtil.get(url, new AbFileHttpResponseListener(apk)
					{
						public void onSuccess(int statusCode, File file)
						{
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
							startActivity(intent);
						};

						// ��ʼִ��ǰ
						@Override
						public void onStart()
						{
							// �򿪽��ȿ�
							View v = LayoutInflater.from(mContext).inflate(R.layout.progress_bar_horizontal, null,
									false);
							mAbProgressBar = (AbHorizontalProgressBar) v.findViewById(R.id.horizontalProgressBar);
							numberText = (TextView) v.findViewById(R.id.numberText);
							maxText = (TextView) v.findViewById(R.id.maxText);

							maxText.setText(progress + "/" + String.valueOf(max));
							mAbProgressBar.setMax(max);
							mAbProgressBar.setProgress(progress);

							mAlertDialog = showDialog("��������", v);
						}

						// ʧ�ܣ�����
						@Override
						public void onFailure(int statusCode, String content, Throwable error)
						{
							showToast(error.getMessage());
						}

						// ���ؽ���
						@Override
						public void onProgress(long bytesWritten, long totalSize)
						{
							maxText.setText(bytesWritten / (totalSize / max) + "/" + max);
							mAbProgressBar.setProgress((int) (bytesWritten / (totalSize / max)));
						}

						// ��ɺ���ã�ʧ�ܣ��ɹ�
						public void onFinish()
						{
							// �������ȡ�����ȿ�
							if (mAlertDialog != null)
							{
								mAlertDialog.cancel();
								mAlertDialog = null;
							}

						};
					});
				}
				else
				{
					showToast("�Ѿ������°棡");
				}

			}

			// ��ʼִ��ǰ
			@Override
			public void onStart()
			{
				// �򿪽��ȿ�
				View v = LayoutInflater.from(mContext).inflate(R.layout.progress_bar_horizontal, null, false);
				mAbProgressBar = (AbHorizontalProgressBar) v.findViewById(R.id.horizontalProgressBar);
				numberText = (TextView) v.findViewById(R.id.numberText);
				maxText = (TextView) v.findViewById(R.id.maxText);

				maxText.setText(progress + "/" + String.valueOf(max));
				mAbProgressBar.setMax(max);
				mAbProgressBar.setProgress(progress);

				mAlertDialog = showDialog("��������", v);
			}

			// ʧ�ܣ�����
			@Override
			public void onFailure(int statusCode, String content, Throwable error)
			{
				showToast(error.getMessage());
			}

			// ���ؽ���
			@Override
			public void onProgress(long bytesWritten, long totalSize)
			{
				maxText.setText(bytesWritten / (totalSize / max) + "/" + max);
				mAbProgressBar.setProgress((int) (bytesWritten / (totalSize / max)));
			}

			// ��ɺ���ã�ʧ�ܣ��ɹ�
			public void onFinish()
			{
				// �������ȡ�����ȿ�
				if (mAlertDialog != null)
				{
					mAlertDialog.cancel();
					mAlertDialog = null;
				}
			};
		});
	}

	private void searchDevice()
	{
		if (AppStaticVar.mBtAdapter.isDiscovering())
		{
			AppStaticVar.mBtAdapter.cancelDiscovery();
			setRightButtonContent("����", R.id.btnRight1);
		}
		else
		{
			showProgressDialog("�豸������...",true);
			list.clear();
			mAdapter.notifyDataSetChanged();

			Set<BluetoothDevice> pairedDevices = AppStaticVar.mBtAdapter.getBondedDevices();
			if (pairedDevices.size() > 0)
			{
				for (BluetoothDevice device : pairedDevices)
				{
					list.add(new SiriListItem(device.getName() + "\n" + device.getAddress(), true));
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(list.size() - 1);
				}
			}
			else
			{
				list.add(new SiriListItem(NO_DEVICE_CAN_CONNECT, true));
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(list.size() - 1);
			}
			/* ��ʼ���� */
			AppStaticVar.mBtAdapter.startDiscovery();
			setRightButtonContent("ֹͣ", R.id.btnRight1);
		}
	}

	@Override
	public void onBackPressed()
	{
		showDialog("�Ƿ�Ҫ�˳�����", new MyAlertDialogListener()
		{
			@Override
			public void onClick(View view)
			{
				switch (view.getId())
				{
					case R.id.btnCancel:
						hideDialog();
						break;
					case R.id.btnOk:
						AppUtil.closeBluetooth();
						finish();
						break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume()
	{
		if (AppUtil.checkBluetooth(mContext))
		{
			searchDevice();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		this.unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action))
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED)
				{
					list.add(new SiriListItem(device.getName() + "\n" + device.getAddress(), false));
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(list.size() - 1);
				}
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
			{
				hideProgressDialog();
				setProgressBarIndeterminateVisibility(false);
				if (mListView.getCount() == 0)
				{
					list.add(new SiriListItem("û�з��������豸", false));
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(list.size() - 1);
				}
				setRightButtonContent("����", R.id.btnRight1);
			}
		}
	};
}

package com.bluetooth.modbus.snrtools.uitls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.bluetooth.modbus.snrtools.Constans;
import com.bluetooth.modbus.snrtools.bean.CommandRead;
import com.bluetooth.modbus.snrtools.bean.CommandWrite;
import com.bluetooth.modbus.snrtools.common.CRC16;
import com.bluetooth.modbus.snrtools.email.MailSenderInfo;
import com.bluetooth.modbus.snrtools.email.SimpleMailSender;
import com.bluetooth.modbus.snrtools.manager.AppStaticVar;

public class AppUtil {
	
	/**
	 * ����Ƿ��Ѿ��������������û�п����ᵯ��������������
	 * 
	 * @param context
	 */
	public static boolean checkBluetooth(Context context) {
		// If BT is not on, request that it be enabled.
		if (AppStaticVar.mBtAdapter == null) {
			AppStaticVar.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		if (AppStaticVar.mBtAdapter == null) {
			Toast.makeText(context, "δ��⵽�����豸���뿪�����������ԣ�", Toast.LENGTH_LONG).show();
			return false;
		}
		if (!AppStaticVar.mBtAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			context.startActivity(enableIntent);
			return false;
		}
		return true;
	}

	public static void closeBluetooth() {
		if (AppStaticVar.mBtAdapter != null)
			AppStaticVar.mBtAdapter.disable();
	}

	/**
	 * msg.what ==Constans.NO_DEVICE_CONNECTED ���豸����ʧ�ܣ��뷵���������ӣ� msg.what
	 * ==Constans.CONNECT_IS_JIM ���豸ͨѶ������ͨѶʧ�ܣ�
	 * 
	 * @param handler
	 * @param write
	 */
	public static void modbusWrite(String className, final Handler handler, CommandRead command, int waittime) {
		synchronized (AppStaticVar.locks) {
			System.out.println("=====" + className);
			Message msg = new Message();
			msg.what = Constans.CONTACT_START;
			msg.obj = "��ʼ�������";
			handler.sendMessage(msg);
			if (AppStaticVar.mSocket == null) {
				Message message = new Message();
				message.what = Constans.NO_DEVICE_CONNECTED;
				message.obj = "���豸����ʧ�ܣ��뷵���������ӣ�";
				handler.sendMessage(message);
				return;
			}
			try {
				OutputStream os = AppStaticVar.mSocket.getOutputStream();
				int count = 0;
				if (command instanceof CommandWrite) {
					count = 6 + 1 + ((CommandWrite) command).getContentMap().size();
				} else if (command instanceof CommandRead) {
					count = 6;
				}
				String[] totalTemp = new String[count];
				int i = 0;
				totalTemp[i++] = command.getDeviceId();
				totalTemp[i++] = command.getCommandNo();

				totalTemp[i++] = command.getStartAddressH();
				totalTemp[i++] = command.getStartAddressL();

				totalTemp[i++] = command.getCountH();
				totalTemp[i++] = command.getCountL();
				if (command instanceof CommandWrite) {
					totalTemp[i++] = ((CommandWrite) command).getByteCount();
					for (int j = 0; j < ((CommandWrite) command).getContentMap().size(); j++) {
						totalTemp[i++] = ((CommandWrite) command).getContentMap().get(j + "");
					}
				}
				String cmd = "";
				for (int ii = 0; ii < totalTemp.length; ii++) {
					cmd += totalTemp[ii];
				}
				System.out.println("======��������=====" + cmd);
				byte[] sendB = CRC16.getSendBuf2(totalTemp);
				synchronized (os) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					os.write(sendB);
					os.flush();
				}

			} catch (IOException e) {
				Message message = new Message();
				message.what = Constans.CONNECT_IS_JIM;
				message.obj = "���豸ͨѶ������ͨѶʧ�ܣ�";
				handler.sendMessage(message);
				e.printStackTrace();
				return;
			}

			final Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					Message message = new Message();
					message.what = Constans.TIME_OUT;
					message.obj = "���ӳ�ʱ��";
					handler.sendMessage(message);
					return;
				}
			}, waittime);

			Timer readtimer = new Timer();
			readtimer.schedule(new TimerTask() {

				@Override
				public void run() {
					InputStream mmInStream = null;
					try {
						if (AppStaticVar.mSocket == null) {
							return;
						}
						mmInStream = AppStaticVar.mSocket.getInputStream();
						byte[] buffer = new byte[10240];
						int bytes;
						if ((bytes = mmInStream.read(buffer)) != -1) {
							byte[] buf_data = new byte[bytes];
							for (int i = 0; i < bytes; i++) {
								buf_data[i] = buffer[i];
							}
							if (CRC16.checkBuf(buf_data)) {
								Message msg = new Message();
								msg.obj = CRC16.getBufHexStr(buf_data);
								msg.what = Constans.DEVICE_RETURN_MSG;
								if (handler != null)
									handler.sendMessage(msg);
							} else {
								System.out.println("==δͨ��CRCУ��==" + CRC16.getBufHexStr(buf_data));
								Message msg = new Message();
								msg.what = Constans.ERROR_START;
								if (handler != null)
									handler.sendMessage(msg);
							}
						}
					} catch (IOException e1) {
						try {
							if (mmInStream != null) {
								mmInStream.close();
							}
							Message msg = new Message();
							msg.obj = "�����Ѿ��Ͽ������������ӣ�";
							msg.what = Constans.CONNECT_IS_CLOSED;
							if (handler != null)
								handler.sendMessage(msg);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					} finally {
						timer.cancel();
					}
				}
			}, 1000);
		}
	}

	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	public static void sendEmail(String userid, String psd, String sender, String to, String title, String content) {

		try {
			final MailSenderInfo mailInfo = new MailSenderInfo();
			mailInfo.setMailServerHost("smtp.163.com");
			// mailInfo.setMailServerPort("25");
			mailInfo.setValidate(true);
			mailInfo.setUserName(userid); // ��������ַ
			mailInfo.setPassword(psd);// ������������
			mailInfo.setFromAddress(sender); // ���͵�����
			mailInfo.setToAddress(to); // �����ĸ��ʼ�ȥ
			mailInfo.setSubject(title);
			mailInfo.setContent("����һ�ݴ����ռ���־\n"+content);

			// �������Ҫ�������ʼ�
			final SimpleMailSender sms = new SimpleMailSender();
			new Thread(new Runnable() {

				@Override
				public void run() {
					sms.sendTextMail(mailInfo);// ���������ʽ
					// sms.sendHtmlMail(mailInfo);//����html��ʽ
				}
			}).start();

		} catch (Exception e) {
			Log.e("SendMail", e.getMessage(), e);
		}

	}
}

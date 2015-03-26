package cn.xyida.toggletest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallEndService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		CallEndReciver callEndReciver=new CallEndReciver();
//		IntentFilter filter=new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
//		registerReceiver(callEndReciver, filter);
		Log.e("yoda", "服务启动!");
		
		final TelephonyManager manager=(TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
		manager.listen(new PhoneStateListener(){
			
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// TODO Auto-generated method stub
				Log.e("yoda-onCallState", String.valueOf(state));
				super.onCallStateChanged(state, incomingNumber);
				if (state==TelephonyManager.CALL_STATE_IDLE) {
					Log.e("yoda", "挂断电话==>"+state);
					Log.e("yoda-networktype", String.valueOf(manager.getNetworkType()));
//							delay(2000);
							CallEndService.cmdMethod(NetWorkType.LTEONLY);
							Log.e("yoda", "切换LTE only");
							delay(5000);
							CallEndService.cmdMethod(NetWorkType.LTEGSMAUTO);
							Log.e("yoda", "切换LTE/GSM auto");
							Log.e("yoda-networktype", String.valueOf(manager.getNetworkType()));
				}
			}
			
		}, PhoneStateListener.LISTEN_CALL_STATE);
		
	}
	public void delay(int i){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("yoda", "CallEndService OnDestroy!");
		super.onDestroy();
		
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	/****
	 * 是否开启selinux
	 */
	public static boolean isOpenSElinux() {
		boolean flag = true;
		if (Build.VERSION.SDK_INT < 19) {
			Log.e("yoda", "系统低于4.4，滚粗");
			return false;
		}
		File f = new File("/sys/fs/selinux/enforce");
		if (f.exists()) {
			try {
				FileInputStream fiStream = new FileInputStream(f);

				if (fiStream.read() != 49) {
					return false;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("yoda", e.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("yoda", e.toString());

			}

			return true;
		}
		return flag;
	}

	public static void cmdMethod(NetWorkType networkType) {
		int type = 9;
		switch (networkType) {
		case LTEONLY:
			type = 11;
			break;

		case LTEGSMAUTO:
			type = 9;
			break;
		}
		DataOutputStream dataOutputStream = null;
		Process cmd = null;
		String[] command;
		if (isOpenSElinux()) {
			command = new String[] { "su", "-cn", "u:r:system_app:s0" };
		} else {
			command = new String[] { "su" };
		}

		try {
			cmd = Runtime.getRuntime().exec(command);
			dataOutputStream = new DataOutputStream(cmd.getOutputStream());
			dataOutputStream.writeBytes("service call phone 85 i32 " + type
					+ " ; " + "exit" + "\n");
			dataOutputStream.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("yoda", e.toString());
		} finally {
			if (dataOutputStream != null) {
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("yoda", e.toString());
				}
			}

		}

	}

}

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
		CallEndReciver callEndReciver=new CallEndReciver();
		IntentFilter filter=new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		registerReceiver(callEndReciver, filter);
		
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
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

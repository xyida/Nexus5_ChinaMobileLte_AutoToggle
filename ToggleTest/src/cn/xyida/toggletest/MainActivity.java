package cn.xyida.toggletest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

enum NetWorkType {
	LTEONLY, LTEGSMAUTO
}

public class MainActivity extends Activity implements OnClickListener {

	private Button lteOnlyBtn, lteGsmAutoBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prepareUI();

		TelephonyManager manager=(TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
		manager.listen(new PhoneStateListener(){

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// TODO Auto-generated method stub
				super.onCallStateChanged(state, incomingNumber);
				if (state==TelephonyManager.CALL_STATE_IDLE) {
					Log.e("yoda", "挂断电话");
				}
			}
			
		}, PhoneStateListener.LISTEN_CALL_STATE);
		
		
		
	}

	public void cmdMethod(NetWorkType networkType) {
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

	private void prepareUI() {
		// TODO Auto-generated method stub
		lteOnlyBtn = (Button) findViewById(R.id.button1);
		lteOnlyBtn.setOnClickListener(this);
		lteGsmAutoBtn = (Button) findViewById(R.id.button2);
		lteGsmAutoBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void changePreferNetWorkType(NetWorkType type) {
		getSystemService("phone");

		// Method setPrefNetmethod;
		// Class telephonyManagerClass;
		// Object ITelephonyStub;
		// Class ITelephonyClass;
		//
		// try {
		// TelephonyManager telephonyManager =
		// (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		// telephonyManagerClass =
		// Class.forName(telephonyManager.getClass().getName());
		// Method getITelephonyMethod =
		// telephonyManagerClass.getDeclaredMethod("getITelephony");
		// getITelephonyMethod.setAccessible(true);
		// ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
		// ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
		//
		// setPrefNetmethod =
		// ITelephonyClass.getDeclaredMethod("setPreferredNetworkType",new
		// Class[] { Integer.class, Message.class });
		//
		// Message response = Message.obtain();
		// setPrefNetmethod.setAccessible(false);
		//
		// setPrefNetmethod.invoke(ITelephonyStub, new Object[] { 11, response
		// });
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// Log.e("yoda",e.toString());
		// } catch (NoSuchMethodException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// Log.e("yoda",e.toString());
		//
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// Log.e("yoda",e.toString());
		//
		// } catch (IllegalArgumentException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// Log.e("yoda",e.toString());
		//
		// } catch (InvocationTargetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// Log.e("yoda",e.toString());
		//
		// }

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			// changePreferNetWorkType(NetWorkType.LTEONLY);
			cmdMethod(NetWorkType.LTEONLY);
			break;

		case R.id.button2:
			// changePreferNetWorkType(NetWorkType.LTEGSMAUTO);
			cmdMethod(NetWorkType.LTEGSMAUTO);
			break;
		}

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

}

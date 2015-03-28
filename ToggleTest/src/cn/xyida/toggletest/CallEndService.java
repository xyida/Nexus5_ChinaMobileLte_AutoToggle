package cn.xyida.toggletest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

public class CallEndService extends Service {
	private static final int COMMAND = 1;
	private static final int DELAY = 15000;
//	private static int currNetworkType = 9;
	private static int toggleNetworkType=9;
//	RemoteViews rviews;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case COMMAND:

				CallEndService.cmdMethod((NetWorkType) msg.obj);
				String type = "LTE/GSM auto";
				switch ((NetWorkType) msg.obj) {
				case LTEONLY:
//					currNetworkType = 11;
					toggleNetworkType=9;
					type = "LTE only";
					break;

				case LTEGSMAUTO:
//					currNetworkType = 9;
					toggleNetworkType=11;
					type = "LTE/GSM auto";
					break;
				}
				Log.e("yoda", "切换" + type);

				break;

			}

		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.e("yoda", "服务启动!");
		

//		RemoteViews rviews=new RemoteViews(getPackageName(), R.layout.widget_layout);
//		Intent it=new Intent();
//		it.setAction("WIDGET_CLICK_ACTION");
//		it.setClass(getApplicationContext(),CallEndService.class);
//		PendingIntent pendingIntent=PendingIntent.getService(getApplicationContext(), 0, it, 0);
//		rviews.setOnClickPendingIntent(R.id.imageView1, pendingIntent);
		
		// 处理widget点击事件
		AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(this);
		ComponentName provider=new ComponentName(this, WidgetProvider.class);
		Log.e("yoda", "CallEndService接受到广播"+intent.getAction());
		if ("WIDGET_CLICK_ACTION".equals(intent.getAction())) {
			Log.e("yoda", "CallEndService-widget按钮被点击");
			RemoteViews rviews = new RemoteViews(this.getPackageName(),
					R.layout.widget_layout);
			int srcId=R.drawable.ltegsmauto;
			Message msg=null;
			switch (toggleNetworkType) {
			case 9:
				msg = handler.obtainMessage();
				msg.what = COMMAND;
				msg.obj = NetWorkType.LTEGSMAUTO;
				handler.sendMessage(msg);
				break;

			case 11:
				msg = handler.obtainMessage();
				msg.what = COMMAND;
				msg.obj = NetWorkType.LTEONLY;
				handler.sendMessage(msg);
				break;
			}
			
			switch (toggleNetworkType) {
			case 9:
				srcId=R.drawable.ltegsmauto;
				break;

			case 11:
				srcId=R.drawable.lteonly;
				break;
			}
			rviews.setImageViewResource(R.id.imageView1, srcId);
			appWidgetManager.updateAppWidget(provider, rviews);
		}

		

	}
   
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 处理通话结束事件
				final TelephonyManager manager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
				manager.listen(new PhoneStateListener() {

					@Override
					public void onCallStateChanged(int state, String incomingNumber) {
						// TODO Auto-generated method stub
						Log.e("yoda-onCallState", String.valueOf(state));
						super.onCallStateChanged(state, incomingNumber);
						if (state == TelephonyManager.CALL_STATE_IDLE) {
							Log.e("yoda", "挂断电话==>" + state);
							Log.e("yoda-networktype",
									String.valueOf(manager.getNetworkType()));

							Thread thread = new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										Thread.sleep(3000);
										Log.e("yoda", "延时:" + 3000 + "秒");
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Message msg = handler.obtainMessage();
									msg.what = COMMAND;
									msg.obj = NetWorkType.LTEONLY;
									handler.sendMessage(msg);

									try {
										Thread.sleep(DELAY);
										Log.e("yoda", "延时:" + DELAY + "秒");
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Message msg2 = handler.obtainMessage();
									msg2.what = COMMAND;
									msg2.obj = NetWorkType.LTEGSMAUTO;
									handler.sendMessage(msg2);
								}
							});
							thread.start();

						}
					}

				}, PhoneStateListener.LISTEN_CALL_STATE);
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

	public static synchronized void cmdMethod(NetWorkType networkType) {
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

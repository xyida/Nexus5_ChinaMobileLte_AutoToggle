package cn.xyida.perfectlte;

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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class CallEndService extends Service {
    public static final int COMMAND = 1;
    SharedPreferences preferences;
    //	private static int currNetworkType = 9;
    TelephonyManager manager = null;
    CallEndListener listener;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case COMMAND:

                    CallEndService.cmdMethod((NetWorkType) msg.obj);
                    String type = "LTE/GSM auto";
                    switch ((NetWorkType) msg.obj) {
                        case LTEONLY:

                            type = "LTE only";
                            preferences.edit().putInt("currentNetworkType", 11).commit();
                            break;

                        case LTEGSMAUTO:

                            preferences.edit().putInt("currentNetworkType", 9).commit();
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
        Log.e("perfectlte", "CallEndService-onBind");
        return null;
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.e("yoda", "CallEndService-onStart");

        // 处理widget点击事件
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName provider = new ComponentName(this, WidgetProvider.class);
//        Log.e("yoda", "CallEndService接受到广播"+intent.getAction());
        if ("WIDGET_CLICK_ACTION".equals(intent.getAction())) {
            Log.e("yoda", "CallEndService-widget按钮被点击");
//            boolean isNeedRestartService=false;
//            if (preferences.getBoolean("perfectlte_startService",true)){//判断当前是否允许开启服务
//                preferences.edit().putBoolean("perfectlte_startService",false).commit();//暂停自动切换服务
//                isNeedRestartService=true;
//            }

            RemoteViews rviews = new RemoteViews(this.getPackageName(),
                    R.layout.widget_layout);
            int currType = preferences.getInt("currentNetworkType", 9);
            int srcId = R.drawable.ltegsmauto;
            Message msg = null;
            switch (currType) {
                case 11:
                    srcId = R.drawable.ltegsmauto;
                    break;

                case 9:
                    srcId = R.drawable.lteonly;
                    break;
            }
            switch (currType) {
                case 11:
                    msg = handler.obtainMessage();
                    msg.what = COMMAND;
                    msg.obj = NetWorkType.LTEGSMAUTO;
                    handler.sendMessage(msg);
                    break;

                case 9:
                    msg = handler.obtainMessage();
                    msg.what = COMMAND;
                    msg.obj = NetWorkType.LTEONLY;
                    handler.sendMessage(msg);
                    break;
            }


            rviews.setImageViewResource(R.id.imageView1, srcId);
            appWidgetManager.updateAppWidget(provider, rviews);

//            if (isNeedRestartService){//恢复服务
//                preferences.edit().putBoolean("perfectlte_startService",true).commit();
//                isNeedRestartService=false;
//            }

        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("perfectlte", "CallEndService-onStartCommand");





        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.e("perfectlte", "CallEndService-onCreate()");
        // 处理通话结束事件
        manager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        listener = new CallEndListener(this, handler);
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        preferences = getSharedPreferences("cn.xyida.perfectlte_preferences", MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.e("yoda", "CallEndService OnDestroy!");
//        stopSelf();
//        if (thread.isAlive()){
//           thread=null;
//        }
//        manager.listen(listener, PhoneStateListener.LISTEN_NONE);
//        if (manager!=null){
//            manager=null;
//        }
        super.onDestroy();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        Log.e("perfectlte", "CallEndService-onUnbind");
        return super.onUnbind(intent);
    }

    /**
     * *
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
        Log.e("perfectlte", "cmdMethod被调用！");
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
            command = new String[]{"su", "-cn", "u:r:system_app:s0"};
        } else {
            command = new String[]{"su"};
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

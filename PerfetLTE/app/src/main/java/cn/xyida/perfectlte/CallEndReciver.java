package cn.xyida.perfectlte;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.sax.StartElementListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import cn.xyida.perfectlte.CallEndService;

public class CallEndReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context c, Intent i) {
        // TODO Auto-generated method stub
        Log.e("yoda", "接收到广播" + i.getAction());
        boolean flag = false;
        ActivityManager activityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if ("cn.xyida.perfectlte.CallEndService".equals(service.service.getClassName())) {
                flag = true;
                Log.e("yoda", "服务已经运行中");
            }
        }
        if (!flag) {
            SharedPreferences preferences = c.getSharedPreferences("cn.xyida.perfectlte_preferences", Context.MODE_PRIVATE);
            if (preferences.getBoolean("perfectlte_startService", true)) {
                Intent intent = new Intent();
                intent.setClass(c, CallEndService.class);
                c.startService(intent);
                Log.e("yoda", "启动服务！！");
                flag = false;
            }

        }
        if ("cn.xyida.perfectlte.intent.action.STOP_SERVICE".equals(i.getAction())) {
            Intent intent = new Intent();
            intent.setClass(c, CallEndService.class);
            c.stopService(intent);
            Log.e("SettingsActivity", "停止后台服务");
            Toast.makeText(c,"后台服务已停止",Toast.LENGTH_SHORT).show();
        }


    }

}

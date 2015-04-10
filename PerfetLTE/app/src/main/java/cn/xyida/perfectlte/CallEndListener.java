package cn.xyida.perfectlte;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by yd on 2015/4/9.
 */
public class CallEndListener extends PhoneStateListener {
    private Context c;
    Handler handler;
    private static  int TOGGLE_DELAY = 0;
    private static  int SEARCH_DELAY = 0;

    Thread thread;

    public CallEndListener(Context c, Handler handler) {
        this.c = c;
        this.handler = handler;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        // TODO Auto-generated method stub
        Log.e("yoda-onCallState", String.valueOf(state));
        super.onCallStateChanged(state, incomingNumber);
        if (state == TelephonyManager.CALL_STATE_IDLE) {
            Log.e("yoda", "挂断电话==>" + state);
//                    Log.e("yoda-networktype",
//                            String.valueOf(manager.getNetworkType()));


            SharedPreferences preferences=c.getSharedPreferences("cn.xyida.perfectlte_preferences",c.MODE_PRIVATE);

            if (preferences.getBoolean("perfectlte_startService", true)){
                SEARCH_DELAY=Integer.parseInt(preferences.getString("perfectlte_searchdelay","7000"));
                TOGGLE_DELAY=Integer.parseInt(preferences.getString("perfectlte_toggledelay","5000"));
                thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Thread.sleep(TOGGLE_DELAY);
                            Log.e("yoda", "延时:" + TOGGLE_DELAY + "秒");
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = CallEndService.COMMAND;
                        msg.obj = NetWorkType.LTEONLY;
                        handler.sendMessage(msg);

                        try {
                            Thread.sleep(SEARCH_DELAY);
                            Log.e("yoda", "延时:" + SEARCH_DELAY + "秒");
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Message msg2 = handler.obtainMessage();
                        msg2.what = CallEndService.COMMAND;
                        msg2.obj = NetWorkType.LTEGSMAUTO;
                        handler.sendMessage(msg2);
                    }
                });
                thread.start();
            }else{
                Log.e("perfectlte","CallEndListener-prefs不允许开启服务");
            }



        }
    }


}

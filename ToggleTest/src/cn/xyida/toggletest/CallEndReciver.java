package cn.xyida.toggletest;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallEndReciver extends BroadcastReceiver{

	@Override
	public void onReceive(Context c, Intent i) {
		// TODO Auto-generated method stub
		Log.e("yoda", "���յ��㲥"+i.getAction());
		boolean flag=false;
		ActivityManager activityManager=(ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("cn.xyida.toggletest.CallEndService".equals(service.service.getClassName())) {
	        	flag=true;
	        	Log.e("yoda", "�����Ѿ�������");
	        }   
	    }
		if(!flag){
			Intent intent=new Intent();
    		intent.setClass(c, CallEndService.class);
    		c.startService(intent);
    		Log.e("yoda", "�������񣡣�");
    		flag=false;
		}
		
		
		
		
	}

}

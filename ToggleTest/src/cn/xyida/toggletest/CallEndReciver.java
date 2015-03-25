package cn.xyida.toggletest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallEndReciver extends BroadcastReceiver{

	@Override
	public void onReceive(Context c, Intent i) {
		// TODO Auto-generated method stub
		TelephonyManager manager=(TelephonyManager)c. getSystemService(Service.TELEPHONY_SERVICE);
		manager.listen(new PhoneStateListener(){

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// TODO Auto-generated method stub
				Log.e("yoda-onCallState", String.valueOf(state));
				super.onCallStateChanged(state, incomingNumber);
				if (state==TelephonyManager.CALL_STATE_IDLE) {
					Log.e("yoda", "�Ҷϵ绰");
					
							CallEndService.cmdMethod(NetWorkType.LTEONLY);
							
						
				}
			}
			
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

}

package cn.xyida.perfetlte;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SettingService extends Service {
    public SettingService() {
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);



    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

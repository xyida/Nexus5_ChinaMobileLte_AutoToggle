package cn.xyida.perfectlte;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

    }
    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        super.onEnabled(context);
        Log.e("yoda", "WidgetProvider-onEnable");


        Intent intent=new Intent();
        intent.setClass(context, CallEndService.class);
        context.startService(intent);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.e("yoda", "WidgetProvider-onUpdate");

        Intent intent=new Intent();
        intent.setAction("WIDGET_CLICK_ACTION");
        intent.setClass(context,	CallEndService.class);
        PendingIntent pendingIntent=PendingIntent.getService(context, 0, intent, 0);
        RemoteViews rviews=new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        rviews.setOnClickPendingIntent(R.id.imageView1, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, rviews);



    }


}

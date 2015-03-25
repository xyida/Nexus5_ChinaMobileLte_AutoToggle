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
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class MainActivity extends Activity implements OnClickListener {

	private Button lteOnlyBtn, lteGsmAutoBtn;
	TelephonyManager telephonyManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		telephonyManager=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		prepareUI();
		Intent intent=new Intent();
		intent.setClass(MainActivity.this, CallEndService.class);
		startService(intent);
		
		

		
		
		
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

	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			// changePreferNetWorkType(NetWorkType.LTEONLY);
			CallEndService.cmdMethod(NetWorkType.LTEONLY);
			Log.e("yoda", telephonyManager.getNetworkOperatorName());
			break;
		
		case R.id.button2:
			// changePreferNetWorkType(NetWorkType.LTEGSMAUTO);
			CallEndService.cmdMethod(NetWorkType.LTEGSMAUTO);
			Log.e("yoda", telephonyManager.getNetworkOperatorName());

			break;
		}

	}



}

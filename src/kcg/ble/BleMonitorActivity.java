package kcg.ble;

import kcg.ble.BleMonitorService.MyLocalBinder;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class BleMonitorActivity extends Activity {

	private BleMonitorService myService;
	private boolean isBound = false;
	private TextView mainText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		updatePreferences();
		mainText = (TextView)findViewById(R.id.textView1);
		mainText.append("\n");
		mainText.setMovementMethod(new ScrollingMovementMethod());
		
		startService(new Intent(BleMonitorActivity.this,BleMonitorService.class));

	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.settings){
			Intent intent;
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
				intent = new Intent(this, Preferences.class);
			else
				intent = new Intent(this, PreferencesForNewVersions.class);
			startActivity(intent);
		}
		return false;
	}

	@Override
	protected void onResume() {
		bindService(new Intent(this, BleMonitorService.class), myConnection, Context.BIND_AUTO_CREATE);
		super.onResume();
	}


	@Override
	protected void onStop() {

		unbindService(myConnection);
		isBound = false;

		super.onStop();
	}

	private ServiceConnection myConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className,
				IBinder service) {
			MyLocalBinder binder = (MyLocalBinder) service;
			myService = binder.getService();
			myService.setActivityForUpdate(BleMonitorActivity.this);
			isBound = true;

			mainText.setText("service connected \r\n" + mainText.getText());
			
		}

		public void onServiceDisconnected(ComponentName arg0) {
			isBound = false;
			mainText.setText("service lost \r\n" + mainText.getText());
		}

	};  


	public void startStopContinues(View view){

		mainText.setText("continues "+isBound+"\r\n" + mainText.getText());

		ToggleButton b = (ToggleButton)view;

		if (b.isChecked()){
			if (isBound){
				myService.startStopContinues(true);
				mainText.setText("start scan called \r\n" + mainText.getText());
			}else{
				mainText.setText("no service bound \r\n" + mainText.getText());
			}
		}
		else{
			if (isBound){
				myService.startStopContinues(false);
				mainText.setText("stop scan called \r\n" + mainText.getText());
			}else{
				mainText.setText("no service bound \r\n" + mainText.getText());
			}
		}

	}

	public void startStopOnce (View view){

		ToggleButton b = (ToggleButton)view;

		if (b.isChecked()){
			if (isBound){
				myService.startStopOnce(true);
				mainText.setText("start scan called \r\n" + mainText.getText());
			}else{
				mainText.setText("no service bound \r\n" + mainText.getText());
			}
		}
		else{
			if (isBound){
				myService.startStopOnce(false);
				mainText.setText("stop scan called \r\n" + mainText.getText());
			}else{
				mainText.setText("no service bound \r\n" + mainText.getText());
			}
		}
	}
	
	private void updatePreferences(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		StaticValues.type = preferences.getString("type", "bus");
		StaticValues.name = preferences.getString("name", "unnamed");
		StaticValues.mac = getBluetoothMacAddress();
		StaticValues.logModeOn = preferences.getBoolean("logModeOn", false);
	}
	
	public static String getBluetoothMacAddress() {
	    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	 
	    // if device does not support Bluetooth
	    if(mBluetoothAdapter==null){
	        Log.d("error","device does not support bluetooth");
	        return null;
	    }
	     
	    return mBluetoothAdapter.getAddress();
	}

	public void startService(View view){
		mainText.append("start service \n");

		startService(new Intent(BleMonitorActivity.this,BleMonitorService.class));
		mainText.append("service should be started \n");
	}

	public void stopService(View view){
		mainText.append("stop service \n");
		stopService(new Intent(BleMonitorActivity.this,BleMonitorService.class));
	}

	public void setScreenMsg(final String msg){

		runOnUiThread(new Runnable(){
			public void run(){
				mainText.setText((msg+"\n"+mainText.getText()));
			}
		});
	}

}
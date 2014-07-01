package kcg.ble;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;

public class BleMonitorService extends Service {

	private BleScanner myBT;
	private BleScanContainer scanContainer;

	private LocationScanner loc;
	private double lastKnownLat=-1,lastKnownLon=-1;

	private Logger logger;

	private static final String SITE="http://kcg-lab.info/bar-ilan/services/insertClient.php";//?
	//private NetworkTask networkTask;
	private connectionHelper network = new connectionHelper(); 

	private BleMonitorActivity _activity;


	private NotificationManager nm;



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	private final IBinder myBinder = new MyLocalBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return myBinder;
	}

	public class MyLocalBinder extends Binder {
		BleMonitorService getService() {
			return BleMonitorService.this;
		}
	}

	public void onCreate() {
		super.onCreate();

		myBT = new BleScanner(this);
		scanContainer = new BleScanContainer();
		scanContainer.loadRegisteredList(this);

		network.start();

		loc = new LocationScanner(this);

		Log.i("ble","service started");


		nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		showNotification("service started");
	}

	@Override
	public void onDestroy() {

		if (myBT != null ){
			myBT.finish();
		}
		if (loc !=null ){
			loc.stoptLocationLestener();
		}

		if (network != null){
			network.finish();
		}

		Log.i("ble","service stoped");
		super.onDestroy();

	}

	//-------------------

	public void setActivityForUpdate(BleMonitorActivity activity){
		_activity = activity;
	}

	public void startStopOnce(boolean isOn){
		myBT.startStopOnce(isOn);

		if (isOn){
			loc.startLocationLestener();

			if (StaticValues.logModeOn){
				logger = new Logger(this);
				logger.initWriteFile("ble_once_"+getDate());
				logger.appendNextLine("time_ms,lat,lon,bssid,rssi");
			}
		}
		else {
			if (StaticValues.logModeOn && logger != null){
				logger.close();
				logger = null;
			}
		}
	}

	public void startStopContinues(boolean isOn){
		myBT.startStopContinues(isOn);
		if (isOn){
			loc.startLocationLestener();

			if (StaticValues.logModeOn){
				logger = new Logger(this);
				logger.initWriteFile("ble_continues_"+getDate());
				logger.appendNextLine("time_ms,lat,lon,bssid,rssi");
			}
		}
		else {
			if (StaticValues.logModeOn && logger != null){
				logger.close();
				logger = null;
			}
		}
	}

	public void setBleResult(String bssid, int rssi){
		Log.i("ble", "service: got ble result "+bssid+","+rssi+" , loc: "+lastKnownLat+","+lastKnownLon);
		if(StaticValues.logModeOn && logger != null){
			logger.appendNextLine(System.currentTimeMillis()+","+lastKnownLat+","+lastKnownLon+","+bssid+","+rssi);
		}

		if (_activity != null){
			_activity.setScreenMsg("service: got ble result "+bssid+","+rssi+" , loc: "+lastKnownLat+","+lastKnownLon);
		}


		boolean update = scanContainer.addScan(bssid, rssi);

		Log.i("ble", "should server be updated ? "+update);

		if (update){
			String uri = SITE + "?table=BarIlan&Information=client_ark&unique="+getRnd()+"&Latitude="+lastKnownLat+"&Longitude="+lastKnownLon+scanContainer.getStatusString();
			network.addDataString(uri);
		}
	}

	public void setLocationResult(double lat,double lon){
		lastKnownLat = lat;
		lastKnownLon = lon;

		//also update on position change
		String uri = SITE + "?table=BarIlan&Information=client_ark&unique="+getRnd()+"&Latitude="+lastKnownLat+"&Longitude="+lastKnownLon+scanContainer.getStatusString();
		network.addDataString(uri);
	}


	private void showNotification(String text) {

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.ic_launcher, text,System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this notification
		//PendingIntent contentIntent = PendingIntent.getActivity(this, 0,null, 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this,"BLE monitor",text, null);


		// Send the notification.
		// We use a layout id because it is a unique number.  We use it later to cancel.
		nm.notify(R.string.hello, notification);
	}

	private static String getDate(){
		return  (String)DateFormat.format("hh_mm_ss_dd_MM_yyyy", Calendar.getInstance());    
	}

	private int getRnd(){
		return (int)(Math.random()*100+Math.random());
	}

}

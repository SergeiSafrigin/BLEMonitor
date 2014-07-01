package kcg.ble;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationScanner {

	//data
	private LocationManager _loc;
	private MyLocationListener _locListener;
	
	private BleMonitorService _service;


	//constractor

	public LocationScanner(BleMonitorService service){
		
		_service = service;
		
		_loc = (LocationManager) _service.getSystemService(Context.LOCATION_SERVICE);

		_locListener = new MyLocationListener();
	}

	//functions
	public void startLocationLestener(){

		_loc.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1,_locListener);
	


	}

	public void stoptLocationLestener(){
		_loc.removeUpdates(_locListener);
	}





	private class MyLocationListener implements LocationListener {

		//location listener

		public void onLocationChanged(Location location) {

			_service.setLocationResult(location.getLatitude(),location.getLongitude());

		}

		public void onStatusChanged(String s, int i, Bundle b) {

		}

		public void onProviderDisabled(String s) {

		}

		public void onProviderEnabled(String s) {

		}

	}


}

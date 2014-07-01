package kcg.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;

public class BleScanner{
	
	private BleMonitorService _service;
	
	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler = new Handler();

	private BleScanContainer cont = new BleScanContainer();
	
	public BleScanner(BleMonitorService service){
		
		_service = service;
		
		cont.loadRegisteredList(_service);
		
		final BluetoothManager bluetoothManager =
				(BluetoothManager) service.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}
	}
	
	public void finish(){
		mHandler.removeCallbacks(startScan);
		mHandler.removeCallbacks(stopScan);
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
	}
	
	public void startStopContinues(boolean isOn){

		if (isOn){
			mBluetoothAdapter.startLeScan(mLeScanCallback);
			mHandler.postDelayed(stopScan, 2500);
			
		}
		else{
			mHandler.removeCallbacks(startScan);
			mHandler.removeCallbacks(stopScan);
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}


	}

	public void startStopOnce (boolean isOn){
		if (isOn){
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		}
		else{
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
	

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,byte[] scanRecord) {

			//Log.i("BLE", "found bluethoos "+device.getName()+" rssi "+rssi);
			_service.setBleResult(device.getAddress(),rssi);
		}
	};

	private Runnable startScan = new Runnable() {       
		@Override
		public void run() {
			mBluetoothAdapter.startLeScan(mLeScanCallback);
			mHandler.postDelayed(stopScan, 2000);
		}               
	};

	private Runnable stopScan = new Runnable() {        
		@Override
		public void run() {         
			mBluetoothAdapter.stopLeScan(mLeScanCallback);                                            
			mHandler.postDelayed(startScan, 250);          
		}
	};

}

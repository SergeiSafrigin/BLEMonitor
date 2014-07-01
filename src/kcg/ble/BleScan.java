package kcg.ble;

public class BleScan {
	String bssid = "";
	int rssi = 0;
	int ttl = 0;
	
	public BleScan(String bssid,int rssi){
		this.bssid = bssid;
		this.rssi = rssi;
		
		ttl = 20;
		
	}
	
	public BleScan(String bssid,int rssi,int ttl){
		this.bssid = bssid;
		this.rssi = rssi;
		this.ttl = ttl;
		
	}
}

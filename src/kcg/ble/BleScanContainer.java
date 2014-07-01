package kcg.ble;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class BleScanContainer {

	/*
	 * this class should hold an array of [5] sorted measurment
	 * sorted by signal level
	 * 
	 * each new bssid should be inserted by level
	 */


	private static final int size = 5;
	private BleScan[] data = new BleScan[size];

	private ArrayList<String> permitedList = null;

	private static final int significant_level = 7;
	private static final int initial_ttl = 20;

	public BleScanContainer (){
		for (int i=0;i<size;i++){
			data[i] = new BleScan("fake", -200);
		}
	}

	public boolean addScan(String bssid,int rssi){
		//look for bssid
		int index = getBleScanIndex(bssid);

		switch (index){

		case -2: {
			//not permited bssid
			return false;
		}

		case -1:{
			//update ttl
			updateTTL();
			
			//look for a place for him (maybe there is no place at all)
			int place = 0;
			while (data[place].rssi>= rssi && place < size){
				place++;
			}
		
			if (place == size) {
				//means all the data in the array is "stronger" than a new measurement
				//so this one is no relevant.
				return false;
			}
			else{
				freeCell(place);
				data[place] = new BleScan(bssid, rssi,initial_ttl);
				return true;
			}
		}

		default:{

			updateTTL();

			int delta = Math.abs((data[index].rssi - rssi));
			if (delta > significant_level){
				//update the device to new signal level
				data[index].rssi = rssi;
				data[index].ttl = initial_ttl;
				return true;
			}
		}
		}
		return false;
	}
	
	public String getStatusString(){
		
		String ans = "&BT1="+data[0].bssid+"&RSSI1="+data[0].rssi+
				"&BT2="+data[1].bssid+"&RSSI2="+data[1].rssi+
				"&BT3="+data[2].bssid+"&RSSI3="+data[2].rssi+
				"&BT4="+data[3].bssid+"&RSSI4="+data[3].rssi+
				"&BT5="+data[4].bssid+"&RSSI5="+data[4].rssi;
		
		return ans;
	}
	

	private void updateTTL(){
		for (int i =0; i< size; i++){
			if (data[i].ttl < 0 && !data[i].bssid.equals("fake")){
				data[i].bssid = "fake";
				data[i].rssi = -200;
			}
			else{
				data[i].ttl--;
			}
		}
	}

	private void freeCell(int index){
		for (int i = size-2;i>=index;i--){
			data[i+1] = data[i];
		}
	}







	public void loadRegisteredList(Context ctx){
		//load a list from sd_card.
		String path = Environment.getExternalStorageDirectory()+"/ble/mac_list.csv";
		try {
			BufferedReader file = new BufferedReader(new FileReader(path));

			permitedList = new ArrayList<String>();

			String line = file.readLine().toLowerCase();
			while (line != null){
				permitedList.add(new String(line));//a deep copy
				line = file.readLine();
			}
			file.close();

		}catch (FileNotFoundException e) {
			//a toast should be shown
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * look for the bssid string in the scanned devices
	 */
	private int getBleScanIndex(String ble_bssid){

		//check if it's a "legal" tag
		if (permitedList.contains(ble_bssid.toLowerCase())){

			for (int i=0;i<size;i++){
				if (data[i].bssid.equals(ble_bssid)){
					return i;
				}
			}
			return -1;
		}
		return -2;
	}


}

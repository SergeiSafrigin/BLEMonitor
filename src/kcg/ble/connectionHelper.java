package kcg.ble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

/*
 * this class is used to get some strings object (with a url)
 * and send them to a server
 */

public class connectionHelper extends Thread {

	//data
	private String uri;
	private LinkedBlockingQueue<String> data = new LinkedBlockingQueue();
	private boolean continue_flag = true;
	
	private HttpClient httpclient = new DefaultHttpClient();
	private HttpGet request=new HttpGet();
	
	
	//constructor
	
	//functions
	public void run(){
		while (continue_flag){
			
			try {
				uri = data.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (uri.equals("finish")){ return;}
			
			try {
				request.setURI(new URI(uri));
				
				HttpResponse response = httpclient.execute(request); 
				InputStream is = response.getEntity().getContent();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				String line = null;
				while ((line = reader.readLine()) != null) 
				{
					Log.i("ble", "server response: "+line);
					//HandlerThread.sleep(1);
				}
				is.close();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static void updateBLEDevice(){
		
		new Thread(){
			@Override
			public void run(){
				String url = "http://kcg-lab.info/bar-ilan/services/insertBLEEntity.php?Information="+StaticValues.name
						+"&MAC="+StaticValues.mac+"&Type="+StaticValues.type;
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpGet request = new HttpGet();
					request.setURI(new URI(url));
					
					httpclient.execute(request); 

				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	
	public void finish(){
		continue_flag = false;
		try {
			data.put("finish");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void addDataString(String dataString){
		try {
			data.put(dataString);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

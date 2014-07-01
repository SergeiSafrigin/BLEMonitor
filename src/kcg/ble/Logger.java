package kcg.ble;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/*
 * this class should be used to read and write files,not for parsing
 */



public class Logger {

	//data
	private BufferedWriter writer;
	private Context _context;

	//constractor

	public Logger(Context c){
		_context = c;
	}



	//functions

	public static boolean isStorageAvailable(){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return true;
		}
		return false;
	}



	public void initWriteFile(String fileName){


		File fpath =new File(Environment.getExternalStorageDirectory()+"/"+fileName+".csv");
		try {
			writer = new BufferedWriter(new FileWriter(fpath));
		}
		catch (IOException ioe){

			ioe.printStackTrace();
		}
	}


	public synchronized void appendNextLine(String line){

		try {
			writer.append(line+"\n");
		}
		catch (IOException ioe){
			Toast.makeText(_context,ioe.getMessage(), Toast.LENGTH_LONG).show();

		}

	}

	public void close(){
		if (writer != null){
			try {
				writer.close();
			}
			catch (IOException ioe){
				Toast.makeText(_context,ioe.getMessage(), Toast.LENGTH_LONG).show();
			}
		}


	}

}

package kcg.ble;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferencesForNewVersions extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
	private SharedPreferences preferences;
	private boolean changed = false;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		String type = preferences.getString("type", "bus");
		String name = preferences.getString("name", "unnamed");
		
		if (type != StaticValues.type || name != StaticValues.name){
			StaticValues.type = preferences.getString("type", "bus");
			StaticValues.name = preferences.getString("name", "unnamed");
			changed = true;
		}
		
		StaticValues.logModeOn = preferences.getBoolean("log", false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		changed = false;
		preferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		preferences.unregisterOnSharedPreferenceChangeListener(this);
		if (changed)
			connectionHelper.updateBLEDevice();
	}
	
	@SuppressLint("NewApi")
	public static class PrefsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference);
		}
	}
}
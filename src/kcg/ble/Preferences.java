package kcg.ble;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

public class Preferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	private SharedPreferences preferences;
	private boolean changed = false;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		addPreferencesFromResource(R.xml.preference);
		
		preferences = getPreferenceScreen().getSharedPreferences();
		
		EditTextPreference pref = (EditTextPreference)findPreference("name");
		pref.setSummary(pref.getText());
	}



	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		changed = false;
		preferences.registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		preferences.unregisterOnSharedPreferenceChangeListener(this);
		if (changed)
			connectionHelper.updateBLEDevice();
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

}

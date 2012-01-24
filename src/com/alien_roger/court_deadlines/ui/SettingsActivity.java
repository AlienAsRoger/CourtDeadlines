package com.alien_roger.court_deadlines.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.statics.StaticData;


public class SettingsActivity extends PreferenceActivity{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.preferences);
	    setTitle(R.string.preferences);
	}


	public static Uri getAlarmRingtone(Context context){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return Uri.parse(pref.getString(StaticData.NOTIFICATION_SOUND_DEFAULT,"no_media"));
	}

	public static boolean vibrate4Alarm(Context context){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(StaticData.NOTIFICATION_VIBRO_DEFAULT,false);
	}


}

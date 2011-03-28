package com.nephoapp.anarxiv;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class preference extends PreferenceActivity
{
	private SharedPreferences prefs;
	private CheckBoxPreference isCustomized;
	private ListPreference maincat;
	private ListPreference subcat;
	

    @Override
     public void onCreate(Bundle icicle)
    {
    	super.onCreate(icicle);
    	addPreferencesFromResource(R.xml.setting);
    }

	

}

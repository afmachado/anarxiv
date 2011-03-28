package com.nephoapp.anarxiv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class preference extends PreferenceActivity {
	private SharedPreferences prefs;
//   private CheckBoxPreference _isCustomized;
	private ListPreference _maincat;
	private ListPreference _subcat;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.setting);
//		_isCustomized = (CheckBoxPreference) findPreference("isCustomized");
		_maincat = (ListPreference) findPreference("maincat");
		_subcat = (ListPreference) findPreference("subcat");

		Context context = getApplicationContext();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (prefs.getString("maincat", null) == null) {
			_subcat.setEnabled(false);
			_maincat.setSummary("Please select main category");

		} else {
			_maincat.setSummary(prefs.getString("maincat", null));

		}

		if (prefs.getString("subcat", null) == null) {
			_subcat.setSummary("Please select subcategory");

		} else {
			_subcat.setSummary(prefs.getString("subcat", null));

		}

		CharSequence[] maincategory = UrlTable.Category;

		_maincat.setEntries(maincategory);
		_maincat.setEntryValues(maincategory);

		if (prefs.getString("maincat", null) == null) {
			/** dumb intitial vale */
			CharSequence[] subcategory = {
					" error please select main category ", "category first" };
			_subcat.setEntries(subcategory);
			_subcat.setEntryValues(subcategory);

		} else {
			CharSequence[] subcategory = anarxiv._urlTbl
					.getSubcategoryList(prefs.getString("maincat", null));

			_subcat.setEntries(subcategory);
			_subcat.setEntryValues(subcategory);
		}

		_subcat.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference arg0) {
				CharSequence maincat = _maincat.getEntry();

				if (maincat == null) {
					_subcat.setEnabled(false);
				} else {
					_subcat.setEnabled(true);
					CharSequence[] subcategory = anarxiv._urlTbl
							.getSubcategoryList((String) maincat);

					_subcat.setEntries(subcategory);
					_subcat.setEntryValues(subcategory);
				}

				return true;
			}
		}

		);
		_maincat.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				CharSequence maincat = (CharSequence) newValue;
				_maincat.setSummary(maincat);
				
				if (maincat == null) {
					_subcat.setEnabled(false);
				} else {
					_subcat.setEnabled(true);
					CharSequence[] subcategory = anarxiv._urlTbl
							.getSubcategoryList((String) maincat);

					_subcat.setEntries(subcategory);
					_subcat.setEntryValues(subcategory);
					_subcat.setSummary(subcategory[0].toString());
					Context context = getApplicationContext();
				    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				    SharedPreferences.Editor editor = prefs.edit();
				    editor.putString("subcat", subcategory[0].toString());
				
				    // Commit the changes.
				    editor.commit();
				}

				return true;
			}
		});

		_subcat.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if (_maincat.getValue() != null) {
					CharSequence subcat = (CharSequence) newValue;
					_subcat.setSummary(subcat);
				}
				return true;
			}
		});

	}

}

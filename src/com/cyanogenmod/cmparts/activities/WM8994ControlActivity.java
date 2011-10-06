/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// WM8994 control stuff
public class WM8994ControlActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    // XXX: The sysfs interface may be re-written to remove reference to the "voodoo" name
    public static final String WM8994_ENABLE = "/sys/devices/virtual/misc/voodoo_sound_control/enable";
    
    private static final String GENERAL_CATEGORY = "general_category";
    
    private static final String WM8994_ENABLE_PREF = "pref_wm8994_control_enable";
    private static final String WM8994_CONTROL_ENABLED = "1";
    
    private static final String TAG = "WM8994Control";
    
    private CheckBoxPreference mWm8994EnablePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String temp;

        setTitle(R.string.wm8994_settings_title_subhead);
        addPreferencesFromResource(R.xml.wm8994_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        PreferenceCategory generalCategory = (PreferenceCategory)prefSet.findPreference (GENERAL_CATEGORY);

        // read value from sysfs interface
        temp = readOneLine(WM8994_ENABLE);
        

        // Enable wm8994 control settings preference
        mWm8994EnablePref = (CheckBoxPreference) prefSet.findPreference(WM8994_ENABLE_PREF);
        mWm8994EnablePref.setChecked(WM8994_CONTROL_ENABLED.equals(temp));
        
    }
    
    // Preference change action for check boxes
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    	
    	// "Enable" box value
    	if (preference == mWm8994EnablePref) {
    		// Store box value as a string in "boxValue"
    		String boxValue = mWm8994EnablePref.isChecked() ? "1" : "0";
    		// Make sure boxValue is not null
    		if (boxValue != null) {
    			// check to make sure boxValue was written to file
    			if (writeOneLine(WM8994_ENABLE, (String) boxValue)) {
    				String debug ="DEBUG: Wrote value " + boxValue + " to " + WM8994_ENABLE;
    				Log.d(TAG, debug);
    				return true;
    			} else {
    				String error = "Writing to " + WM8994_ENABLE + " failed! Value: " + boxValue;
    				Log.e(TAG, error);
    				return false;
				}
    		}
    	}
        return false;
    }

    // preference change action for list boxes
    /*public boolean onPreferenceChange(Preference preference, Object newValue) {
        String fname = "";

        if (newValue != null) {
            if (preference == mWm8994EnablePref) {
                fname = WM8994_ENABLE;
            }

            if (writeOneLine(fname, (String) newValue)) {
            	String debug = "DEBUG: Writing to " + fname + " value " + newValue;
            	Log.d(TAG, debug);
                return true;
            } else {
            	String error = "Writing to " + fname + " failed!";
            	Log.e(TAG, error);
                return false;
            }
        }
        
        return false;
    }*/

    // Read value from sysfs interface
    // "Borrowed" from CPUActivity.java
    public static String readOneLine(String fname) {
        BufferedReader br;
        String line = null;

        try {
            br = new BufferedReader(new FileReader(fname), 512);
            try {
                line = br.readLine();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "IO Exception when reading /sys/ file", e);
        }
        return line;
    }

    // Write value to sysfs interface
    // "Borrowed" from CPUActivity.java
    public static boolean writeOneLine(String fname, String value) {
        try {
            FileWriter fw = new FileWriter(fname);
            try {
                fw.write(value);
            } finally {
                fw.close();
            }
        } catch (IOException e) {
            String Error = "Error writing to " + fname + ". Exception: ";
            Log.e(TAG, Error, e);
            return false;
        }
        return true;
    }
}

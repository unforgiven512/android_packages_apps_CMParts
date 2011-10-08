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
public class WM8994ControlActivity extends PreferenceActivity /*implements
        Preference.OnPreferenceChangeListener */{

    // XXX: The sysfs interface may be re-written to remove reference to the "voodoo" name
	// SYSFS paths
    public static final String WM8994_ENABLE = "/sys/devices/virtual/misc/voodoo_sound_control/enable";
    public static final String WM8994_SPEAKER_TUNING = "/sys/devices/virtual/misc/voodoo_sound/speaker_tuning";
    public static final String WM8994_MONO_DOWNMIX = "/sys/devices/virtual/misc/voodoo_sound/mono_downmix";
    public static final String WM8994_STEREO_EXPANSION = "/sys/devices/virtual/misc/voodoo_sound/stereo_expansion";
    public static final String WM8994_DAC_DIRECT = "/sys/devices/virtual/misc/voodoo_sound/dac_direct";
    public static final String WM8994_DAC_OSR128 = "/sys/devices/virtual/misc/voodoo_sound/dac_osr128";
    public static final String WM8994_ADC_OSR128 = "/sys/devices/virtual/misc/voodoo_sound/adc_osr128";
    public static final String WM8994_FLL_TUNING = "/sys/devices/virtual/misc/voodoo_sound/fll_tuning";
    
    // Preference Objects
    private CheckBoxPreference mWm8994EnablePref;
    private CheckBoxPreference mSpeakerTuning;
    private CheckBoxPreference mMonoDownmix;
    private CheckBoxPreference mStereoExpansion;
    private CheckBoxPreference mDacDirect;
    private CheckBoxPreference mDacOsr128;
    private CheckBoxPreference mAdcOsr128;
    private CheckBoxPreference mFllTuning;
    
    // XML item names
    private static final String WM8994_ENABLE_PREF = "pref_wm8994_control_enable";
    private static final String SPEAKER_TUNING_PREF = "pref_wm8994_speaker_tuning";
    private static final String MONO_DOWNMIX_PREF = "pref_wm8994_mono_downmix";
    private static final String STEREO_EXPANSION_PREF = "pref_wm8994_stereo_expansion";
    private static final String DAC_DIRECT_PREF = "pref_wm8994_dac_direct";
    private static final String DAC_OSR128_PREF = "pref_wm8994_dac_osr128";
    private static final String ADC_OSR128_PREF = "pref_wm8994_adc_osr128";
    private static final String FLL_TUNING_PREF = "pref_wm8994_fll_tuning";
    
    // Categories
    private static final String GENERAL_CATEGORY = "general_category";
    private static final String INTERNAL_SPEAKER_CATEGORY = "wm8994_internal_speaker_category";
    private static final String SIGNAL_PROCESSING_CATEGORY = "wm8994_signal_processing_category";
    private static final String DAC_CONFIG_CATEGORY = "wm8994_dac_config_category";
    private static final String CODEC_OPTIMIZATION_CATEGORY = "wm8994_codec_optimization_category";
    
    // Misc
    private static final String PREF_ENABLED = "1";
    private static final String TAG = "WM8994Control";
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String temp;

        setTitle(R.string.wm8994_settings_title_subhead);
        addPreferencesFromResource(R.xml.wm8994_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        // "General" category
        PreferenceCategory generalCategory = (PreferenceCategory)prefSet.findPreference (GENERAL_CATEGORY);

        // WM8994 enabled
        temp = readOneLine(WM8994_ENABLE);
        mWm8994EnablePref = (CheckBoxPreference) prefSet.findPreference(WM8994_ENABLE_PREF);
        mWm8994EnablePref.setChecked(PREF_ENABLED.equals(temp));
        
        // "Internal Speaker" category
        PreferenceCategory internalSpeakerCategory = (PreferenceCategory)prefSet.findPreference(INTERNAL_SPEAKER_CATEGORY);
        
        // speaker tuning
        temp = readOneLine(WM8994_SPEAKER_TUNING);
        mSpeakerTuning = (CheckBoxPreference) prefSet.findPreference(SPEAKER_TUNING_PREF);
        mSpeakerTuning.setChecked(PREF_ENABLED.equals(temp));
        
        // "Signal Processing" category
        PreferenceCategory signalProcessingCategory = (PreferenceCategory)prefSet.findPreference(SIGNAL_PROCESSING_CATEGORY);
        
        // mono downmix
        temp = readOneLine(WM8994_MONO_DOWNMIX);
        mMonoDownmix = (CheckBoxPreference) prefSet.findPreference(MONO_DOWNMIX_PREF);
        mMonoDownmix.setChecked(PREF_ENABLED.equals(temp));
        
        // stereo expansion
        temp = readOneLine(WM8994_STEREO_EXPANSION);
        mStereoExpansion = (CheckBoxPreference) prefSet.findPreference(STEREO_EXPANSION_PREF);
        mStereoExpansion.setChecked(PREF_ENABLED.equals(temp));
        
        // D/AC A/DC category
        PreferenceCategory dacConfigCategory = (PreferenceCategory)prefSet.findPreference(DAC_CONFIG_CATEGORY);
        
        // DAC direct
        temp = readOneLine(WM8994_DAC_DIRECT);
        mDacDirect = (CheckBoxPreference) prefSet.findPreference(DAC_DIRECT_PREF);
        mDacDirect.setChecked(PREF_ENABLED.equals(temp));
        
        // DAC OSR128
        temp = readOneLine(WM8994_DAC_OSR128);
        mDacOsr128 = (CheckBoxPreference) prefSet.findPreference(DAC_OSR128_PREF);
        mDacOsr128.setChecked(PREF_ENABLED.equals(temp));
        
        // ADC OSR128
        temp = readOneLine(WM8994_ADC_OSR128);
        mAdcOsr128 = (CheckBoxPreference) prefSet.findPreference(ADC_OSR128_PREF);
        mAdcOsr128.setChecked(PREF_ENABLED.equals(temp));
        
        // CODEC optimization category
        PreferenceCategory codecOptimizationCategory = (PreferenceCategory)prefSet.findPreference(CODEC_OPTIMIZATION_CATEGORY);
        
        // FLL tuning
        temp = readOneLine(WM8994_FLL_TUNING);
        mFllTuning = (CheckBoxPreference) prefSet.findPreference(FLL_TUNING_PREF);
        mFllTuning.setChecked(PREF_ENABLED.equals(temp));
        
    }
    
    // Preference change action for check boxes
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    	
    	// WM8994 enable/disable box
    	if (preference == mWm8994EnablePref) {
    		String boxValue = mWm8994EnablePref.isChecked() ? "1" : "0";
    		commitSysfsPreference(WM8994_ENABLE, boxValue);
    	}
    	
    	// Speaker tuning enable/disable box
    	if (preference == mSpeakerTuning) {
    		String boxValue = mSpeakerTuning.isChecked() ? "1" : "0";
    		commitSysfsPreference(WM8994_SPEAKER_TUNING, boxValue);
    	}
    	
    	// Mono downmix enable/disable box
    	if (preference == mMonoDownmix) {
    		String boxValue = mMonoDownmix.isChecked() ? "1" : "0";
    		commitSysfsPreference(WM8994_MONO_DOWNMIX, boxValue);
    	}
    	
    	// Stereo expansion enable/disable box
    	if (preference == mStereoExpansion) {
    		String boxValue = mStereoExpansion.isChecked() ? "1" : "0";
    		commitSysfsPreference(WM8994_STEREO_EXPANSION, boxValue);
    	}
    	
    	// D/AC direct
    	if (preference == mDacDirect) {
    		String boxValue = mDacDirect.isChecked() ? "1" : "0";
    		commitSysfsPreference(WM8994_DAC_DIRECT, boxValue);
    	}
    	
    	// D/AC OSR128
    	if (preference == mDacOsr128) {
    		String boxValue = mDacOsr128.isChecked() ? "1" : "0";
    		commitSysfsPreference(WM8994_DAC_OSR128, boxValue);
    	}
    	
    	// A/DC OSR128
    	if (preference == mAdcOsr128) {
    		String boxValue = mAdcOsr128.isChecked() ? "1" : "0";
    		commitSysfsPreference(WM8994_ADC_OSR128, boxValue);
    	}
    	
    	// FLL tuning
    	if (preference == mFllTuning) {
    		String boxValue = mFllTuning.isChecked() ? "1" : "0";
    		commitSysfsPreference(WM8994_FLL_TUNING, boxValue);
    	}
    	
    	// otherwise quit, and return false (if preference name doesn't match a condition)
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
    
    // function to handle writing to sysfs interface
    public static boolean commitSysfsPreference(String prefFile, String prefValue) {
    	if (prefValue != null) {
			if (writeOneLine(prefFile, prefValue)) {
				String debug ="DEBUG: Wrote value " + prefValue + " to " + prefFile;
				Log.d(TAG, debug);
				return true;
			} else {
				String error = "Writing to " + prefFile + " failed! Value: " + prefValue;
				Log.e(TAG, error);
				return false;
			}
		}
    	return false;
    }
}

/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.chinafocus.huaweimdm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;


/**
 * The SharedPreferenceUtil for this Sample
 *
 * @author huawei mdm
 * @since 2019-10-23
 */
public class SharedPreferenceUtil  {
    private static String EULA_PREFIX = "eula_useraccepted_";
    private int mVersionCode;
    private String mEulaKey = null;
    private Context mContext = null;
    private SharedPreferences mSharedPreferences = null;
    
    public SharedPreferenceUtil(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // the eulaKey changes every time you increment the version number in AndroidManifest.xml
        mVersionCode = getVersionCodeInner();;
        mEulaKey = EULA_PREFIX + mVersionCode;
    }

    public boolean hasUserAccepted() {
        return mSharedPreferences.getBoolean(mEulaKey, false);
    }

    public void saveUserChoice(boolean accepted) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(mEulaKey, accepted);
        editor.commit();
    }
    
    public int getVersionCode(){
        return mVersionCode;
    }
    
    private int getVersionCodeInner() {
        PackageInfo pi = null;
        try {
             pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi.versionCode; 
    }
}
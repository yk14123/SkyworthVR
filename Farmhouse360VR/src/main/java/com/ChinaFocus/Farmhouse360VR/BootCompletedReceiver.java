package com.ChinaFocus.Farmhouse360VR;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.ssnwt.sdk.MainActivity;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "StartupReceiver");
        wl.acquire();
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("StartupReceiver");
        if (intent.getAction().equals(("android.intent.action.BOOT_COMPLETED"))) {
            Log.e("MyLog", "---------onReceive: 开机启动----------");
            //开机启动
            Intent mainIntent = new Intent(context, MainActivity.class);
            //在BroadcastReceiver中显示Activity，必须要设置FLAG_ACTIVITY_NEW_TASK标志
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }
    }
}
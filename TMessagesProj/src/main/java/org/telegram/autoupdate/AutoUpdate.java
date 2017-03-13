package org.telegram.autoupdate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import org.telegram.messenger.BuildConfig;

import java.io.File;


public class AutoUpdate {
    private static AutoUpdate instance = null;
    private File apk = null;
    private Activity context;

    public static AutoUpdate getInstance() {
        if (instance == null) instance = new AutoUpdate();
        return instance;
    }

    public void init(Activity context, String appName) {
        apk = new File(Environment.getExternalStorageDirectory(), appName + "_autoupdate.apk");
        this.context = context;
        setAppName(appName);
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apk.getPath(), 0);
        if (apk.exists() && info != null && versionCompare(BuildConfig.VERSION_CODE, info.versionCode))
            checkForInstall();


        if (getState() != 2) {
            context.stopService(new Intent(context, Service.class));
            context.startService(new Intent(context, Service.class));
        }
    }

    private void checkForInstall() {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.parse("file:///" + apk.getPath()), "application/vnd.android.package-archive")
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(promptInstall);
        context.finish();

    }

    private int getState() {
        SharedPreferences stateConfig = context.getSharedPreferences("autoupdate", Context.MODE_PRIVATE);
        return stateConfig.getInt("state", 0);
    }

    private void setAppName(String name) {
        SharedPreferences.Editor shareConfig = context.getSharedPreferences("autoupdate", Context.MODE_PRIVATE).edit();
        shareConfig.putString("name", name).commit();
    }

    private boolean versionCompare(int current, int apkversion) {
        return (apkversion > current);
        /*
        String[] var1 = current.split("\\.");
        String[] var2 = apkversion.split("\\.");
        for (int i = 0; i < var1.length; i++) {
            if (Integer.parseInt(var2[i]) > Integer.parseInt(var1[i]))
                return true;
        }
        return false;*/

    }
}
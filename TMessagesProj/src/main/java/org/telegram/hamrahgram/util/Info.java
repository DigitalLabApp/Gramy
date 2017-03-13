package org.telegram.hamrahgram.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;


public class Info {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public Info(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public String getApi(){
        String api = String.valueOf(Integer.valueOf(Build.VERSION.SDK_INT));
        return  api;
    }

    public String getCarrieName(){
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();
        return carrierName;
    }
    public String getVersion() {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = info.versionCode;
        return Integer.toString(version);
    }

    public String getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        String res = "ERROR";
        try {
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            int dens = dm.densityDpi;
            double wi = (double) width / (double) dens;
            double hi = (double) height / (double) dens;
            double x = Math.pow(wi, 2);
            double y = Math.pow(hi, 2);
            double screenInches = Math.sqrt(x + y);
            res = String.valueOf(screenInches);
            res = res.substring(0, 4);
        } catch (Exception e) {

        }

        return res;
    }


    public String getNetwork() {
        TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
        int Type = telephonyManager.getNetworkType();
        String netWorkType = "";
        switch (Type) {
            case 7:
                netWorkType = "1xRTT";
                break;
            case 4:
                netWorkType = "CDMA";
                break;
            case 2:
                netWorkType = "EDGE";
                break;
            case 14:
                netWorkType = "eHRPD";
                break;
            case 5:
                netWorkType = "EVDO rev. 0";
                break;
            case 6:
                netWorkType = "EVDO rev. A";
                break;
            case 12:
                netWorkType = "EVDO rev. B";
                break;
            case 1:
                netWorkType = "GPRS";
                break;
            case 8:
                netWorkType = "HSDPA";
                break;
            case 10:
                netWorkType = "HSPA";
                break;
            case 15:
                netWorkType = "HSPA+";
                break;
            case 9:
                netWorkType = "HSUPA";
                break;
            case 11:
                netWorkType = "iDen";
                break;
            case 13:
                netWorkType = "LTE";
                break;
            case 3:
                netWorkType = "UMTS";
                break;
            case 0:
                netWorkType = "Unknown";
                break;
        }
        return netWorkType;
    }

    public String getPhoneID1() {
        String phoneID1;
        phoneID1 = sharedPreferences.getString("ID", "");
        if (phoneID1.equals("")) {
            phoneID1 = generateID();// generateID();
            editor.putString("ID", phoneID1);
            editor.commit();
        }

        return phoneID1;
    }

    public String getPhoneID2() {
        String phoneID2;
        phoneID2 = readPhoneID2();
        if (phoneID2.equals("")) {
            phoneID2 = UUID.randomUUID().toString();
            storePhoneID2(phoneID2);
            phoneID2 = readPhoneID2();
        }

        return phoneID2;

    }

    private String readPhoneID2() {
        String id = "";
        StringBuilder text = new StringBuilder();
        String filename = "AndroidPhoneID.txt";
        File file = new File("/mnt/sdcard/", filename);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                //text.append('\n');
            }
            br.close();
            id = text.toString();
        } catch (Exception e) {
            //You'll need to add proper error handling here
        }
        return id;
    }

    private void storePhoneID2(String id) {
        String filename = "AndroidPhoneID.txt";
        File file = new File("/mnt/sdcard/", filename);
        FileOutputStream fos;
        byte[] data = id.getBytes();
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // handle exception
        } catch (IOException e) {
            // handle exception
        }
    }


    private String generateID() {
        final String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        final String model = Build.MODEL;
        final String manufacture = Build.MANUFACTURER;
        final String production = Build.PRODUCT;
        UUID deviceUuid = new UUID(androidId.hashCode() + manufacture.hashCode(), ((long) production.hashCode() << 32) | model.hashCode());
        return deviceUuid.toString();
    }


}

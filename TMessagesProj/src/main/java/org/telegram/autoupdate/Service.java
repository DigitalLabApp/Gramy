package org.telegram.autoupdate;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.coolerfall.download.DownloadCallback;
import com.coolerfall.download.DownloadManager;
import com.coolerfall.download.DownloadRequest;
import com.coolerfall.download.OkHttpDownloader;
import com.coolerfall.download.Priority;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.R;
import org.telegram.ui.LaunchActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class Service extends android.app.Service {
    private boolean showInProg = false;
    private int interval;
    private File apk = null;
    private DownloadManager downloadManager;
    private Context context;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private String webServicePath = "http://backend.appsazan.com/autoUpdate.svc/CU/";
    private final int id = 1;
    private InputStream is = null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init(this, getAppName() + "_autoupdate.apk");
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);

    }

    public void init(Context context, String appName) {
        this.context = context;
        webServicePath += context.getPackageName();

        apk = new File(Environment.getExternalStorageDirectory(), appName);
        OkHttpClient client = new OkHttpClient.Builder().build();
        downloadManager =
                new DownloadManager.Builder().context(context)
                        .downloader(OkHttpDownloader.create(client))
                        .threadPoolSize(2)
                        .build();
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("بروزرسانی گرامی")
                .setContentText("در حال بروزرسانی")
                .setSmallIcon(R.drawable.appico);
        final PackageManager pm = context.getPackageManager();
        String fullPath = apk.getPath();
        PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);
        if (isOnline(context)) {
            new checkForUpdate().execute();
        } else {
            setAlarm(100000);
            stopSelf();

        }
    }

    private void startDownload(String apkurl) {
        setState(2);
        cancleAlarmManager();
        mBuilder.setProgress(100, 0, false);
        if (showInProg)
            mNotifyManager.notify(id, mBuilder.build());
        DownloadRequest request = new DownloadRequest.Builder()
                .url(apkurl)
                .downloadCallback(new Callback())
                .retryTime(5)
                .retryInterval(3, TimeUnit.SECONDS)
                .progressInterval(1, TimeUnit.SECONDS)
                .destinationFilePath(apk.getPath())
                .priority(Priority.NORMAL)
                .allowedNetworkTypes(DownloadRequest.NETWORK_WIFI)
                .build();
        int downloadId = downloadManager.add(request);
    }

    private class Callback extends DownloadCallback {
        private long startTimestamp = 0;
        private long startSize = 0;

        @Override
        public void onStart(int downloadId, long totalBytes) {
            startTimestamp = System.currentTimeMillis();
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!isOnline(context)) {
                        setState(-1);
                        setAlarm(interval);
                        downloadManager.cancelAll();
                        if (showInProg)
                            mNotifyManager.cancel(id);
                    }
                }
            }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }

        @Override
        public void onRetry(int downloadId) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgress(int downloadId, long bytesWritten, long totalBytes) {
            int progress = (int) (bytesWritten * 100f / totalBytes);
            progress = progress == 100 ? 0 : progress;
            long currentTimestamp = System.currentTimeMillis();
            int speed;
            int deltaTime = (int) (currentTimestamp - startTimestamp + 1);
            speed = (int) ((bytesWritten - startSize) * 1000 / deltaTime) / 1024;
            startSize = bytesWritten;
            mBuilder.setProgress(100, progress, false);
            if (showInProg)
                mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        public void onSuccess(int downloadId, String filePath) {
            mBuilder.setContentText("بروزرسانی به اتمام رسید");
            mBuilder.setProgress(0, 0, false);
            if (showInProg)
                mNotifyManager.notify(id, mBuilder.build());
            mNotifyManager.cancel(id);
            PushNotification(context);

            //  installAPK();
            downloadManager.release();

            setState(1);
            setAlarm(interval);
            stopSelf();
        }

        @Override
        public void onFailure(int downloadId, int statusCode, String errMsg) {
            mBuilder.setContentText("بروزرسانی قطع شد");
            mBuilder.setProgress(0, 0, false);
            if (showInProg)
                mNotifyManager.notify(id, mBuilder.build());

            setState(-1);
            setAlarm(interval);
            stopSelf();
            mNotifyManager.cancel(id);
        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    private void installAPK() {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.parse("file:///" + apk.getPath()),
                        "application/vnd.android.package-archive")

                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(promptInstall);
    }

    private class checkForUpdate extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(webServicePath);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
            try {
                if (is != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    return sb.toString();
                }

            } catch (Exception e) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    JSONObject obj = new JSONObject(s);
                    interval = obj.getInt("interval") * 3600000;
                    showInProg = obj.getBoolean("showInProgressBar");
                    if (versionCompare(BuildConfig.VERSION_CODE, obj.getInt("version"))) {
                        if (!checkLocalUpdate()) {
                            // if (apk.exists()) apk.delete();

                            startDownload(obj.getString("address"));
                        } else setAlarm(interval);
                    } else {
                        stopSelf();
                        setAlarm(interval);
                    }

                } catch (Exception e) {
                }
            } else setAlarm(3600000);
        }
    }

    private void setAlarm(int interval) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + interval, restartServicePendingIntent);

    }

    private void cancleAlarmManager() {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.cancel(restartServicePendingIntent);

    }

    private void setState(int state) {
        SharedPreferences.Editor stateConfig = getSharedPreferences("autoupdate", Context.MODE_PRIVATE).edit();
        stateConfig.putInt("state", state).commit();
    }

    private boolean versionCompare(int current, int json) {
        return (json > current);

     /*   String[] var1 = current.split("\\.");
        String[] var2 = json.split("\\.");
        for (int i = 0; i < var1.length; i++) {
            if (Integer.parseInt(var2[i]) > Integer.parseInt(var1[i]))
                return true;
        }
        return false;*/

    }

    private String getAppName() {
        SharedPreferences shareConfig = getSharedPreferences("autoupdate", Context.MODE_PRIVATE);
        return shareConfig.getString("name", "");

    }

    private boolean checkLocalUpdate() {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apk.getPath(), 0);
        return apk.exists() && info != null && versionCompare(BuildConfig.VERSION_CODE, info.versionCode);


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void PushNotification(Context ctx) {
        try {
            NotificationManager nm = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(ctx);
            Intent notificationIntent = new Intent(ctx, LaunchActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
            builder.setContentIntent(contentIntent);
            builder.setSmallIcon(R.drawable.appico);
            builder.setContentText("برای بروزرسانی ضربه بزنید");
            builder.setContentTitle("بروزرسانی گرامی");
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_ALL);
            Notification notification = builder.build();
            nm.notify((int) System.currentTimeMillis(), notification);
        } catch (Exception e) {

        }
    }
}

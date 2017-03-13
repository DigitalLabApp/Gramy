package org.telegram.hamrahgram;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.farsitel.bazaar.ILoginCheckService;

import org.telegram.hamrahgram.util.payment.IabHelper;
import org.telegram.hamrahgram.util.payment.IabResult;
import org.telegram.hamrahgram.util.payment.Inventory;
import org.telegram.hamrahgram.util.payment.Purchase;
import org.telegram.hamrahgram.util.payment.SkuDetails;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;


public class BuyPremiumActivity extends Activity {
    private ILoginCheckService service;
    private LoginCheckServiceConnection connection;
    private static final String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDENCJOzpSOPhYj58BB4qaM0W97qAO+ooD/zM+3cKrJAjw1UbvHDG+50a/h9G1I6RT2T6t4ouZWeW2sjYAGE7mFWHew8ZWMzlwcHcHXuzPZguCwIi3STRdx9C8+KYtdgczgdHrc/hLqCyXZe8//bjM0UtMsIBCVRLGcuFmn+eGJIY10vBlIQ9jbDRBcfJvdRCgz3a5qb8SVd4gpcuzPBbR7lHLHrJL7PBG8kXjNxH8CAwEAAQ==";
    private static final String SKU_ANALYTICS = "Amargir";
    private static final int RC_REQUEST = 10001;
    private SharedPreferences.Editor share1;
    private IabHelper mHelper;
    private Button buy;
    private TextView description;
    private TextView title;
    private boolean pricessLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (!appInstalledOrNot("com.farsitel.bazaar")) {
            Toast.makeText(this, LocaleController.getString("CaffebazaarNotInstalled", R.string.CaffebazaarNotInstalled), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        initService();

    }

    private void initPurchase() {
        share1 = getSharedPreferences("payment", Context.MODE_PRIVATE).edit();
        setContentView(R.layout.inapp_purchase);
        buy = (Button) findViewById(R.id.buy);
        buy.setText(LocaleController.getString("WaitingForGettingPrices", R.string.WaitingForGettingPrices));
        buy.setEnabled(false);
        description = (TextView) findViewById(R.id.description);
        title = (TextView) findViewById(R.id.title);
        buy.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        description.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        title.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        buy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pricessLoaded) return;
                buy.setEnabled(false);
                buy.setText(LocaleController.getString("WaitingForGettingPrices", R.string.WaitingForGettingPrices));
                if (!mHelper.subscriptionsSupported()) {
                    return;
                }

                mHelper.launchPurchaseFlow(BuyPremiumActivity.this, SKU_ANALYTICS, RC_REQUEST, mPurchaseFinishedListener, "");
                buy.setText(LocaleController.getString("Payment", R.string.Payment));
                buy.setEnabled(true);
            }
        });

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    return;
                }
                if (mHelper == null) return;

                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseService();
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    boolean verifyDeveloperPayload(Purchase p) {
        return true;
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (mHelper == null) return;
            if (result.isFailure()) {
                return;
            }
            SkuDetails details = inventory.getSkuDetails(SKU_ANALYTICS);
            pricessLoaded = true;
            buy.setText(LocaleController.getString("Payment", R.string.Payment));
            buy.setEnabled(true);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {

        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (mHelper == null) return;
            if (result.isFailure()) {
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                buy.setText(LocaleController.getString("Payment", R.string.Payment));
                buy.setEnabled(true);
                return;
            }
            if (purchase.getSku().equals(SKU_ANALYTICS)) {
                share1.putBoolean("analytics", true).commit();
                Toast.makeText(BuyPremiumActivity.this, LocaleController.getString("ThanksForPurchase", R.string.ThanksForPurchase), Toast.LENGTH_LONG).show();

                finish();
            }
            buy.setEnabled(true);
        }
    };

    @Override
    public void onBackPressed() {
        if (buy != null && !buy.isEnabled()) return;
        else

            super.onBackPressed();
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;

        }

    }

    private void initService() {
        connection = new LoginCheckServiceConnection();
        Intent i = new Intent("com.farsitel.bazaar.service.LoginCheckService.BIND");
        i.setPackage("com.farsitel.bazaar");
        boolean ret = bindService(i, connection, Context.BIND_AUTO_CREATE);
    }

    private void releaseService() {
        unbindService(connection);
        connection = null;
    }

    public class LoginCheckServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = ILoginCheckService.Stub.asInterface((IBinder) boundService);
            try {
                boolean isLoggedIn = service.isLoggedIn();
                if (isLoggedIn) {
                    initPurchase();
                } else {
                    Toast.makeText(BuyPremiumActivity.this, LocaleController.getString("NeedLoginBazaar", R.string.NeedLoginBazaar), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("bazaar://login"));
                    intent.setPackage("com.farsitel.bazaar");
                    startActivity(intent);
                    finish();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    }
}



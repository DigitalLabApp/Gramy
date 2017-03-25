package org.telegram.hamrahgram.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.telegram.hamrahgram.util.Info;
import org.telegram.hamrahgram.util.JSONParser;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;

public class ContactUs extends BaseFragment {


    private EditText email;
    private EditText message;
    private Button done;


    private static class LinkMovementMethodMy extends LinkMovementMethod {
        @Override
        public boolean onTouchEvent(@NonNull TextView widget, @NonNull Spannable buffer, @NonNull MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
            return false;
        }
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override
    public View createView(final Context context) {
        final ActionBarMenu menu = actionBar.createMenu();
        email = new EditText(getParentActivity());
        email.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        email.setTextColor(Color.BLACK);
        email.setHintTextColor(0xff6d6d72);
        email.setSingleLine();
        email.setHint(ApplicationLoader.mContext.getResources().getString(R.string.EnterEmail));
        message = new EditText(getParentActivity());
        message.setHint(ApplicationLoader.mContext.getResources().getString(R.string.EnterMessage));
        //  message.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        //  message.setLines(10);
        message.setGravity(Gravity.TOP);
        message.setPadding(0, AndroidUtilities.dp(10), 0, 0);
        message.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        message.setTextColor(Color.BLACK);
        message.setHintTextColor(0xff6d6d72);

        Drawable drawable = email.getBackground();
        drawable.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_ATOP);
     /*   if (Build.VERSION.SDK_INT > 16) {
            email.setBackground(drawable);
            message.setBackground(drawable);
        } else {
            email.setBackgroundDrawable(drawable);
            message.setBackgroundDrawable(drawable);

        }*/
        done = new Button(getParentActivity());
        done.setText(getParentActivity().getApplicationContext().getResources().getString(R.string.Send));
        done.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkEmailText = email.getText().toString();
                String checkMessageText = message.getText().toString();
                checkEmailText.replaceAll("'", "");
                checkMessageText.replaceAll("'", "");

                if (isConnectingToInternet()) {
                    if (TextUtils.isEmpty(checkEmailText)) {
                        email.setError("لطفا ایمیل یا شماره تماس خود را وارد نمایید");
                    } else if (TextUtils.isEmpty(checkMessageText)) {
                        message.setError("لطفا متن پیام خود را وارد نمایید");
                    } else {
                        saveErrorLog(getParentActivity());
                        Toast.makeText(getParentActivity(), "پیام شما با موفقیت ارسال گردید.", Toast.LENGTH_SHORT).show();
                        finishFragment();
                    }
                } else {
                    Toast.makeText(getParentActivity(), "اتصال شما به اینترنت مقدور نمیباشد", Toast.LENGTH_SHORT).show();
                }
            }

        });
        if (Build.VERSION.SDK_INT > 16)
            done.setBackground(makeSelector(Color.alpha(20)));
        else done.setBackgroundDrawable(makeSelector(Color.BLUE));
        done.setTextColor(Color.WHITE);

        done.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAddToContainer(false);
        actionBar.setBackgroundColor(Color.parseColor(ApplicationLoader.applicationTheme));
        actionBar.setTitle(ApplicationLoader.applicationContext.getResources().getString(R.string.contactus));

        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });


        fragmentView = new FrameLayout(context) {
            @Override
            protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {

                return super.drawChild(canvas, child, drawingTime);

            }
        };
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.addView(actionBar);
        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(email);
        linearLayout.addView(message);
        linearLayout.addView(done);
        message.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(200)));
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));
        ViewGroup.MarginLayoutParams marginLayoutParamsdone = (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
        marginLayoutParamsdone.setMargins(AndroidUtilities.dp(5), AndroidUtilities.dp(100), AndroidUtilities.dp(5), AndroidUtilities.dp(5));
        needLayout();
        return fragmentView;
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        MediaController.getInstance().checkAutodownloadSettings();
    }

    public void performAskAQuestion() {
        final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        int uid = preferences.getInt("support_id", 0);
        TLRPC.User supportUser = null;
        if (uid != 0) {
            supportUser = MessagesController.getInstance().getUser(uid);
            if (supportUser == null) {
                String userString = preferences.getString("support_user", null);
                if (userString != null) {
                    try {
                        byte[] datacentersBytes = Base64.decode(userString, Base64.DEFAULT);
                        if (datacentersBytes != null) {
                            SerializedData data = new SerializedData(datacentersBytes);
                            supportUser = TLRPC.User.TLdeserialize(data, data.readInt32(false), false);
                            if (supportUser != null && supportUser.id == 333000) {
                                supportUser = null;
                            }
                            data.cleanup();
                        }
                    } catch (Exception e) {
                        FileLog.e("tmessages", e);
                        supportUser = null;
                    }
                }
            }
        }
        if (supportUser == null) {
            final ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
            progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            TLRPC.TL_help_getSupport req = new TLRPC.TL_help_getSupport();
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                @Override
                public void run(TLObject response, TLRPC.TL_error error) {
                    if (error == null) {

                        final TLRPC.TL_help_support res = (TLRPC.TL_help_support) response;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("support_id", res.user.id);
                                SerializedData data = new SerializedData();
                                res.user.serializeToStream(data);
                                editor.putString("support_user", Base64.encodeToString(data.toByteArray(), Base64.DEFAULT));
                                editor.commit();
                                data.cleanup();
                                try {
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    FileLog.e("tmessages", e);
                                }
                                ArrayList<TLRPC.User> users = new ArrayList<>();
                                users.add(res.user);
                                MessagesStorage.getInstance().putUsersAndChats(users, null, true, true);
                                MessagesController.getInstance().putUser(res.user, false);
                                Bundle args = new Bundle();
                                args.putInt("user_id", res.user.id);
                            }
                        });
                    } else {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    FileLog.e("tmessages", e);
                                }
                            }
                        });
                    }
                }
            });
        } else {
            MessagesController.getInstance().putUser(supportUser, true);
            Bundle args = new Bundle();
            args.putInt("user_id", supportUser.id);
        }
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void saveSelfArgs(Bundle args) {

    }

    @Override
    public void restoreSelfArgs(Bundle args) {

    }


    @Override
    public void onResume() {
        super.onResume();
        if (DialogsActivity.helpmode) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishFragment();

                }
            }, 500);
            return;
        }

        updateUserData();
        fixLayout();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    private void needLayout() {
        FrameLayout.LayoutParams layoutParams;
        int newTop = (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();


    }

    private void fixLayout() {
        if (fragmentView == null) {
            return;
        }
        fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (fragmentView != null) {
                    needLayout();
                    fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
    }

    private void updateUserData() {
        TLRPC.User user = MessagesController.getInstance().getUser(UserConfig.getClientUserId());
        TLRPC.FileLocation photo = null;
        TLRPC.FileLocation photoBig = null;
        if (user.photo != null) {
            photo = user.photo.photo_small;
            photoBig = user.photo.photo_big;
        }

    }

    public static StateListDrawable makeSelector(int color) {
        StateListDrawable res = new StateListDrawable();
        res.setExitFadeDuration(2000);
        res.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
        res.addState(new int[]{}, new ColorDrawable(Color.parseColor(ApplicationLoader.applicationTheme)));
        return res;
    }

    private void saveErrorLog(Context context) {
        if (new JSONParser().isInternetAvailable(context)) {

            Info info = new Info(context);
            ArrayList params = new ArrayList<>();
            params.add(new BasicNameValuePair("phoneId1", info.getPhoneID1()));
            params.add(new BasicNameValuePair("packageName", context.getPackageName()));
            params.add(new BasicNameValuePair("numberOrEmail", email.getText().toString()));
            params.add(new BasicNameValuePair("Description", message.getText().toString()));
            params.add(new BasicNameValuePair("Operator", info.getCarrieName()));
            params.add(new BasicNameValuePair("model", Build.MODEL));
            params.add(new BasicNameValuePair("api", info.getApi()));


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = "http://backend.appsazan.com/contactus.svc/gcd/";
                        String s = new JSONParser().makeHttpRequest(url, "POST", params, 0);

                    } catch (Exception e) {

                    }

                }
            }).start();
        }

    }

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getParentActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

}

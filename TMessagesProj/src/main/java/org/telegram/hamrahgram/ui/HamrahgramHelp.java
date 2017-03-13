package org.telegram.hamrahgram.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.hamrahgram.database.Database;
import org.telegram.hamrahgram.IHelpWizard;
import org.telegram.hamrahgram.util.Analytics;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * <h1>java CustomSetting class in org.telegram.ui</h1>
 *
 * @author Hossein Moradi
 * @version 1.0
 * @since 1394
 */
public class HamrahgramHelp extends BaseFragment {
    private ListView listView;
    private ListAdapter listAdapter;


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
        ((IHelpWizard) DialogsActivity.fragmentHolder).onHelpEnable();
        super.onFragmentDestroy();
    }

    @Override
    public View createView(final Context context) {
        Analytics.getInstance(getParentActivity()).setScreen("Support");
        final ActionBarMenu menu = actionBar.createMenu();
        actionBar.setTitle(getParentActivity().getApplicationContext().getResources().getString(R.string.qhelp));
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAddToContainer(false);
        actionBar.setBackgroundColor(Color.parseColor(ApplicationLoader.applicationTheme));
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
        listAdapter = new ListAdapter(context);
        fragmentView = new FrameLayout(context) {
            @Override
            protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {
                if (child == listView) {
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    if (parentLayout != null) {
                        int actionBarHeight = 0;
                        int childCount = getChildCount();
                        for (int a = 0; a < childCount; a++) {
                            View view = getChildAt(a);
                            if (view == child) {
                                continue;
                            }
                            if (view instanceof ActionBar && view.getVisibility() == VISIBLE) {
                                if (((ActionBar) view).getCastShadows()) {
                                    actionBarHeight = view.getMeasuredHeight();
                                }
                                break;
                            }
                        }
                        parentLayout.drawHeaderShadow(canvas, actionBarHeight);
                    }
                    return result;
                } else {
                    return super.drawChild(canvas, child, drawingTime);
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(listView, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                switch (i) {
                    case 2:
                        Analytics.getInstance(getParentActivity()).sendEvent("Support", "Click", Analytics.INVITE_FRIENDS);
                        String[] blacklist = new String[]{
                                "org.telegram.messenger"
                                , "ir.persianfox.messenger"
                                , "ir.amatis.vistagram"
                                , "ir.felegram"
                                , "com.shaltouk.mytelegram"
                                , "ir.teletalk.app"
                                , "ir.rrgc.telegram"
                                , "org.telegram.igram"
                                , "ir.alimodaresi.mytelegram"
                                , "org.securetelegram.messenger"
                                , "com.telepersian.behdadsystem"
                                , "org.telegram.engmariaamani.messenger"
                                , "org.mygram"
                                , "com.goldengram"
                                , "ir.pishroid.telehgram"
                                , "ir.ahoura.messenger"
                                , "com.mostfet.redegram"
                                , "com.baranak.turbogram"
                                , "com.mihan.mihangram"
                                , "com.nitro.telegram"
                                , "com.negahetazehco.cafetelegram"
                                , "ir.amirsoft.mobonitro"
                                , "ir.ndesign_ir.lock.telegramfree"
                                , "com.bobardo.telegramPlayer"
                                , "ir.rana.telegramgallery"
                                , "com.panahit.telegramma"
                                , "org.abbasnaghdi.messenger"
                                , "com.srsoft.telegramidfinder"
                                , "ir.javan.messenger"
                                , "ir.skyt.telegramplus"
                                , "com.hanista.mobogram"

                        };
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, ContactsController.getInstance().getInviteText());
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, LocaleController.getString("InviteFriends", R.string.InviteFriends));
                        getParentActivity().startActivity(generateCustomChooserIntent(intent, blacklist));
                        break;
                    case 3:
                        if (Database.getInstance().checkHiddenMode(getParentActivity())) {
                            Toast.makeText(getParentActivity(), getParentActivity().getResources().getString(R.string.FirstChangeMode), Toast.LENGTH_LONG).show();
                        } else {
                            DialogsActivity.helpmode = true;
                            finishFragment();
                        }
                        Analytics.getInstance(getParentActivity()).sendEvent("Support", "Click", Analytics.HAMRAHGRAM_HELP);
                        break;

                    case 4:
                        Browser.openUrl(getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
                        Analytics.getInstance(getParentActivity()).sendEvent("Support", "Click", Analytics.COMMON_QUESTIONS);
                        break;
                    case 5:
                        if (getParentActivity() == null) {
                            return;
                        }
                        final TextView message = new TextView(getParentActivity());
                        message.setText(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", R.string.AskAQuestionInfo)));
                        message.setTextSize(ApplicationLoader.textSize + 2);
                        message.setLinkTextColor(Theme.MSG_LINK_TEXT_COLOR);
                        message.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(5), AndroidUtilities.dp(8), AndroidUtilities.dp(6));
                        message.setMovementMethod(new LinkMovementMethodMy());

                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setView(message);
                        builder.setPositiveButton(LocaleController.getString("AskButton", R.string.AskButton), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                performAskAQuestion();
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        showDialog(builder.create());
                        Analytics.getInstance(getParentActivity()).sendEvent("Support", "Click", Analytics.ASK_A_QUESTION);
                        break;
                    case 6:
                        presentFragment(new ContactUs());

                        Analytics.getInstance(getParentActivity()).sendEvent("Support", "Click", Analytics.CONTACT_US);
                        break;
                }


            }
        });
        frameLayout.addView(actionBar);
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
            // presentFragment(new ChatActivity(args));
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

        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
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
        if (listView != null) {
            layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                listView.setLayoutParams(layoutParams);

            }
        }

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


    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    view = new EmptyCell(mContext);
                }
                if (i == 1) {

                } else {

                }

            } else if (type == 1) {
                if (view == null) {
                    view = new TextSettingsCell(mContext);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == 2) {
                    textCell.setText(LocaleController.getString("invitefriends", R.string.invitefriends), true);
                }
                if (i == 3) {
                    textCell.setText(LocaleController.getString("hamrahgramhelp", R.string.hamrahgramhelp), true);
                }


                if (i == 4) {
                    textCell.setText(LocaleController.getString("TelegramFaq", R.string.TelegramFaq), true);
                }
                if (i == 5) {
                    textCell.setText(LocaleController.getString("answeryourquestion", R.string.answeryourquestion), true);
                }
                if (i == 6) {
                    textCell.setText(LocaleController.getString("contactus", R.string.contactus), true);
                }

            }


            return view;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 0 || i == 1) {
                return 0;
            } else return 1;

        }

        @Override
        public int getViewTypeCount() {
            return 7;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }


    private Intent generateCustomChooserIntent(Intent prototype, String[] forbiddenChoices) {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        List<HashMap<String, String>> intentMetaInfo = new ArrayList<HashMap<String, String>>();
        Intent chooserIntent;
        Intent dummy = new Intent(prototype.getAction());
        dummy.setType(prototype.getType());
        List<ResolveInfo> resInfo = getParentActivity().getPackageManager().queryIntentActivities(dummy, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (resolveInfo.activityInfo == null || Arrays.asList(forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
                    continue;
                HashMap<String, String> info = new HashMap<String, String>();
                info.put("packageName", resolveInfo.activityInfo.packageName);
                info.put("className", resolveInfo.activityInfo.name);
                String appName = String.valueOf(resolveInfo.activityInfo.loadLabel(getParentActivity().getPackageManager()));
                info.put("simpleName", appName);
                intentMetaInfo.add(info);
            }

            if (!intentMetaInfo.isEmpty()) {
                Collections.sort(intentMetaInfo,
                        new Comparator<HashMap<String, String>>() {
                            @Override
                            public int compare(
                                    HashMap<String, String> map,
                                    HashMap<String, String> map2) {
                                return map.get("simpleName").compareTo(
                                        map2.get("simpleName"));
                            }
                        });
                for (HashMap<String, String> metaInfo : intentMetaInfo) {
                    Intent targetedShareIntent = (Intent) prototype.clone();
                    targetedShareIntent.setPackage(metaInfo.get("packageName"));
                    targetedShareIntent.setClassName(
                            metaInfo.get("packageName"),
                            metaInfo.get("className"));
                    targetedShareIntents.add(targetedShareIntent);
                }
                String shareVia = LocaleController.getString("InviteFriends", R.string.InviteFriends);
                String shareTitle = shareVia.substring(0, 1).toUpperCase()
                        + shareVia.substring(1);
                chooserIntent = Intent.createChooser(targetedShareIntents
                        .remove(targetedShareIntents.size() - 1), shareTitle);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents.toArray(new Parcelable[]{}));
                return chooserIntent;
            }
        }

        return Intent.createChooser(prototype, "");
    }
}

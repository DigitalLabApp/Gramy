package org.telegram.hamrahgram.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.query.BotQuery;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;

import java.io.File;
import java.util.ArrayList;

public class InternetAndMemorySetting extends BaseFragment {
    private ListView listView;
    private ListAdapter listAdapter;
    private long databaseSize = -1;

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
        actionBar.setTitle(getParentActivity().getApplicationContext().getResources().getString(R.string.InternetAndMemorySetting));

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
                        presentFragment(new CacheControlActivity());

                        break;
                    case 3:
                        localDatabase();

                        break;
                    case 4:

                        downloadState(0);
                        break;
                    case 5:
                        downloadState(1);
                        break;

                    case 6:
                        downloadState(2);
                        break;
                    case 7:
                        MediaController.getInstance().toggleSaveToGallery();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(MediaController.getInstance().canSaveToGallery());
                        }
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
            return 8;
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
            String[] itemNames = mContext.getResources().getStringArray(R.array.internetandmemorysetting);

            if (type == 0) {
                if (view == null) {
                    view = new EmptyCell(mContext);
                }
                if (i == 1) {

                } else {

                }

            } else if (type == 1) {


                if (i - 2 == itemNames.length - 1) {

                    if (view == null) {
                        view = new TextCheckCell(mContext);
                        TextCheckCell textCell = (TextCheckCell) view;
                        textCell.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", R.string.SaveToGallerySettings), MediaController.getInstance().canSaveToGallery(), false);
                    }


                } else {
                    if (view == null) {
                        view = new TextSettingsCell(mContext);
                    }
                    TextSettingsCell textCell = (TextSettingsCell) view;
                    textCell.setText(itemNames[i - 2], true);
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

    private void localDatabase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setMessage(LocaleController.getString("LocalDatabaseClear", R.string.LocalDatabaseClear));
        builder.setPositiveButton(LocaleController.getString("CacheClear", R.string.CacheClear), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
                progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                            ArrayList<Long> dialogsToCleanup = new ArrayList<>();
                            SQLiteCursor cursor = database.queryFinalized("SELECT did FROM dialogs WHERE 1");
                            StringBuilder ids = new StringBuilder();
                            while (cursor.next()) {
                                long did = cursor.longValue(0);
                                int lower_id = (int) did;
                                int high_id = (int) (did >> 32);
                                if (lower_id != 0 && high_id != 1) {
                                    dialogsToCleanup.add(did);
                                }
                            }
                            cursor.dispose();

                            SQLitePreparedStatement state5 = database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                            SQLitePreparedStatement state6 = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                            SQLitePreparedStatement state7 = database.executeFast("REPLACE INTO messages_imp_holes VALUES(?, ?, ?)");
                            SQLitePreparedStatement state8 = database.executeFast("REPLACE INTO channel_group VALUES(?, ?, ?, ?)");

                            database.beginTransaction();
                            for (int a = 0; a < dialogsToCleanup.size(); a++) {
                                Long did = dialogsToCleanup.get(a);
                                int messagesCount = 0;
                                cursor = database.queryFinalized("SELECT COUNT(mid) FROM messages WHERE uid = " + did);
                                if (cursor.next()) {
                                    messagesCount = cursor.intValue(0);
                                }
                                cursor.dispose();
                                if (messagesCount <= 2) {
                                    continue;
                                }

                                cursor = database.queryFinalized("SELECT last_mid_i, last_mid FROM dialogs WHERE did = " + did);
                                ArrayList<TLRPC.Message> arrayList = new ArrayList<>();
                                if (cursor.next()) {
                                    long last_mid_i = cursor.longValue(0);
                                    long last_mid = cursor.longValue(1);
                                    SQLiteCursor cursor2 = database.queryFinalized("SELECT data FROM messages WHERE uid = " + did + " AND mid IN (" + last_mid_i + "," + last_mid + ")");
                                    try {
                                        while (cursor2.next()) {
                                        }
                                    } catch (Exception e) {
                                        FileLog.e("tmessages", e);
                                    }
                                    cursor2.dispose();

                                    database.executeFast("DELETE FROM messages WHERE uid = " + did + " AND mid != " + last_mid_i + " AND mid != " + last_mid).stepThis().dispose();
                                    database.executeFast("DELETE FROM channel_group WHERE uid = " + did).stepThis().dispose();
                                    database.executeFast("DELETE FROM messages_holes WHERE uid = " + did).stepThis().dispose();
                                    database.executeFast("DELETE FROM messages_imp_holes WHERE uid = " + did).stepThis().dispose();
                                    database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + did).stepThis().dispose();
                                    database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + did).stepThis().dispose();
                                    database.executeFast("DELETE FROM media_v2 WHERE uid = " + did).stepThis().dispose();
                                    database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + did).stepThis().dispose();
                                    BotQuery.clearBotKeyboard(did, null);
                                    //h333
                                    //    MessagesStorage.createFirstHoles(did, state5, state6, state7, state8, arrayList);
                                }
                                cursor.dispose();
                            }
                            state5.dispose();
                            state6.dispose();
                            state7.dispose();
                            state8.dispose();
                            database.commitTransaction();
                            database.executeFast("VACUUM").stepThis().dispose();
                        } catch (Exception e) {
                            FileLog.e("tmessages", e);
                        } finally {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        progressDialog.dismiss();
                                    } catch (Exception e) {
                                        FileLog.e("tmessages", e);
                                    }
                                    if (listAdapter != null) {
                                        File file = new File(ApplicationLoader.getFilesDirFixed(), "cache4.Database");
                                        databaseSize = file.length();
                                        listAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        showDialog(builder.create());
    }

    private void downloadState(int i) {
        if (getParentActivity() == null) {
            return;
        }
        final boolean maskValues[] = new boolean[6];
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());

        int mask = 0;
        if (i == 0) {
            mask = MediaController.getInstance().mobileDataDownloadMask;
        } else if (i == 1) {
            mask = MediaController.getInstance().wifiDownloadMask;
        } else if (i == 2) {
            mask = MediaController.getInstance().roamingDownloadMask;
        }

        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (int a = 0; a < 6; a++) {
            String name = null;
            if (a == 0) {
                maskValues[a] = (mask & MediaController.AUTODOWNLOAD_MASK_PHOTO) != 0;
                name = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
            } else if (a == 1) {
                maskValues[a] = (mask & MediaController.AUTODOWNLOAD_MASK_AUDIO) != 0;
                name = LocaleController.getString("AttachAudio", R.string.AttachAudio);
            } else if (a == 2) {
                maskValues[a] = (mask & MediaController.AUTODOWNLOAD_MASK_VIDEO) != 0;
                name = LocaleController.getString("AttachVideo", R.string.AttachVideo);
            } else if (a == 3) {
                maskValues[a] = (mask & MediaController.AUTODOWNLOAD_MASK_DOCUMENT) != 0;
                name = LocaleController.getString("AttachDocument", R.string.AttachDocument);
            } else if (a == 4) {
                maskValues[a] = (mask & MediaController.AUTODOWNLOAD_MASK_MUSIC) != 0;
                name = LocaleController.getString("AttachMusic", R.string.AttachMusic);
            } else if (a == 5) {
                maskValues[a] = (mask & MediaController.AUTODOWNLOAD_MASK_GIF) != 0;
                name = LocaleController.getString("AttachGif", R.string.AttachGif);
            }
            CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity());
            checkBoxCell.setTag(a);
            checkBoxCell.setBackgroundResource(R.drawable.list_selector);
            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
            checkBoxCell.setText(name, "", maskValues[a], true);
            checkBoxCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBoxCell cell = (CheckBoxCell) v;
                    int num = (Integer) cell.getTag();
                    maskValues[num] = !maskValues[num];
                    cell.setChecked(maskValues[num], true);
                }
            });
        }
        BottomSheet.BottomSheetCell cell = new BottomSheet.BottomSheetCell(getParentActivity(), 1);
        cell.setBackgroundResource(R.drawable.list_selector);
        cell.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
        cell.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (visibleDialog != null) {
                        visibleDialog.dismiss();
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
                int newMask = 0;
                for (int a = 0; a < 6; a++) {
                    if (maskValues[a]) {
                        if (a == 0) {
                            newMask |= MediaController.AUTODOWNLOAD_MASK_PHOTO;
                        } else if (a == 1) {
                            newMask |= MediaController.AUTODOWNLOAD_MASK_AUDIO;
                        } else if (a == 2) {
                            newMask |= MediaController.AUTODOWNLOAD_MASK_VIDEO;
                        } else if (a == 3) {
                            newMask |= MediaController.AUTODOWNLOAD_MASK_DOCUMENT;
                        } else if (a == 4) {
                            newMask |= MediaController.AUTODOWNLOAD_MASK_MUSIC;
                        } else if (a == 5) {
                            newMask |= MediaController.AUTODOWNLOAD_MASK_GIF;
                        }
                    }
                }
                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).edit();
                if (i == 0) {
                    editor.putInt("mobileDataDownloadMask", newMask);
                    MediaController.getInstance().mobileDataDownloadMask = newMask;
                } else if (i == 1) {
                    editor.putInt("wifiDownloadMask", newMask);
                    MediaController.getInstance().wifiDownloadMask = newMask;
                } else if (i == 2) {
                    editor.putInt("roamingDownloadMask", newMask);
                    MediaController.getInstance().roamingDownloadMask = newMask;
                }
                editor.commit();
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
        linearLayout.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }
}

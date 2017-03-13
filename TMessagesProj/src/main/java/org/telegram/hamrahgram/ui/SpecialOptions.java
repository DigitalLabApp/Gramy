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
import android.graphics.drawable.Drawable;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.hamrahgram.database.Database;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.BlockedUsersActivity;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.ChannelCreateActivity;
import org.telegram.ui.ChannelIntroActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.ContactsActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;

/**
 * <h1>java CustomSetting class in org.telegram.ui</h1>
 *
 * @author Hossein Moradi
 * @version 1.0
 * @since 1394
 */
public class SpecialOptions extends BaseFragment {
    private int rowCounter = -1;
    private ListView listView;
    private ListAdapter listAdapter;
    private ActionBarMenu menu;


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
        Analytics.getInstance(getParentActivity()).setScreen("ContactsActivity");
        menu = actionBar.createMenu();
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(getParentActivity().getApplicationContext().getResources().getString(R.string.Contacts));
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
                LaunchActivity.groupCreatePermision = MessagesController.isFeatureEnabled("chat_create", SpecialOptions.this);
                LaunchActivity.channelCreatePermision = MessagesController.isFeatureEnabled("broadcast_create", SpecialOptions.this);
                switch (i) {
                    case 2:
                        presentFragment(new ContactsActivity(null));
                        Analytics.getInstance(getParentActivity()).sendEvent("contacts", "click", Analytics.CONTACT_LIST);
                        break;
                    case 3:
                        if (!LaunchActivity.groupCreatePermision) break;
                        presentFragment(new GroupCreateActivity());
                        Analytics.getInstance(getParentActivity()).sendEvent("contacts", "click", Analytics.NEW_GROUP);
                        break;
                    case 4:
                        if (!LaunchActivity.channelCreatePermision) break;
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                        if (preferences.getBoolean("channel_intro", false)) {
                            Bundle args2 = new Bundle();
                            args2.putInt("step", 0);
                            presentFragment(new ChannelCreateActivity(args2));
                        } else {
                            presentFragment(new ChannelIntroActivity());
                            preferences.edit().putBoolean("channel_intro", true).commit();
                        }
                        Analytics.getInstance(getParentActivity()).sendEvent("contacts", "click", Analytics.NEW_CHANNEL);
                        break;
                    case 5:
                        Bundle args = new Bundle();
                        args.putBoolean("onlyUsers", true);
                        args.putBoolean("destroyAfterSelect", true);
                        args.putBoolean("createSecretChat", true);
                        args.putBoolean("allowBots", false);
                        presentFragment(new ContactsActivity(args));
                        Analytics.getInstance(getParentActivity()).sendEvent("contacts", "click", Analytics.NEW_SECRETCHAT);
                        break;
                    case 7:
                        ContactsController.showFavoriteContact = true;
                        ContactsController.showFavoriteContact = true;
                        ContactsController.getInstance().cleanup();
                        ContactsController.getInstance().readContacts();
                        presentFragment(new ContactsActivity(null));
                        Analytics.getInstance(getParentActivity()).sendEvent("contacts", "click", Analytics.FAVORITE_CONTACTS);
                        break;
                    case 8:
                        Analytics.getInstance(getParentActivity()).sendEvent("contacts", "click", Analytics.FAVORITE_CONTACTS_PHOTO_UPDATE);
                        Database Database = new Database();
                        if (Database.getFavoriteUsers(context) != null && Database.getFavoriteUsers(context).size() > 0) {

                            presentFragment(new UpdateUserPhoto());
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.noupdateyet), Toast.LENGTH_LONG).show();
                            break;
                        }
                        break;
                    case 9:
                        Analytics.getInstance(getParentActivity()).sendEvent("contacts", "click", Analytics.ONLINE_CONTACTS);
                        ContactsController.showOnlineContact = true;
                        ContactsController.showOnlineContact = true;
                        ContactsController.getInstance().cleanup();
                        ContactsController.getInstance().readContacts();
                        presentFragment(new ContactsActivity(null));
                        break;
                    case 10:
                        presentFragment(new BlockedUsersActivity());
                        Analytics.getInstance(getParentActivity()).sendEvent("contacts", "click", Analytics.BLOCKED_USERS);
                        break;
                    case 11:
                        EditCategoryDialog dialog = new EditCategoryDialog(getParentActivity());
                        dialog.show();
                        Analytics.getInstance(getParentActivity()).sendEvent("contacts", "click", Analytics.EDIT_CATEGORIES);

                        break;
                    case 12:
                        presentFragment(new Idsearcher());
                        Analytics.getInstance(getParentActivity()).sendEvent("contacts", "click", Analytics.ID_FINDER);
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


//        updateGhostMenu();
        if (DialogsActivity.helpmode) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishFragment();

                }
            }, 400);
            return;
        }

        if (listAdapter != null) {
            rowCounter = -1;
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
            return 13;
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
            String[] items = ApplicationLoader.mContext.getResources().getStringArray(R.array.specialoptions);
            int[] icons = {
                    R.drawable.menu_contacts,
                    R.drawable.menu_newgroup,
                    R.drawable.menu_broadcast,
                    R.drawable.menu_secret,
                    R.drawable.menu_favcontact,
                    R.drawable.menu_favpic,
                    R.drawable.menu_onlinecontact,
                    R.drawable.menu_blacklist,
                    R.drawable.ic_edit_categorize,
                    R.drawable.menu_idfinder
            };
            if (type == 0) {
                if (view == null) {
                    view = new EmptyCell(mContext);
                }
                if (i == 1) {
                } else {

                }

            } else if (type == 2) {
                view = new ShadowSectionCell(mContext);

            } else if (type == 1) {
                rowCounter = i > 5 ? i - 3 : i - 2;
                if (view == null) {
                    view = new TextSettingsCell(mContext);
                }
                Drawable drawable = mContext.getResources().getDrawable(icons[rowCounter]);
                drawable.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (rowCounter == icons.length - 1)
                    textCell.setTextAndIcon(items[rowCounter], drawable, false);
                else
                    textCell.setTextAndIcon(items[rowCounter], drawable, true);

            }
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 6) return 2;
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
}

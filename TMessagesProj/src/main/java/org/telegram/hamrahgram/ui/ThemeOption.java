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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import org.telegram.hamrahgram.database.Database;
import org.telegram.hamrahgram.SelectMode;
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
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

import static org.telegram.ui.DialogsActivity.restartApplication;

/**
 * <h1>java CustomSetting class in org.telegram.ui</h1>
 *
 * @author Hossein Moradi
 * @version 1.0
 * @since 1394
 */
public class ThemeOption extends BaseFragment implements SelectMode {
    public static boolean onSelectMode = false;
    public static SelectMode listener;
    private String selectedColor;
    private ActionBarMenuItem doneButton;
    private ActionBarMenuItem deleteButton;
    private int screenSize;
    private Context context;
    private ListView listView;
    private ListAdapter listAdapter;
    private int rowSize = 0;
    public int i = 0;
    public LinearLayout temp;
    public static boolean flag = false;
    private LinearLayout rootLinear;
    org.telegram.hamrahgram.database.Database Database = new Database();

    @Override
    public void onCancle() {
        doneButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
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
        this.context = context;
        selectedColor = Database.getInstance().readSetting("3", getParentActivity());
        listener = this;
        final ActionBarMenu menu = actionBar.createMenu();
        deleteButton = menu.addItem(3, R.drawable.ic_delete);
        doneButton = menu.addItem(2, R.drawable.ic_accept);
        doneButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);

        actionBar.setTitle(context.getResources().getString(R.string.selectColor));
        rootLinear = new LinearLayout(getParentActivity());
        rootLinear.setOrientation(LinearLayout.VERTICAL);
        rootLinear.setGravity(Gravity.CENTER | Gravity.TOP);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        rootLinear.setLayoutParams(params);
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
                }   else if (id == 2) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getParentActivity());
                    alert.setMessage(R.string.ConfirmToSet);
                    alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //AppearanceAndStickerSetting.RestartFlag = true;
                            Database Database = new Database();
                            Database.addSetting("3", selectedColor, context);
                            showMessage(context.getResources().getString(R.string.themechanged));
                            //flag = true;
                            doneButton.setVisibility(View.GONE);
                            deleteButton.setVisibility(View.GONE);
                            rootLinear.removeAllViews();
                            drawRow();
                            SharedPreferences.Editor shareEdit = getParentActivity().getSharedPreferences("dialogs", Context.MODE_PRIVATE).edit();
                            SharedPreferences shareRead = getParentActivity().getSharedPreferences("dialogs", Context.MODE_PRIVATE);
                            if (shareRead.getBoolean("showfonthelpdialog", true)) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Alert alert = new Alert(DialogsActivity.fragmentHolder.getParentActivity());
                                        alert.setTitle(getParentActivity().getResources().getString(R.string.ChangeFont));
                                        alert.setCancelable(false);
                                        alert.setMessage(getParentActivity().getResources().getString(R.string.RestartForConfirmChanges));
                                        alert.setCheckBoxText(getParentActivity().getResources().getString(R.string.DontShowAgain));
                                        alert.addActionListener(getParentActivity().getResources().getString(R.string.okdialog), new Alert.ActionListener() {
                                            @Override
                                            public void doAction(boolean enable) {
                                                restartApplication(getParentActivity().getApplicationContext(), 100);
                                                return;
                                            }
                                        });
                                        alert.addCheckBoxActionListener(new Alert.ActionListener() {
                                            @Override
                                            public void doAction(boolean enable) {
                                                shareEdit.putBoolean("showfonthelpdialog", !enable).commit();
                                            }
                                        });
                                        alert.show();
                                    }
                                }, 200);
                            } else {
                                Toast.makeText(getParentActivity(), getParentActivity().getApplicationContext().getResources().getString(R.string.Restarting), Toast.LENGTH_LONG).show();
                                new Handler().postDelayed(() -> restartApplication(getParentActivity().getApplicationContext(), 100), 1000);
                            }
                        }
                    }).setNegativeButton(R.string.nodialog, null)

                            .show();

                } else if (id == 3) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getParentActivity());
                    alert.setMessage(R.string.ConfirmDellTheme);
                    alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Database.getInstance().deleteAppTheme(Color.parseColor(selectedColor) + "", getParentActivity());
                            rootLinear.removeAllViews();
                            drawRow();
                            doneButton.setVisibility(View.GONE);
                            deleteButton.setVisibility(View.GONE);
                            Toast.makeText(getParentActivity(), getParentActivity().getResources().getString(R.string.SelectedThemeDeleted), Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton(R.string.nodialog, null)

                            .show();
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

        getFragmentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (screenSize == 0) {
                    screenSize = getFragmentView().getWidth();
                    rowSize = getWidth(context) / 5;
                    rowSize += rowSize / 7;
                    drawRow();
                    ScrollView scrollView = new ScrollView(getParentActivity());
                    scrollView.addView(rootLinear);
                    rootLinear.setGravity(Gravity.CENTER | Gravity.TOP);
                    scrollView.setVerticalScrollBarEnabled(false);
                    frameLayout.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.CENTER));
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    frameLayout.addView(actionBar);
                    needLayout();
                }
            }
        });

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
        if (flag)
            DialogsActivity.restart = true;

        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        updateUserData();
        fixLayout();
    }


    /**
     * <p>this method for create gridView Themes</p>
     *
     * @since 1394
     */
    public void drawRow() {
        Database Database = new Database();
        final ArrayList<String> color = new ArrayList<>();
        color.add("#ffffff");

        //  color.add("#008aac");
        //    color.add("#08b9c4");
        color.add("#08b9c4");
        color.add("#527da3");
        color.add("#757575");
        color.add("#9c27b0");
        color.add("#4caf50");
        color.add("#ff5722");
        color.add("#536dfe");
        color.add("#ffc107");

        //   color.add("#820082");
        // color.add("#2479DC");

        if (Database.getColorAtPosition(context) != null)
            color.addAll(Database.getColorAtPosition(context));

        LinearLayout.LayoutParams optional = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        temp = new LinearLayout(getParentActivity());
        temp.setOrientation(LinearLayout.HORIZONTAL);
        temp.setGravity(Gravity.RIGHT);
        rootLinear.addView(temp);
        for (i = 0; i < color.size(); i++) {


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(rowSize, rowSize);

            //   layoutParams.setMargins(2, 2, 2, 2);

            if (i > getRowCount() - 1 && i % getRowCount() == 0) {
                temp = new LinearLayout(getParentActivity());
                temp.setOrientation(LinearLayout.HORIZONTAL);
                temp.setGravity(Gravity.RIGHT);
                rootLinear.addView(temp);

            }
            FadeImageView img = new FadeImageView(getParentActivity());
            img.setPadding(2, 2, 2, 2);
            if (i == 0) {
               /* if (i == 2) {
                    img.setTag("btndell");
                    img.setLayoutParams(layoutParams);
                    img.setImageResource(R.drawable.trash);
                    img.setBackgroundColor(Color.WHITE);
                    img.setPadding(2, 2, 2, 2);
                }
                if (i == 1) {
                    img.setTag("btnfont");
                    img.setLayoutParams(layoutParams);
                    img.setImageResource(R.drawable.nfont);
                    img.setBackgroundColor(Color.WHITE);
                    img.setPadding(2, 2, 2, 2);
                }*/
                if (i == 0) {
                    img.setTag("btncolor");
                    img.setLayoutParams(layoutParams);
                    img.setImageResource(R.drawable.ic_plus);
                    img.setBackgroundResource(R.drawable.plus_bg);
                    img.setScaleType(ImageView.ScaleType.CENTER);


                }
            } else {

                final String current = color.get(i);
                img.setLayoutParams(layoutParams);

                if (color.get(i).equals(selectedColor)) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getParentActivity().getResources(), R.drawable.blur);
                    changeBitmapColor(bitmap, img, Color.parseColor(color.get(i)));
                } else {
                    img.setImageDrawable(new ColorDrawable(Color.parseColor(color.get(i))));
                }
                int k = i;
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedColor = current;
                     /*   Bitmap bitmap = BitmapFactory.decodeResource(getParentActivity().getResources(), R.drawable.blur);
                        changeBitmapColor(bitmap, img, Color.parseColor(selectedColor));*/
                        rootLinear.removeAllViews();
                        drawRow();
                        doneButton.setVisibility(View.VISIBLE);

                        if (k > 7)
                            deleteButton.setVisibility(View.VISIBLE);
                        ThemeOption.onSelectMode = true;
                    }
                });
            }

            temp.addView(img);

        }
        LinearLayout LINEAR = new LinearLayout(getParentActivity());
        ImageView pick = (ImageView) rootLinear.findViewWithTag("btncolor");
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPicker();
            }
        });

    }


    public void showMessage(String m) {
        Toast.makeText(context, m, Toast.LENGTH_LONG).show();

    }


    public int getRowCount() {
        return 4;
    }


    public void colorPicker() {

        try {
            AmbilWarnaDialog dialog = new AmbilWarnaDialog(getParentActivity(), Color.parseColor(ApplicationLoader.applicationTheme), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    Database.addAppTheme(String.valueOf(color), getParentActivity().getApplicationContext());
                    rootLinear.removeAllViews();
                    drawRow();

                }

                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                }
            });
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    private void needLayout() {
        FrameLayout.LayoutParams layoutParams;
        int newTop = (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        if (rootLinear != null) {
            layoutParams = (FrameLayout.LayoutParams) rootLinear.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                //   layoutParams.topMargin = newTop;
                rootLinear.setPadding(0, newTop, 0, 0);
                rootLinear.setLayoutParams(layoutParams);

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
                    textCell.setText(LocaleController.getString("hamrahgramhelp", R.string.hamrahgramhelp), true);
                }
                if (i == 3) {
                    textCell.setText(LocaleController.getString("TelegramFaq", R.string.TelegramFaq), true);
                }
                if (i == 4) {
                    textCell.setText(LocaleController.getString("AskAQuestion", R.string.AskAQuestion), true);
                }
                if (i == 5) {
                    textCell.setText(LocaleController.getString("contactus", R.string.contactus), true);
                }
                if (i == 6) {
                    textCell.setText(LocaleController.getString("InviteFriends", R.string.InviteFriends), true);
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

    private int getWidth(Context context) {
        return screenSize;
    }

    private void changeBitmapColor(Bitmap sourceBitmap, ImageView image, int color) {

        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);
        image.setImageBitmap(resultBitmap);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
    }

}

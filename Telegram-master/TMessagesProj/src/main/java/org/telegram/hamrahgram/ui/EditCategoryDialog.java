package org.telegram.hamrahgram.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.hamrahgram.ITabsLayoutUpdate;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.DialogsActivity;

public class EditCategoryDialog extends AlertDialog.Builder {
    private TextView textView;
    private LinearLayout titleBar;
    private CheckBox favoriteDialogs;
    private CheckBox userDialogs;
    private CheckBox groupDialogs;
    private CheckBox superGroupDialogs;
    private CheckBox channelDialogs;
    private CheckBox botDialogs;
    private CheckBox onlineDialogs;
    private CheckBox blockUsersDialogs;
    private TextView addNewCategory;
    private SharedPreferences shareRead;
    private SharedPreferences.Editor shareEdit;
    private LinearLayout root;
    private String[] names;


    public EditCategoryDialog(Context context) {
        super(context);
        LinearLayout.LayoutParams checkboxparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleBar = new LinearLayout(context);
        titleBar.setGravity(Gravity.CENTER);
        titleBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView = new TextView(context);
        textView.setText(context.getResources().getString(R.string.edittabs));
        titleBar.addView(textView);
        titleBar.setPadding(AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5));
        textView.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        //   textView.setTextSize(new TextView(context).getTextSize());
        textView.setTextColor(Color.BLACK);
        setCustomTitle(titleBar);
        names = context.getResources().getStringArray(R.array.edittabs);
        shareRead = context.getSharedPreferences("tabs", Context.MODE_PRIVATE);
        shareEdit = context.getSharedPreferences("tabs", Context.MODE_PRIVATE).edit();
        root = new LinearLayout(context);
        root.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10));
        root.setOrientation(LinearLayout.VERTICAL);
        favoriteDialogs = new CheckBox(context);
        userDialogs = new CheckBox(context);
        groupDialogs = new CheckBox(context);
        superGroupDialogs = new CheckBox(context);
        channelDialogs = new CheckBox(context);
        botDialogs = new CheckBox(context);
        onlineDialogs = new CheckBox(context);
        blockUsersDialogs = new CheckBox(context);
        addNewCategory = new TextView(context);
        favoriteDialogs.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        userDialogs.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        groupDialogs.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        superGroupDialogs.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        channelDialogs.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        botDialogs.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        onlineDialogs.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        blockUsersDialogs.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        favoriteDialogs.setText(names[0]);
        userDialogs.setText(names[1]);
        groupDialogs.setText(names[2]);
        superGroupDialogs.setText(names[3]);
        channelDialogs.setText(names[4]);
        botDialogs.setText(names[5]);
        onlineDialogs.setText(names[6]);
        blockUsersDialogs.setText(names[7]);
        favoriteDialogs.setTextColor(Color.BLACK);
        userDialogs.setTextColor(Color.BLACK);
        groupDialogs.setTextColor(Color.BLACK);
        superGroupDialogs.setTextColor(Color.BLACK);
        channelDialogs.setTextColor(Color.BLACK);
        botDialogs.setTextColor(Color.BLACK);
        onlineDialogs.setTextColor(Color.BLACK);
        blockUsersDialogs.setTextColor(Color.BLACK);
        favoriteDialogs.setLayoutParams(checkboxparams);
        userDialogs.setLayoutParams(checkboxparams);
        groupDialogs.setLayoutParams(checkboxparams);
        superGroupDialogs.setLayoutParams(checkboxparams);
        channelDialogs.setLayoutParams(checkboxparams);
        botDialogs.setLayoutParams(checkboxparams);
        onlineDialogs.setLayoutParams(checkboxparams);
        blockUsersDialogs.setLayoutParams(checkboxparams);
        userDialogs.setChecked(shareRead.getBoolean("contacts", true));
        groupDialogs.setChecked(shareRead.getBoolean("groups", true));
        superGroupDialogs.setChecked(shareRead.getBoolean("superGroups", true));
        channelDialogs.setChecked(shareRead.getBoolean("channels", true));
        botDialogs.setChecked(shareRead.getBoolean("bots", true));
        onlineDialogs.setChecked(shareRead.getBoolean("onlineContacts", true));
        favoriteDialogs.setChecked(shareRead.getBoolean("fav", true));
        blockUsersDialogs.setChecked(shareRead.getBoolean("block", false));
        root.addView(favoriteDialogs);
        root.addView(userDialogs);
        root.addView(groupDialogs);
        root.addView(superGroupDialogs);
        root.addView(channelDialogs);
        root.addView(botDialogs);
        root.addView(onlineDialogs);
        root.addView(blockUsersDialogs);
        favoriteDialogs.setOnClickListener((v) -> {

                    shareEdit.putBoolean("fav", favoriteDialogs.isChecked()).commit();
                    ((ITabsLayoutUpdate) DialogsActivity.fragmentHolder).onUpdate();
                }


        );
        userDialogs.setOnClickListener((v) -> {
                    shareEdit.putBoolean("contacts", userDialogs.isChecked()).commit();
                    ((ITabsLayoutUpdate) DialogsActivity.fragmentHolder).onUpdate();
                }
        );
        groupDialogs.setOnClickListener((v) -> {

                    shareEdit.putBoolean("groups", groupDialogs.isChecked()).commit();
                    ((ITabsLayoutUpdate) DialogsActivity.fragmentHolder).onUpdate();
                }


        );
        superGroupDialogs.setOnClickListener((v) -> {
            shareEdit.putBoolean("superGroups", superGroupDialogs.isChecked()).commit();
            ((ITabsLayoutUpdate) DialogsActivity.fragmentHolder).onUpdate();

        });
        channelDialogs.setOnClickListener((v) ->
                {

                    shareEdit.putBoolean("channels", channelDialogs.isChecked()).commit();
                    ((ITabsLayoutUpdate) DialogsActivity.fragmentHolder).onUpdate();
                }


        );
        botDialogs.setOnClickListener((v) ->
                {
                    shareEdit.putBoolean("bots", botDialogs.isChecked()).commit();
                    ((ITabsLayoutUpdate) DialogsActivity.fragmentHolder).onUpdate();
                }


        );
        onlineDialogs.setOnClickListener((v) ->
                {
                    shareEdit.putBoolean("onlineContacts", onlineDialogs.isChecked()).commit();
                    ((ITabsLayoutUpdate) DialogsActivity.fragmentHolder).onUpdate();
                }
        );
        blockUsersDialogs.setOnClickListener((v) ->
                {
                    shareEdit.putBoolean("block", blockUsersDialogs.isChecked()).commit();
                    ((ITabsLayoutUpdate) DialogsActivity.fragmentHolder).onUpdate();
                }


        );
        setView(root);
        setPositiveButton(context.getResources().getString(R.string.closeDialog), null);
    }


}

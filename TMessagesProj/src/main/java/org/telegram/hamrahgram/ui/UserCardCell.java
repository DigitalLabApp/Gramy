package org.telegram.hamrahgram.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;


public class UserCardCell extends LinearLayout {
    private String userId;
    private TextCell addToFavorite;
    private TextCell blockUser;
    private TextCell deleteUser;
    private TextCell createShortcut;
    private ShadowSectionCell shadowSectionCell;
    private Resources res;
    private boolean userBlocked;

    public UserCardCell(Context context, String userId) {
        super(context);
        this.userId = userId;
        userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.parseInt(userId));
        setOrientation(VERTICAL);
        addToFavorite = new TextCell(context);
        blockUser = new TextCell(context);
        deleteUser = new TextCell(context);
        createShortcut = new TextCell(context);
        addToFavorite.setBackgroundResource(R.drawable.list_selector);
        blockUser.setBackgroundResource(R.drawable.list_selector);
        deleteUser.setBackgroundResource(R.drawable.list_selector);
        createShortcut.setBackgroundResource(R.drawable.list_selector);
        shadowSectionCell = new ShadowSectionCell(context);
        res = context.getResources();
        addToFavorite.setText(ApplicationLoader.favoriteDialogs.contains(userId) ? res.getString(R.string.deleteFromFav) : res.getString(R.string.addToFav));
        blockUser.setText(userBlocked ? res.getString(R.string.unblockUser) : res.getString(R.string.blockusers));
        deleteUser.setText(res.getString(R.string.deleteUser));
        createShortcut.setText(res.getString(R.string.createShortCut));
        addView(shadowSectionCell);
        addView(addToFavorite);
        addView(blockUser);
        addView(deleteUser);
        addView(createShortcut);
    }

    public void setUserId(String userId) {
        this.userId = userId;
        if (ApplicationLoader.favoriteDialogs.contains(userId + "")) {
            addToFavorite.setText(getResources().getString(R.string.dellfromfav));
        }
    }

    public boolean isBlockUser() {
        return this.userBlocked;
    }

    public void addOnAddFavoriteListener(View.OnClickListener listener) {
        addToFavorite.setOnClickListener(listener);

    }

    public void addOnBlockListener(View.OnClickListener listener) {
        blockUser.setOnClickListener(listener);

    }

    public void addOnDeleteListener(View.OnClickListener listener) {
        deleteUser.setOnClickListener(listener);
    }

    public void addOnShortCutListener(View.OnClickListener listener) {
        createShortcut.setOnClickListener(listener);
    }

    public void hideBlockUser() {
        this.blockUser.setVisibility(View.GONE);
    }

}

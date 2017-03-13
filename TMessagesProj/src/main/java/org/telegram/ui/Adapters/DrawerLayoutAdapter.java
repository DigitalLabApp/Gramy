/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2015.
 */

package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import org.telegram.hamrahgram.database.Database;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.EmptyCell;

public class DrawerLayoutAdapter extends BaseAdapter {
    public static int help_type;

    private Context mContext;

    public DrawerLayoutAdapter(Context context) {
        mContext = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return !(i == 1 || i == 8);
    }

    @Override
    public int getCount() {
        return UserConfig.isClientActivated() ? 13 : 0;
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
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        if (type == 0) {
            if (view == null) {
                view = new DrawerProfileCell(mContext);
            }
            ((DrawerProfileCell) view).setUser(MessagesController.getInstance().getUser(UserConfig.getClientUserId()));
        } else if (type == 1) {
            if (view == null) {
                view = new EmptyCell(mContext, AndroidUtilities.dp(8));
            }
        } else if (type == 2) {
            if (view == null) {
                view = new DividerCell(mContext);
            }
        } else if (type == 3) {
            if (view == null) {
                view = new DrawerActionCell(mContext);
            }
            DrawerActionCell actionCell = (DrawerActionCell) view;

            //By Hossein **************************************
            if (i == 2) {
                actionCell.setTextAndIcon(LocaleController.getString("ShowSchedule", R.string.ShowSchedule), R.drawable.ic_show_chart);
                actionCell.setDivier(false);
            }
            if (i == 3) {
                actionCell.setTextAndIcon(Database.getInstance().readSetting("7", ApplicationLoader.mContext).equals("1") ? LocaleController.getString("exitghostmode", R.string.exitghostmode) : LocaleController.getString("ghostMode", R.string.ghostMode), Database.getInstance().readSetting("7", ApplicationLoader.mContext).equals("1") ? R.drawable.ic_ghost_on : R.drawable.ic_ghost_off);
                actionCell.setDivier(true);
            }
            if (i == 4) {
                actionCell.setTextAndIcon(LocaleController.getString("clearmemory", R.string.clearmemory), R.drawable.ic_cleaner);
                actionCell.setDivier(true);
            }

            if (i == 5) {
                actionCell.setTextAndIcon(LocaleController.getString("unreport", R.string.unreport), R.drawable.ic_cancle_reporting);
                actionCell.setDivier(true);
            }


            if (i == 6) {
                actionCell.setTextAndIcon(LocaleController.getString("invitefriends", R.string.invitefriends), R.drawable.ic_invite);
                actionCell.setDivier(true);
            }

            if (i == 7)
                actionCell.setTextAndIcon(LocaleController.getString("topapps", R.string.topapps), R.drawable.applst);


            if (i == 9) {
                actionCell.setTextAndIcon(LocaleController.getString("specialOptions", R.string.specialOptions), R.drawable.ic_contacts);
                actionCell.setDivier(true);
            }

            if (i == 10) {
                actionCell.setTextAndIcon(LocaleController.getString("Settings", R.string.Settings), R.drawable.ic_setting);
                actionCell.setDivier(true);
            }
            if (i == 11) {
                actionCell.setTextAndIcon(LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.ic_security_setting);
                actionCell.setDivier(true);
            }

            if (i == 12) {
                actionCell.setTextAndIcon(LocaleController.getString("qhelp", R.string.qhelp), R.drawable.ic_support);
                actionCell.setDivier(false);
            }


        }
//****************************************************************************************************
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        } else if (i == 1) {
            return 1;
        } else if (i == 8) {
            return 2;
        }
        return 3;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return !UserConfig.isClientActivated();
    }

    public void show(String m) {
        Toast.makeText(mContext, m, Toast.LENGTH_SHORT).show();

    }
}

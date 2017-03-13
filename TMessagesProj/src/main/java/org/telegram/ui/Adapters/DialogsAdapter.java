/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.hamrahgram.database.Database;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.LoadingCell;

import java.util.ArrayList;

public class DialogsAdapter extends RecyclerView.Adapter {
    //Hossein
    private Context mContext;
    private int dialogsType;
    private long openedDialogId;
    private int currentCount;

    private class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public DialogsAdapter(Context context, int type) {
        mContext = context;
        dialogsType = type;
    }

    public void setOpenedDialogId(long id) {
        openedDialogId = id;
    }

    public boolean isDataSetChanged() {
        int current = currentCount;
        return current != getItemCount() || current == 1;
    }

    private ArrayList<TLRPC.TL_dialog> getDialogsArray() {
        //Hossein
        try {
            if (Database.getInstance().checkHiddenMode(mContext)) {
                if (dialogsType == 0) {
                    return ClearSpamBot(MessagesController.getInstance().dialogs);
                } else if (dialogsType == 1)
                    return ClearSpamBot(MessagesController.getInstance().dialogsServerOnly);
                else if (dialogsType == 2)
                    return ClearSpamBot(MessagesController.getInstance().dialogsGroupsOnly);
                else if (dialogsType == 3)
                    return ClearSpamBot(MessagesController.getInstance().dialogsBots);
                else if (dialogsType == 4)
                    return ClearSpamBot(MessagesController.getInstance().dialogsUsers);
                else if (dialogsType == 8)
                    return ClearSpamBot(MessagesController.getInstance().dialogsFavs);
                else if (dialogsType == 10)
                    return ClearSpamBot(MessagesController.getInstance().dialogsChannels);
                else if (dialogsType == 11)
                    return ClearSpamBot((MessagesController.getInstance().dialogsunread));
                else if (dialogsType == 12)
                    return ClearSpamBot(MessagesController.getInstance().dialogshidden);
                else if (dialogsType == 14)
                    return ClearSpamBot(MessagesController.getInstance().dialogsmegagroups);
                else if (dialogsType == 15) {
                    ArrayList<TLRPC.TL_dialog> result = new ArrayList<>();
                    result.addAll(MessagesController.getInstance().dialogsFavoriteContact);
                    result.addAll(MessagesController.getInstance().dialogsFavs);
                    return ClearSpamBot(result);
                } else if (dialogsType == 16)
                    return ClearSpamBot(MessagesController.getInstance().dialogsBlockedUsers);
                else if (dialogsType == 17)
                    return ClearSpamBot(MessagesController.getInstance().dialogsOnlineUsers);

            } else {
                if (dialogsType == 0) {
                    if (deleteHiddenDialogs(MessagesController.getInstance().dialogs) != null)
                        return (deleteHiddenDialogs(MessagesController.getInstance().dialogs));

                } else if (dialogsType == 1)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsServerOnly);
                else if (dialogsType == 2)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsGroupsOnly);
                else if (dialogsType == 3)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsBots);
                else if (dialogsType == 4)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsUsers);
                else if (dialogsType == 8)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsFavs);
                else if (dialogsType == 10)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsChannels);
                else if (dialogsType == 11)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsunread);
                else if (dialogsType == 12)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogshidden);
                else if (dialogsType == 14)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsmegagroups);
                else if (dialogsType == 15) {
                    ArrayList<TLRPC.TL_dialog> result = new ArrayList<>();
                    result.addAll(MessagesController.getInstance().dialogsFavoriteContact);
                    result.addAll(MessagesController.getInstance().dialogsFavs);
                    return deleteHiddenDialogs(result);
                } else if (dialogsType == 16)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsBlockedUsers);
                else if (dialogsType == 17)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsOnlineUsers);
                else if (dialogsType == 18)
                    return deleteHiddenDialogs(MessagesController.getInstance().dialogsjusgroups);
            }
        } catch (Exception e) {


        }
        return null;
        //--------------------
    }

    @Override
    public int getItemCount() {
        int count = 0;
        try {
            count = getDialogsArray().size();
        } catch (Exception e) {


        }
        if (count == 0 && MessagesController.getInstance().loadingDialogs) {
            return 0;
        }
        if (!MessagesController.getInstance().dialogsEndReached) {
            count++;
        }
        currentCount = count;
        return count;
    }

    public TLRPC.TL_dialog getItem(int i) {
        ArrayList<TLRPC.TL_dialog> arrayList = getDialogsArray();
        if (i < 0 || i >= arrayList.size()) {
            return null;
        }
        return arrayList.get(i);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof DialogCell) {
            ((DialogCell) holder.itemView).checkCurrentDialogIndex();
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = new DialogCell(mContext);
        } else if (viewType == 1) {
            view = new LoadingCell(mContext);
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 0) {
            DialogCell cell = (DialogCell) viewHolder.itemView;
            cell.useSeparator = (i != getItemCount() - 1);
            TLRPC.TL_dialog dialog = getItem(i);
            if (dialogsType == 0) {
                if (AndroidUtilities.isTablet()) {
                    cell.setDialogSelected(dialog.id == openedDialogId);
                }
            }
            cell.setDialog(dialog, i, dialogsType);
        }
    }

    @Override
    public int getItemViewType(int i) {
        if (i == getDialogsArray().size()) {
            return 1;
        }
        return 0;
    }

    //Hossein --------------------------
    public ArrayList<TLRPC.TL_dialog> deleteHiddenDialogs(final ArrayList<TLRPC.TL_dialog> h) { //Hossein
        if (h != null) {
            for (int a = 0; a < h.size(); a++) {
                TLRPC.TL_dialog d = h.get(a);
                if (ApplicationLoader.hiddenDialogs.contains(String.valueOf(d.id)) || (MessageObject.blockMode && d.id == MessageObject.spamBotId)) {
                    h.remove(a);
                    if (MessageObject.blockMode && MessageObject.spamBotId == d.id) {
                        MessagesController.getInstance().deleteDialog(d.id, 0);
                    }
                }
            }
        }
        return h;
    }

    public ArrayList<TLRPC.TL_dialog> ClearSpamBot(final ArrayList<TLRPC.TL_dialog> h) {
        if (h != null) {
            if (MessageObject.blockMode) {
                for (int a = 0; a < h.size(); a++) {
                    TLRPC.TL_dialog d = h.get(a);
                    if (MessageObject.spamBotId == d.id) {
                        MessagesController.getInstance().deleteDialog(d.id, 0);
                    }
                }
            }
        }
        return h;
    }
    //-----------------------------
}

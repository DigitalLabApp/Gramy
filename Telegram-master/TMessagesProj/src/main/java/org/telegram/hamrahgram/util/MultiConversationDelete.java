package org.telegram.hamrahgram.util;

import android.widget.Toast;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;

import java.util.ArrayList;

public class MultiConversationDelete {
    private static MultiConversationDelete instance;
    public ArrayList<Long> selectedDialogs = new ArrayList<>();

    private MultiConversationDelete() {
    }
    public static MultiConversationDelete getInstance() {
        if (instance == null) {
            instance = new MultiConversationDelete();
        }
        return instance;

    }

    public void addDialog(Long dialogId) {
        if (this.selectedDialogs.contains(dialogId)) return;
        this.selectedDialogs.add(dialogId);
    }

    public void removeDialog(Long dialogId) {
        this.selectedDialogs.remove(dialogId);
    }

    public void deleteSelected() {
        if (selectedDialogs.size() < 1) return;
        for (Long dId : selectedDialogs)
            MessagesController.getInstance().deleteDialog(dId, 0);
        Toast.makeText(ApplicationLoader.mContext, selectedDialogs.size() + " " + ApplicationLoader.mContext.getResources().getString(R.string.dialogdeleted), Toast.LENGTH_LONG).show();
        selectedDialogs.clear();
    }

    public boolean checkDialog(Long id) {
        return selectedDialogs.contains(id);
    }
    public void clean() {
        selectedDialogs.clear();


    }

}

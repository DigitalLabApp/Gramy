package org.telegram.hamrahgram.util;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class GhostOption {
    private static GhostOption instance;

    public static GhostOption getInstance() {
        if (instance == null) {
            instance = new GhostOption();
        }
        return instance;
    }


    public void setGhostMode(boolean mode) {
        TLRPC.TL_account_setPrivacy req = new TLRPC.TL_account_setPrivacy();
        req.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
        req.rules.add(mode ? new TLRPC.TL_inputPrivacyValueDisallowAll() : new TLRPC.TL_inputPrivacyValueAllowAll());
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            @Override
            public void run(final TLObject response, final TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (error == null) {
                            TLRPC.TL_account_privacyRules rules = (TLRPC.TL_account_privacyRules) response;
                            MessagesController.getInstance().putUsers(rules.users, false);
                            ContactsController.getInstance().setPrivacyRules(rules.rules, false);
                        }
                    }
                });
            }
        }, ConnectionsManager.RequestFlagFailOnServerErrors);
    }
}

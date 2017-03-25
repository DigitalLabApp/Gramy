package org.telegram.hamrahgram.util;


import android.app.Activity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;

public class Analytics {
    public static final String GHOST_MODE = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsGhostMode);
    public static final String CLEAR_CACHE = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsClearCache);
    public static final String UNREPORT = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsUnreport);
    public static final String INVITE_FRIENDS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsInviteFriends);
    public static final String TOP_APPS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsTopApps);
    public static final String CONTACTS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsContacts);
    public static final String SETTINGS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsSettings);
    public static final String PRIVACY_SECURITY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsPrivacyAndSecurity);
    public static final String SUPPORT = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsSupport);
    public static final String ALL_MESSAGES_CATEGORY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsAllMessagesCategory);
    public static final String UNREAD_MESSAGES_CATEGORY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsUnreadMessagesCategory);
    public static final String FAVORITE_MESSAGES_CATEGORY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsFavoriteMessagesCategory);
    public static final String CONTACTS_MESSAGES_CATEGORY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsContactsMessagesCategory);
    public static final String GROUPS_MESSAGES_CATEGORY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsGroupsMessagesCategory);
    public static final String SUPPERGROUPS_MESSAGES_CATEGORY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsSuperGroupMessagesCategory);
    public static final String ONLINE_MESSAGES_CATEGORY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsOnlineMessagesCategory);
    public static final String CHANNEL_MESSAGES_CATEGORY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsChannelsMessagesCategory);
    public static final String BOT_MESSAGES_CATEGORY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsBotMessagesCategory);
    public static final String BLOCKED_MESSAGES_CATEGORY = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsBlockedMessagesCategory);
    public static final String FLOATING_ACTION_BUTTON = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsFloatingActionButton);
    public static final String SEARCH_BUTTON = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsSearchButton);
    public static final String CONTACT_LIST = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsContactsLists);
    public static final String NEW_GROUP = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsNewGroup);
    public static final String NEW_CHANNEL = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsNewChannel);
    public static final String NEW_SECRETCHAT = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsNewSecretChat);
    public static final String FAVORITE_CONTACTS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsFavoriteContacts);
    public static final String FAVORITE_CONTACTS_PHOTO_UPDATE = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsFavoriteContactsPhotoUpdate);
    public static final String ONLINE_CONTACTS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsOnlineContacts);
    public static final String BLOCKED_USERS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsBlockedUsers);
    public static final String EDIT_CATEGORIES = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsEditCategoris);
    public static final String ID_FINDER = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsIdFinder);
    public static final String CHANGE_USERNAME = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsChangeUserName);
    public static final String INTERNET_AND_MEMORY_SETTINGS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsInternetAndMemorySetting);
    public static final String APPEARANCE_SETTINGS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsAppearanceAndStickersSetting);
    public static final String ALARM_AND_SOUNDS_SETTINGS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsAlarmAndSoundsSetting);
    public static final String OTHER_SETTINGS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsOtherSettings);
    public static final String HAMRAHGRAM_HELP = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsHamrahgramHelp);
    public static final String COMMON_QUESTIONS = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsCommonQuestions);
    public static final String ASK_A_QUESTION = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsAskAQuestion);
    public static final String CONTACT_US = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsContactUs);
    public static final String SHOW_SCHEDULER = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsShowScheduler);
    public static final String PICK_FROM_CALENDAR = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsPICKFROMPERCALENDAR);
    public static final String PICK_FROM_MILADICALENDAR = ApplicationLoader.applicationContext.getResources().getString(R.string.AnalyticsPICKFROMMILADICALENDAR);
    private static Analytics instance = null;
    private static Tracker tracker = null;

    public static Analytics getInstance(Activity activity) {
        if (instance == null) {
            instance = new Analytics();
        }

        if (tracker == null) {
            ApplicationLoader applicationLoader = (ApplicationLoader) activity.getApplication();
            tracker = applicationLoader.getDefaultTracker();
            tracker.enableAdvertisingIdCollection(true);
        }

        return instance;
    }

    public void setScreen(String stringName) {
        tracker.setScreenName(stringName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    public void sendEvent(String category, String action, String label) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

}

package org.telegram.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.hamrahgram.IFloatingAction;
import org.telegram.hamrahgram.IHelpWizard;
import org.telegram.hamrahgram.ITabsLayoutUpdate;
import org.telegram.hamrahgram.database.Database;
import org.telegram.hamrahgram.ui.CenteredImageSpan;
import org.telegram.hamrahgram.ui.CircleOverlayView;
import org.telegram.hamrahgram.ui.FloatingActionDialog;
import org.telegram.hamrahgram.ui.Idsearcher;
import org.telegram.hamrahgram.util.Analytics;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.RecyclerListView;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, IHelpWizard, ITabsLayoutUpdate, IFloatingAction {
    //Hossein
    private boolean isRecyclerScrollabdle;
    private int visiblePossition = 0;
    private LinearLayout linearLayout;
    private final int MAX_HELP = 19;
    private String currentDialogPassword = null;
    private boolean photo, video, audio, doc, music;
    private Handler wizardHandler;
    private Runnable wizardRunnable;
    public static float floatingX;
    public static float floatingRawX;
    public static float floatingY;
    private boolean hasClickHideItem = false;
    private int hideClickCounter = 0;
    private ActionBarMenuItem hideItem;
    private boolean hideHelp = false;
    private Handler helpHandler;
    private Dialog hidePopUp;
    private int j = 0;
    private ActionBarMenu menu;
    public static boolean helpmode;
    public static int helpmode_type;
    public static int dialog_t = 0;
    private Context context;
    private int canSwipe = 0;
    private int currentTab;
    private Drawable backDrawable;
    private View IDFinderCell;
    private TextView emptyHeader;
    private TextView emptyMessage;
    public static BaseFragment fragmentHolder;
    private Dialog helpDialog;
    private Context appContext;
    private String[] itemNames = ApplicationLoader.applicationContext.getResources().getStringArray(R.array.tabsname);
    private LinearLayout tabsLayout;
    private LinearLayout tabsContainer;
    private ImageView favoriteContacts;
    private ImageView allDialogs;
    private ImageView userDialogs;
    private ImageView groupDialogs;
    private ImageView channelDialogs;
    private ImageView botDialogs;
    private ImageView favoriteDialogs;
    private ImageView unreadDialogs;
    private ImageView superGroupDialogs;
    private ImageView onlineUserDialogs;
    private ImageView blockUserDialogs;
    public static boolean restart;
    private ObjectAnimator animator2;
    //----------------------------------------
    private boolean doProcessAnimation = false;
    private RecyclerListView listView;
    private LinearLayoutManager layoutManager;
    private DialogsAdapter dialogsAdapter;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private ProgressBar progressView;
    private LinearLayout emptyView;
    private ActionBarMenuItem passcodeItem;
    private ImageView floatingButton;
    private AlertDialog permissionDialog;
    private int prevPosition;
    private int prevTop;
    private boolean scrollUpdated;
    private boolean floatingHidden;
   // private boolean listHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private boolean checkPermission = true;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private String addToGroupAlertString;
    private int dialogsType;
    public static boolean dialogsLoaded;
    private boolean searching;
    private boolean searchWas;
    private boolean onlySelect;
    private long selectedDialog;
    private String searchString;
    private long openedDialogId;
    private boolean cantSendToChannels;
    private DialogsActivityDelegate delegate;

    @Override
    public void onDialogClose() {
        floatingButton.setImageResource(R.drawable.floating_pencil);
    }

    @Override
    public void onUpdate() {
        new Handler().postDelayed(() -> {
            tabsLayout.removeAllViews();
            createTabs(getParentActivity());
            filterDialogs(getParentActivity(), 0, allDialogs);
        }, 1000);
    }

    @Override
    public void onHelpEnable() {
        if (helpmode) {
            tabsLayout.removeAllViews();
            createTabs(getParentActivity());
            helpmode = false;
            new Handler().postDelayed(() -> helpWizard(-1), 300);
        }
    }

    public interface DialogsActivityDelegate {
        void didSelectDialog(DialogsActivity fragment, long dialog_id, boolean param);
    }

    public DialogsActivity(Bundle args) {
        super(args);
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (getArguments() != null) {
            onlySelect = arguments.getBoolean("onlySelect", false);
            cantSendToChannels = arguments.getBoolean("cantSendToChannels", false);
            dialogsType = arguments.getInt("dialogsType", 0);
            selectAlertString = arguments.getString("selectAlertString");
            selectAlertStringGroup = arguments.getString("selectAlertStringGroup");
            addToGroupAlertString = arguments.getString("addToGroupAlertString");
        }
        if (searchString == null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadHints);
        }

        if (!dialogsLoaded) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            ContactsController.getInstance().checkInviteText();
            StickersQuery.checkFeaturedStickers();
            dialogsLoaded = true;
        }
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (wizardHandler != null)
            wizardHandler.removeCallbacks(wizardRunnable);
        actionBar.setDialogsActivity(false);
        if (searchString == null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadHints);
        }
        delegate = null;
    }

    @Override
    public View createView(final Context context) {
        Analytics.getInstance(getParentActivity()).setScreen("DialogsActivity");
        if (!getFirst()) {
            wizardHandler = new Handler();
            wizardRunnable = new Runnable() {
                @Override
                public void run() {
                    helpWizard(-1);
                    setFirst();
                }
            };
            wizardHandler.postDelayed(wizardRunnable, 1000);
        }
        helpHandler = new Handler();
        helpDialog = new Dialog(getParentActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        helpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        SharedPreferences sharedPreferences = getParentActivity().getSharedPreferences("helpwizard", MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferences2 = getParentActivity().getSharedPreferences("helpwizard", MODE_PRIVATE).edit();
        if (sharedPreferences.getBoolean("showhiddendialogs", false)) {
            sharedPreferences2.putBoolean("showhiddendialogs", false).commit();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    helpWizard(22);
                }
            }, 1000);
        }
        fragmentHolder = this;
        tabsLayout = new LinearLayout(context);
        tabsLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabsLayout.setBackgroundColor(Color.WHITE);
        if (Database.getInstance().checkHiddenMode(context)) {
            Toast.makeText(getParentActivity().getApplicationContext(), ApplicationLoader.mContext.getResources().getString(R.string.hiddenmode), Toast.LENGTH_SHORT).show();
        }
        createTabs(context);
        //---------------------------------------------------------
        searching = false;
        searchWas = false;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Theme.loadRecources(context);
            }
        });
        menu = actionBar.createMenu();
        actionBar.setDialogsActivity(true);
        if (!onlySelect && searchString == null) {
            passcodeItem = menu.addItem(1, R.drawable.lock_close);
            updatePasscodeButton();
        }
        hideItem = menu.addItem(10, R.drawable.ic_view);
        hideItem.setVisibility(Database.getInstance().checkHiddenMode(getParentActivity()) ? View.VISIBLE : View.GONE);

        //---------------------------------------------------
        final ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {
                Analytics.getInstance(getParentActivity()).sendEvent("actionbar", "click", Analytics.SEARCH_BUTTON);
                hideItem.setVisibility(View.GONE);
                actionBar.setBackgroundColor(Color.WHITE);
                actionBar.hideBackButton();
                backDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                IDFinderCell.setVisibility(View.VISIBLE);
                tabsContainer.setVisibility(View.INVISIBLE);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) listView.getLayoutParams();
                marginLayoutParams.setMargins(0, AndroidUtilities.dp(80), 0, 0);
                searching = true;
                if (listView != null) {
                    if (searchString != null) {
                        listView.setEmptyView(searchEmptyView);
                        progressView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                    }
                    if (!onlySelect) {
                        floatingButton.setVisibility(View.GONE);
                    }
                }
                updatePasscodeButton();
            }

            @Override
            public boolean canCollapseSearch() {
                if (searchString != null) {
                    finishFragment();
                    return false;
                }
                return true;
            }

            @Override
            public void onSearchCollapse() {
                //Hossein
                hideItem.setVisibility(Database.getInstance().checkHiddenMode(getParentActivity()) ? View.VISIBLE : View.GONE);
                actionBar.setBackgroundColor(Color.parseColor(ApplicationLoader.applicationTheme));
                actionBar.hideBackButton();
                backDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                actionBar.setBackButtonDrawable(new MenuDrawable());
                tabsContainer.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) listView.getLayoutParams();
                marginLayoutParams.setMargins(0, tabsContainer.getLayoutParams().height, 0, 0);
                IDFinderCell.setVisibility(View.GONE);
                //----------------------------------------------
                searching = false;
                searchWas = false;
                if (listView != null) {
                    searchEmptyView.setVisibility(View.GONE);
                    if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                        emptyView.setVisibility(View.GONE);
                        listView.setEmptyView(progressView);
                    } else {
                        progressView.setVisibility(View.GONE);
                        listView.setEmptyView(emptyView);
                    }
                    if (!onlySelect) {
                        floatingButton.setVisibility(View.VISIBLE);
                        floatingHidden = true;
                        floatingButton.setTranslationY(AndroidUtilities.dp(100));
                        hideFloatingButton(false, false);
                    }
                    if (listView.getAdapter() != dialogsAdapter) {
                        listView.setAdapter(dialogsAdapter);
                        dialogsAdapter.notifyDataSetChanged();
                    }
                }
                if (dialogsSearchAdapter != null) {
                    dialogsSearchAdapter.searchDialogs(null);
                }
                updatePasscodeButton();
            }

            @Override
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (text.length() != 0 || dialogsSearchAdapter != null && dialogsSearchAdapter.hasRecentRearch()) {
                    searchWas = true;
                    if (dialogsSearchAdapter != null && listView.getAdapter() != dialogsSearchAdapter) {
                        listView.setAdapter(dialogsSearchAdapter);
                        dialogsSearchAdapter.notifyDataSetChanged();
                    }
                    if (searchEmptyView != null && listView.getEmptyView() != searchEmptyView) {
                        emptyView.setVisibility(View.GONE);
                        progressView.setVisibility(View.GONE);
                        searchEmptyView.showTextView();
                        listView.setEmptyView(searchEmptyView);
                    }
                }
                if (dialogsSearchAdapter != null) {
                    dialogsSearchAdapter.searchDialogs(text);
                }
            }
        });
        item.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        item.setOnCancleListener(new ActionBarMenuItem.OnCancleListener() {
            @Override
            public void onCancle() {
                actionBar.closeSearchField();
            }
        });
        //Hossein
        backDrawable = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_back);
        //------------------------------------------------------------
        if (onlySelect) {
            actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));
        } else {
            if (searchString != null) {
                actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            } else {
                actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                actionBar.setTitle(LocaleController.getString("AppNameBeta", R.string.AppNameBeta));
            } else {
                actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
            }
        }
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    if (onlySelect) {
                        finishFragment();
                    } else if (parentLayout != null) {
                        parentLayout.getDrawerLayoutContainer().openDrawer(false);
                    }
                } else if (id == 1) {
                    UserConfig.appLocked = !UserConfig.appLocked;
                    UserConfig.saveConfig(false);
                    updatePasscodeButton();
                }   else if (id == 10) {
                    hideClickCounter++;
                    if (!hasClickHideItem) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (hideClickCounter >= 3) {
                                    if (Database.getInstance().checkHiddenMode(ApplicationLoader.applicationContext))
                                        Database.getInstance().setHiddenMode(ApplicationLoader.applicationContext, false);
                                    else
                                        Database.getInstance().setHiddenMode(ApplicationLoader.applicationContext, true);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            restartApplication(ApplicationLoader.applicationContext, 1);
                                        }
                                    }, 1000);
                                }
                                hideClickCounter = 0;
                                hasClickHideItem = false;
                            }
                        }, 5000);


                    }

                    Toast.makeText(getParentActivity(), getParentActivity().getResources().getString(R.string.HideModeIsEnable), Toast.LENGTH_LONG).show();

                }
                //Hossein

            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        fragmentView = frameLayout;
        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(true);
        listView.setItemAnimator(null);
        listView.setInstantClick(true);
        listView.setLayoutAnimation(null);
        listView.setTag(4);
        layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);

        //Hossein
        IDFinderCell = getParentActivity().getLayoutInflater().inflate(R.layout.idfindercell, null, false);
        ((TextView) IDFinderCell.findViewById(R.id.txt)).setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        ((TextView) IDFinderCell.findViewById(R.id.txt2)).setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        IDFinderCell.setBackgroundResource(R.drawable.list_selector);
        IDFinderCell.setOnClickListener((v) -> presentFragment(new Idsearcher()));
        IDFinderCell.setVisibility(View.GONE);
        //-------------------------
        frameLayout.addView(IDFinderCell, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        IDFinderCell.setVisibility(View.INVISIBLE);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) IDFinderCell.getLayoutParams();
        params.setMargins(0, 0, 0, AndroidUtilities.dp(10));
        tabsContainer = new LinearLayout(getParentActivity());
        tabsContainer.setOrientation(LinearLayout.VERTICAL);
        View border = new View(getParentActivity());
        View emptyCell = new View(getParentActivity());

        border.setBackgroundColor(Color.parseColor("#BCBCBC"));
        tabsContainer.addView(tabsLayout);
        tabsLayout.setPadding(AndroidUtilities.dp(10), 0, AndroidUtilities.dp(10), 0);
        tabsContainer.addView(emptyCell);
        tabsContainer.addView(border);
        ViewGroup.MarginLayoutParams borderMargin = (ViewGroup.MarginLayoutParams) border.getLayoutParams();
        borderMargin.setMargins(0, AndroidUtilities.dp(5), 0, 0);
        border.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        emptyCell.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(5)));
        linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(tabsContainer);
        linearLayout.addView(listView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setOnTouchListener(new OnSwipeTouchListener(getParentActivity()) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return super.onTouch(v, event);
            }

            public void onSwipeRight() {
                doSwipe(true);
            }

            public void onSwipeLeft() {
                doSwipe(false);
            }

            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                // if (!isRecyclerScrollabdle)
                //  hideTabLayout(false);

            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                // if (!isRecyclerScrollabdle)
                //   hideTabLayout(true);

            }
        });
        //Hossein
        ViewGroup.MarginLayoutParams tabMargin = (ViewGroup.MarginLayoutParams) tabsContainer.getLayoutParams();
        tabMargin.setMargins(0, AndroidUtilities.dp(5), 0, 0);
        //----------
        listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (helpDialog.isShowing()) return;
                if (listView == null || listView.getAdapter() == null) {
                    return;
                }
                long dialog_id = 0;
                int message_id = 0;
                RecyclerView.Adapter adapter = listView.getAdapter();
                if (adapter == dialogsAdapter) {
                    TLRPC.TL_dialog dialog = dialogsAdapter.getItem(position);
                    if (dialog == null) {
                        return;
                    }

                    dialog_id = dialog.id;

                } else if (adapter == dialogsSearchAdapter) {
                    Object obj = dialogsSearchAdapter.getItem(position);
                    if (obj instanceof TLRPC.User) {
                        dialog_id = ((TLRPC.User) obj).id;
                        if (dialogsSearchAdapter.isGlobalSearch(position)) {
                            ArrayList<TLRPC.User> users = new ArrayList<>();
                            users.add((TLRPC.User) obj);
                            MessagesController.getInstance().putUsers(users, false);
                            MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                        }
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.User) obj);
                        }
                    } else if (obj instanceof TLRPC.Chat) {
                        if (dialogsSearchAdapter.isGlobalSearch(position)) {
                            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
                            chats.add((TLRPC.Chat) obj);
                            MessagesController.getInstance().putChats(chats, false);
                            MessagesStorage.getInstance().putUsersAndChats(null, chats, false, true);
                        }
                        if (((TLRPC.Chat) obj).id > 0) {
                            dialog_id = -((TLRPC.Chat) obj).id;
                        } else {
                            dialog_id = AndroidUtilities.makeBroadcastId(((TLRPC.Chat) obj).id);
                        }
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.Chat) obj);
                        }
                    } else if (obj instanceof TLRPC.EncryptedChat) {
                        dialog_id = ((long) ((TLRPC.EncryptedChat) obj).id) << 32;
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.EncryptedChat) obj);
                        }
                    } else if (obj instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) obj;
                        dialog_id = messageObject.getDialogId();
                        message_id = messageObject.getId();
                        dialogsSearchAdapter.addHashtagsFromMessage(dialogsSearchAdapter.getLastSearchString());
                    } else if (obj instanceof String) {
                        actionBar.openSearchField((String) obj);
                    }
                }

                if (dialog_id == 0) {
                    return;
                }

                if (onlySelect) {
                    didSelectResult(dialog_id, true, false);
                } else {
                    Bundle args = new Bundle();
                    int lower_part = (int) dialog_id;
                    int high_id = (int) (dialog_id >> 32);
                    if (lower_part != 0) {
                        if (high_id == 1) {
                            args.putInt("chat_id", lower_part);
                        } else {
                            if (lower_part > 0) {
                                args.putInt("user_id", lower_part);
                            } else if (lower_part < 0) {
                                if (message_id != 0) {
                                    TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_part);
                                    if (chat != null && chat.migrated_to != null) {
                                        args.putInt("migrated_to", lower_part);
                                        lower_part = -chat.migrated_to.channel_id;
                                    }
                                }
                                args.putInt("chat_id", -lower_part);
                            }
                        }
                    } else {
                        args.putInt("enc_id", high_id);
                    }
                    if (message_id != 0) {
                        args.putInt("message_id", message_id);
                    } else {
                        if (actionBar != null) {
                            actionBar.closeSearchField();
                        }
                    }
                    if (AndroidUtilities.isTablet()) {
                        //Hossein
                     /*   if (openedDialogId == dialog_id && adapter != dialogsSearchAdapter) {

                            return;
                        }*/
                        if (dialogsAdapter != null) {
                            dialogsAdapter.setOpenedDialogId(openedDialogId = dialog_id);
                            updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                        }
                    }
                    if (searchString != null) {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats);


                            showLoginDialog(dialog_id, args, false, false);

                        }
                    } else {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {

                            showLoginDialog(dialog_id, args, false, false);
                        }
                    }
                }
            }
        });
        listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {
                if (hideHelp && !Database.getInstance().checkHiddenMode(context)) {
                    helpWizard(20);
                }
                if (onlySelect || searching && searchWas || getParentActivity() == null) {
                    if (searchWas && searching || dialogsSearchAdapter.isRecentSearchDisplayed()) {
                        RecyclerView.Adapter adapter = listView.getAdapter();
                        if (adapter == dialogsSearchAdapter) {
                            Object item = dialogsSearchAdapter.getItem(position);
                            if (item instanceof String || dialogsSearchAdapter.isRecentSearchDisplayed()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (dialogsSearchAdapter.isRecentSearchDisplayed()) {
                                            dialogsSearchAdapter.clearRecentSearch();
                                        } else {
                                            dialogsSearchAdapter.clearRecentHashtags();
                                        }
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                showDialog(builder.create());
                                return true;
                            }
                        }
                    }
                    return false;
                }
                TLRPC.TL_dialog dialog;
                ArrayList<TLRPC.TL_dialog> dialogs = getDialogsArray();
                if (position < 0 || position >= dialogs.size()) {
                    return false;
                }
                dialog = dialogs.get(position);
                selectedDialog = dialog.id;

                BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
                int lower_id = (int) selectedDialog;
                int high_id = (int) (selectedDialog >> 32);

                if (DialogObject.isChannel(dialog)) {
                    final TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_id);
                    CharSequence items[];
                    String hideItem = getParentActivity().getResources().getString(R.string.hideOption);
                    String unhideItem = getParentActivity().getResources().getString(R.string.unhideOption);

                    SpannableString ss = new SpannableString(!Database.getInstance().checkForHidden(selectedDialog + "", getParentActivity()) ? hideItem + "   . " : unhideItem + "   . ");

                    CenteredImageSpan span = new CenteredImageSpan(getParentActivity(), LocaleController.isRTL ? R.drawable.ic_guide_arrow : R.drawable.ic_guide_arrow_rotate);
                    ss.setSpan(span, ss.toString().indexOf("."), ss.toString().indexOf(".") + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    CharSequence hideOption = !hideHelp ? Database.getInstance().checkForHidden(selectedDialog + "", getParentActivity()) ? getParentActivity().getApplicationContext().getResources().getString(R.string.unhideOption) : getParentActivity().getApplicationContext().getResources().getString(R.string.hideOption) : ss;

                    if (chat != null && chat.megagroup) {
                        items = new CharSequence[]{Database.getInstance().checkLock(selectedDialog, getParentActivity()) ? LocaleController.getString("UnlockChat", R.string.UnlockChat) : LocaleController.getString("LockChat", R.string.LockChat), LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache), LocaleController.getString("SpecialFileManager", R.string.SpecialFileManager), chat == null || !chat.creator ? LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu) : LocaleController.getString("DeleteMegaMenu", R.string.DeleteMegaMenu), hideOption};
                    } else {
                        items = new CharSequence[]{Database.getInstance().checkLock(selectedDialog, getParentActivity()) ? LocaleController.getString("UnlockChat", R.string.UnlockChat) : LocaleController.getString("LockChat", R.string.LockChat), LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache), LocaleController.getString("SpecialFileManager", R.string.SpecialFileManager), chat == null || !chat.creator ? LocaleController.getString("LeaveChannelMenu", R.string.LeaveChannelMenu) : LocaleController.getString("ChannelDeleteMenu", R.string.ChannelDeleteMenu), hideOption};
                    }
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            if (which == 0) {
                                if (Database.getInstance().checkLock(selectedDialog, getParentActivity()))
                                    showLoginDialog(selectedDialog, null, true, false);
                                else

                                    lockDialog(selectedDialog);


                            } else if (which == 1) {
                                if (chat != null && chat.megagroup) {
                                    builder.setMessage(LocaleController.getString("AreYouSureClearHistorySuper", R.string.AreYouSureClearHistorySuper));
                                } else {
                                    builder.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", R.string.AreYouSureClearHistoryChannel));
                                }
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        MessagesController.getInstance().deleteDialog(selectedDialog, 2);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                showDialog(builder.create());
                                return;


                            } else if (which == 2) {
                                advanceClearCache(selectedDialog);
                            } else if (which == 3) {
                                if (chat != null && chat.megagroup) {
                                    if (!chat.creator) {
                                        builder.setMessage(LocaleController.getString("MegaLeaveAlert", R.string.MegaLeaveAlert));
                                    } else {
                                        builder.setMessage(LocaleController.getString("MegaDeleteAlert", R.string.MegaDeleteAlert));
                                    }
                                } else {
                                    if (chat == null || !chat.creator) {
                                        builder.setMessage(LocaleController.getString("ChannelLeaveAlert", R.string.ChannelLeaveAlert));
                                    } else {
                                        builder.setMessage(LocaleController.getString("ChannelDeleteAlert", R.string.ChannelDeleteAlert));
                                    }
                                }
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        MessagesController.getInstance().deleteUserFromChat((int) -selectedDialog, UserConfig.getCurrentUser(), null);
                                        if (AndroidUtilities.isTablet()) {
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, selectedDialog);
                                        }
                                    }
                                });
                                //dele
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                showDialog(builder.create());

                                return;


                            } else if (which == 4) {
                                if (Database.getInstance().checkForHidden(selectedDialog + "", getParentActivity())) {
                                    Database.getInstance().deleteFromHidden(selectedDialog + "", getParentActivity());
                                    if (hideHelp)
                                        helpWizard(23);

                                } else {
                                    Database.getInstance().hideDialogById(selectedDialog + "", getParentActivity());
                                    if (hideHelp) helpWizard(21);
                                }
                                dialogsAdapter.notifyDataSetChanged();
                                return;

                                //hide


                            }


                        }
                    });
                    showDialog(builder.create());
                } else {
                    final boolean isChat = lower_id < 0 && high_id != 1;
                    TLRPC.User user = null;
                    if (!isChat && lower_id > 0 && high_id != 1) {
                        user = MessagesController.getInstance().getUser(lower_id);
                    }
                    final boolean isBot = user != null && user.bot;
                    String hideItem = getParentActivity().getResources().getString(R.string.hideOption);
                    String unhideItem = getParentActivity().getResources().getString(R.string.unhideOption);

                    SpannableString ss = new SpannableString(!Database.getInstance().checkForHidden(selectedDialog + "", getParentActivity()) ? hideItem + "   . " : unhideItem + "   . ");
                    CenteredImageSpan span = new CenteredImageSpan(getParentActivity(), LocaleController.isRTL ? R.drawable.ic_guide_arrow : R.drawable.ic_guide_arrow_rotate);
                    ss.setSpan(span, ss.toString().indexOf("."), ss.toString().indexOf(".") + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    CharSequence hideOption = !hideHelp ? Database.getInstance().checkForHidden(selectedDialog + "", getParentActivity()) ? getParentActivity().getApplicationContext().getResources().getString(R.string.unhideOption) : getParentActivity().getApplicationContext().getResources().getString(R.string.hideOption) : ss;

                    builder.setItems(new CharSequence[]{Database.getInstance().checkLock(selectedDialog, getParentActivity()) ? LocaleController.getString("UnlockChat", R.string.UnlockChat) : LocaleController.getString("LockChat", R.string.LockChat), LocaleController.getString("ClearHistory", R.string.ClearHistory), LocaleController.getString("SpecialFileManager", R.string.SpecialFileManager), isChat ? LocaleController.getString("DeleteChat", R.string.DeleteChat) : isBot ? LocaleController.getString("DeleteAndStop", R.string.DeleteAndStop) : LocaleController.getString("Delete", R.string.Delete), hideOption}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            if (which == 0) {
                                if (Database.getInstance().checkLock(selectedDialog, getParentActivity()))
                                    showLoginDialog(selectedDialog, null, true, false);
                                else

                                    lockDialog(selectedDialog);
                            } else if (which == 1) {
                                builder.setMessage(LocaleController.getString("AreYouSureClearHistory", R.string.AreYouSureClearHistory));
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        MessagesController.getInstance().deleteDialog(selectedDialog, 1);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                showDialog(builder.create());

                            } else if (which == 2) {
                                advanceClearCache(selectedDialog);
                                //file manager
                            } else if (which == 3) {
                                if (isChat) {
                                    builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
                                } else {
                                    builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
                                }
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (isChat) {
                                            TLRPC.Chat currentChat = MessagesController.getInstance().getChat((int) -selectedDialog);
                                            if (currentChat != null && ChatObject.isNotInChat(currentChat)) {
                                                MessagesController.getInstance().deleteDialog(selectedDialog, 0);
                                            } else {
                                                MessagesController.getInstance().deleteUserFromChat((int) -selectedDialog, MessagesController.getInstance().getUser(UserConfig.getClientUserId()), null);
                                            }
                                        } else {
                                            MessagesController.getInstance().deleteDialog(selectedDialog, 0);
                                        }
                                        if (isBot) {
                                            MessagesController.getInstance().blockUser((int) selectedDialog);
                                        }
                                        if (AndroidUtilities.isTablet()) {
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, selectedDialog);
                                        }

                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                showDialog(builder.create());
                            } else if (which == 4) {
                                if (Database.getInstance().checkForHidden(selectedDialog + "", getParentActivity())) {
                                    Database.getInstance().deleteFromHidden(selectedDialog + "", getParentActivity());
                                    if (hideHelp)
                                        helpWizard(23);
                                } else {
                                    Database.getInstance().hideDialogById(selectedDialog + "", getParentActivity());
                                    if (hideHelp) helpWizard(21);
                                }
                                dialogsAdapter.notifyDataSetChanged();
                                return;
                            }


                        }
                    });
                    showDialog(builder.create());

                }
                return true;
            }
        });

        searchEmptyView = new EmptyTextProgressView(context);
        searchEmptyView.setVisibility(View.GONE);
        searchEmptyView.setShowAtCenter(true);
        searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(searchEmptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        ViewGroup.MarginLayoutParams emptyViewLayoutParams2 = (ViewGroup.MarginLayoutParams) searchEmptyView.getLayoutParams();
        emptyViewLayoutParams2.setMargins(0, AndroidUtilities.dp(60), 0, 0);
        emptyView = new LinearLayout(context);
        emptyView.setOrientation(LinearLayout.VERTICAL);
        emptyView.setVisibility(View.GONE);
        emptyView.setGravity(Gravity.CENTER);
        frameLayout.addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        //Hossein
        ViewGroup.MarginLayoutParams emptyViewLayoutParams = (ViewGroup.MarginLayoutParams) emptyView.getLayoutParams();
        emptyViewLayoutParams.setMargins(0, AndroidUtilities.dp(60), 0, 0);
        //---------------
        emptyView.setOnTouchListener(new OnSwipeTouchListener(getParentActivity()) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return super.onTouch(v, event);
            }

            public void onSwipeRight() {
                doSwipe(true);
            }

            public void onSwipeLeft() {
                doSwipe(false);
            }

        });
        emptyHeader = new TextView(context);
        emptyHeader.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        emptyHeader.setText(LocaleController.getString("NoChats", R.string.NoChats));
        emptyHeader.setTextColor(0xff959595);
        emptyHeader.setGravity(Gravity.CENTER);
        emptyHeader.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        emptyView.addView(emptyHeader, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        emptyMessage = new TextView(context);
        emptyMessage.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        String help = LocaleController.getString("NoChatsHelp", R.string.NoChatsHelp);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            help = help.replace('\n', ' ');
        }
        emptyMessage.setText(help);
        emptyMessage.setTextColor(0xff959595);
        emptyMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        emptyMessage.setGravity(Gravity.CENTER);
        emptyMessage.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(6), AndroidUtilities.dp(8), 0);
        emptyMessage.setLineSpacing(AndroidUtilities.dp(2), 1);
        emptyView.addView(emptyMessage, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        progressView = new ProgressBar(context);
        progressView.setVisibility(View.GONE);
        frameLayout.addView(progressView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        floatingButton = new ImageView(context);
        floatingButton.setVisibility(onlySelect ? View.GONE : View.VISIBLE);
        floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        floatingButton.setBackgroundResource(R.drawable.floating_states);
        Drawable floatingBackground = getParentActivity().getResources().getDrawable(R.drawable.floating_states);
        floatingBackground.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
        floatingButton.setBackgroundDrawable(floatingBackground);
        floatingButton.setImageResource(R.drawable.floating_pencil);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(2), AndroidUtilities.dp(4)).setDuration(200));
            animator.addState(new int[]{}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(4), AndroidUtilities.dp(2)).setDuration(200));
            floatingButton.setStateListAnimator(animator);
            floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint("NewApi")
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
                }
            });
        }
        frameLayout.addView(floatingButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, LocaleController.isRTL ? 14 : 0, 0, LocaleController.isRTL ? 0 : 14, 14));
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  int[] pos = new int[2];
                floatingButton.getLocationOnScreen(pos);
                if (pos[0] > 0 && pos[1] > 0) {
                    floatingRawX = pos[0];
                }
                floatingButton.setImageResource(R.drawable.ic_close_float);*/


                new FloatingActionDialog(getParentActivity(), DialogsActivity.this).show();
                Analytics.getInstance(getParentActivity()).sendEvent("floating", "click", Analytics.FLOATING_ACTION_BUTTON);
            }
        });


        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && searching && searchWas) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = Math.abs(layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();

                if (searching && searchWas) {
                    if (visibleItemCount > 0 && layoutManager.findLastVisibleItemPosition() == totalItemCount - 1 && !dialogsSearchAdapter.isMessagesSearchEndReached()) {
                        dialogsSearchAdapter.loadMoreSearchMessages();
                    }
                    return;
                }
                if (visibleItemCount > 0) {
                    if (layoutManager.findLastVisibleItemPosition() >= getDialogsArray().size() - 10) {
                        MessagesController.getInstance().loadDialogs(-1, 100, !MessagesController.getInstance().dialogsEndReached);
                    }
                }

                if (floatingButton.getVisibility() != View.GONE) {
                    final View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean goingDown;
                    boolean changed = true;
                    if (prevPosition == firstVisibleItem) {
                        final int topDelta = prevTop - firstViewTop;
                        goingDown = firstViewTop < prevTop;
                        changed = Math.abs(topDelta) > 1;
                    } else {
                        goingDown = firstVisibleItem > prevPosition;
                    }
                    if (changed && scrollUpdated) {
                        hideFloatingButton(goingDown, dy <= 0 ? true : totalItemCount > visibleItemCount);
                    }

                    prevPosition = firstVisibleItem;
                    prevTop = firstViewTop;
                    scrollUpdated = true;
                }
            }
        });

        if (searchString == null) {
            dialogsAdapter = new DialogsAdapter(context, dialogsType);
            if (AndroidUtilities.isTablet() && openedDialogId != 0) {
                dialogsAdapter.setOpenedDialogId(openedDialogId);
            }
            listView.setAdapter(dialogsAdapter);
        }
        int type = 0;
        if (searchString != null) {
            type = 2;
        } else if (!onlySelect) {
            type = 1;
        }
        dialogsSearchAdapter = new DialogsSearchAdapter(context, type, dialogsType);
        dialogsSearchAdapter.setDelegate(new DialogsSearchAdapter.DialogsSearchAdapterDelegate() {
            @Override
            public void searchStateChanged(boolean search) {
                if (searching && searchWas && searchEmptyView != null) {
                    if (search) {
                        searchEmptyView.showProgress();
                    } else {
                        searchEmptyView.showTextView();
                    }
                }
            }

            @Override
            public void didPressedOnSubDialog(int did) {
                if (onlySelect) {
                    didSelectResult(did, true, false);
                } else {
                    Bundle args = new Bundle();
                    if (did > 0) {
                        args.putInt("user_id", did);
                    } else {
                        args.putInt("chat_id", -did);
                    }
                    if (actionBar != null) {
                        actionBar.closeSearchField();
                    }
                    if (AndroidUtilities.isTablet()) {
                        if (dialogsAdapter != null) {
                            dialogsAdapter.setOpenedDialogId(openedDialogId = did);
                            updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                        }
                    }
                    if (searchString != null) {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats);

                            showLoginDialog(did > 0 ? did : -did, args, false, false);

                        }
                    } else {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {

                            showLoginDialog(did > 0 ? did : -did, args, false, false);

                        }
                    }
                }
            }

            @Override
            public void needRemoveHint(final int did) {
                if (getParentActivity() == null) {
                    return;
                }
                TLRPC.User user = MessagesController.getInstance().getUser(did);
                if (user == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", R.string.ChatHintsDelete, ContactsController.formatName(user.first_name, user.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SearchQuery.removePeer(did);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            }
        });

        if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
            searchEmptyView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            listView.setEmptyView(progressView);
        } else {
            searchEmptyView.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);
            listView.setEmptyView(emptyView);
        }
        if (searchString != null) {
            actionBar.openSearchField(searchString);
        }

        if (!onlySelect && dialogsType == 0) {
            frameLayout.addView(new PlayerView(context, this), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 39, Gravity.TOP | Gravity.LEFT, 0, -36, 0, 0));
        }

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        actionBar.setDialogsActivity(true);
        //Hossein
        floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] pos = new int[2];
                floatingButton.getLocationOnScreen(pos);
                if (pos[0] > 0 && pos[1] > 0) {
                    floatingRawX = pos[0];
                    floatingX = pos[0] + (floatingButton.getWidth() / 2);
                    floatingY = pos[1] + (floatingButton.getHeight() / 2);
                    if (Build.VERSION.SDK_INT >= 16)
                        floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        //----------------------
        if (dialogsAdapter != null) {
            dialogsAdapter.notifyDataSetChanged();
        }
        if (dialogsSearchAdapter != null) {
            dialogsSearchAdapter.notifyDataSetChanged();
        }
        if (checkPermission && !onlySelect && Build.VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                checkPermission = false;
                if (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionContacts", R.string.PermissionContacts));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        showDialog(permissionDialog = builder.create());
                    } else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        showDialog(permissionDialog = builder.create());
                    } else {
                        askForPermissons();
                    }
                }
            }
        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askForPermissons() {
        Activity activity = getParentActivity();
        if (activity == null) {
            return;
        }
        ArrayList<String> permissons = new ArrayList<>();
        if (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissons.add(Manifest.permission.READ_CONTACTS);
            permissons.add(Manifest.permission.WRITE_CONTACTS);
            permissons.add(Manifest.permission.GET_ACCOUNTS);
        }
        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissons.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissons.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        String[] items = permissons.toArray(new String[permissons.size()]);
        activity.requestPermissions(items, 1);
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (permissionDialog != null && dialog == permissionDialog && getParentActivity() != null) {
            askForPermissons();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        visiblePossition = 0;
        if (!onlySelect && floatingButton != null) {
            floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    floatingButton.setTranslationY(floatingHidden ? AndroidUtilities.dp(100) : 0);
                    floatingButton.setClickable(!floatingHidden);
                    if (floatingButton != null) {
                        if (Build.VERSION.SDK_INT < 16) {
                            floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length <= a || grantResults[a] != PackageManager.PERMISSION_GRANTED) {
                    continue;
                }
                switch (permissions[a]) {
                    case Manifest.permission.READ_CONTACTS:
                        ContactsController.getInstance().readContacts();
                        break;
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        ImageLoader.getInstance().checkMediaPaths();
                        break;
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            if (dialogsAdapter != null) {
                if (dialogsAdapter.isDataSetChanged()) {
                    dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows(MessagesController.UPDATE_MASK_NEW_MESSAGE);
                }
            }
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.notifyDataSetChanged();
            }
            if (listView != null) {
                try {
                    if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                        searchEmptyView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                        listView.setEmptyView(progressView);
                    } else {
                        progressView.setVisibility(View.GONE);
                        if (searching && searchWas) {
                            emptyView.setVisibility(View.GONE);
                            listView.setEmptyView(searchEmptyView);
                        } else {
                            searchEmptyView.setVisibility(View.GONE);
                            listView.setEmptyView(emptyView);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e); //TODO fix it in other way?
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.updateInterfaces) {
            updateVisibleRows((Integer) args[0]);
        } else if (id == NotificationCenter.appDidLogout) {
            dialogsLoaded = false;
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.contactsDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.openedChatChanged) {
            if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                boolean close = (Boolean) args[1];
                long dialog_id = (Long) args[0];
                if (close) {
                    if (dialog_id == openedDialogId) {
                        openedDialogId = 0;
                    }
                } else {
                    openedDialogId = dialog_id;
                }
                if (dialogsAdapter != null) {
                    dialogsAdapter.setOpenedDialogId(openedDialogId);
                }
                updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
            }
        } else if (id == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.messageReceivedByAck || id == NotificationCenter.messageReceivedByServer || id == NotificationCenter.messageSendError) {
            updateVisibleRows(MessagesController.UPDATE_MASK_SEND_STATE);
        } else if (id == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        }
        if (id == NotificationCenter.needReloadRecentDialogsSearch) {
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (id == NotificationCenter.didLoadedReplyMessages) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.reloadHints) {
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.notifyDataSetChanged();
            }
        }
    }

    private ArrayList<TLRPC.TL_dialog> getDialogsArray() {
        if (dialogsType == 0) {
            return MessagesController.getInstance().dialogs;
        } else if (dialogsType == 1) {
            return MessagesController.getInstance().dialogsServerOnly;
        } else if (dialogsType == 2) {
            return MessagesController.getInstance().dialogsGroupsOnly;
        } else if (dialogsType == 4) return MessagesController.getInstance().dialogsUsers;
        else if (dialogsType == 3) return MessagesController.getInstance().dialogsBots;
        else if (dialogsType == 8) return MessagesController.getInstance().dialogsFavs;
        else if (dialogsType == 10) return MessagesController.getInstance().dialogsChannels;
        else if (dialogsType == 11) return MessagesController.getInstance().dialogsunread;
        else if (dialogsType == 12) return MessagesController.getInstance().dialogshidden;
        else if (dialogsType == 14) return MessagesController.getInstance().dialogsmegagroups;
        else if (dialogsType == 15) {
            ArrayList<TLRPC.TL_dialog> result = new ArrayList<>();
            result.addAll(MessagesController.getInstance().dialogsFavoriteContact);
            result.addAll(MessagesController.getInstance().dialogsFavs);
            return deleteHiddenDialogs(result);

        } else if (dialogsType == 16) return MessagesController.getInstance().dialogsBlockedUsers;
        else if (dialogsType == 17) return MessagesController.getInstance().dialogsOnlineUsers;
        else if (dialogsType == 18) return MessagesController.getInstance().dialogsjusgroups;

        return null;
    }

    private void updatePasscodeButton() {
        if (passcodeItem == null) {
            return;
        }
        if (UserConfig.passcodeHash.length() != 0 && !searching) {
            passcodeItem.setVisibility(View.VISIBLE);
            if (UserConfig.appLocked) {
                passcodeItem.setIcon(R.drawable.lock_close);
            } else {
                passcodeItem.setIcon(R.drawable.lock_open);
            }
        } else {
            passcodeItem.setVisibility(View.GONE);
        }
    }

    private void hideFloatingButton(boolean hide, boolean tabLayout) {
        if (visiblePossition == 0) {
            visiblePossition = ((LinearLayoutManager) listView.getLayoutManager()).findLastVisibleItemPosition();
        }
        if (floatingHidden == hide) {
            return;
        }


        floatingHidden = hide;
        ObjectAnimator animator = ObjectAnimator.ofFloat(floatingButton, "translationY", floatingHidden ? AndroidUtilities.dp(100) : 0).setDuration(300);
        animator.setInterpolator(floatingInterpolator);
        floatingButton.setClickable(!hide);
        animator.start();
       /* if (listHidden != hide && !doProcessAnimation && tabLayout) {
            animator2 = ObjectAnimator.ofFloat(linearLayout, "translationY", floatingHidden ? -tabsContainer.getHeight() - AndroidUtilities.dp(5) : 0).setDuration(300);
            animator2.setInterpolator(floatingInterpolator);
            animator2.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    doProcessAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    doProcessAnimation = false;

                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    doProcessAnimation = false;
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            animator2.start();
            linearLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, floatingHidden ? linearLayout.getHeight() + tabsContainer.getHeight() : ViewGroup.LayoutParams.MATCH_PARENT));
        }
        listHidden = hide;*/

    }

    private void updateVisibleRows(int mask) {
        if (listView == null) {
            return;
        }
        int count = listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = listView.getChildAt(a);
            if (child instanceof DialogCell) {
                if (listView.getAdapter() != dialogsSearchAdapter) {
                    DialogCell cell = (DialogCell) child;
                    if ((mask & MessagesController.UPDATE_MASK_NEW_MESSAGE) != 0) {
                        cell.checkCurrentDialogIndex();
                        if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                            cell.setDialogSelected(cell.getDialogId() == openedDialogId);
                        }
                    } else if ((mask & MessagesController.UPDATE_MASK_SELECT_DIALOG) != 0) {
                        if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                            cell.setDialogSelected(cell.getDialogId() == openedDialogId);
                        }
                    } else {
                        cell.update(mask);
                    }
                }
            } else if (child instanceof UserCell) {
                ((UserCell) child).update(mask);
            } else if (child instanceof ProfileSearchCell) {
                ((ProfileSearchCell) child).update(mask);
            } else if (child instanceof RecyclerListView) {
                RecyclerListView innerListView = (RecyclerListView) child;
                int count2 = innerListView.getChildCount();
                for (int b = 0; b < count2; b++) {
                    View child2 = innerListView.getChildAt(b);
                    if (child2 instanceof HintDialogCell) {
                        ((HintDialogCell) child2).checkUnreadCounter(mask);
                    }
                }
            }
        }
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        delegate = dialogsActivityDelegate;
    }

    public void setSearchString(String string) {
        searchString = string;
    }

    public boolean isMainDialogList() {
        return delegate == null && searchString == null;
    }

    private void didSelectResult(final long dialog_id, boolean useAlert, final boolean param) {
        if (addToGroupAlertString == null) {
            if ((int) dialog_id < 0 && ChatObject.isChannel(-(int) dialog_id) && (cantSendToChannels || !ChatObject.isCanWriteToChannel(-(int) dialog_id))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("ChannelCantSendMessage", R.string.ChannelCantSendMessage));
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                showDialog(builder.create());
                return;
            }
        }
        if (useAlert && (selectAlertString != null && selectAlertStringGroup != null || addToGroupAlertString != null)) {
            if (getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            int lower_part = (int) dialog_id;
            int high_id = (int) (dialog_id >> 32);
            if (lower_part != 0) {
                if (high_id == 1) {
                    TLRPC.Chat chat = MessagesController.getInstance().getChat(lower_part);
                    if (chat == null) {
                        return;
                    }
                    builder.setMessage(LocaleController.formatStringSimple(selectAlertStringGroup, chat.title));
                } else {
                    if (lower_part > 0) {
                        TLRPC.User user = MessagesController.getInstance().getUser(lower_part);
                        if (user == null) {
                            return;
                        }
                        builder.setMessage(LocaleController.formatStringSimple(selectAlertString, UserObject.getUserName(user)));
                    } else if (lower_part < 0) {
                        TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_part);
                        if (chat == null) {
                            return;
                        }
                        if (addToGroupAlertString != null) {
                            builder.setMessage(LocaleController.formatStringSimple(addToGroupAlertString, chat.title));
                        } else {
                            builder.setMessage(LocaleController.formatStringSimple(selectAlertStringGroup, chat.title));
                        }
                    }
                }
            } else {
                TLRPC.EncryptedChat chat = MessagesController.getInstance().getEncryptedChat(high_id);
                TLRPC.User user = MessagesController.getInstance().getUser(chat.user_id);
                if (user == null) {
                    return;
                }
                builder.setMessage(LocaleController.formatStringSimple(selectAlertString, UserObject.getUserName(user)));
            }

            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    didSelectResult(dialog_id, false, false);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        } else {
            if (delegate != null) {
                delegate.didSelectDialog(DialogsActivity.this, dialog_id, param);
                delegate = null;
            } else {
                finishFragment();
            }
        }
    }

    //Hossein
    private void createTabs(final Context ctx) {
        final SharedPreferences pefrence = ApplicationLoader.applicationContext.getSharedPreferences("shared1", MODE_PRIVATE);
        context = getParentActivity().getApplicationContext();
        allDialogs = new ImageView(ctx);
        allDialogs.setImageResource(R.drawable.ic_all_chats_fill);
        userDialogs = new ImageView(ctx);
        userDialogs.setImageResource(R.drawable.ic_content_fill);
        favoriteContacts = new ImageView(ctx);
        favoriteContacts.setImageResource(R.drawable.ic_favorite_contant_fill);
        groupDialogs = new ImageView(ctx);
        groupDialogs.setImageResource(R.drawable.ic_group_fill);
        superGroupDialogs = new ImageView(ctx);
        superGroupDialogs.setImageResource(R.drawable.ic_super_group_fill);
        channelDialogs = new ImageView(ctx);
        channelDialogs.setImageResource(R.drawable.ic_channel_fill);
        botDialogs = new ImageView(ctx);
        botDialogs.setImageResource(R.drawable.ic_robot_fill);
        favoriteDialogs = new ImageView(ctx);
        favoriteContacts.setImageResource(R.drawable.ic_favorite_contant_fill);
        unreadDialogs = new ImageView(ctx);
        unreadDialogs.setImageResource(R.drawable.ic_unread_chat_fill);
        onlineUserDialogs = new ImageView(ctx);
        onlineUserDialogs.setImageResource(R.drawable.ic_online_contant_fill);
        blockUserDialogs = new ImageView(ctx);
        blockUserDialogs.setImageResource(R.drawable.blockusers);

        View[] items = {
                allDialogs,
                userDialogs,
                favoriteContacts,
                groupDialogs,
                superGroupDialogs,
                channelDialogs,
                botDialogs,
                favoriteDialogs,
                unreadDialogs,
                onlineUserDialogs,
                blockUserDialogs
        };
        for (int i = 0; i < items.length; i++) {
            ImageView imageView = (ImageView) items[i];
            imageView.setBackgroundResource(R.drawable.list_selector);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
        }
        canSwipe = 0;
        SharedPreferences shareRead = getParentActivity().getApplicationContext().getSharedPreferences("tabs", MODE_PRIVATE);
        if (shareRead.getBoolean("fav", true)) canSwipe++;
        if (shareRead.getBoolean("contacts", true)) canSwipe++;
        if (shareRead.getBoolean("groups", true)) canSwipe++;
        if (shareRead.getBoolean("superGroups", true)) canSwipe++;
        if (shareRead.getBoolean("channels", true)) canSwipe++;
        if (shareRead.getBoolean("bots", true)) canSwipe++;
        if (shareRead.getBoolean("onlineContacts", true)) canSwipe++;
        if (shareRead.getBoolean("block", false)) canSwipe++;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getParentActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        tabsLayout.addView(allDialogs, LayoutHelper.createLinear(0, -1, 1.0f));
        tabsLayout.addView(unreadDialogs, LayoutHelper.createLinear(0, -1, 1.0f));
        if (shareRead.getBoolean("fav", true) || helpmode)
            tabsLayout.addView(favoriteContacts, LayoutHelper.createLinear(0, -1, 1.0f));
        if (shareRead.getBoolean("contacts", true) || helpmode)
            tabsLayout.addView(userDialogs, LayoutHelper.createLinear(0, -1, 1.0f));
        if (shareRead.getBoolean("groups", true) || helpmode)
            tabsLayout.addView(groupDialogs, LayoutHelper.createLinear(0, -1, 1.0f));
        if (shareRead.getBoolean("superGroups", true) || helpmode)
            tabsLayout.addView(superGroupDialogs, LayoutHelper.createLinear(0, -1, 1.0f));
        if (shareRead.getBoolean("onlineContacts", true) || helpmode)
            tabsLayout.addView(onlineUserDialogs, LayoutHelper.createLinear(0, -1, 1.0f));
        if (shareRead.getBoolean("channels", true) || helpmode)
            tabsLayout.addView(channelDialogs, LayoutHelper.createLinear(0, -1, 1.0f));
        if (shareRead.getBoolean("bots", true) || helpmode)
            tabsLayout.addView(botDialogs, LayoutHelper.createLinear(0, -1, 1.0f));
        if (shareRead.getBoolean("block", false) && !helpmode)
            tabsLayout.addView(blockUserDialogs, LayoutHelper.createLinear(0, -1, 1.0f));

        allDialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setTitle(itemNames[0]);
                filterDialogs(context, 0, v);
                new Thread(() ->
                {
                }).start();

            }
        });
        superGroupDialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pefrence.getBoolean("chk2", false)) {
                    actionBar.setTitle(itemNames[5]);
                    filterDialogs(context, 14, v);
                    new Thread(() -> {
                    }).start();
                }
            }
        });
        userDialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pefrence.getBoolean("chk1", false)) {
                    actionBar.setTitle(itemNames[3]);
                    filterDialogs(context, 4, v);
                    new Thread(() -> {
                    }).start();
                }
            }
        });
        favoriteContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setTitle(itemNames[2]);
                filterDialogs(context, 15, v);
                new Thread(() -> {
                }).start();
            }
        });
        groupDialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pefrence.getBoolean("chk2", false)) {
                    actionBar.setTitle(itemNames[4]);
                    filterDialogs(context, 18, v); //2
                    new Thread(() -> {
                    }).start();
                }

            }
        });


        channelDialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pefrence.getBoolean("chk3", false)) {
                    actionBar.setTitle(itemNames[7]);
                    filterDialogs(context, 10, v);
                }

            }
        });
        botDialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pefrence.getBoolean("chk4", false)) {
                    actionBar.setTitle(itemNames[8]);
                    filterDialogs(context, 3, v);
                    new Thread(() -> {
                    }).start();
                }

            }
        });
        favoriteDialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setTitle(itemNames[6]);
                filterDialogs(context, 8, v);
                new Thread(() -> {
                }).start();

            }
        });
        unreadDialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!pefrence.getBoolean("chk1", false) && !pefrence.getBoolean("chk2", false) && !pefrence.getBoolean("chk3", false) && !pefrence.getBoolean("chk4", false)) {
                    actionBar.setTitle(itemNames[1]);
                    filterDialogs(context, 11, v);
                    new Thread(() -> {
                    }).start();
                }
            }
        });
        onlineUserDialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setTitle(itemNames[6]);
                new Thread(() -> {
                }).start();
                filterDialogs(context, 17, v);
            }
        });
        blockUserDialogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setTitle(itemNames[9]);
                filterDialogs(context, 16, v);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (wizardHandler != null)
            wizardHandler.removeCallbacks(wizardRunnable);
        actionBar.setDialogsActivity(false);
    }

    private void refreshTabs() {
        allDialogs.clearColorFilter();
        userDialogs.clearColorFilter();
        favoriteContacts.clearColorFilter();
        groupDialogs.clearColorFilter();
        superGroupDialogs.clearColorFilter();
        channelDialogs.clearColorFilter();
        botDialogs.clearColorFilter();
        favoriteContacts.clearColorFilter();
        unreadDialogs.clearColorFilter();
        onlineUserDialogs.clearColorFilter();
        blockUserDialogs.clearColorFilter();
    }

    private void filterDialogs(Context ctx, int dlg, View item) {
        refreshTabs();
        if (dlg == 0) {
            allDialogs.setImageResource(R.drawable.ic_all_chats_fill);
            allDialogs.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            Analytics.getInstance(getParentActivity()).sendEvent("tabslayout", "click", Analytics.ALL_MESSAGES_CATEGORY);
        } else if (dlg == 14) {
            superGroupDialogs.setImageResource(R.drawable.ic_super_group_fill);
            superGroupDialogs.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            Analytics.getInstance(getParentActivity()).sendEvent("tabslayout", "click", Analytics.SUPPERGROUPS_MESSAGES_CATEGORY);
        } else if (dlg == 4) {
            userDialogs.setImageResource(R.drawable.ic_content_fill);
            userDialogs.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            Analytics.getInstance(getParentActivity()).sendEvent("tabslayout", "click", Analytics.CONTACTS_MESSAGES_CATEGORY);
        } else if (dlg == 15) {
            favoriteContacts.setImageResource(R.drawable.ic_favorite_contant_fill);
            favoriteContacts.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            Analytics.getInstance(getParentActivity()).sendEvent("tabslayout", "click", Analytics.FAVORITE_CONTACTS);
        } else if (dlg == 18) {
            groupDialogs.setImageResource(R.drawable.ic_group_fill);
            groupDialogs.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            Analytics.getInstance(getParentActivity()).sendEvent("tabslayout", "click", Analytics.GROUPS_MESSAGES_CATEGORY);
        } else if (dlg == 10) {
            channelDialogs.setImageResource(R.drawable.ic_channel_fill);
            channelDialogs.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            Analytics.getInstance(getParentActivity()).sendEvent("tabslayout", "click", Analytics.CHANNEL_MESSAGES_CATEGORY);
        } else if (dlg == 3) {
            botDialogs.setImageResource(R.drawable.ic_robot_fill);
            botDialogs.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            Analytics.getInstance(getParentActivity()).sendEvent("tabslayout", "click", Analytics.BOT_MESSAGES_CATEGORY);
        } else if (dlg == 11) {
            unreadDialogs.setImageResource(R.drawable.ic_unread_chat_fill);
            unreadDialogs.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            Analytics.getInstance(getParentActivity()).sendEvent("tabslayout", "click", Analytics.UNREAD_MESSAGES_CATEGORY);
        } else if (dlg == 17) {
            onlineUserDialogs.setImageResource(R.drawable.ic_online_contant_fill);
            onlineUserDialogs.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            Analytics.getInstance(getParentActivity()).sendEvent("tabslayout", "click", Analytics.ONLINE_MESSAGES_CATEGORY);
        } else if (dlg == 16) {
            blockUserDialogs.setImageResource(R.drawable.blockusers);
            blockUserDialogs.setColorFilter(Color.parseColor(ApplicationLoader.applicationTheme), PorterDuff.Mode.SRC_IN);
            Analytics.getInstance(getParentActivity()).sendEvent("tabslayout", "click", Analytics.BLOCKED_MESSAGES_CATEGORY);
        }
        if (item != null) {
            AnimationSet animSet = new AnimationSet(true);
            animSet.setInterpolator(new DecelerateInterpolator());
            animSet.setFillAfter(true);
            animSet.setFillEnabled(true);
            final RotateAnimation animRotate = new RotateAnimation(0.0f, -360.0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            animRotate.setDuration(1500);
            animRotate.setFillAfter(true);
            animSet.addAnimation(animRotate);
            SharedPreferences shareRead = getParentActivity().getApplicationContext().getSharedPreferences("tabs", MODE_PRIVATE);
            ArrayList<View> itemView = new ArrayList<>();
            itemView.add(allDialogs);
            itemView.add(unreadDialogs);
            if (shareRead.getBoolean("fav", true)) itemView.add(favoriteContacts);
            if (shareRead.getBoolean("contacts", true)) itemView.add(userDialogs);
            if (shareRead.getBoolean("groups", true)) itemView.add(groupDialogs);
            if (shareRead.getBoolean("superGroups", true)) itemView.add(superGroupDialogs);
            if (shareRead.getBoolean("onlineContacts", true)) itemView.add(onlineUserDialogs);
            if (shareRead.getBoolean("channels", true)) itemView.add(channelDialogs);
            if (shareRead.getBoolean("bots", true)) itemView.add(botDialogs);

            itemView.add(blockUserDialogs);

            for (int i = 0; i < itemView.size(); i++) {
                if ((item) == itemView.get(i)) currentTab = i;
                (itemView.get(i)).setBackgroundResource(0);

            }

        }


        appContext = ctx;
        dialogsType = dlg;
        new dialogLoader().execute();
    }

    private class dialogLoader extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            dialogsAdapter = new DialogsAdapter(appContext, dialogsType);
            if (AndroidUtilities.isTablet() && openedDialogId != 0) {
                dialogsAdapter.setOpenedDialogId(openedDialogId);
            }
            dialog_t = dialogsType;
            return dialogsAdapter.getItemCount() != 0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (animator2 != null)
                animator2.cancel();
            hideFloatingButton(false, false);
            hideTabLayout(false);
            if (aBoolean) {
                listView.setAdapter(dialogsAdapter);
            } else {
                listView.setAdapter(dialogsAdapter);
                listView.setEmptyView(emptyView);
                Resources res = getParentActivity().getApplicationContext().getResources();
                String header = null, message = null;
                if (dialogsType == 11) {
                    header = res.getString(R.string.newemptyheader);
                    message = res.getString(R.string.newemptytext);
                } else if (dialogsType == 0 || dialogsType == 3 || dialogsType == 4 || dialogsType == 15) {
                    header = res.getString(R.string.contactemptyheader);
                    message = res.getString(R.string.contactemptytext);

                } else if (dialogsType == 2) {
                    header = res.getString(R.string.groupemptytext);
                    message = res.getString(R.string.groupemptytext);

                } else if (dialogsType == 10) {
                    header = res.getString(R.string.channelemptytext);
                    message = res.getString(R.string.groupemptytext);

                } else if (dialogsType == 14) {
                    header = res.getString(R.string.megagroupemptytext);
                    message = res.getString(R.string.groupemptytext);

                } else if (dialogsType == 16) {
                    header = res.getString(R.string.blockuseremptyheader);
                    message = res.getString(R.string.blockuseremptytext);
                } else if (dialogsType == 17) {
                    header = res.getString(R.string.onlineemptyheader);
                    message = res.getString(R.string.onlineemptytext);
                } else if (dialogsType == 18) {
                    header = res.getString(R.string.grouptsemptytext);
                    message = res.getString(R.string.groupemptytext);
                }

                if (header != null && message != null) {
                    emptyHeader.setText(header);
                    emptyMessage.setText(message);
                }
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setOnTouchListener(new OnSwipeTouchListener(getParentActivity()) {
                    public void onSwipeTop() {

                    }

                    public void onSwipeRight() {

                        doSwipe(true);
                    }

                    public void onSwipeLeft() {
                        doSwipe(false);
                    }

                    public void onSwipeBottom() {

                    }

                });


            }

        }
    }

    private void doSwipe(boolean back) {
        SharedPreferences shareRead = getParentActivity().getApplicationContext().getSharedPreferences("tabs", MODE_PRIVATE);
        if (currentTab == 0 && back) currentTab = canSwipe + 2;
        if (currentTab == canSwipe + 1 && !back) currentTab = -1;
        if (back) currentTab--;
        else currentTab++;
        ArrayList<Integer> itemTypes = new ArrayList<>();
        ArrayList<String> itemName = new ArrayList<>();
        itemTypes.add(0);
        itemTypes.add(11);
        if (shareRead.getBoolean("fav", true)) itemTypes.add(15);
        if (shareRead.getBoolean("contacts", true)) itemTypes.add(4);
        if (shareRead.getBoolean("groups", true)) itemTypes.add(18);
        if (shareRead.getBoolean("superGroups", true)) itemTypes.add(14);
        if (shareRead.getBoolean("onlineContacts", true)) itemTypes.add(17);
        if (shareRead.getBoolean("channels", true)) itemTypes.add(10);
        if (shareRead.getBoolean("bots", true)) itemTypes.add(3);

        itemTypes.add(16);
        filterDialogs(getParentActivity(), itemTypes.get(currentTab), null);
        itemName.add(this.itemNames[0]);
        itemName.add(this.itemNames[1]);
        if (shareRead.getBoolean("fav", true)) itemName.add(this.itemNames[2]);
        if (shareRead.getBoolean("contacts", true)) itemName.add(this.itemNames[3]);
        if (shareRead.getBoolean("groups", true)) itemName.add(this.itemNames[4]);
        if (shareRead.getBoolean("superGroups", true)) itemName.add(this.itemNames[5]);
        if (shareRead.getBoolean("channels", true)) itemName.add(this.itemNames[6]);
        if (shareRead.getBoolean("bots", true)) itemName.add(this.itemNames[7]);
        if (shareRead.getBoolean("onlineContacts", true)) itemName.add(this.itemNames[8]);
        itemName.add(this.itemNames[9]);
        View[] items = {allDialogs, unreadDialogs, favoriteContacts, userDialogs, groupDialogs, superGroupDialogs, onlineUserDialogs, channelDialogs, botDialogs, blockUserDialogs};

        ArrayList<View> itemView = new ArrayList<>();
        itemView.add(allDialogs);
        itemView.add(unreadDialogs);
        if (shareRead.getBoolean("fav", true)) itemView.add(favoriteContacts);
        if (shareRead.getBoolean("contacts", true)) itemView.add(userDialogs);
        if (shareRead.getBoolean("groups", true)) itemView.add(groupDialogs);
        if (shareRead.getBoolean("superGroups", true)) itemView.add(superGroupDialogs);
        if (shareRead.getBoolean("onlineContacts", true)) itemView.add(onlineUserDialogs);
        if (shareRead.getBoolean("channels", true)) itemView.add(channelDialogs);
        if (shareRead.getBoolean("bots", true)) itemView.add(botDialogs);

        itemView.add(blockUserDialogs);
        actionBar.setTitle(itemName.get(currentTab));
        for (int i = 0; i < itemView.size(); i++)
            if (i == currentTab) {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(1000);
                AnimationSet animation = new AnimationSet(false);
                animation.addAnimation(fadeIn);
                listView.startAnimation(animation);
                AnimationSet animSet = new AnimationSet(true);
                animSet.setInterpolator(new DecelerateInterpolator());
                animSet.setFillAfter(true);
                animSet.setFillEnabled(true);
                final RotateAnimation animRotate = new RotateAnimation(0.0f, -360.0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                animRotate.setDuration(1500);
                animRotate.setFillAfter(true);
                animSet.addAnimation(animRotate);
                ObjectAnimator animation2 = ObjectAnimator.ofFloat((itemView.get(i)), "rotationY", 0.0f, 360f);
                animation2.setDuration(1000);
            } else {
                (itemView.get(i)).setBackgroundResource(0);
            }

    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return false;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }

    private ArrayList<TLRPC.TL_dialog> deleteHiddenDialogs(final ArrayList<TLRPC.TL_dialog> h) { //Hossein
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


    public static void restartApplication(Context context, int delay) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Build.VERSION.SDK_INT >= 11 ? Intent.FLAG_ACTIVITY_CLEAR_TASK : Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        System.exit(2);
    }

    private void helpWizard(final int type) {
        if (type == -1) {
            helpDialog.setCancelable(false);
        } else helpDialog.setCancelable(true);
        hideHelp = true;
        ListView drawerList = LaunchActivity.listView;
        j = 0;
        int delay = 100;
        if (type >= 9) {
            if (type == 9) {
                LaunchActivity.drawerLayoutContainer.openDrawer(true);
            }
            j = type - 8 + 1;
            if (j >= 7) j++;
            drawerList.smoothScrollToPosition(j);
        }
        getParentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        new Handler().postDelayed(() -> {
            boolean helpDrawer = false;
            boolean hideDialog = false;
            int u = 0;
            if (type >= 9 && type < MAX_HELP) {
                if (type == 9) {
                    LaunchActivity.drawerLayoutContainer.openDrawer(true);
                }
                helpDrawer = true;
                u = type - 8 + 1;
                if (u >= 8) u++;
            } else if (type >= MAX_HELP) {
                LaunchActivity.drawerLayoutContainer.closeDrawer(true);
                hideDialog = true;
            }
            int[] x = new int[2];
            if (helpDrawer) {
                if (drawerList.getChildAt(u) == null) {
                    helpDialog.dismiss();
                    return;
                }
                drawerList.getChildAt(u).getLocationOnScreen(x);
            }
            Float[] xLoc = {allDialogs.getX() + allDialogs.getWidth() / 2, unreadDialogs.getX() + unreadDialogs.getWidth() / 2, favoriteContacts.getX() + favoriteContacts.getWidth() / 2, userDialogs.getX() + userDialogs.getWidth() / 2, groupDialogs.getX() + groupDialogs.getWidth() / 2, superGroupDialogs.getX() + superGroupDialogs.getWidth() / 2, onlineUserDialogs.getX() + onlineUserDialogs.getWidth() / 2, channelDialogs.getX() + channelDialogs.getWidth() / 2, botDialogs.getX() + botDialogs.getWidth() / 2};
            String[] helpTitles = ApplicationLoader.mContext.getResources().getStringArray(R.array.helpTitle);
            String[] helpMessages = ApplicationLoader.mContext.getResources().getStringArray(R.array.help);
            DisplayMetrics metrics = getParentActivity().getApplicationContext().getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            if (helpDrawer || j >= 9 && j != MAX_HELP) {
                CircleOverlayView.setCx(AndroidUtilities.dp(30));
                if (j <= drawerList.getLastVisiblePosition())
                    CircleOverlayView.setCy(x[1] + drawerList.getChildAt(u).getHeight() / 2);
            } else {
                if (type == -1) {
                    CircleOverlayView.setRect();

                } else {
                    CircleOverlayView.setCx(xLoc[type]);
                    CircleOverlayView.setCy(AndroidUtilities.dp(100));
                }
            }
            helpDialog.setContentView(R.layout.help_dialog);
            if (type == -1) {
                helpDialog.findViewById(R.id.before).setVisibility(View.INVISIBLE);
            }
            helpDialog.getWindow().setLayout(width, height);
            TextView help_title = (TextView) helpDialog.findViewById(R.id.help_title);
            TextView help_message = (TextView) helpDialog.findViewById(R.id.message);
            TextView close_help = (TextView) helpDialog.findViewById(R.id.closeHelp);
            if (helpDrawer) {
                RelativeLayout.LayoutParams newParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams newParam2 = new RelativeLayout.LayoutParams(AndroidUtilities.dp(250), ViewGroup.LayoutParams.MATCH_PARENT);
                help_title.setGravity(Gravity.RIGHT);
                help_message.setGravity(Gravity.RIGHT);
                newParam.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.CENTER_HORIZONTAL);
                newParam.setMargins(0, AndroidUtilities.dp(40), AndroidUtilities.dp(5), 0);
                newParam2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); //RelativeLayout.ALIGN_PARENT_TOP |
                newParam2.addRule(RelativeLayout.BELOW, help_title.getId());
                newParam2.setMargins(0, AndroidUtilities.dp(10), 0, 0);
                help_title.setLayoutParams(newParam);
                help_message.setLayoutParams(newParam2);

            }
            help_message.setLineSpacing(1.25f, 1.25f);
            if (type == -1) {
                help_title.setText(getParentActivity().getApplicationContext().getResources().getString(R.string.ConversationsTab));
                help_message.setText(getParentActivity().getApplicationContext().getResources().getString(R.string.CategoryOfChatKinds));
            } else {
                help_title.setText(helpTitles[type]);
                help_message.setText(helpMessages[type]);
            }
            help_title.setTypeface(AndroidUtilities.getTypeface("fonts/is.ttf"));
            help_message.setTypeface(AndroidUtilities.getTypeface("fonts/is.ttf"));
            close_help.setTypeface(AndroidUtilities.getTypeface("fonts/is.ttf"));
            helpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    hideHelp = false;
                    getParentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    tabsLayout.removeAllViews();
                    createTabs(getParentActivity());
                }
            });
            helpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    hideHelp = false;
                    getParentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    tabsLayout.removeAllViews();
                    createTabs(getParentActivity());
                }
            });
            helpDialog.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type < MAX_HELP) {
                        helpWizard(type + 1);
                    } else {
                        hideHelp = false;
                        getParentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        helpDialog.dismiss();
                        LaunchActivity.drawerLayoutContainer.closeDrawer(true);
                        tabsLayout.removeAllViews();
                        createTabs(getParentActivity());
                    }
                }
            });
            helpDialog.findViewById(R.id.before).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == MAX_HELP) LaunchActivity.drawerLayoutContainer.openDrawer(true);
                    if (type == 9) LaunchActivity.drawerLayoutContainer.closeDrawer(true);
                    if (type > -1) {
                        helpWizard(type - 1);
                    } else {
                        hideHelp = false;
                        getParentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        helpDialog.dismiss();
                        tabsLayout.removeAllViews();
                        createTabs(getParentActivity());
                    }
                }
            });
            close_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideHelp = false;
                    getParentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    helpDialog.dismiss();
                    tabsLayout.removeAllViews();
                    createTabs(getParentActivity());
                }
            });
            helpDialog.findViewById(R.id.dialog_root).setOnTouchListener(new OnSwipeTouchListener(getParentActivity()) {
                public void onSwipeTop() {
                }

                public void onSwipeRight() {
                    if (type == 9) LaunchActivity.drawerLayoutContainer.closeDrawer(true);
                    if (type > -1) {
                        helpWizard(type - 1);
                    } else {
                        if (type != -1) {
                            hideHelp = false;
                            tabsLayout.removeAllViews();
                            createTabs(getParentActivity());
                            helpDialog.dismiss();
                            getParentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        }
                    }
                }

                public void onSwipeLeft() {
                    if (type < MAX_HELP) {
                        helpWizard(type + 1);
                    } else {
                        hideHelp = false;
                        tabsLayout.removeAllViews();
                        createTabs(getParentActivity());
                        helpDialog.dismiss();
                        getParentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                }

                public void onSwipeBottom() {

                }
            });
            if (hideDialog) {
                helpDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                helpDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                if (type == 19) {
                    CircleOverlayView.setCx(0);
                    CircleOverlayView.setCy(0);
                    ImageView imageView = new ImageView(getParentActivity());
                    imageView.setImageResource(R.drawable.ic_long_time_press);
                    showPressGuid(130, 130, Gravity.TOP);
                    helpDialog.getWindow().setLayout(width - AndroidUtilities.dp(100), height);
                    helpDialog.getWindow().setGravity(Gravity.LEFT);

                } else if (type == 20) {
                    CircleOverlayView.setCx(0);
                    CircleOverlayView.setCy(0);
                    helpDialog.getWindow().setLayout(width - AndroidUtilities.dp(100), height);
                    helpDialog.getWindow().setGravity(Gravity.LEFT);
                } else if (type == 21) {
                    SharedPreferences.Editor share1 = getParentActivity().getSharedPreferences("helpwizard", MODE_PRIVATE).edit();
                    share1.putBoolean("showhiddendialogs", true).commit();
                    ImageView imageView = new ImageView(getParentActivity());
                    imageView.setImageResource(R.drawable.ic_long_time_press);
                    CircleOverlayView.setCx(0);
                    CircleOverlayView.setCy(0);
                    helpDialog.getWindow().setLayout(width - AndroidUtilities.dp(100), height - AndroidUtilities.dp(80));
                    helpDialog.getWindow().setGravity(Gravity.LEFT | Gravity.BOTTOM);
                    showPressGuid(40, 40, Gravity.TOP);
                } else if (type == 22) {
                    CircleOverlayView.setCx(0);
                    CircleOverlayView.setCy(0);
                    ImageView imageView = new ImageView(getParentActivity());
                    imageView.setImageResource(R.drawable.ic_long_time_press);
                    helpDialog.getWindow().setLayout(width - AndroidUtilities.dp(100), height);
                    helpDialog.getWindow().setGravity(Gravity.LEFT);
                } else if (type == 23) {
                    CircleOverlayView.setCx(0);
                    CircleOverlayView.setCy(0);
                    helpDialog.getWindow().setLayout(width - AndroidUtilities.dp(100), height - AndroidUtilities.dp(80));
                    helpDialog.getWindow().setGravity(Gravity.LEFT | Gravity.BOTTOM);
                    showPressGuid(40, 40, Gravity.TOP);
                }
            }
            helpDialog.show();
            if (hideDialog)
                helpDialog.findViewById(R.id.next).setVisibility(View.GONE);
            if (type == 12) {
                help_title.setPadding(AndroidUtilities.dp(30), 0, 0, 0);
                help_message.setPadding(AndroidUtilities.dp(30), 0, 0, 0);
            }
            if (!helpDrawer) {
                if (type >= 4) helpDialog.findViewById(R.id.arrow).setRotationY(180);
                helpDialog.findViewById(R.id.arrow).setVisibility(View.VISIBLE);
                if (type != -1 && !hideDialog) {
                    helpDialog.findViewById(R.id.arrow).setX(type >= 4 ? xLoc[type] - AndroidUtilities.dp(80) : xLoc[type] - AndroidUtilities.dp(10));
                    helpDialog.findViewById(R.id.arrow).setY(AndroidUtilities.dp(135));
                } else if (hideDialog) {
                    helpDialog.findViewById(R.id.arrow).setVisibility(View.GONE);
                } else {
                    helpDialog.findViewById(R.id.arrow).setX(xLoc[0] + AndroidUtilities.dp(100));
                    helpDialog.findViewById(R.id.arrow).setY(AndroidUtilities.dp(135));
                }
            } else {
                helpDialog.findViewById(R.id.arrow).setVisibility(View.GONE);
                helpDialog.findViewById(R.id.arrow).setX(AndroidUtilities.dp(30));
                helpDialog.findViewById(R.id.arrow).setY(x[1] + drawerList.getChildAt(u).getHeight() / 2);
            }
        }, delay);
    }

    private void showPressGuid(int x, int y, int Gravity) {
        Bitmap bitmap = BitmapFactory.decodeResource(getParentActivity().getResources(), R.drawable.ic_long_time_press);
        ImageView imageView = new ImageView(getParentActivity());
        imageView.setImageResource(R.drawable.ic_long_time_press);
        hidePopUp = new Dialog(getParentActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        hidePopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        hidePopUp.getWindow().setLayout(bitmap.getWidth(), bitmap.getHeight());
        hidePopUp.setContentView(imageView);
        WindowManager.LayoutParams wmlp = hidePopUp.getWindow().getAttributes();
        wmlp.gravity = Gravity;
        wmlp.x = AndroidUtilities.dp(x);
        wmlp.y = AndroidUtilities.dp(y);
        hidePopUp.show();
        helpHandler.postDelayed(() -> hidePopUp.dismiss(), 2000);
    }

    private boolean getFirst() {
        SharedPreferences reader = ApplicationLoader.mContext.getSharedPreferences("def1", MODE_PRIVATE);
        return reader.getBoolean("showwizard", false);
    }

    private void setFirst() {
        SharedPreferences.Editor edt = ApplicationLoader.mContext.getSharedPreferences("def1", MODE_PRIVATE).edit();
        edt.putBoolean("showwizard", true);
        edt.commit();
    }

    private void advanceClearCache(long dialogId) {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        photo = true;
        video = true;
        audio = true;
        doc = true;
        music = true;
        for (int a = 0; a < 5; a++) {
            final int k = a;
            String name = null;
            if (a == 0)
                name = LocaleController.getString("LocalPhotoCache", R.string.LocalPhotoCache);
            else if (a == 1)
                name = LocaleController.getString("LocalVideoCache", R.string.LocalVideoCache);
            else if (a == 2)
                name = LocaleController.getString("LocalAudioCache", R.string.LocalAudioCache);
            else if (a == 3)
                name = LocaleController.getString("LocalDocumentCache", R.string.LocalDocumentCache);
            else if (a == 4)
                name = LocaleController.getString("LocalMusicCache", R.string.LocalMusicCache);
            CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity());
            checkBoxCell.setTag(a);
            checkBoxCell.setBackgroundResource(R.drawable.list_selector);
            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
            checkBoxCell.setText(name, "", true, true);
            checkBoxCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBoxCell cell = (CheckBoxCell) v;
                    cell.setChecked(!cell.isChecked(), true);
                    if (k == 0) photo = cell.isChecked();
                    else if (k == 1) video = cell.isChecked();
                    else if (k == 2) audio = cell.isChecked();
                    else if (k == 3) doc = cell.isChecked();
                    else if (k == 4) music = cell.isChecked();
                }
            });

        }
        BottomSheet.BottomSheetCell cell = new BottomSheet.BottomSheetCell(getParentActivity(), 1);
        cell.setBackgroundResource(R.drawable.list_selector);
        cell.setTextAndIcon(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache).toUpperCase(), 0);
        cell.setTextColor(0xffcd5a5a);
        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.create().dismiss();
                MessagesStorage.getInstance().advancedClearCache(dialogId, photo, video, audio, doc, music);
            }
        });
        linearLayout.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    private void lockDialog(long dialogId) {
        if (UserConfig.passcodeHash.length() > 0 || getParentActivity().getSharedPreferences("applock", Context.MODE_PRIVATE).getString("pass", "").length() > 0) {
            showLoginDialog(dialogId, null, false, true);
            return;
        }
        TextView textView = new TextView(getParentActivity());
        textView.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        textView.setHintTextColor(Color.GRAY);
        textView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        textView.setTextColor(Color.BLACK);
        textView.setText(currentDialogPassword == null ? LocaleController.getString("LockChatDescription", R.string.LockChatDescription) : LocaleController.getString("LockChatRepeatPassword", R.string.LockChatRepeatPassword));
        EditText editText = new EditText(getParentActivity());
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        editText.setHint(currentDialogPassword == null ? LocaleController.getString("LockChatPasswordHint", R.string.LockChatPasswordHint) : LocaleController.getString("LockChatRepeatPasswordHint", R.string.LockChatRepeatPasswordHint));
        editText.setHintTextColor(Color.GRAY);
        editText.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        editText.setTextColor(Color.BLACK);
        editText.setPadding(AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5));
        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5));
        linearLayout.addView(textView, params);
        linearLayout.addView(editText);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getParentActivity());
        dialog.setView(linearLayout);
        dialog.setPositiveButton(LocaleController.getString("okdialog", R.string.okdialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (currentDialogPassword == null) {
                    currentDialogPassword = editText.getText().toString();
                    lockDialog(dialogId);
                } else {
                    if (currentDialogPassword.equals(editText.getText().toString())) {
                        if (!getParentActivity().getSharedPreferences("lockchat", Context.MODE_PRIVATE).getBoolean("dontshow", false))
                            showLockChatHelp();
                        SharedPreferences.Editor share1 = getParentActivity().getSharedPreferences("applock", Context.MODE_PRIVATE).edit();
                        share1.putString("pass", currentDialogPassword).commit();
                        Database.getInstance().lockDialog(dialogId, getParentActivity(), currentDialogPassword);
                        currentDialogPassword = null;
                        dialogsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getParentActivity(), LocaleController.getString("LockChatPasswordsNotMached", R.string.LockChatPasswordsNotMached), Toast.LENGTH_LONG).show();
                        currentDialogPassword = null;
                    }
                }
            }
        });
        dialog.setNegativeButton(LocaleController.getString("nodialog", R.string.nodialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
        editText.requestFocus();
        new Handler().postDelayed(() -> {
            InputMethodManager keyboard = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(editText, 0);
        }, 700);
    }

    private void showLockChatHelp() {
        SharedPreferences.Editor sharedPreferences = getParentActivity().getSharedPreferences("lockchat", Context.MODE_PRIVATE).edit();
        TextView textView = new TextView(getParentActivity());
        textView.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        textView.setHintTextColor(Color.GRAY);
        textView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        textView.setTextColor(Color.BLACK);
        textView.setText(LocaleController.getString("LockChatHelp", R.string.LockChatHelp));
        CheckBox checkBox = new CheckBox(getParentActivity());
        checkBox.setPadding(AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5));
        checkBox.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        checkBox.setTextColor(Color.GRAY);
        checkBox.setText(LocaleController.getString("DontShowAgain", R.string.DontShowAgain));
        checkBox.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        checkBox.setPadding(AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5));
        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5));
        linearLayout.addView(textView, params);
        linearLayout.addView(checkBox);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getParentActivity());
        dialog.setView(linearLayout);
        dialog.setPositiveButton(LocaleController.getString("okdialog", R.string.okdialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (checkBox.isChecked()) {
                    sharedPreferences.putBoolean("dontshow", true).commit();
                }

            }
        });
        dialog.setNegativeButton(LocaleController.getString("nodialog", R.string.nodialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private void showLoginDialog(long dialogId, Bundle bundle, boolean unlock, boolean usingLast) {
        if (!unlock && !Database.getInstance().checkLock(dialogId, getParentActivity()) && !usingLast) {
            presentFragment(new ChatActivity(bundle));
            return;
        }
        EditText editText = new EditText(getParentActivity());
        editText.setTextColor(Color.BLACK);
        editText.setHintTextColor(Color.GRAY);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        editText.setPadding(AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5), AndroidUtilities.dp(5));
        editText.setTypeface(AndroidUtilities.getTypeface(ApplicationLoader.applicationTypeFace));
        editText.setHint(LocaleController.getString("EnterPasswordForLockChat", R.string.EnterPasswordForLockChat));

        AlertDialog.Builder dialog = new AlertDialog.Builder(getParentActivity());
        dialog.setView(editText);
        dialog.setPositiveButton(LocaleController.getString("okdialog", R.string.okdialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (usingLast) {
                    SharedPreferences share1 = getParentActivity().getSharedPreferences("applock", Context.MODE_PRIVATE);

                    if (UserConfig.checkPasscode(editText.getText().toString()) || share1.getString("pass", "").equals(editText.getText().toString())) {
                        Database.getInstance().lockDialog(dialogId, getParentActivity(), editText.getText().toString());
                        dialogsAdapter.notifyDataSetChanged();
                    } else {

                        Toast.makeText(getParentActivity(), LocaleController.getString("LockChatInvalidPassword", R.string.LockChatInvalidPassword), Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                SharedPreferences share1 = getParentActivity().getSharedPreferences("applock", Context.MODE_PRIVATE);

                if (UserConfig.checkPasscode(editText.getText().toString()) || share1.getString("pass", "").equals(editText.getText().toString())) {
                    if (unlock) {
                        Database.getInstance().unlockDialog(dialogId, getParentActivity(), editText.getText().toString());
                        dialogsAdapter.notifyDataSetChanged();
                    } else
                        presentFragment(new ChatActivity(bundle));

                } else
                    Toast.makeText(getParentActivity(), LocaleController.getString("LockChatInvalidPassword", R.string.LockChatInvalidPassword), Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton(LocaleController.getString("nodialog", R.string.nodialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
        editText.requestFocus();
        new Handler().postDelayed(() -> {
            InputMethodManager keyboard = (InputMethodManager) getParentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(editText, 0);
        }, 700);


    }

    private boolean isRecyclerScrollable() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
        RecyclerView.Adapter adapter = listView.getAdapter();
        if (layoutManager == null || adapter == null) return false;

        return layoutManager.findLastCompletelyVisibleItemPosition() < adapter.getItemCount() - 1;
    }

    //--------------
    private void hideTabLayout(boolean hide) {
        Log.d("qweeqwqeqweqw", "count:" + listView.getAdapter().getItemCount() + "-vis:" + layoutManager.findLastVisibleItemPosition());

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(linearLayout, "translationY", hide ? -tabsContainer.getHeight() - AndroidUtilities.dp(5) : 0).setDuration(200);
        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                doProcessAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                doProcessAnimation = false;

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator2.start();
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hide ? linearLayout.getHeight() + tabsContainer.getHeight() : ViewGroup.LayoutParams.MATCH_PARENT));


    }
}

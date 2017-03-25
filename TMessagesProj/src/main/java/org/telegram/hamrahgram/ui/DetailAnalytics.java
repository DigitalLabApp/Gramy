package org.telegram.hamrahgram.ui;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.telegram.hamrahgram.database.Database;
import org.telegram.hamrahgram.model.Analytics;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import static org.telegram.hamrahgram.util.DateUtil.getTime;

public class DetailAnalytics extends BaseFragment implements OnDateSelectedListener, OnMonthChangedListener {
    private ArrayList<Analytics> chats;
    private View view;
    private TextView total;
    private TextView totalValue;
    private TextView recentChats;
    private TextView recentGroups;
    private TextView recentChannels;
    private TextView chatsduration;
    private TextView groupsduration;
    private TextView channelsduration;
    private ListView listView;
    private Year currentYear;

    private class Year {
        private int year;
        private int month;
        private int day;

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public void setDay(int day) {
            this.day = day;
        }
    }

    public DetailAnalytics(Bundle bundle) {
        currentYear = new Year();
        currentYear.setYear(bundle.getInt("year"));
        currentYear.setMonth(bundle.getInt("month"));
        currentYear.setDay(bundle.getInt("day"));
    }

    @Override
    public View createView(final Context context) {
        this.view = getParentActivity().getLayoutInflater().inflate(R.layout.detail_analytics, null);
        recentChats = (TextView) this.view.findViewById(R.id.chats);
        recentGroups = (TextView) this.view.findViewById(R.id.groups);
        recentChannels = (TextView) this.view.findViewById(R.id.channels);
        chatsduration = (TextView) this.view.findViewById(R.id.chatdurations);
        groupsduration = (TextView) this.view.findViewById(R.id.groupsduration);
        channelsduration = (TextView) this.view.findViewById(R.id.channelsduration);
        total = (TextView) this.view.findViewById(R.id.total);
        totalValue = (TextView) this.view.findViewById(R.id.totalvalue);
        listView = (ListView) this.view.findViewById(R.id.listView);
        total.setTextColor(0xff3e90cf);
        chats = Database.getInstance().getChatAnalyticsByDate(getParentActivity(), currentYear.getYear(), currentYear.getMonth(), currentYear.getDay());
        long chatDuration = 0, groupDuration = 0, channelDuration = 0;
        for (Analytics analytics : chats) {
            if (analytics.getChatType() == 0) {
                chatDuration += analytics.getDuration();
            } else if (analytics.getChatType() == -1 || analytics.getChatType() == 1) {
                groupDuration += analytics.getDuration();

            } else if (analytics.getChatType() == 2) {
                channelDuration += analytics.getDuration();

            }
        }
        Typeface typeFace = Typeface.createFromAsset(getParentActivity().getResources().getAssets(), "fonts/analyticsfont.ttf");
        recentChats.setTypeface(typeFace);
        recentChannels.setTypeface(typeFace);
        recentGroups.setTypeface(typeFace);
        long total = 0;
        total += chatDuration + groupDuration + channelDuration;
        totalValue.setText(getTime(Database.getInstance().getAppAnalyticsByDate(getParentActivity(), currentYear.getYear(), currentYear.getMonth(), currentYear.getDay()).getDuration()));
        chatsduration.setText(getTime(chatDuration));
        groupsduration.setText(getTime(groupDuration));
        channelsduration.setText(getTime(channelDuration));
        if (chatDuration > 0)
            chatDuration = (chatDuration * 100) / total;
        if (groupDuration > 0)
            groupDuration = (groupDuration * 100) / total;
        if (channelDuration > 0)
            channelDuration = (channelDuration * 100) / total;
        recentChats.setText(String.valueOf(chatDuration));
        recentGroups.setText(String.valueOf(groupDuration));
        recentChannels.setText(String.valueOf(channelDuration));
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAddToContainer(false);
        actionBar.setBackgroundColor(Color.parseColor(ApplicationLoader.applicationTheme));
        actionBar.setTitle(ApplicationLoader.applicationContext.getResources().getString(R.string.DelayAnalytics));
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

        fragmentView = new FrameLayout(context) {
            @Override
            protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {

                return super.drawChild(canvas, child, drawingTime);

            }
        };
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.addView(actionBar);
        frameLayout.addView(this.view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        needLayout();
        long chatTotals = 0;
        for (int i = 0; i < chats.size(); i++) {
            chatTotals += chats.get(i).getDuration();
            Analytics obj = chats.get(i);
            if (obj.getChatType() == 0) {
                TLRPC.User user = MessagesController.getInstance().getUser((int) obj.getChatId());
                if (user != null)
                    chats.get(i).setChatTitle(user.first_name);
                else
                    chats.get(i).setChatTitle(LocaleController.getString("UnknowChatTitleInReport", R.string.UnknowChatTitleInReport));
            } else if (obj.getChatType() == 1 || obj.getChatType() == -1) {

                TLRPC.Chat chat = MessagesController.getInstance().getChat((int) obj.getChatId() > 0 ? (int) obj.getChatId() : (int) -obj.getChatId());
                if (chats != null)

                    chats.get(i).setChatTitle(chat.title);
                else
                    chats.get(i).setChatTitle(LocaleController.getString("UnknowChatTitleInReport", R.string.UnknowChatTitleInReport));


            } else if (obj.getChatType() == 2) {
                TLRPC.Chat chat = MessagesController.getInstance().getChat((int) obj.getChatId() > 0 ? (int) obj.getChatId() : (int) -obj.getChatId());
                if (chats != null)
                    chats.get(i).setChatTitle(chat.title);
                else
                    chats.get(i).setChatTitle(LocaleController.getString("UnknowChatTitleInReport", R.string.UnknowChatTitleInReport));
            }


        }
        getSortedData();
        for (int i = 0; i < chats.size(); i++)
            chats.get(i).setPerCent((int) ((chats.get(i).getDuration() * 100) / chatTotals));


        Collections.sort(chats, new Comparator<Analytics>() {
            @Override
            public int compare(Analytics analytics, Analytics t1) {
                return Integer.valueOf(t1.getPerCent()).compareTo(analytics.getPerCent());
            }
        });

        listView.setAdapter(new ListAdapter());
        return fragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
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
        if (this.view != null) {
            layoutParams = (FrameLayout.LayoutParams) this.view.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.view.setLayoutParams(layoutParams);
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

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Analytics obj = Database.getInstance().getAppAnalyticsByDate(getParentActivity(), date.getYear(), date.getMonth(), date.getDay());
        long duration = obj.getDuration();
        String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration), TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        Toast.makeText(getParentActivity(), time, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }

    private void getSortedData() {
        ArrayList<Long> data = new ArrayList<>();
        ArrayList<Analytics> result = new ArrayList<>();
        for (Analytics analytics : chats) {
            if (!data.contains(analytics.getChatId())) {
                analytics.setDuration(getTotalDuration(analytics.getChatId()));
                result.add(analytics);
                data.add(analytics.getChatId());

            }
        }
        chats.clear();
        chats.addAll(result);
    }

    private long getTotalDuration(long chatId) {
        long result = 0;
        for (Analytics analytics : chats)
            if (analytics.getChatId() == chatId)
                result += analytics.getDuration();
        return result;
    }

    private class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {


            return chats.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Analytics obj = chats.get(i);
            return createView(obj.getChatTitle(), getTime(obj.getDuration()), obj.getPerCent(), obj.getPhoto());
        }

        private View createView(String name, String time, int percent, String path) {
            View view = getParentActivity().getLayoutInflater().inflate(R.layout.detail_analytics_list_item, null);
            ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
            ProgressBar prog = (ProgressBar) view.findViewById(R.id.progress);
            prog.setIndeterminate(false);
            Bitmap bitmap = null;
            if (path.length() > 0)
                bitmap = BitmapFactory.decodeFile(path);
            else
                bitmap = BitmapFactory.decodeResource(getParentActivity().getResources(), R.drawable.book_user);
            avatar.setImageBitmap(bitmap);
            prog.setProgress(percent);
            ((TextView) view.findViewById(R.id.name)).setText(name);
            ((TextView) view.findViewById(R.id.duration)).setText(time);
            return view;
        }


    }
}

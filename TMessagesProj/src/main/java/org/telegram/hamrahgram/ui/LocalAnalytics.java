package org.telegram.hamrahgram.ui;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.telegram.hamrahgram.BuyPremiumActivity;
import org.telegram.hamrahgram.adapter.WeekAdapter;
import org.telegram.hamrahgram.database.Database;
import org.telegram.hamrahgram.model.Analytics;
import org.telegram.hamrahgram.util.DateUtil;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static org.telegram.hamrahgram.util.DateUtil.getTime;

public class LocalAnalytics extends BaseFragment implements OnDateSelectedListener, OnMonthChangedListener {
    private AdapterLoader adapterLoader;
    private ArrayList<Analytics> chats;
    private ArrayList<Analytics> yestDetails;
    private View view;
    private TextView todayView;
    private TextView allAnalytics;
    private Timer timer;
    private TextView yesterday;
    private TextView today;
    private TextView recentChats;
    private TextView recentGroups;
    private TextView recentChannels;
    private TextView chatsduration;
    private TextView groupsduration;
    private TextView channelsduration;
    private TextView weeklyChart;
    private TextView emptyView;
    private TextView shamsiView;
    private ViewPager viewPager;
    private ImageView leftArrow;
    private ImageView rightArrow;
    private MaterialCalendarView calendarView;
    private long recentDuration;
    private ArrayList<Analytics> overAppAnalytics = null;
    private ArrayList<Long[]> weeks = new ArrayList<>();
    private ImageView shadow2;
    private ImageView expandableIndicator;
    private ListView yesterdayDetails;
    private boolean expandableOpen = false;
    private int rowSize = 0;
    private DatePickerDialog datePickerDialog;
    private SharedPreferences share1;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        timer.cancel();
    }

    @Override
    public View createView(final Context context) {
        share1 = getParentActivity().getSharedPreferences("payment", Context.MODE_PRIVATE);
        PersianCalendar persianCalendar = new PersianCalendar();
        datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        if (!checkPayment()) {
                            getParentActivity().startActivity(new Intent(getParentActivity(), BuyPremiumActivity.class));
                            return;
                        }
                        org.telegram.hamrahgram.util.Analytics.getInstance(getParentActivity()).sendEvent("PersianCalendar", "Click", org.telegram.hamrahgram.util.Analytics.PICK_FROM_CALENDAR);
                        persianCalendar.setPersianDate(year, monthOfYear, dayOfMonth);
                        Bundle bundle = new Bundle();
                        bundle.putInt("year", persianCalendar.get(persianCalendar.YEAR));
                        bundle.putInt("month", persianCalendar.get(persianCalendar.MONTH));
                        bundle.putInt("day", persianCalendar.get(persianCalendar.DAY_OF_MONTH));
                        presentFragment(new DetailAnalytics(bundle));
                    }
                },
                persianCalendar.getPersianYear(),
                persianCalendar.getPersianMonth(),
                persianCalendar.getPersianDay()
        );


        swipeBackEnabled = false;
        this.view = getParentActivity().getLayoutInflater().inflate(R.layout.delay_analytics, null);
        emptyView = (TextView) this.view.findViewById(R.id.emptyView);
        recentChats = (TextView) this.view.findViewById(R.id.chats);
        recentGroups = (TextView) this.view.findViewById(R.id.groups);
        recentChannels = (TextView) this.view.findViewById(R.id.channels);
        chatsduration = (TextView) this.view.findViewById(R.id.chatdurations);
        groupsduration = (TextView) this.view.findViewById(R.id.groupsduration);
        channelsduration = (TextView) this.view.findViewById(R.id.channelsduration);
        weeklyChart = (TextView) this.view.findViewById(R.id.weeklychart);
        shamsiView = (TextView) this.view.findViewById(R.id.shamsiView);
        viewPager = (ViewPager) this.view.findViewById(R.id.viewPager);
        leftArrow = (ImageView) this.view.findViewById(R.id.leftArrow);
        rightArrow = (ImageView) this.view.findViewById(R.id.rightArrow);
        shadow2 = (ImageView) this.view.findViewById(R.id.shadow2);
        expandableIndicator = (ImageView) this.view.findViewById(R.id.expandable_indicator);
        yesterdayDetails = (ListView) this.view.findViewById(R.id.yesterdayDetails);
        Typeface typeFace = Typeface.createFromAsset(getParentActivity().getResources().getAssets(), "fonts/analyticsfont.ttf");
        recentChats.setTypeface(typeFace);
        recentGroups.setTypeface(typeFace);
        recentChannels.setTypeface(typeFace);
        Calendar calendar = Calendar.getInstance();
        overAppAnalytics = Database.getInstance().getAppAnalytics(getParentActivity());
        adapterLoader = new AdapterLoader();
        Analytics firstObj = null;
        Analytics lastObj = null;
        getSortedOverAppData();
        if (overAppAnalytics.size() > 3) {
            firstObj = overAppAnalytics.get(0);
            lastObj = overAppAnalytics.get(overAppAnalytics.size() - 1);
        }
        if (firstObj != null && lastObj != null && overAppAnalytics.size() > 3 && lastObj.getYear() <= firstObj.getYear() && lastObj.getMonth() <= firstObj.getMonth() && lastObj.getDay() <= firstObj.getDay()) {
            emptyView.setText(LocaleController.getString("ChartLoadingError", R.string.ChartLoadingError));
            emptyView.setVisibility(View.VISIBLE);
        } else {
            if (overAppAnalytics.size() > 3) {
                new Handler().postDelayed(() -> adapterLoader.execute(), 700);

            } else
                emptyView.setVisibility(View.VISIBLE);
        }

        chats = Database.getInstance().getChatAnalyticsByDate(getParentActivity(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) - 1);
        yestDetails = Database.getInstance().getChatAnalyticsByDate(getParentActivity(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) - 1);
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
        long total = 0;
        total += chatDuration + groupDuration + channelDuration;
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
        yesterday = (TextView) this.view.findViewById(R.id.yesterday);

        expandableIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableOpen = !expandableOpen;
                ((ImageView) view).setImageResource(expandableOpen ? R.drawable.arrow_up : R.drawable.arrow_down);
                if (!expandableOpen) {
                    Log.d("qeweqqeeqwe", "1");
                    ValueAnimator anim = ValueAnimator.ofInt(rowSize * yestDetails.size(), 0);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            yesterdayDetails.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, val));

                        }
                    });
                    anim.setDuration(500);
                    anim.start();

                    yesterdayDetails.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    return;
                }

                yesterdayDetails.setVisibility(View.VISIBLE);
                ValueAnimator anim = ValueAnimator.ofInt(0, rowSize * yestDetails.size());
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        yesterdayDetails.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, val));

                    }
                });
                anim.setDuration(500);
                anim.start();

            }
        });
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
        shamsiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show(getParentActivity().getFragmentManager(), "DatePicker");
            }
        });
        today = (TextView) this.view.findViewById(R.id.today);
        allAnalytics = (TextView) this.view.findViewById(R.id.allanalytics);
        yesterday.setTextColor(0xff3e90cf);
        today.setTextColor(0xff3e90cf);
        allAnalytics.setTextColor(0xff3e90cf);
        weeklyChart.setTextColor(0xff3e90cf);
        calendarView = (MaterialCalendarView) this.view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);
        todayView = (TextView) this.view.findViewById(R.id.today_timer);

        long duration = System.currentTimeMillis() - ((LaunchActivity) getParentActivity()).getStartMillis();
        duration += recentDuration;
        String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration), TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        todayView.setText(time);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long duration = System.currentTimeMillis() - ((LaunchActivity) getParentActivity()).getStartMillis();
                duration += recentDuration;
                String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration), TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                getParentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        todayView.setText(time);
                    }
                });
            }
        }, 1000, 1000);

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
        yesterdayDetails.setVisibility(View.INVISIBLE);
        getYesterdayDetail();
        yesterdayDetails.setAdapter(new ListAdapter());
        yesterdayDetails.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT > 16)
                    yesterdayDetails.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                rowSize = yesterdayDetails.getHeight();
                yesterdayDetails.setVisibility(View.GONE);

            }
        });

        return fragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        Calendar todayCalendar = Calendar.getInstance();
        recentDuration = Database.getInstance().getAppAnalyticsByDate(getParentActivity(), todayCalendar.get(Calendar.YEAR), todayCalendar.get(Calendar.MONTH), todayCalendar.get(Calendar.DAY_OF_MONTH)).getDuration();
        fixLayout();
        if (todayView != null) {

            long duration = System.currentTimeMillis() - ((LaunchActivity) getParentActivity()).getStartMillis();
            duration += recentDuration;
            String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration), TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
            todayView.setText(time);
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
        if (!checkPayment()) {
            getParentActivity().startActivity(new Intent(getParentActivity(), BuyPremiumActivity.class));
            return;
        }
        org.telegram.hamrahgram.util.Analytics.getInstance(getParentActivity()).sendEvent("PersianCalendar", "Click", org.telegram.hamrahgram.util.Analytics.PICK_FROM_MILADICALENDAR);
        Bundle bundle = new Bundle();
        bundle.putInt("year", date.getYear());
        bundle.putInt("month", date.getMonth());
        bundle.putInt("day", date.getDay());
        presentFragment(new DetailAnalytics(bundle));
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }


    private void getSortedOverAppData() {
        ArrayList<Analytics> sorted = new ArrayList<>();
        ArrayList<String> exist = new ArrayList<>();
        for (Analytics analytics : overAppAnalytics) {
            if (!exist.contains(analytics.getYear() + "_" + analytics.getMonth() + "_" + analytics.getDay())) {
                exist.add(analytics.getYear() + "_" + analytics.getMonth() + "_" + analytics.getDay());
                analytics.setDuration(getDurationByDay(analytics.getYear(), analytics.getMonth(), analytics.getDay()));
                sorted.add(analytics);
            }
        }
        overAppAnalytics.clear();
        overAppAnalytics.addAll(sorted);


    }

    private long getDurationByDay(int y, int m, int d) {
        long result = 0;
        for (Analytics analytics : overAppAnalytics) {
            if (analytics.getYear() == y && analytics.getMonth() == m && analytics.getDay() == d) {
                result += analytics.getDuration();
            }
        }
        return result;
    }

    private void getWeeks() {
        if (overAppAnalytics.size() < 3) return;
        Calendar c1 = Calendar.getInstance();
        Analytics obj = overAppAnalytics.get(0);
        c1.set(Calendar.YEAR, obj.getYear());
        c1.set(Calendar.MONTH, obj.getMonth() + 1);
        c1.set(Calendar.DAY_OF_MONTH, obj.getDay());
        Calendar c2 = Calendar.getInstance();
        Analytics obj2 = overAppAnalytics.get(overAppAnalytics.size() - 1);
        c2.set(Calendar.YEAR, obj2.getYear());
        c2.set(Calendar.MONTH, obj2.getMonth() + 1);
        c2.set(Calendar.DAY_OF_MONTH, obj2.getDay());
        String firstDate = DateUtil.getCalculatedDate(c1.getTime(), obj.getDayNumber() > 0 ? -(obj.getDayNumber()) : 0);
        String lastDate = DateUtil.getCalculatedDate(c2.getTime(), obj2.getDayNumber());
        Calendar calendar = Calendar.getInstance();
        Long[] data = null;
        List<Date> dateList = DateUtil.getDates(firstDate, lastDate);
        int max = dateList.size();
        for (int i = 0; i < dateList.size(); i += 7) {
            data = new Long[7];
            if (i < max) {
                calendar.setTime(dateList.get(i));
                data[0] = getDurationByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } else data[0] = 0l;
            if (i + 1 < max) {
                calendar.setTime(dateList.get(i + 1));
                data[1] = getDurationByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } else data[1] = 0l;
            if (i + 2 < max) {
                calendar.setTime(dateList.get(i + 2));
                data[2] = getDurationByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } else data[2] = 0l;
            if (i + 3 < max) {
                calendar.setTime(dateList.get(i + 3));
                data[3] = getDurationByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } else data[3] = 0l;
            if (i + 4 < max) {
                calendar.setTime(dateList.get(i + 4));
                data[4] = getDurationByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } else data[4] = 0l;
            if (i + 5 < max) {
                calendar.setTime(dateList.get(i + 5));
                data[5] = getDurationByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } else data[5] = 0l;
            if (i + 6 < max) {
                calendar.setTime(dateList.get(i + 6));
                data[6] = getDurationByDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } else data[6] = 0l;
            weeks.add(data);
        }
        Long[] lng = weeks.get(weeks.size() - 1);
        boolean notNull = false;
        for (int i = 0; i < lng.length; i++) {
            if (lng[i] > 0) notNull = true;


        }
        if (!notNull) weeks.remove(weeks.size() - 1);


    }

    private long getDurationByDate(int y, int m, int d) {
        for (Analytics obj : overAppAnalytics) {
            if (obj.getYear() == y && obj.getMonth() == m && obj.getDay() == d) {
                return obj.getDuration();
            }
        }
        return 0;


    }

    private class AdapterLoader extends AsyncTask<Void, Void, WeekAdapter> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(LocaleController.getString("LoadingChart", R.string.LoadingChart));
        }

        @Override
        protected WeekAdapter doInBackground(Void... voids) {

            getWeeks();
            return new WeekAdapter(weeks, getParentActivity());
        }

        @Override
        protected void onPostExecute(WeekAdapter weekAdapter) {
            super.onPostExecute(weekAdapter);
            if (weekAdapter != null) {
                emptyView.setVisibility(View.INVISIBLE);
                viewPager.setAdapter(weekAdapter);
                viewPager.setCurrentItem(weekAdapter.getCount() - 1);
                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                      if (viewPager.getCurrentItem() != weekAdapter.getCount() - 1 && !checkPayment()) {
                            getParentActivity().startActivity(new Intent(getParentActivity(), BuyPremiumActivity.class));
                            viewPager.setCurrentItem(weekAdapter.getCount() - 1);
                            return;
                        }
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }

        }
    }

    private class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return yestDetails.size();
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
            Analytics obj = yestDetails.get(i);
            return createView(obj.getChatTitle(), getTime(obj.getDuration()), obj.getPerCent(), obj.getPhoto());
        }

        private View createView(String name, String time, int percent, String path) {
            View view = getParentActivity().getLayoutInflater().inflate(R.layout.detail_analytics_list_item, null);
            ((RelativeLayout) view.findViewById(R.id.root)).setBackgroundColor(Color.WHITE);
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

    private void getYesterdayDetail() {
        long chatTotals = 0;
        for (int i = 0; i < yestDetails.size(); i++) {
            chatTotals += yestDetails.get(i).getDuration();
            Analytics obj = yestDetails.get(i);
            if (obj.getChatType() == 0) {
                TLRPC.User user = MessagesController.getInstance().getUser((int) obj.getChatId());
                if (user != null)
                    yestDetails.get(i).setChatTitle(user.first_name);
                else
                    yestDetails.get(i).setChatTitle(LocaleController.getString("UnknowChatTitleInReport", R.string.UnknowChatTitleInReport));
            } else if (obj.getChatType() == 1 || obj.getChatType() == -1) {

                TLRPC.Chat chat = MessagesController.getInstance().getChat((int) obj.getChatId() > 0 ? (int) obj.getChatId() : (int) -obj.getChatId());
                if (yestDetails != null)

                    yestDetails.get(i).setChatTitle(chat.title);
                else
                    yestDetails.get(i).setChatTitle(LocaleController.getString("UnknowChatTitleInReport", R.string.UnknowChatTitleInReport));


            } else if (obj.getChatType() == 2) {
                TLRPC.Chat chat = MessagesController.getInstance().getChat((int) obj.getChatId() > 0 ? (int) obj.getChatId() : (int) -obj.getChatId());
                if (yestDetails != null)
                    yestDetails.get(i).setChatTitle(chat.title);
                else
                    yestDetails.get(i).setChatTitle(LocaleController.getString("UnknowChatTitleInReport", R.string.UnknowChatTitleInReport));
            }


        }
        ArrayList<Long> data = new ArrayList<>();
        ArrayList<Analytics> result = new ArrayList<>();

        for (Analytics analytics : yestDetails) {
            if (!data.contains(analytics.getChatId())) {
                analytics.setDuration(getTotalDuration(analytics.getChatId()));
                result.add(analytics);
                data.add(analytics.getChatId());

            }

        }
        yestDetails.clear();
        yestDetails.addAll(result);
        for (int i = 0; i < yestDetails.size(); i++)
            yestDetails.get(i).setPerCent((int) ((yestDetails.get(i).getDuration() * 100) / chatTotals));


        Collections.sort(yestDetails, new Comparator<Analytics>() {
            @Override
            public int compare(Analytics analytics, Analytics t1) {
                return Integer.valueOf(t1.getPerCent()).compareTo(analytics.getPerCent());
            }
        });

    }

    private long getTotalDuration(long chatId) {
        long result = 0;
        for (Analytics analytics : yestDetails)
            if (analytics.getChatId() == chatId)
                result += analytics.getDuration();
        return result;


    }

    private boolean checkPayment() {

        return share1.getBoolean("analytics", false);


    }
}



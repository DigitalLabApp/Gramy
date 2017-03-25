package org.telegram.hamrahgram.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.hamrahgram.util.DateUtil;
import org.telegram.hamrahgram.util.chart.LineView;
import org.telegram.messenger.ApplicationLoader;

import java.util.ArrayList;


public class WeekAdapter extends PagerAdapter {
    private ArrayList<Long[]> weeks;
    private Activity activity;
    private final ArrayList<String> names = new ArrayList<>();

    @Override
    public int getCount() {
        return this.weeks.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public WeekAdapter(ArrayList<Long[]> weeks, Activity activity) {
        super();
        this.weeks = weeks;
        this.activity = activity;
        this.names.clear();
      /*  names.add("Sat");
        names.add("Sun");
        names.add("Mon");
        names.add("Tue");
        names.add("Wed");
        names.add("Thu");
        names.add("Fri");*/
        names.add("شنبه");
        names.add("یکشنبه");
        names.add("دوشنبه");
        names.add("سه شنبه");
        names.add("چهارشنبه");
        names.add("پنج شنبه");
        names.add("جمعه");
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = getWeeklyView(weeks.get(position));
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((LineView) object));
    }

    private View getWeeklyView(Long[] data) {
        LineView lineView = new LineView(activity);
        lineView.setBottomTextList(names);
        lineView.setColorArray(new int[]{Color.parseColor(ApplicationLoader.applicationTheme)});
        lineView.setDrawDotLine(true);
        lineView.setShowPopup(LineView.SHOW_POPUPS_NONE);
        ArrayList<Integer> dataList = new ArrayList<>();
        ArrayList<String> times = new ArrayList<>();
        long total = 0;
        for (int i = 0; i < data.length; i++) {
            total += data[i];
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null && data[i] > 0) {

                dataList.add((int) ((data[i] * 100) / total));

                times.add(DateUtil.getTimeAsString(data[i]));
            } else {
                dataList.add(0);
                times.add(DateUtil.getTime(0));


            }


        }
        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
        lineView.setDataList(dataLists, times);
        return lineView;
    }


}

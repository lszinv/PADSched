package com.lsz.padsched;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;

public class ScheduleTask extends AsyncTask<Fragment, Void, List<EventScheduleRow>> {
    private GregorianCalendar jpEventDate;
    private Fragment parentFrag;
    boolean addViews;

    private static final Map<String, Integer> dungeonMap;
    static {
        dungeonMap = new HashMap<String, Integer>();
        dungeonMap.put(Constants.EMERALD_DORA_NAME, Constants.EMERALD_DORA);
        dungeonMap.put(Constants.SAPPHIRE_DORA_NAME, Constants.SAPPHIRE_DORA);
        dungeonMap.put(Constants.RUBY_DORA_NAME, Constants.RUBY_DORA);
        dungeonMap.put(Constants.PENGU_DORA_NAME, Constants.PENGU_DORA);
        dungeonMap.put(Constants.META_DORA_NAME, Constants.META_DORA);
        dungeonMap.put(Constants.GOLD_DORA_NAME, Constants.GOLD_DORA);
        dungeonMap.put(Constants.DRAGON_PLANTS_NAME, Constants.DRAGON_PLANTS);
        dungeonMap.put(Constants.KING_CARNIVAL_NAME, Constants.KING_CARNIVAL);
        dungeonMap.put(Constants.SUPER_KING_NAME, Constants.SUPER_KING);
        dungeonMap.put(Constants.META_GOLD_NAME, Constants.META_GOLD);
        dungeonMap.put(Constants.EVO_RUSH_NAME, Constants.EVO_RUSH);
        dungeonMap.put(Constants.STAR_VAULT_NAME, Constants.STAR_VAULT);
        dungeonMap.put(Constants.EXTREME_METAL_NAME, Constants.EXTREME_METAL);
        dungeonMap.put(Constants.TAMADRA_NAME, Constants.TAMADRA);
        dungeonMap.put(Constants.COIN_NAME, Constants.COIN);
    }

    @Override
    protected List<EventScheduleRow> doInBackground(Fragment... arg) {
        Document doc;
        Elements tables;
        Elements rows;
        Elements columns;
        String className;
        String[] timeSplit;
        parentFrag = arg[0];
        Calendar calMilis = Calendar.getInstance(TimeZone.getTimeZone("Japan"));
        if (parentFrag instanceof ScheduleFragment) {
            addViews = true;
        } else {
            addViews = false;
        }
        List<EventScheduleRow> eventSchedules = new ArrayList<EventScheduleRow>();
        EventScheduleRow currentEvent = null;
        int group;
        try {
            doc = Jsoup.connect("http://paznet.net/").get();
            tables = doc.getElementsByClass("table");
            jpEventDate = ParseJpDate(doc.getElementsByTag("caption").get(0).toString().replaceAll("</?caption>", ""));
            rows = tables.get(0).getElementsByTag("tr");
            rows.remove(0); // removes the header row
            if (rows.size() == 0) {
                currentEvent = new EventScheduleRow();
                currentEvent.event = Constants.NO_EVENT;
                eventSchedules.add(currentEvent);
                return eventSchedules;
            }

            for (Element row : rows) {
                columns = row.getElementsByTag("td");
                group = Constants.GROUP_A;
                currentEvent = new EventScheduleRow();
                currentEvent.jpDate = jpEventDate;
                for (Element col : columns) {
                    if (group == Constants.GROUP_A) {
                        className = col.className();
                        if (dungeonMap.containsKey(className)) {
                            currentEvent.event = dungeonMap.get(className);
                        } else {
                            currentEvent.event = Constants.UNKNOWN_EVENT;
                        }
                    }
                    if (col.text().length() > 0) {
                        timeSplit = col.text().split(":");
                        calMilis.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSplit[0]));
                        if (timeSplit.length > 1) {
                            calMilis.set(Calendar.MINUTE, Integer.parseInt(timeSplit[1]));
                        } else {
                            calMilis.set(Calendar.MINUTE, 0);
                        }
                        calMilis.set(Calendar.SECOND, 0);
                        calMilis.set(Calendar.MILLISECOND, 0);

                        currentEvent.times[group] = calMilis.getTimeInMillis();

                    } else {
                        currentEvent.times[group] = -1;
                    }
                    group++;
                }
                eventSchedules.add(currentEvent);
            }

        } catch (IOException e) {
            currentEvent = new EventScheduleRow();
            currentEvent.event = Constants.NO_EVENT;
            eventSchedules.add(currentEvent);
            return eventSchedules;
        }
        return eventSchedules;
    }

    @Override
    protected void onPostExecute(List<EventScheduleRow> result) {
        ScheduleDataSource scheduleDS = null;
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeZone(TimeZone.getTimeZone("Japan"));

        int lastCheckSum;
        int currentCheckSum = 0;
        boolean writeDb = false;
        SharedPreferences prefs = (parentFrag.getActivity()).getSharedPreferences(Constants.SCHEDULE_HISTORY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;

        currentCheckSum = ScheduleChecksum(result);
        lastCheckSum = prefs.getInt(Constants.SCHEDULE_HISTORY_CHECKSUM, -1);
        if (lastCheckSum != currentCheckSum) {
            writeDb = true;
        } else if (lastCheckSum == currentCheckSum && !addViews) {
            return;
        }

        if (writeDb) {
            scheduleDS = new ScheduleDataSource(parentFrag.getActivity());
            scheduleDS.clearSchedule();
        }
        for (EventScheduleRow es : result) {
            if (addViews) {
                ((ScheduleFragment) parentFrag).addScheduleRow(es);
            }
            if (writeDb) {
                scheduleDS.addEvent(es);
            }
        }
        if (writeDb) {
            editor = prefs.edit();
            editor.putInt(Constants.SCHEDULE_HISTORY_CHECKSUM, currentCheckSum);
            editor.putInt(Constants.SCHEDULE_HISTORY_DATE, currentDate.get(Calendar.DAY_OF_YEAR));
            editor.apply();
            scheduleDS.close();
        }
        if (addViews) {
            ((ScheduleFragment) parentFrag).getSwipeLayout().setRefreshing(false);
            if (((ScheduleFragment) parentFrag).getAnimate()) {
                (((ScheduleFragment) parentFrag).getEventTable()).setAlpha(0f);
                (((ScheduleFragment) parentFrag).getEventTable()).setVisibility(View.VISIBLE);
                (((ScheduleFragment) parentFrag).getEventTable()).animate().setDuration(ScheduleFragment.ANIMATION_DURATION).translationY(0).alpha(1.0f).setListener(null);
            }
        }
        AlertsFragment.setAlertAlarms(parentFrag.getActivity());
    };

    private int ScheduleChecksum(List<EventScheduleRow> toCheck) {

        int sum = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Japan"));

        sum += 100 * cal.get(Calendar.DAY_OF_YEAR);
        for (EventScheduleRow check : toCheck) {
            sum += (check.event * 10) + check.times[Constants.GROUP_A] * 13 + check.times[Constants.GROUP_B] * 29 + check.times[Constants.GROUP_C] * 11 + check.times[Constants.GROUP_D] * 3
                    + check.times[Constants.GROUP_E] * 19;
        }
        return sum;

    }

    private GregorianCalendar ParseJpDate(String date) {
        GregorianCalendar jpEventDate;
        String[] daysplit = date.split("月|日| ");

        jpEventDate = new GregorianCalendar(TimeZone.getTimeZone("Japan"));

        jpEventDate.set(Calendar.MONTH, Integer.parseInt(daysplit[1]) - 1);
        jpEventDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(daysplit[2]));
        jpEventDate.set(Calendar.SECOND, 0);
        jpEventDate.set(Calendar.MILLISECOND, 0);

        return jpEventDate;
    }

}
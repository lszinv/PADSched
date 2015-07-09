package com.lsz.padsched;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

public class ScheduleAlarmReceiver extends BroadcastReceiver {
    Context parentContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        parentContext = context;
        sendNotification(intent.getExtras().getInt("event"), intent.getExtras().getInt("group"));
        AlertsFragment.setAlertAlarms(context);
    }

    @SuppressLint("NewApi")
    public void sendNotification(int event, int group) {
        String eventName = "";
        switch (group){
            case Constants.GROUP_A:
                eventName="(A)";
                break;
            case Constants.GROUP_B:
                eventName="(B)";
                break;
            case Constants.GROUP_C:
                eventName="(C)";
                break;
            case Constants.GROUP_D:
                eventName="(D)";
                break;
            case Constants.GROUP_E:
                eventName="(E)";
                break;
            default:
                eventName="(?)";
        };

        EventInfoObject eio = new EventInfoObject(event);
        Notification n = new Notification.Builder(parentContext)
                .setContentText(eventName)
                .setContentTitle("Urgent Dungeon! " + eventName)
                .setColor(0xFF000000)
                .setSmallIcon(eio.getNotification())
                .setLargeIcon(BitmapFactory.decodeResource(parentContext.getResources(), eio.getEventDrawable())).build();

        NotificationManager nm = (NotificationManager) parentContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, n);
    };

    public static NextScheduleObject getNextScheduleObject(Context context) {
        AlertScheduleDataSource alertDS = new AlertScheduleDataSource(context);
        ArrayList<AlertScheduleObject> alertScheduleObj = alertDS.getAlertSchedule();
        alertDS.close();

        AlertSwitchDataSource atDS = new AlertSwitchDataSource(context);
        AlertSwitchObject[] atos = new AlertSwitchObject[5];
        AlertMasterToggleDataSource amsDS = new AlertMasterToggleDataSource(context);
        long currentLongTime = Calendar.getInstance().getTimeInMillis();
        boolean[] masterSwitch = new boolean[5];
        for (int group = Constants.GROUP_A; group <= Constants.GROUP_E; group++) {
            atos[group] = atDS.getSwitches(group);
            masterSwitch[group] = amsDS.getMasterToggle(group);
        }
        amsDS.close();

        long nsoTime = -1;
        int nsoGroup = 0;
        int nsoEvent = 999;

        for (AlertScheduleObject aso : alertScheduleObj) {
            if (masterSwitch[aso.getGroup()] && aso.getTime() >= currentLongTime && atos[aso.getGroup()].getSwitch(aso.getEvent())) {
                nsoTime = aso.getTime();
                nsoEvent = aso.getEvent();
                nsoGroup = aso.getGroup();
                break;
            }
        }
        return new NextScheduleObject(nsoTime, nsoEvent, nsoGroup);
    }

}

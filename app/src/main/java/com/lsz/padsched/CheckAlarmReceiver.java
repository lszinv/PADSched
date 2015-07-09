package com.lsz.padsched;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CheckAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlertsFragment.setAlertAlarms(context);
    }

}

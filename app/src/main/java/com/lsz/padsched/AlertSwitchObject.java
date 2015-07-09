package com.lsz.padsched;

import android.util.Log;

// Object containing switch data returned from the database
public class AlertSwitchObject {
    private int group;
    private boolean[] switches = new boolean[Constants.DUNGEON_COUNT];

    public AlertSwitchObject(int group, boolean[] switches) {
        this.setGroup(group);
        this.switches = switches;
    }

    public boolean getSwitch(int event) {
        return switches[event];
    }

    public void debugSwitches() {
        for (int i = 0; i < Constants.DUNGEON_COUNT; i++) {
            Log.d("lsz", "" + switches[i]);
        }
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }
}

package com.lsz.padsched;

public class NextScheduleObject {
    private long time;
    private int group;
    private int event;

    public NextScheduleObject(long time, int event, int group) {
        this.time = time;
        this.event = event;
        this.setGroup(group);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

}

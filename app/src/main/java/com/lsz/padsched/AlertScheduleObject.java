package com.lsz.padsched;

public class AlertScheduleObject {
    private long time;
    private int event;
    private int group;

    public AlertScheduleObject(int event, int group, long time) {
        this.setTime(time);
        this.setGroup(group);
        this.setEvent(event);
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

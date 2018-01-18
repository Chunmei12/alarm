package com.alarmclock.administrator.alarmclock.Class;

/**
 * Created by Administrator on 2017/1/9.
 */

public class Alarm {
    private String hour;
    private String minute;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private boolean OnOff;

    public Alarm(String hour, String minute, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday, boolean onOff) {
        this.hour = hour;
        this.minute = minute;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        OnOff = onOff;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public boolean isOnOff() {
        return OnOff;
    }

    public void setOnOff(boolean onOff) {
        OnOff = onOff;
    }
}



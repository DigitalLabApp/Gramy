package org.telegram.hamrahgram.model;


public class WeekModel {
    private Long[] days;
    private int firstDayNumber;

    public void setFirstDayNumber(int firstDayNumber) {
        this.firstDayNumber = firstDayNumber;
    }

    public int getFirstDayNumber() {

        return firstDayNumber;
    }

    public Long[] getDays() {
        return days;
    }

    public void setDays(Long[] days) {

        this.days = days;
    }

}

package org.telegram.hamrahgram.model;


public class Analytics {

    private int year;
    private int month;
    private int day;
    private long duration;
    private long chatId;
    private int dayNumber;
    private int chatType;
    private String chatTitle;
    private int perCent;
    private String photo;

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {

        return photo;
    }

    public void setPerCent(int perCent) {
        this.perCent = perCent;
    }

    public int getPerCent() {

        return perCent;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {

        this.chatTitle = chatTitle;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public int getChatType() {

        return chatType;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getDayNumber() {

        return dayNumber;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public long getDuration() {
        return duration;
    }

    public long getChatId() {
        return chatId;
    }

    public void setYear(int year) {

        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}

package com.pscalendarevent.pscalendarevent.vo;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TitleData extends DayData {
    private String title;
    private boolean isTitle;
    Calendar cal = Calendar.getInstance();

    public TitleData(DateData dayData) {
        super(dayData);
        isTitle = true;
        switch (dayData.getDay()) {
            case 1:
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
            case 2:
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case 3:
                cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                break;
            case 4:
                cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                break;
            case 5:
                cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                break;
            case 6:
                cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                break;
            case 7:
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                break;
            default:
                break;
        }

        String dayValue = new SimpleDateFormat("EEE").format(new java.util.Date(cal.getTimeInMillis()))
                .replaceAll("\\.", "");
        Log.w("dayValue",dayData.getDay()+"##"+dayValue);
        if (dayValue.length() > 1) {
            String DayValueUpperCased = dayValue.substring(0, 1).toUpperCase() + dayValue.substring(1);
            StringBuilder stringBuilder = new StringBuilder(DayValueUpperCased);
           // stringBuilder.deleteCharAt(2);
            setTitle(stringBuilder.toString());
        }
    }

    @Override
    public String getText() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTitle() {
        return isTitle;
    }
}

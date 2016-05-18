package br.com.onedreams.galo.DAO;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by root on 21/04/16.
 */
public class DaoTime {

    private int hour = 0;
    private int minute = 0;
    private DaoLog daoLog;
    private String pathSdCard;

    public DaoTime() {}

    public int getHour() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        minute = calendar.get(Calendar.MINUTE);
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}

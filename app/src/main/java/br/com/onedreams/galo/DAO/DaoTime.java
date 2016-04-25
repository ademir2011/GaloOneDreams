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

    public DaoTime(final int DEFAULT_TIME_TIME_UPDATE_AND_SHOW) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){

                    new UpdateTime().execute();

                    try {
                        Thread.sleep(DEFAULT_TIME_TIME_UPDATE_AND_SHOW);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

    }

    public class UpdateTime extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);

            return null;
        }
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}

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

    public DaoTime(final int DEFAULT_TIME_TIME_UPDATE_AND_SHOW, final DaoLog daoLog, final String pathSdCard) {

        this.daoLog = daoLog;
        this.pathSdCard = pathSdCard;

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoTime() -> entrou no método");

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){

                    daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoTime() -> com internet - entrou no while");

                    new UpdateTime().execute();

                    daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoTime() -> com internet - executou o UpdateTime.execute()");

                    try {
                        Thread.sleep(DEFAULT_TIME_TIME_UPDATE_AND_SHOW);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoTime() -> finalizou");

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

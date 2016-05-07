package br.com.onedreams.galo.DAO;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.onedreams.galo.Activities.MainActivity;
import br.com.onedreams.galo.Classes.CheckConnection;

/**
 * Created by root on 03/05/16.
 */
public class DaoLog {

    private Date date;
    private String propagandasLog = "";
    private String horaLog = "";
    private String temperaturaLog = "";
    private boolean internetLog = false;
    private String dolarLog = "";
    private String resumoMsgAvisoLog = "";
    CheckConnection checkConnection;

    public DaoLog(final int DEFAULT_UPDATE_SEND_LOG, Context context, final String pathSdCard ) {

        checkConnection = new CheckConnection(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    new SendLogTxt().execute(pathSdCard);
                    try {
                        Thread.sleep(DEFAULT_UPDATE_SEND_LOG);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public class SendLogTxt extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            DateFormat df = new SimpleDateFormat("dd MM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());

            File file = new File(params[0], "log.txt");
            FileWriter fileWritter = null;
            try {
                fileWritter = new FileWriter(file,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            try {

                if (checkConnection.isOnline()) internetLog = true;
                else internetLog = false;

                String msgLog = date+"|"+propagandasLog+"|"+horaLog+"|"+temperaturaLog+"|"+String.valueOf(internetLog)+"|"+dolarLog+"|"+resumoMsgAvisoLog+"\n";

                bufferWritter.write(msgLog);

                propagandasLog = "";
                horaLog = "";
                temperaturaLog = "";
                internetLog = false;
                dolarLog = "";
                resumoMsgAvisoLog = "";

                Log.e("LOG", msgLog);


            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufferWritter.close();
                fileWritter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPropagandasLog() {
        return propagandasLog;
    }

    public void setPropagandasLog(String propagandasLog) {
        this.propagandasLog = propagandasLog;
    }

    public String getHoraLog() {
        return horaLog;
    }

    public void setHoraLog(String horaLog) {
        this.horaLog = horaLog;
    }

    public String getTemperaturaLog() {
        return temperaturaLog;
    }

    public void setTemperaturaLog(String temperaturaLog) {
        this.temperaturaLog = temperaturaLog;
    }

    public boolean isInternetLog() {
        return internetLog;
    }

    public void setInternetLog(boolean internetLog) {
        this.internetLog = internetLog;
    }

    public String getDolarLog() {
        return dolarLog;
    }

    public void setDolarLog(String dolarLog) {
        this.dolarLog = dolarLog;
    }

    public String getResumoMsgAvisoLog() {
        return resumoMsgAvisoLog;
    }

    public void setResumoMsgAvisoLog(String resumoMsgAvisoLog) {
        this.resumoMsgAvisoLog = resumoMsgAvisoLog;
    }
}

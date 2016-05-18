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
    CheckConnection checkConnection;

    public DaoLog(Context context ) {

        checkConnection = new CheckConnection(context);

    }

    public void SendMsgToTxt(String pathSdCard, String txtName, String data) {

        DateFormat df = new SimpleDateFormat("dd MM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        File file = new File(pathSdCard, txtName);
        FileWriter fileWritter = null;

        try {
            fileWritter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

        String msgLog = date+" - "+data+"\n";

        try {
            bufferWritter.write(msgLog);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bufferWritter.close();
            fileWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

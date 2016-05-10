package br.com.onedreams.galo.DAO;

import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 28/04/16.
 */
public class DAOSDcard {

    Map<Calendar, String> mapSdCardPropaganda;
    private boolean enable = false;
    private DaoLog daoLog;
    private String pathSdCard;

    public DAOSDcard(final String pathSdCard, final int DEFAULT_TIME_READ_CONFIG_TXT, final DaoLog daoLog){

        mapSdCardPropaganda = new HashMap<>();
        this.daoLog = daoLog;
        this.pathSdCard = pathSdCard;

        //daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoSDcard() -> entrou no método");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){

                    //daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoSDcard() -> com internet - entrou no while");

                    new ReadSDcard().execute(pathSdCard);

                    //daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoSDcard() -> com internet - executou o ReadSDcard.execute()");

                    try {
                        Thread.sleep(DEFAULT_TIME_READ_CONFIG_TXT);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

        //daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoSDcard() -> finalizou");

    }

    public class ReadSDcard extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            File file = new File( params[0] );

            String line;

            try {

                BufferedReader br = new BufferedReader(new FileReader(file));

                String lineArray[];

                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

                enable = false;
                mapSdCardPropaganda.clear();

                while((line = br.readLine()) != null){

                    lineArray = line.split("-");

                    try {

                        java.sql.Date date = new java.sql.Date(formatter.parse(lineArray[0]).getTime());

                        Calendar cal = GregorianCalendar.getInstance();
                        cal.setTime(date);

                        mapSdCardPropaganda.put(cal, lineArray[1]);

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }

                enable = true;
                br.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public Map<Calendar, String> getMapSdCardPropaganda() {
        return mapSdCardPropaganda;
    }

    public void setMapSdCardPropaganda(Map<Calendar, String> mapSdCardPropaganda) {
        this.mapSdCardPropaganda = mapSdCardPropaganda;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

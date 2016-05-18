package br.com.onedreams.galo.DAO;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.onedreams.galo.Classes.CheckConnection;
import br.com.onedreams.galo.Classes.RssItem;
import br.com.onedreams.galo.Classes.RssReader;

/**
 * Created by root on 21/04/16.
 */
public class DaoRss {

    private int contadorRss;
    private String rss = "";
    private boolean enable = false;
    public static final String DEFAULT_MENSSAGE = "Galo Mídias Avançadas";
    CheckConnection checkConnection;

    public DaoRss(final String urlRssFonte, final int DEFAULT_TIME_UPDATE_RSS, Context context, final DaoLog daoLog, final String pathSdCard) {

        checkConnection = new CheckConnection(context);

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoRss() -> entrou no método");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while(true) {

                    while (checkConnection.isOnline()) {

                        rss = "";

                        contadorRss = 0;

                        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoRss() -> com internet - entrou no while");

                        new UpdateRss().execute(urlRssFonte);

                        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoRss() -> com internet - executou o UpdateRss.execute()");

                        try {

                            Thread.sleep(DEFAULT_TIME_UPDATE_RSS);

                        } catch (Exception e) {
                        }

                    }

                }
            }
        }).start();

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoRss() -> finalizou");

    }

    public class UpdateRss extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            try {

                enable = false;

                RssReader rssReader = new RssReader(params[0]);

                for (RssItem item : rssReader.getItems()){
                    String tempTitle = item.getTitle();
                    tempTitle = tempTitle.replace("\"", "'");

                    char character = tempTitle.charAt(0);

                    if (  Character.isUpperCase( character ) ){
                        rss += tempTitle + " - Fonte G1 - ";
                    }

                }

                enable = true;

            } catch (Exception e) {
                Log.v("Error Parsing Data", e + "");
            }

            return null;
        }

    }

    public String getRss() {
        return rss;
    }

    public void setRss(String rss) {
        this.rss = rss;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

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
    private List<String> mListNoticias;
    public static final String DEFAULT_MENSSAGE = "Galo Mídias Avançadas";
    CheckConnection checkConnection;


    public DaoRss(final String urlRssFonte, final int DEFAULT_TIME_UPDATE_RSS, Context context) {

        mListNoticias = new ArrayList<>();
        mListNoticias.add(DEFAULT_MENSSAGE);
        checkConnection = new CheckConnection(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (checkConnection.isOnline()) {

                    mListNoticias.clear();

                    contadorRss = 0;

                    new UpdateRss().execute(urlRssFonte);

                    try {

                        Thread.sleep(DEFAULT_TIME_UPDATE_RSS);

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }

    public class UpdateRss extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            try {

                RssReader rssReader = new RssReader(params[0]);
                for (RssItem item : rssReader.getItems()){
                    mListNoticias.add(item.getTitle());
                }
            } catch (Exception e) {
                Log.v("Error Parsing Data", e + "");
                mListNoticias.clear();
                mListNoticias.add(DEFAULT_MENSSAGE);
            }

            return null;
        }

    }

    public List<String> getmListNoticias() {
        return mListNoticias;
    }

    public void setmListNoticias(List<String> mListNoticias) {
        this.mListNoticias = mListNoticias;
    }

    public int getContadorRss() {
        return contadorRss;
    }

    public void setContadorRss(int contadorRss) {
        this.contadorRss = contadorRss;
    }
}

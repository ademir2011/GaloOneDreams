package br.com.onedreams.galo.DAO;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import br.com.onedreams.galo.Classes.CheckConnection;

/**
 * Created by root on 22/04/16.
 */
public class DaoPropaganda {

    private int contadorRss = 0;
    Map<Calendar, String> mapPropaganda;
    private boolean enable = false;
    CheckConnection checkConnection;

    public DaoPropaganda(final URL urlPropagandas, final int DEFAULT_TIME_UPDATE_PROPAGANDA, Context context) {

        mapPropaganda = new HashMap<>();
        checkConnection = new CheckConnection(context);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (checkConnection.isOnline()) {

                    mapPropaganda.clear();

                    contadorRss = 0;

                    new UpdatePropaganda().execute(urlPropagandas);

                    try {

                        Thread.sleep(DEFAULT_TIME_UPDATE_PROPAGANDA);

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }

    public class UpdatePropaganda extends AsyncTask<URL, Void, Void>{

        @Override
        protected Void doInBackground(URL... params) {

            HttpURLConnection urlConnection = null;

            try {

                urlConnection = (HttpURLConnection) params[0].openConnection();

                if (urlConnection.getResponseCode() == 200) {

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    if (in != null) {

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                        String line = "";
                        String lineArray[];

                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

                        enable = false;

                        while ((line = bufferedReader.readLine()) != null) {

                            lineArray = line.split("-");

                            try {

                                java.sql.Date date = new java.sql.Date(formatter.parse(lineArray[0]).getTime());

                                Calendar cal = GregorianCalendar.getInstance();
                                cal.setTime(date);

                                mapPropaganda.put(cal, lineArray[1]);

                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.e("Erro", "Erro ou pegar linha");
                            }

                        }

                        enable = true;

                    }
                    in.close();
                }

            } catch (IOException e) {
                mapPropaganda.clear();
                e.printStackTrace();
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return null;
        }
    }

    public int getContadorRss() {
        return contadorRss;
    }

    public void setContadorRss(int contadorRss) {
        this.contadorRss = contadorRss;
    }

    public Map<Calendar, String> getMapPropaganda() {
        return mapPropaganda;
    }

    public void setMapPropaganda(Map<Calendar, String> mapPropaganda) {
        this.mapPropaganda = mapPropaganda;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

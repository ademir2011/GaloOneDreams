package br.com.onedreams.galo.Activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.onedreams.galo.Classes.RssItem;
import br.com.onedreams.galo.Classes.RssReader;
import br.com.onedreams.galo.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    List<String> mListNoticias;
    List<String> listAvisos;
    Map<Calendar, String> mapPropaganda;

    @Bind(R.id.tvRssMain) TextView tvRssMain;
    @Bind(R.id.tvAvisoMain) TextView tvAvisoMain;
    @Bind(R.id.ivPropagandaMain) ImageView ivPropagandaMain;

    Handler mHandlerRss = new Handler();
    int contadorRss = 0;

    Handler mHandlerAvisos = new Handler();
    int contadorAvisos = 0;

    Handler mHandlerPropaganda = new Handler();
    boolean boolActive = false;

    public static final String PEP_ID = "1";
    public static final int DEFAULT_TIME_SHOW_RSS = 5 * 1000;
    public static final int DEFAULT_TIME_UPDATE_RSS = 1 * 60 * 1000;
    public static final int DEFAULT_TIME_SHOW_AVISOS = 5 * 1000;
    public static final int DEFAULT_TIME_UPDATE_AVISOS = 1 * 60 * 1000;
    public static final int DEFAULT_TIME_UPDATE_PROPAGANDA = 1 * 60  * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fullscreen();

        //--------------- ATUALIZA E EXIBE RSS

        mListNoticias = new ArrayList<>();

        new GetRssFeed().execute("http://g1.globo.com/dynamo/brasil/rss2.xml");

        showRss();

        updateRss();

        //--------------- ATUALIZA E EXIBE AVISOS

        listAvisos = new ArrayList<>();

        new UpdateAvisos().execute();

        updateAvisos();

        showAvisos();

        //-------------- ATUALIZA E EXIBE PROPAGANDAS

        mapPropaganda = new HashMap<>();

        new UpdatePropaganda().execute();

        updatePropaganda();

        showPropaganda();

    }

    private void updatePropaganda() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {

                        new UpdatePropaganda().execute();

                        Log.v("debug", "ATUALIZOU PROPAGANDA");

                        mapPropaganda.clear();

                        Thread.sleep(DEFAULT_TIME_UPDATE_PROPAGANDA);

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }

    private void showPropaganda() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (true) {
                    if (boolActive) {



                        try {

                            Date date = new Date();   // given date
                            final Calendar currentCalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                            currentCalendar.setTime(date);   // assigns calendar to given date

                            for (Map.Entry<Calendar, String> entry : mapPropaganda.entrySet()) {

                                Calendar key = entry.getKey();
                                final String value = entry.getValue();

                                if (currentCalendar.get(Calendar.HOUR_OF_DAY) == key.get(Calendar.HOUR_OF_DAY) &&
                                    currentCalendar.get(Calendar.MINUTE) == key.get(Calendar.MINUTE) &&
                                    currentCalendar.get(Calendar.SECOND) == key.get(Calendar.SECOND)) {

                                    boolActive = false;

                                    mHandlerPropaganda.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            Picasso.with(MainActivity.this).load("http://onedreams.com.br/galo/gestao/" + value).into(ivPropagandaMain);
                                            Log.v("debug", "PROPAGANDA EXIBIDO NA TELA - " + value + " - " + currentCalendar.get(Calendar.HOUR_OF_DAY) + ":" + currentCalendar.get(Calendar.MINUTE) + ":" + currentCalendar.get(Calendar.SECOND));
                                        }
                                    });


                                }

                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }
            }
        }).start();

    }

    private void updateAvisos() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {

                        new UpdateAvisos().execute();

                        Log.v("debug", "ATUALIZOU AVISO");

                        contadorAvisos = 0;

                        Thread.sleep(DEFAULT_TIME_UPDATE_AVISOS);

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }

    private void showAvisos() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(DEFAULT_TIME_SHOW_AVISOS);
                        mHandlerAvisos.post(new Runnable() {

                            @Override
                            public void run() {

                                tvAvisoMain.setText(listAvisos.get(contadorAvisos++));

                                Log.v("debug", "AVISO EXIBIDO NA TELA");

                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }

    private void updateRss() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(DEFAULT_TIME_UPDATE_RSS);

                        new GetRssFeed().execute("http://g1.globo.com/dynamo/brasil/rss2.xml");

                        contadorRss = 0;

                        Log.v("debug", "ATUALIZOU RSS");

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }

    private void showRss() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(DEFAULT_TIME_SHOW_RSS);
                        mHandlerRss.post(new Runnable() {

                            @Override
                            public void run() {

                                tvRssMain.setText(mListNoticias.get(contadorRss++));
                                Log.v("debug", "RSS EXIBIDO NA TELA");

                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }

    private class GetRssFeed extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                RssReader rssReader = new RssReader(params[0]);
                for (RssItem item : rssReader.getItems()){
                    mListNoticias.add(item.getTitle());
                }
            } catch (Exception e) {
                Log.v("Error Parsing Data", e + "");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    class UpdateAvisos extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {

                URL url = new URL("http://onedreams.com.br/galo/gestao/pep_" + PEP_ID + "/avisos/config_avisos.txt");

                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();

                if (code == 200) {

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null) {

                            listAvisos.add(line);

                        }

                    }
                    in.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return null;

        }

    }

    class UpdatePropaganda extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {

                URL url = new URL("http://onedreams.com.br/galo/gestao/pep_"+PEP_ID+"/propagandas/config_propagandas.txt");

                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();

                if (code == 200) {

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null) {

                            String lineArray[] = line.split("-");

                            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

                            try {

                                java.sql.Date date = new java.sql.Date(formatter.parse(lineArray[0]).getTime());

                                Calendar cal = GregorianCalendar.getInstance();
                                cal.setTime(date);

                                mapPropaganda.put(cal, lineArray[1]);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }

                        boolActive = true;

                    }
                    in.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return null;

        }

    }

    private void fullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}

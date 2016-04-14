package br.com.onedreams.galo.Activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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
    @Bind(R.id.tvCotacaoDolarMain) TextView tvCotacaoDolarMain;

    Handler mHandlerRss = new Handler();
    int contadorRss = 0;

    Handler mHandlerAvisos = new Handler();
    int contadorAvisos = 0;

    Handler mHandlerPropaganda = new Handler();
    boolean boolActive = false;

    Handler mHandlerDolar = new Handler();

    public static final String PEP_ID = "1";
    public static final String DEFAULT_MENSSAGE = "Galo Mídias Avançadas";
    public static final int DEFAULT_UPDATE_AND_SHOW_DOLAR = 1 * 5 * 1000;
    public static final int DEFAULT_TIME_SHOW_RSS = 1 * 5 * 1000;
    public static final int DEFAULT_TIME_UPDATE_RSS = 1 * 5 * 1000;
    public static final int DEFAULT_TIME_SHOW_AVISOS = 1 * 5 * 1000;
    public static final int DEFAULT_TIME_UPDATE_AVISOS = 1 * 5 * 1000;
    public static final int DEFAULT_TIME_UPDATE_PROPAGANDA = 1 * 5  * 1000;

    private URL urlAvisos;

    private String urlJsonObj = "http://api.promasters.net.br/cotacao/v1/valores?moedas=USD&alt=json";

    RequestQueue requestQueue;

    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        fullscreen();

        try {
            urlAvisos = new URL("http://onedreams.com.br/galo/gestao/pep_" + PEP_ID + "/avisos/config_avisos.txt");
        } catch (MalformedURLException e) {
            urlAvisos = null;
            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(this);

        //--------------- ATUALIZA E EXIBE RSS

        mListNoticias = new ArrayList<>();

        updateRss();

        showRss();

        //--------------- ATUALIZA E EXIBE AVISOS

        listAvisos = new ArrayList<>();

        updateAvisos();

        showAvisos();

        //-------------- ATUALIZA E EXIBE PROPAGANDAS

        mapPropaganda = new HashMap<>();

        updatePropaganda();

        showPropaganda();

        //-------------- ATUALIZA E EXIBE DOLAR

        updateAndShowDolar();

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

                                    Toast.makeText(getApplicationContext(), "PROPAGANDA ATUALIZADA", Toast.LENGTH_SHORT).show();

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

                        mHandlerAvisos.post(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    tvAvisoMain.setText(listAvisos.get(contadorAvisos));
                                    System.out.println(contadorAvisos);
                                    contadorAvisos++;
                                } catch (Exception e) {
                                    System.out.println(e);
                                    tvAvisoMain.setText(DEFAULT_MENSSAGE);
                                }

                            }
                        });

                        Thread.sleep(DEFAULT_TIME_SHOW_AVISOS);

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


                while (true) {

                    try {

                        Thread.sleep(DEFAULT_TIME_SHOW_RSS);

                        mHandlerRss.post(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    tvRssMain.setText(mListNoticias.get(contadorRss));
                                    contadorRss++;
                                } catch (Exception e) {
                                    System.out.println(e);
                                    tvRssMain.setText(DEFAULT_MENSSAGE);
                                }

                            }
                        });

                    } catch (Exception e) {
                        tvRssMain.setText(DEFAULT_MENSSAGE);
                    }

                }

            }
        }).start();

    }

    public class GetRssFeed extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                mListNoticias.clear();
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public class UpdateAvisos extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {

                listAvisos.clear();

                urlConnection = (HttpURLConnection) urlAvisos.openConnection();

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
                listAvisos.clear();
                listAvisos.add(DEFAULT_MENSSAGE);
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return null;

        }

    }

    public class UpdatePropaganda extends AsyncTask<Void, Void, Void> {

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
                mapPropaganda.clear();
                e.printStackTrace();
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return null;

        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void fullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        onStart();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    public void updateAndShowDolar(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                            urlJsonObj, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONObject obs1 = response.getJSONObject("valores");
                                JSONObject obs2 = obs1.getJSONObject("USD");
                                final String dolar = obs2.getString(String.valueOf("valor"));

                                mHandlerDolar.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        tvCotacaoDolarMain.setText("VALOR DO DOLAR "+dolar+" REAIS");
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("ERROR>>>>", "Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);

                    try {
                        Thread.sleep(DEFAULT_UPDATE_AND_SHOW_DOLAR);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void updateAndShowTemperature(){

    }

}

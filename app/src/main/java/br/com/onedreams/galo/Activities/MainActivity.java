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
import com.squareup.picasso.Cache;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;

import br.com.onedreams.galo.Classes.RssItem;
import br.com.onedreams.galo.Classes.RssReader;
import br.com.onedreams.galo.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    List<String> mListNoticias;
    List<String> listAvisos;
    Map<Calendar, String> mapPropaganda;

    @Bind(R.id.tvRssMain) TextView tvRssMain;
    @Bind(R.id.tvAvisoMain) TextView tvAvisoMain;
    @Bind(R.id.ivPropagandaMain) ImageView ivPropagandaMain;
    @Bind(R.id.tvCotacaoDolarMain) TextView tvCotacaoDolarMain;
    @Bind(R.id.tvCloudMain) TextView tvCloudMain;
    @Bind(R.id.tvTimeMain) TextView tvTimeMain;

    Handler mHandlerRss = new Handler();
    int contadorRss = 0;

    Handler mHandlerAvisos = new Handler();
    int contadorAvisos = 0;

    Handler mHandlerPropaganda = new Handler();

    boolean boolActivePropaganda = false;
    boolean boolActiveAvisos = false;
    boolean boolActiveRss = false;

    Handler mHandlerDolar = new Handler();

    Handler mHandlerTime = new Handler();

    public static final String PEP_ID = "1";
    public static final String DEFAULT_MENSSAGE = "Galo Mídias Avançadas";
    public static final int DEFAULT_UPDATE_AND_SHOW_DOLAR = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_TIME_TIME_UPDATE_AND_SHOW = 1 * 1 * 1 * 50;
    public static final int DEFAULT_UPDATE_AND_SHOW_TEMPERATURA = 1 * 1 * 60 * 1000;
    public static final int DEFAULT_TIME_SHOW_RSS = 1 * 2 * 1000;
    public static final int DEFAULT_TIME_UPDATE_RSS = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_TIME_SHOW_AVISOS = 1 * 2 * 1000;
    public static final int DEFAULT_TIME_UPDATE_AVISOS = 10 * 60 * 1000;
    public static final int DEFAULT_TIME_UPDATE_PROPAGANDA = 1 * 60 * 60  * 1000;
    public static final int DEFAULT_TIME_SHOW_PROPAGANDA = 1 * 1  * 100;

    private URL urlAvisos;

    private String urlJsonObjDolar = "http://api.promasters.net.br/cotacao/v1/valores?moedas=USD&alt=json";

    private String cidade = "Natal";
    private String apikeyweather = "6c44fc59654648e97c2132a1acecd057";
    private String urlJsonObsTemperatura = "http://api.openweathermap.org/data/2.5/weather?q="+cidade+",br&appid="+apikeyweather;

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

        //-------------- ATUALIZA E EXIBE TEMPERATURA

        updateAndShowTemperature();

        //-------------- ATUALIZA E EXIBE HORA

        updateAndShowTime();

    }

    private void updateAndShowTime() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {

                        mHandlerTime.post(new Runnable() {
                            @Override
                            public void run() {

                                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);

                                tvTimeMain.setText(hour+":"+minute);

                            }
                        });

                        Thread.sleep(DEFAULT_TIME_TIME_UPDATE_AND_SHOW);

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }

    private void updatePropaganda() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {

                        new UpdatePropaganda().execute();

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

                while (true) {

                    if (boolActivePropaganda){

                        try {

                            Log.e("Size", String.valueOf( mapPropaganda.size() ) );

                            Calendar calendar = Calendar.getInstance(Locale.getDefault());

                            for (Map.Entry<Calendar, String> entry : mapPropaganda.entrySet()) {

                                Calendar key = entry.getKey();
                                final String value = entry.getValue();

                                if (calendar.get(Calendar.HOUR_OF_DAY) == key.get(Calendar.HOUR_OF_DAY) &&
                                        calendar.get(Calendar.MINUTE) == key.get(Calendar.MINUTE) &&
                                        calendar.get(Calendar.SECOND) == key.get(Calendar.SECOND) ) {

                                    mHandlerPropaganda.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                Picasso.with(MainActivity.this).load("http://onedreams.com.br/galo/gestao/" + value).into(ivPropagandaMain);
                                                Log.e("PROPAGANDA EXIBIDA","PROPAGANDA EXIBIDO NA TELA - " + value);
                                            } catch (Exception e) {
                                                ivPropagandaMain.setImageResource(R.drawable.overdose);
                                            }
                                        }
                                    });

                                }

                            }
                            deleteCache(MainActivity.this);
                        } catch (Exception e) {}

                        try {
                            Thread.sleep(DEFAULT_TIME_SHOW_PROPAGANDA);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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

                        contadorAvisos = 0;

                        new UpdateAvisos().execute();

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

                        if(boolActiveAvisos){

                            mHandlerAvisos.post(new Runnable() {

                                @Override
                                public void run() {

                                    try {
                                        Log.e("SIZE aviso", String.valueOf(listAvisos.size())+" - "+contadorAvisos);
                                        tvAvisoMain.setText(listAvisos.get(contadorAvisos));

                                        if(contadorAvisos < listAvisos.size()-1 ){
                                            contadorAvisos++;
                                        } else {
                                            contadorAvisos = 0;
                                        }

                                    } catch (Exception e) {
                                        System.out.println(e);
                                        tvAvisoMain.setText(DEFAULT_MENSSAGE);
                                    }

                                }
                            });
                        }

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

                        new GetRssFeed().execute("http://g1.globo.com/dynamo/brasil/rss2.xml");

                        contadorRss = 0;

                        Thread.sleep(DEFAULT_TIME_UPDATE_RSS);

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

                    if(boolActiveRss){

                        try {

                            Thread.sleep(DEFAULT_TIME_SHOW_RSS);

                            mHandlerRss.post(new Runnable() {

                                @Override
                                public void run() {

                                try {
                                    tvRssMain.setText(mListNoticias.get(contadorRss));

                                    if(contadorRss < mListNoticias.size()-1 ){
                                        contadorRss++;
                                    } else {
                                        contadorRss = 0;
                                    }

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

            }
        }).start();

    }

    public class GetRssFeed extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                boolActiveRss = false;
                mListNoticias.clear();
                RssReader rssReader = new RssReader(params[0]);
                for (RssItem item : rssReader.getItems()){
                    mListNoticias.add(item.getTitle());
                }
                boolActiveRss = true;
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

                boolActiveAvisos = false;
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
                        boolActiveAvisos = true;
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

            mapPropaganda.clear();
            boolActivePropaganda = false;

            try {

                URL url = new URL("http://onedreams.com.br/galo/gestao/pep_"+PEP_ID+"/propagandas/config_propagandas.txt");

                urlConnection = (HttpURLConnection) url.openConnection();

                if (urlConnection.getResponseCode() == 200) {

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    if (in != null) {

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                        String line = "";
                        String lineArray[];

                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

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

                        boolActivePropaganda = true;

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
                            urlJsonObjDolar, null, new Response.Listener<JSONObject>() {

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                            urlJsonObsTemperatura, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONObject obs1 = response.getJSONObject("main");
                                final double temp = obs1.getDouble(String.valueOf("temp"));

                                mHandlerDolar.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        tvCloudMain.setText(String.valueOf((int)(temp-273.15))+"ºC");
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
                        Thread.sleep(DEFAULT_UPDATE_AND_SHOW_TEMPERATURA);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Bind(R.id.tvTest) TextView tvTest;

    String imageUri[] = {"https://upload.wikimedia.org/wikipedia/commons/c/cc/Atardecer_desde_Monserrate.jpg",
                        "http://eoimages.gsfc.nasa.gov/images/imagerecords/3000/3695/ISS007-E-10974_lrg.jpg",
                        "http://naturalearth.springercarto.com/ne3_data/gallery/asia_night1.jpg",
                        "https://upload.wikimedia.org/wikipedia/commons/a/ab/Dahlia_x_hybrida.jpg",
                        "http://images.ipac.caltech.edu/eso/eso0844a/eso_eso0844a_1280.jpg"};
    long allTime;

    @OnClick(R.id.btStart)
    public void clickBtStart(View view){

        tvTest.setText(String.valueOf(0));

        allTime = 0;

        final long startTime1 = System.currentTimeMillis();

        ImageHandler.getSharedInstance(getApplicationContext()).load(imageUri[0]).into(ivPropagandaMain, new Callback() {
            @Override
            public void onSuccess() {

                long difference1 = System.currentTimeMillis() - startTime1;

                tvTest.setText(String.valueOf(difference1/1000));

                Toast.makeText(getApplicationContext(), "Imagem[1]"+difference1, Toast.LENGTH_SHORT).show();

                final long startTime2 = System.currentTimeMillis();

                ImageHandler.getSharedInstance(getApplicationContext()).load(imageUri[1]).into(ivPropagandaMain, new Callback() {
                    @Override
                    public void onSuccess() {
                        long difference2 = System.currentTimeMillis() - startTime2;

                        tvTest.setText(String.valueOf((int) (Integer.valueOf((String) tvTest.getText()) + difference2/1000)));

                        Toast.makeText(getApplicationContext(), "Imagem[2]", Toast.LENGTH_SHORT).show();

                        final long startTime3 = System.currentTimeMillis();

                        ImageHandler.getSharedInstance(getApplicationContext()).load(imageUri[2]).into(ivPropagandaMain, new Callback() {
                            @Override
                            public void onSuccess() {
                                long difference3 = System.currentTimeMillis() - startTime3;

                                tvTest.setText(String.valueOf((int) (Integer.valueOf((String) tvTest.getText()) + difference3/1000)));

                                Toast.makeText(getApplicationContext(), "Imagem[3]", Toast.LENGTH_SHORT).show();

                                final long startTime4 = System.currentTimeMillis();

                                ImageHandler.getSharedInstance(getApplicationContext()).load(imageUri[3]).into(ivPropagandaMain, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        long difference4 = System.currentTimeMillis() - startTime4;

                                        tvTest.setText(String.valueOf((int) (Integer.valueOf((String) tvTest.getText()) + difference4/1000)));

                                        Toast.makeText(getApplicationContext(), "Imagem[4]", Toast.LENGTH_SHORT).show();

                                        final long startTime5 = System.currentTimeMillis();

                                        ImageHandler.getSharedInstance(getApplicationContext()).load(imageUri[4]).into(ivPropagandaMain, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                long difference5 = System.currentTimeMillis() - startTime5;

                                                tvTest.setText(String.valueOf((int) (Integer.valueOf((String) tvTest.getText()) + difference5/1000)));

                                                Toast.makeText(getApplicationContext(), "Imagem[5]", Toast.LENGTH_SHORT).show();

                                                deleteCache(MainActivity.this);
                                            }

                                            @Override
                                            public void onError() {
                                                Toast.makeText(getApplicationContext(), "Falha, tente novamente", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        deleteCache(MainActivity.this);
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(getApplicationContext(), "Falha, tente novamente", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                deleteCache(MainActivity.this);
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(getApplicationContext(), "Falha, tente novamente", Toast.LENGTH_SHORT).show();
                            }
                        });

                        deleteCache(MainActivity.this);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), "Falha, tente novamente", Toast.LENGTH_SHORT).show();
                    }
                });

                deleteCache(MainActivity.this);
            }

            @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), "Falha, tente novamente", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static class ImageHandler {

        private static Picasso instance;

        public static Picasso getSharedInstance(Context context)
        {
            if(instance == null)
            {
                instance = new Picasso.Builder(context).executor(Executors.newSingleThreadExecutor()).memoryCache(Cache.NONE).indicatorsEnabled(true).build();
            }
            return instance;
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


}

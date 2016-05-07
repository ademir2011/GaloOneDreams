package br.com.onedreams.galo.Activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;

import br.com.onedreams.galo.Classes.CheckConnection;
import br.com.onedreams.galo.DAO.DAOSDcard;
import br.com.onedreams.galo.DAO.DaoAvisos;
import br.com.onedreams.galo.DAO.DaoDolar;
import br.com.onedreams.galo.DAO.DaoLog;
import br.com.onedreams.galo.DAO.DaoPropaganda;
import br.com.onedreams.galo.DAO.DaoRss;
import br.com.onedreams.galo.DAO.DaoTemperatura;
import br.com.onedreams.galo.DAO.DaoTime;
import br.com.onedreams.galo.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tvRssMain) TextView tvRssMain;
    @Bind(R.id.tvAvisoMain) TextView tvAvisoMain;
    @Bind(R.id.ivPropagandaMain) ImageView ivPropagandaMain;
    @Bind(R.id.tvCotacaoDolarMain) TextView tvCotacaoDolarMain;
    @Bind(R.id.tvCloudMain) TextView tvCloudMain;
    @Bind(R.id.tvTimeMain) TextView tvTimeMain;
    @Bind(R.id.pbLoading) ProgressBar pbLoading;
    @Bind(R.id.vvPropagandaMain) VideoView vvPropagandaMain;
    @Bind(R.id.tvTabMain) TextView tvTabMain;

    Handler mHandlerScreen = new Handler();

    Handler mHandlerRss = new Handler();

    Handler mHandlerAvisos = new Handler();

    Handler mHandlerPropaganda = new Handler();

    Handler mHandlerPB = new Handler();

    public static final String PEP_ID = "1";
    public static final String pathSdCard = "storage/external_storage/sdcard1/";
    //public static final String pathSdCard = "mnt/external_sdcard/sdcard/";
    public static final int DEFAULT_UPDATE_AND_SHOW_DOLAR = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_TIME_TIME_UPDATE_AND_SHOW = 1 * 1 * 1 * 10;
    public static final int DEFAULT_UPDATE_AND_SHOW_TEMPERATURA = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_TIME_SHOW_RSS = 1 * 10 * 1000;
    public static final int DEFAULT_TIME_UPDATE_RSS = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_TIME_SHOW_AVISOS = 1 * 10 * 1000;
    public static final int DEFAULT_TIME_UPDATE_AVISOS = 1 * 1 * 60 * 1000;
    public static final int DEFAULT_TIME_UPDATE_PROPAGANDA = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_TIME_SHOW_PROPAGANDA = 1 * 1 * 100;
    public static final int DEFAULT_TIME_READ_CONFIG_TXT = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_UPDATE_SEND_LOG = 1 * 1 * 60 * 1000;

    private URL urlAvisos;
    private URL urlPropagandas;

    RequestQueue requestQueue;

    public static final String DEFAULT_MENSSAGE = "Galo Mídias Avançadas";
    private String cidade = "Natal";
    private String apikeyweather = "6c44fc59654648e97c2132a1acecd057";
    private String urlJsonObsTemperatura = "http://api.openweathermap.org/data/2.5/weather?q="+cidade+",br&appid="+apikeyweather;
    private String urlJsonObjDolar = "http://api.promasters.net.br/cotacao/v1/valores?moedas=USD&alt=json";
    private String urlRssFonte = "http://g1.globo.com/dynamo/rn/rio-grande-do-norte/rss2.xml";

    DaoPropaganda daoPropaganda;
    DaoRss daoRss;
    DaoAvisos daoAvisos;
    DaoDolar daoDolar;
    DaoTemperatura daoTemperatura;
    DaoTime daoTime;
    CheckConnection checkConnection;
    DAOSDcard daosDcard;

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
            urlPropagandas = new URL("http://onedreams.com.br/galo/gestao/pep_" + PEP_ID + "/propagandas/config_propagandas.txt");
        } catch (MalformedURLException e) {
            urlAvisos = null;
            urlPropagandas = null;
            e.printStackTrace();
        }

        checkConnection = new CheckConnection(this);

        requestQueue = Volley.newRequestQueue(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!checkConnection.isOnline()){
                    Log.e(">>","SEM INTERNET");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.e(">>","COM INTERNET");
                try {
                    executaAtualizacoes();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    boolean showPb = false;
    private int secondTemp;

    private void showPropagandas() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {

                    try {

                        if (daosDcard.isEnable()) {

                            if (!showPb) {
                                pbLoading.setVisibility(View.INVISIBLE);
                                showPb = true;
                            }

                            Calendar calendar = Calendar.getInstance(Locale.getDefault());

                            for (Map.Entry<Calendar, String> entry : daosDcard.getMapSdCardPropaganda().entrySet()) {

                                Calendar key = entry.getKey();
                                final String value = entry.getValue();

                                if (calendar.get(Calendar.HOUR_OF_DAY) == key.get(Calendar.HOUR_OF_DAY) &&
                                    calendar.get(Calendar.MINUTE) == key.get(Calendar.MINUTE) &&
                                    calendar.get(Calendar.SECOND) == key.get(Calendar.SECOND)) {

                                    if( secondTemp !=  key.get(Calendar.SECOND)){
                                        secondTemp = key.get(Calendar.SECOND);
                                        mHandlerPropaganda.post(new Runnable() {

                                            @Override
                                            public void run() {
                                                try {

                                                    File f = new File(pathSdCard+"assets/"+value );

                                                    String ext = value.substring(value.lastIndexOf(".") + 1);

                                                    if(ext.equals("mp4")){
                                                        ivPropagandaMain.setVisibility(View.GONE);
                                                        vvPropagandaMain.setVisibility(View.VISIBLE);
                                                        vvPropagandaMain.setVideoPath(f.getAbsolutePath());
                                                        vvPropagandaMain.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                            @Override
                                                            public void onPrepared(MediaPlayer mp) {
                                                        vvPropagandaMain.start();

                                                            }
                                                        });
                                                    } else {
                                                        vvPropagandaMain.setVisibility(View.GONE);
                                                        ivPropagandaMain.setVisibility(View.VISIBLE);
                                                        Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                                                        ivPropagandaMain.setImageBitmap(bmp);

                                                    }

                                                    Log.e("PROPAGANDA EXIBIDA", "PROPAGANDA EXIBIDO NA TELA - " + value);

                                                } catch (Exception e) {}
                                            }
                                        });
                                    } else {
                                        secondTemp = key.get(Calendar.SECOND);
                                    }

                                }
                            }
                        }
                        //deleteCache(MainActivity.this);
                    } catch (Exception e) {}

                    try {
                        Thread.sleep(DEFAULT_TIME_SHOW_PROPAGANDA);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private void showAvisos() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {

                        mHandlerAvisos.post(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    Log.e("SIZE aviso", String.valueOf(daoAvisos.getListAvisos().size())+" - "+daoAvisos.getContadorAvisos());
//                                    tvAvisoMain.setText(daoAvisos.getListAvisos().get(daoAvisos.getContadorAvisos()));
//
//                                    if(daoAvisos.getContadorAvisos() < daoAvisos.getListAvisos().size()-1 ){
//                                        daoAvisos.setContadorAvisos(daoAvisos.getContadorAvisos()+1);
//                                    } else {
//                                        daoAvisos.setContadorAvisos(0);
//                                    }

//                                    if(daoRss.getContadorRss() < daoRss.getmListNoticias().size()-1 ){
//                                        daoRss.setContadorRss(daoRss.getContadorRss()+1);
//                                    } else {
//                                        daoRss.setContadorRss(0);
//                                    }

                                } catch (Exception e) {
                                    System.out.println(e);
                                    tvAvisoMain.setText(DEFAULT_MENSSAGE);
                                }

                            }
                        });

                        Thread.sleep(DEFAULT_TIME_SHOW_AVISOS);

                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    private void showRss() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {

                    mHandlerRss.post(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                if(new Random().nextInt(2) == 1){

                                    tvTabMain.setText("Noticias");
                                    tvRssMain.setText(daoRss.getmListNoticias().get(daoRss.getContadorRss()));

                                    if(daoRss.getContadorRss() < daoRss.getmListNoticias().size()-1 ){
                                        daoRss.setContadorRss(daoRss.getContadorRss()+1);
                                    } else {
                                        daoRss.setContadorRss(0);
                                    }

                                } else {

                                    tvTabMain.setText("Avisos");
                                    tvRssMain.setText(daoAvisos.getListAvisos().get(daoAvisos.getContadorAvisos()));
                                    Log.e("SIZE aviso", String.valueOf(daoAvisos.getListAvisos().size())+" - "+daoAvisos.getContadorAvisos());

                                    if(daoAvisos.getContadorAvisos() < daoAvisos.getListAvisos().size()-1 ){
                                        daoAvisos.setContadorAvisos(daoAvisos.getContadorAvisos()+1);
                                    } else {
                                        daoAvisos.setContadorAvisos(0);
                                    }
                                }

                            } catch (Exception e) {
                                System.out.println(e);
                                tvRssMain.setText(DEFAULT_MENSSAGE);
                            }

                        }
                    });

                    try {
                        Thread.sleep(DEFAULT_TIME_SHOW_RSS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    private void showDolarTimeTemperatura() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){

                    mHandlerScreen.post(new Runnable() {
                        @Override
                        public void run() {

                        tvCotacaoDolarMain.setText("VALOR DO DOLAR "+daoDolar.getDolar()+" REAIS");

                        if (daoTime.getMinute() < 10){
                            tvTimeMain.setText(daoTime.getHour()+":0"+daoTime.getMinute());
                        } else {
                            tvTimeMain.setText(daoTime.getHour()+":"+daoTime.getMinute());
                        }

                        tvCloudMain.setText(String.valueOf((int)(daoTemperatura.getTemperatura()-273.15))+"ºC");

                        }
                    });

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    public void executaAtualizacoes() throws IOException {

        //--------------- ATUALIZA E EXIBE RSS

        daoRss = new DaoRss(urlRssFonte, DEFAULT_TIME_UPDATE_RSS, this);

        //--------------- ATUALIZA E EXIBE AVISOS

        daoAvisos = new DaoAvisos(urlAvisos, DEFAULT_TIME_UPDATE_AVISOS, this);

        //-------------- ATUALIZA E EXIBE DOLAR

        daoDolar = new DaoDolar(urlJsonObjDolar, DEFAULT_UPDATE_AND_SHOW_DOLAR, requestQueue, this);

        //-------------- ATUALIZA E EXIBE TEMPERATURA

        daoTemperatura = new DaoTemperatura(urlJsonObsTemperatura, DEFAULT_UPDATE_AND_SHOW_TEMPERATURA, requestQueue, this);

        //-------------- ATUALIZA E EXIBE HORA

        daoTime = new DaoTime(DEFAULT_TIME_TIME_UPDATE_AND_SHOW);

        //-------------- ATUALIZA E EXIBE PROPAGANDAS

        //daoPropaganda = new DaoPropaganda(urlPropagandas, DEFAULT_TIME_UPDATE_PROPAGANDA, this);
        daosDcard = new DAOSDcard(pathSdCard+"config.txt", DEFAULT_TIME_READ_CONFIG_TXT);

        //-------------- UPDATE SCREEN

        showPropagandas();

        showDolarTimeTemperatura();

        showRss();

        //showAvisos();

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void fullscreen() {

        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

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

    public static class ImageHandler {

        private static Picasso instance;

        public static Picasso getSharedInstance(Context context){
            if(instance == null){
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

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

}

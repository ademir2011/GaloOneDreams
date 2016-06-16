package br.com.onedreams.galo.Activities;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import br.com.onedreams.galo.Classes.CheckConnection;
import br.com.onedreams.galo.Classes.Statistics;
import br.com.onedreams.galo.DAO.DAOSDcard;
import br.com.onedreams.galo.DAO.DaoAvisos;
import br.com.onedreams.galo.DAO.DaoDolar;
import br.com.onedreams.galo.DAO.DaoLog;
import br.com.onedreams.galo.DAO.DaoRss;
import br.com.onedreams.galo.DAO.DaoTemperatura;
import br.com.onedreams.galo.DAO.DaoTime;
import br.com.onedreams.galo.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tvRssMain) TextView tvRssMain;
    @Bind(R.id.ivPropagandaMain) ImageView ivPropagandaMain;
    @Bind(R.id.tvCotacaoDolarMain) TextView tvCotacaoDolarMain;
    @Bind(R.id.tvCloudMain) TextView tvCloudMain;
    @Bind(R.id.tvTimeMain) TextView tvTimeMain;
    @Bind(R.id.pbLoading) ProgressBar pbLoading;
    @Bind(R.id.vvPropagandaMain) VideoView vvPropagandaMain;
    @Bind(R.id.tvRssBottomMain) TextView tvRssBottomMain;

    Handler mHandlerScreen = new Handler();

    Handler mHandlerRss = new Handler();

    Handler mHandlerAvisos = new Handler(); 

    Handler mHandlerPropaganda = new Handler();
    public static final String PEP_ID = "0";
    public static final String pathSdCard = "storage/external_storage/sdcard1/";
//    public static final String pathSdCard = "mnt/external_sd/";
    public static final int DEFAULT_UPDATE_AND_SHOW_DOLAR = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_UPDATE_AND_SHOW_TEMPERATURA = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_TIME_SHOW_RSS = 1 * 15 * 1000;
    public static final int DEFAULT_TIME_UPDATE_RSS = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_TIME_UPDATE_AVISOS = 1 * 10 * 60 * 1000;
    public static final int DEFAULT_TIME_SHOW_PROPAGANDA = 1 * 1 * 100;
    public static final int DEFAULT_TIME_READ_CONFIG_TXT = 1 * 60 * 60 * 1000;
    public static final int DEFAULT_TIME_WRITE_STATISTICS = 1 * 1 * 10 * 1000;

    private URL urlAvisos;

    RequestQueue requestQueue;

    public static final String DEFAULT_MENSSAGE = "Galo Mídias Avançadas";
    private String cidade = "Natal";
    private String apikeyweather = "6c44fc59654648e97c2132a1acecd057";
    private String urlJsonObsTemperatura = "http://api.openweathermap.org/data/2.5/weather?q="+cidade+",br&appid="+apikeyweather;
    private String urlJsonObjDolar = "http://api.promasters.net.br/cotacao/v1/valores?moedas=USD&alt=json";
    private String urlRssFonte = "http://g1.globo.com/dynamo/rn/rio-grande-do-norte/rss2.xml";

    DaoRss daoRss;
    DaoAvisos daoAvisos;
    DaoDolar daoDolar;
    DaoTemperatura daoTemperatura;
    DaoTime daoTime;
    CheckConnection checkConnection;
    DAOSDcard daosDcard;
    DaoLog daoLog;

    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        daoLog = new DaoLog(this);

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "----------- daoLogInstanciado -----------");

        ButterKnife.bind(this);

        //new Statistics(this).execute(pathSdCard, DEFAULT_TIME_WRITE_STATISTICS);

        tvRssBottomMain.setMovementMethod(new ScrollingMovementMethod());
        tvRssBottomMain.setSelected(true);

        fullscreen();

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "FullScreenAtivado");

        try {
            urlAvisos = new URL("http://onedreams.com.br/galo/gestao/pep_" + PEP_ID + "/avisos/config_avisos.txt");
        } catch (MalformedURLException e) {
            urlAvisos = null;
            e.printStackTrace();
        }

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "url instanciada");

        checkConnection = new CheckConnection(this);

        requestQueue = Volley.newRequestQueue(this);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while(!checkConnection.isOnline()){
                    try {
                        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "dispositivo sem internet");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "dispositivo com internet");
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

                                                        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "EXIBE O VÌDEO");

                                                        try {

                                                            daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "entra no try");

                                                            ivPropagandaMain.setVisibility(View.GONE);
                                                            vvPropagandaMain.setVisibility(View.VISIBLE);

                                                            if(f.getAbsolutePath().equals("") || f.getAbsolutePath() == null){
                                                                vvPropagandaMain.setVisibility(View.GONE);
                                                                ivPropagandaMain.setVisibility(View.VISIBLE);
                                                                ivPropagandaMain.setImageBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.pep003) );
                                                                daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "problema ao reproduzir vídeo");
                                                            }

                                                            vvPropagandaMain.setVideoPath(f.getAbsolutePath());
                                                            vvPropagandaMain.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                                @Override
                                                                public void onPrepared(MediaPlayer mp) {
                                                                    vvPropagandaMain.start();
                                                                    daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "video iniciado");
                                                                }
                                                            });

                                                            vvPropagandaMain.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                                                @Override
                                                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                                                    vvPropagandaMain.stopPlayback();
                                                                    vvPropagandaMain.setVisibility(View.GONE);
                                                                    ivPropagandaMain.setVisibility(View.VISIBLE);
                                                                    ivPropagandaMain.setImageBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.pep003) );
                                                                    daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "Erro ao processar video");
                                                                    return true;
                                                                }
                                                            });

                                                        } catch ( Exception e) {

                                                            vvPropagandaMain.setVisibility(View.GONE);
                                                            ivPropagandaMain.setVisibility(View.VISIBLE);
                                                            ivPropagandaMain.setImageBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.pep003) );
                                                            daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "problema ao reproduzir vídeo");

                                                        }

                                                    } else {

                                                        try {

                                                            daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "inicio imagem propaganda");
                                                            vvPropagandaMain.setVisibility(View.GONE);
                                                            ivPropagandaMain.setVisibility(View.VISIBLE);
                                                            Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                                                            ivPropagandaMain.setImageBitmap(bmp);

                                                        } catch ( Exception e) {

                                                            vvPropagandaMain.setVisibility(View.GONE);
                                                            ivPropagandaMain.setVisibility(View.VISIBLE);
                                                            ivPropagandaMain.setImageBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.pep003) );
                                                            daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "problema ao reproduzir imagem");

                                                        }

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

                while(true) {

                    mHandlerRss.post(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                tvRssMain.setText(daoAvisos.getListAvisos().get(daoAvisos.getContadorAvisos()));

                                if( daoAvisos.getContadorAvisos() < daoAvisos.getListAvisos().size()-1 ){
                                    daoAvisos.setContadorAvisos(daoAvisos.getContadorAvisos()+1);
                                } else {
                                    daoAvisos.setContadorAvisos(0);
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

    private void showRss() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    if(daoRss.isEnable()){

                        daoRss.setEnable(false);

                        try {

                            mHandlerAvisos.post(new Runnable() {

                                @Override
                                public void run() {

                                    try {

                                        tvRssBottomMain.setText(daoRss.getRss());
                                        Log.e("EXIBIU","EXIBIU");

                                    } catch (Exception e) {
                                        System.out.println(e);
                                        tvRssBottomMain.setText(DEFAULT_MENSSAGE);
                                    }

                                }
                            });

                        } catch (Exception e) {

                        }
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

                        double valorDodolar = round(Double.parseDouble(daoDolar.getDolar()),2);

                        if( String.valueOf(valorDodolar).length() == 3 ){
                            tvCotacaoDolarMain.setText("VALOR DO DOLAR "+valorDodolar+"0 REAIS");
                        } else {
                            tvCotacaoDolarMain.setText("VALOR DO DOLAR "+valorDodolar+" REAIS");
                        }

                        if (daoTime.getMinute() < 10){
                            tvTimeMain.setText(daoTime.getHour()+":0"+daoTime.getMinute());
                        } else {
                            tvTimeMain.setText(daoTime.getHour()+":"+daoTime.getMinute());
                        }

                        double temperatura = daoTemperatura.getTemperatura()-273.15;

                        if(temperatura != -273.15){
                            tvCloudMain.setText(String.valueOf((int) temperatura )+"ºC");
                        }

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

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "entrou no metodo executaAtualizacoes()");

        //--------------- ATUALIZA E EXIBE RSS

        daoRss = new DaoRss(urlRssFonte, DEFAULT_TIME_UPDATE_RSS, this, daoLog, pathSdCard);
        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoRss instanciada");

        //--------------- ATUALIZA E EXIBE AVISOS

        daoAvisos = new DaoAvisos(urlAvisos, DEFAULT_TIME_UPDATE_AVISOS, this, daoLog, pathSdCard);
        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoAvisos instanciada");

        //-------------- ATUALIZA E EXIBE DOLAR

        daoDolar = new DaoDolar(urlJsonObjDolar, DEFAULT_UPDATE_AND_SHOW_DOLAR, requestQueue, this, daoLog, pathSdCard);
        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoDolar instanciada");

        //-------------- ATUALIZA E EXIBE TEMPERATURA

        daoTemperatura = new DaoTemperatura(urlJsonObsTemperatura, DEFAULT_UPDATE_AND_SHOW_TEMPERATURA, requestQueue, this, daoLog, pathSdCard);
        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoTemperatura instanciada");

        //-------------- ATUALIZA E EXIBE HORA

        daoTime = new DaoTime();
        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoTime instanciada");

        //-------------- ATUALIZA E EXIBE PROPAGANDAS

        daosDcard = new DAOSDcard(pathSdCard+"config.txt", pathSdCard, DEFAULT_TIME_READ_CONFIG_TXT, daoLog);
        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daosDcard instanciada");

        //-------------- UPDATE SCREEN

        showPropagandas();
        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "showPropagandas iniciada");

        showDolarTimeTemperatura();
        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "showDolarTimeTemperatura iniciada");

        showRss();
        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "showRss iniciada");

        showAvisos();
        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "showAvisos iniciada");

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void fullscreen() {

        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}

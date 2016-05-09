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
import java.util.ArrayList;
import java.util.List;

import br.com.onedreams.galo.Activities.MainActivity;
import br.com.onedreams.galo.Classes.CheckConnection;

/**
 * Created by root on 22/04/16.
 */
public class DaoAvisos {

    int contadorAvisos = 0;
    List<String> listAvisos;
    public static final String DEFAULT_MENSSAGE = "Galo Mídias Avançadas";
    CheckConnection checkConnection;
    private DaoLog daoLog;
    private String pathSdCard;

    public DaoAvisos(final URL urlAvisos, final int DEFAULT_TIME_UPDATE_AVISOS, Context context, final DaoLog daoLog, final String pathSdCard) {
        listAvisos = new ArrayList<>();
        listAvisos.add(DEFAULT_MENSSAGE);
        checkConnection = new CheckConnection(context);
        this.daoLog = daoLog;
        this.pathSdCard = pathSdCard;

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoAvisos() -> entrou no método");

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (checkConnection.isOnline()) {

                    try {

                        listAvisos.clear();

                        contadorAvisos = 0;

                        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoAvisos() -> com internet - entrou no while");

                        new UpdateAvisos().execute(urlAvisos);

                        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoAvisos() -> com internet - executou o UpdateAvisos().execute()");

                        Thread.sleep(DEFAULT_TIME_UPDATE_AVISOS);

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoAvisos() -> finalizou");

    }

    public class UpdateAvisos extends AsyncTask<URL, Void, Void>{

        @Override
        protected Void doInBackground(URL... params) {

            HttpURLConnection urlConnection = null;

            try {

                urlConnection = (HttpURLConnection) params[0].openConnection();

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

    public int getContadorAvisos() {
        return contadorAvisos;
    }

    public void setContadorAvisos(int contadorAvisos) {
        this.contadorAvisos = contadorAvisos;
    }

    public List<String> getListAvisos() {
        return listAvisos;
    }

    public void setListAvisos(List<String> listAvisos) {
        this.listAvisos = listAvisos;
    }
}

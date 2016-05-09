package br.com.onedreams.galo.DAO;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.onedreams.galo.Classes.CheckConnection;

/**
 * Created by root on 21/04/16.
 */
public class DaoDolar {

    private String dolar = "00.00";
    RequestQueue requestQueueDolar;
    CheckConnection checkConnection;
    private DaoLog daoLog;
    private String pathSdCard;

    public DaoDolar(final String urlJsonObjDolar, final int DEFAULT_UPDATE_AND_SHOW_DOLAR, RequestQueue requestQueue, Context context, final DaoLog daoLog, final String pathSdCard) {

        requestQueueDolar = requestQueue;
        checkConnection = new CheckConnection(context);
        this.daoLog = daoLog;
        this.pathSdCard = pathSdCard;

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoDolar() -> entrou no método");

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(checkConnection.isOnline()){

                    daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoDolar() -> com internet - entrou no while");

                    new Update().execute(urlJsonObjDolar);

                    daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoDolar() -> com internet - executou o Update().execute()");

                    try {
                        Thread.sleep(DEFAULT_UPDATE_AND_SHOW_DOLAR);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

        daoLog.SendMsgToTxt(pathSdCard, "initLog.txt", "daoDolar() -> finalizou");

    }

    public class Update extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    params[0], null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                        JSONObject obs1 = response.getJSONObject("valores");
                        JSONObject obs2 = obs1.getJSONObject("USD");
                        dolar = obs2.getString(String.valueOf("valor"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("ERROR>>>>", "Error: " + error.getMessage());
                }

            });

            requestQueueDolar.add(jsonObjectRequest);

            return null;
        }

    }

    public String getDolar() {
        return dolar;
    }

    public void setDolar(final String dolar) {
        this.dolar = dolar;
    }
}

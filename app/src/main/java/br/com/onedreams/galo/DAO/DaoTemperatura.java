package br.com.onedreams.galo.DAO;

import android.content.Context;
import android.os.AsyncTask;
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
public class DaoTemperatura {

    private double temperatura = 0;
    private RequestQueue requestQueueTemperatura;
    CheckConnection checkConnection;


    public DaoTemperatura(final String urlJsonObsTemperatura, final int DEFAULT_UPDATE_AND_SHOW_TEMPERATURA, RequestQueue requestQueue, Context context) {

        requestQueueTemperatura = requestQueue;
        checkConnection = new CheckConnection(context);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(checkConnection.isOnline()){

                    new UpdateTemperatura().execute(urlJsonObsTemperatura);

                    try {
                        Thread.sleep(DEFAULT_UPDATE_AND_SHOW_TEMPERATURA);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

    }

    public class UpdateTemperatura extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    params[0], null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                        JSONObject obs1 = response.getJSONObject("main");
                        temperatura = obs1.getDouble(String.valueOf("temp"));

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

            requestQueueTemperatura.add(jsonObjectRequest);

            return null;
        }

    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }
}

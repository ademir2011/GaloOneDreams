package br.com.onedreams.galo.DAO;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.onedreams.galo.Classes.Log;

/**
 * Created by root on 01/05/16.
 */
public class DaoEstatistica {

    RequestQueue requestQueueEstatistica;
    private String urlLog;

    public DaoEstatistica(RequestQueue requestQueue, String urlLog) {
        requestQueueEstatistica = requestQueue;
        this.urlLog = urlLog;
    }

    public void sendLog(final Log log){

        StringRequest request = new StringRequest(Request.Method.POST, urlLog, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("pep", log.getPep());
                parameters.put("date", log.getDate().toString());
                parameters.put("operation", log.getOperation());
                parameters.put("data", log.getData());
                parameters.put("observacoes", log.getObservacoes());

                return parameters;
            }
        };

        requestQueueEstatistica.add(request);

    }

}

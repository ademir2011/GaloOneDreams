package br.com.onedreams.galo.DAO;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import br.com.onedreams.galo.Classes.CheckConnection;

/**
 * Created by root on 07/06/16.
 */
public class DAOsendLogServer {

    CheckConnection checkConnection;

    public DAOsendLogServer(RequestQueue requestQueue, Context context, String url) {

        checkConnection = new CheckConnection(context);

        while(true){

            while( checkConnection.isOnline() ){

                new SendLogServerAsync().execute("");

            }

        }

    }

    public class SendLogServerAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            return null;
        }
    }

}

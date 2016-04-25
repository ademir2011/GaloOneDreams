package br.com.onedreams.galo.Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by root on 22/04/16.
 */
public class CheckConnection {

    Context context;
    public boolean connected = false;

    public CheckConnection(Context context) {

        this.context = context;

    }

    public boolean isOnline(){

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isOnline = (networkInfo != null && networkInfo.isConnected());
        if(!isOnline){
            connected = false;
        } else {
            connected = true;
        }

        return isOnline;

    }

}

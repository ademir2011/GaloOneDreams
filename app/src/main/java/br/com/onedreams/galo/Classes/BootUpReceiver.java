package br.com.onedreams.galo.Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.com.onedreams.galo.Activities.MainActivity;

/**
 * Created by root on 28/04/16.
 */
public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}

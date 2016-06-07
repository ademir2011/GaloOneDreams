package br.com.onedreams.galo.Classes;

import android.content.Context;
import android.net.TrafficStats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by root on 26/05/16.
 */
public class Statistics {

    private long bytesReads;
    private File file;
    private DateFormat dateFormat;
    private String date;
    private FileWriter fileWritter;
    private BufferedWriter bufferWritter;
    private String msgLog;
    private CheckConnection checkConnection;

    public Statistics(Context context) {

        checkConnection = new CheckConnection(context);
        bytesReads = 0;

    }

    public void execute(final String pathSdCard, final int DEFAULT_TIME_WRITE_STATISTICS){

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){

                    while(checkConnection.isOnline()){

                        writeTxtData( pathSdCard, getRxBytesReads() );

                        try {
                            Thread.sleep(DEFAULT_TIME_WRITE_STATISTICS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }
        }).start();

    }

    public String getRxBytesReads(){

        bytesReads = TrafficStats.getTotalRxBytes();

        return String.valueOf( bytesReads );

    }

    public void writeTxtData(String pathSdCard, String data){

        dateFormat      = new SimpleDateFormat("dd MM yyyy, HH:mm:ss");
        date            = dateFormat.format(Calendar.getInstance().getTime());

        file            = new File(pathSdCard, "internetStatistics.txt");
        fileWritter     = null;

        try {

            fileWritter     = new FileWriter(file, true);

            bufferWritter   = new BufferedWriter(fileWritter);

            msgLog          = date + " - " + data + "\n";

            bufferWritter.write(msgLog);
            bufferWritter.close();
            fileWritter.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}

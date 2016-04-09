package br.com.onedreams.galo.Classes;

import io.realm.RealmObject;

/**
 * Created by root on 08/04/16.
 */
public class Config_avisos extends RealmObject {

    private String Aviso;

    public String getAviso() {
        return Aviso;
    }

    public void setAviso(String aviso) {
        Aviso = aviso;
    }
}

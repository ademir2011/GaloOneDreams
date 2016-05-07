package br.com.onedreams.galo.Classes;

import java.util.Date;

/**
 * Created by root on 01/05/16.
 */
public class Log {

    private String pep;
    private Date date;
    private String operation;
    private String data;
    private String observacoes;

    public Log(String pep, Date date, String operation, String data, String observacoes) {
        this.pep = pep;
        this.date = date;
        this.operation = operation;
        this.data = data;
        this.observacoes = observacoes;
    }

    public String getPep() {
        return pep;
    }

    public void setPep(String pep) {
        this.pep = pep;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}

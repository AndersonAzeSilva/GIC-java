/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.megacenter.model;

import java.sql.Timestamp;

/**
 *
 * @author andin
 */
public class Log {
    private int id;
    private String usuario;
    private String acao;
    private String tela;
    private Timestamp datahora;

    public Log() { }

    public Log(int id, String usuario, String acao, String tela, Timestamp datahora) {
        this.id = id;
        this.usuario = usuario;
        this.acao = acao;
        this.tela = tela;
        this.datahora = datahora;
    }

    public Log(String usuario, String acao, String tela) {
        this.usuario = usuario;
        this.acao = acao;
        this.tela = tela;
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getAcao() { return acao; }
    public String getTela() { return tela; }
    public Timestamp getDatahora() { return datahora; }

    public void setId(int id) { this.id = id; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setAcao(String acao) { this.acao = acao; }
    public void setTela(String tela) { this.tela = tela; }
    public void setDatahora(Timestamp datahora) { this.datahora = datahora; }
}

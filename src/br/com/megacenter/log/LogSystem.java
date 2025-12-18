/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.megacenter.log;


import br.com.megacenter.dao.LogDAO;
import br.com.megacenter.model.Log;
/**
 *
 * @author andin
 */


public class LogSystem {

    public static void registrar(String usuario, String acao, String tela) {
        try {
            Log log = new Log(usuario, acao, tela);
            LogDAO dao = new LogDAO();
            dao.registrar(log);
        } catch (Exception e) {
            e.printStackTrace(); // Mostra o erro real
            System.out.println("Erro ao registrar log: " + e.toString());
        }
    }
}
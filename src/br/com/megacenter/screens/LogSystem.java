/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.megacenter.screens;

import br.com.megacenter.dal.ModuloConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author andin
 */
public class LogSystem {

    public static void registrar(String usuario, String acao, String tela) {
        Connection conexao = null;
        PreparedStatement pst = null;

        try {
            conexao = ModuloConexao.conector();

            String sql = "INSERT INTO logs (usuario, acao, tela, sistema) VALUES (?, ?, ?, ?)";

            pst = conexao.prepareStatement(sql);
            pst.setString(1, usuario);
            pst.setString(2, acao);
            pst.setString(3, tela);
            pst.setString(4, "Sistema Java Desktop");

            pst.executeUpdate();

        } catch (Exception e) {
            System.out.println("Erro ao registrar log: " + e);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.megacenter.dao;

import br.com.megacenter.dal.ModuloConexao;
import br.com.megacenter.model.Log;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andin
 */
public class LogDAO {
    private Connection conexao;

    public LogDAO() {
        conexao = ModuloConexao.conector();
    }

    // 🔹 Registrar log
    public void registrar(Log log) throws SQLException {
        String sql = "INSERT INTO logs (usuario, acao, tela, datahora) VALUES (?, ?, ?, NOW())";
        PreparedStatement pst = conexao.prepareStatement(sql);

        pst.setString(1, log.getUsuario());
        pst.setString(2, log.getAcao());
        pst.setString(3, log.getTela());

        pst.executeUpdate();
    }

    // 🔹 Listar todos os logs
    public List<Log> listarTodos() throws SQLException {
        String sql = "SELECT * FROM logs ORDER BY datahora DESC";
        PreparedStatement pst = conexao.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        List<Log> lista = new ArrayList<>();

        while (rs.next()) {
            Log log = new Log(
                    rs.getInt("id"),
                    rs.getString("usuario"),
                    rs.getString("acao"),
                    rs.getString("tela"),
                    rs.getTimestamp("datahora")
            );
            lista.add(log);
        }
        return lista;
    }

    // 🔹 Buscar logs com filtros
    public List<Log> buscarFiltrado(String usuario, String acao, String tela, String dataInicial, String dataFinal) throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT * FROM logs WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (usuario != null && !usuario.isEmpty()) {
            sql.append(" AND usuario LIKE ?");
            params.add("%" + usuario + "%");
        }

        if (acao != null && !acao.isEmpty()) {
            sql.append(" AND acao LIKE ?");
            params.add("%" + acao + "%");
        }

        if (tela != null && !tela.isEmpty()) {
            sql.append(" AND tela LIKE ?");
            params.add("%" + tela + "%");
        }

        if (dataInicial != null && !dataInicial.isEmpty()) {
            sql.append(" AND DATE(datahora) >= ?");
            params.add(dataInicial);
        }

        if (dataFinal != null && !dataFinal.isEmpty()) {
            sql.append(" AND DATE(datahora) <= ?");
            params.add(dataFinal);
        }

        sql.append(" ORDER BY datahora DESC");

        PreparedStatement pst = conexao.prepareStatement(sql.toString());

        // Adiciona parâmetros dinamicamente
        for (int i = 0; i < params.size(); i++) {
            pst.setObject(i + 1, params.get(i));
        }

        ResultSet rs = pst.executeQuery();
        List<Log> lista = new ArrayList<>();

        while (rs.next()) {
            Log log = new Log(
                    rs.getInt("id"),
                    rs.getString("usuario"),
                    rs.getString("acao"),
                    rs.getString("tela"),
                    rs.getTimestamp("datahora")
            );
            lista.add(log);
        }

        return lista;
    }
}

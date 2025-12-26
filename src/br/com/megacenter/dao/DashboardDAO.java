/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.megacenter.dao;

import br.com.megacenter.dal.ModuloConexao;
import br.com.megacenter.model.DashboardData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author andin
 */
public class DashboardDAO {
    
    private Connection conexao;

    public DashboardDAO() {
        conexao = ModuloConexao.conector();
    }

    public DashboardData carregarDashboard() throws SQLException {

        DashboardData data = new DashboardData();

        data.setTotalEquipamentos(getTotal());
        data.setAtivos(getPorStatus("Ativo"));
        data.setDevolvidos(getPorStatus("Devolvido"));
        data.setPendentes(getPorStatus("Pendente"));
        data.setReservas(getPorStatus("Reserva"));
        data.setValorTotal(getValorTotal());

        data.setEquipamentosPorTipo(getPorTipo());
        data.setEquipamentosPorStatus(getPorStatusMapa());

        return data;
    }

    private int getTotal() throws SQLException {
        return executarInt("SELECT COUNT(*) FROM equipamentos");
    }

    private int getPorStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM equipamentos WHERE status=?";
        try (PreparedStatement pst = conexao.prepareStatement(sql)) {
            pst.setString(1, status);
            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    private double getValorTotal() throws SQLException {
        String sql = "SELECT IFNULL(SUM(valor),0) FROM equipamentos";
        return executarDouble(sql);
    }

    private Map<String, Integer> getPorTipo() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT tipo, COUNT(*) FROM equipamentos GROUP BY tipo";
        try (PreparedStatement pst = conexao.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        }
        return map;
    }

    private Map<String, Integer> getPorStatusMapa() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT IFNULL(status,'Não informado'), COUNT(*) FROM equipamentos GROUP BY status";
        try (PreparedStatement pst = conexao.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        }
        return map;
    }

    private int executarInt(String sql) throws SQLException {
        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private double executarDouble(String sql) throws SQLException {
        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getDouble(1);
        }
    }
}

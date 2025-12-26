/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.megacenter.services;

import br.com.megacenter.dal.ModuloConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.sql.*;

/**
 *
 * @author andin
 */
public class HistoricoEquipamentoService {

    public static ResultSet buscarHistorico(
            String equipamento,
            String descricao,
            String tipo,
            String usuario,
            String filial,
            Date dataEntrada,
            Date dataSaida
    ) throws SQLException {

        Connection conexao = ModuloConexao.conector();

        StringBuilder sql = new StringBuilder(
                "SELECT data_evento, acao, usuario_responsavel, "
                + "CONCAT(IFNULL(status_anterior,'-'),' → ',IFNULL(status_novo,'-')) AS status, "
                + "observacao "
                + "FROM historico_equipamentos WHERE 1=1 "
        );

        if (!equipamento.isEmpty()) {
            sql.append(" AND idequipamento = ? ");
        }
        if (!descricao.isEmpty()) {
            sql.append(" AND observacao LIKE ? ");
        }
        if (!tipo.equals(" ")) {
            sql.append(" AND acao = ? ");
        }
        if (!usuario.equals(" ")) {
            sql.append(" AND usuario_responsavel = ? ");
        }
        if (!filial.equals(" ")) {
            sql.append(" AND filial = ? ");
        }
        if (dataEntrada != null) {
            sql.append(" AND data_evento >= ? ");
        }
        if (dataSaida != null) {
            sql.append(" AND data_evento <= ? ");
        }

        sql.append(" ORDER BY data_evento DESC");

        PreparedStatement pst = conexao.prepareStatement(sql.toString());

        int idx = 1;

        if (!equipamento.isEmpty()) {
            pst.setInt(idx++, Integer.parseInt(equipamento));
        }
        if (!descricao.isEmpty()) {
            pst.setString(idx++, "%" + descricao + "%");
        }
        if (!tipo.equals(" ")) {
            pst.setString(idx++, tipo);
        }
        if (!usuario.equals(" ")) {
            pst.setString(idx++, usuario);
        }
        if (!filial.equals(" ")) {
            pst.setString(idx++, filial);
        }
        if (dataEntrada != null) {
            pst.setTimestamp(idx++, new Timestamp(dataEntrada.getTime()));
        }
        if (dataSaida != null) {
            pst.setTimestamp(idx++, new Timestamp(dataSaida.getTime()));
        }

        return pst.executeQuery();
    }

    public static void registrarHistoricoImportacao(
            Connection conexao,
            int idEquipamento,
            String usuario
    ) throws SQLException {

        String sql = "INSERT INTO historico_equipamentos (idequipamento, acao, usuario_responsavel, observacao, data_evento)"
                + "VALUES( ?,'IMPORTAÇÃO EXCEL', ?,'Equipamento importado via planilha', NOW())";

    try (PreparedStatement pst = conexao.prepareStatement(sql)) {
            pst.setInt(1, idEquipamento);
            pst.setString(2, usuario);
            pst.executeUpdate();
        }
    }

    static void registrar(int idEquipamento, String importação, Object object, String cellValue, String usuarioLogado, String equipamento_importado_via_Excel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

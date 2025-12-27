package br.com.megacenter.services;

import br.com.megacenter.dal.ModuloConexao;
import java.sql.*;
import java.util.Date;

public class ImportacoesExcelService {

    public static ResultSet buscarTodas() throws Exception {
        Connection con = ModuloConexao.conector();

        String sql = "SELECT id, data_importacao, usuario, nome_arquivo, total, validas, invalidas, observacao, caminho_arquivo "
                   + "FROM " + DBTables.TBL_IMPORTACOES_EXCEL + " "
                   + "ORDER BY data_importacao DESC";

        PreparedStatement pst = con.prepareStatement(sql);
        return pst.executeQuery();
    }

    public static ResultSet buscar(String usuario, Date inicio, Date fim) throws Exception {
        Connection con = ModuloConexao.conector();

        StringBuilder sb = new StringBuilder(
            "SELECT id, data_importacao, usuario, nome_arquivo, total, validas, invalidas, observacao, caminho_arquivo "
          + "FROM " + DBTables.TBL_IMPORTACOES_EXCEL + " WHERE 1=1 "
        );

        if (usuario != null && !usuario.trim().isEmpty()) sb.append(" AND usuario LIKE ? ");
        if (inicio != null) sb.append(" AND data_importacao >= ? ");
        if (fim != null) sb.append(" AND data_importacao <= ? ");
        sb.append(" ORDER BY data_importacao DESC");

        PreparedStatement pst = con.prepareStatement(sb.toString());
        int idx = 1;

        if (usuario != null && !usuario.trim().isEmpty()) pst.setString(idx++, "%" + usuario.trim() + "%");
        if (inicio != null) pst.setTimestamp(idx++, new Timestamp(inicio.getTime()));
        if (fim != null) pst.setTimestamp(idx++, new Timestamp(fim.getTime()));

        return pst.executeQuery();
    }
}

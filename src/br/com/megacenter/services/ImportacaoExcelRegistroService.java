package br.com.megacenter.services;

import java.sql.*;

public class ImportacaoExcelRegistroService {

    public static Integer criarRegistroInicial(Connection con,
            String nomeArquivo,
            String caminhoArquivo,
            String usuario) throws Exception {

        String sql = "INSERT INTO " + DBTables.TBL_IMPORTACOES_EXCEL
                + " (data_importacao, usuario, nome_arquivo, total, validas, invalidas, observacao, caminho_arquivo) "
                + " VALUES (NOW(), ?, ?, 0, 0, 0, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, usuario);
            pst.setString(2, nomeArquivo);
            pst.setString(3, "Registro criado. Aguardando leitura do preview.");
            pst.setString(4, caminhoArquivo);

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new Exception("Não foi possível obter o ID da importação.");
    }

    public static void atualizarTotais(Connection con,
            Integer idImportacao,
            int total,
            int validas,
            int invalidas,
            String observacao) throws Exception {

        String sql = "UPDATE " + DBTables.TBL_IMPORTACOES_EXCEL
                + " SET total=?, validas=?, invalidas=?, observacao=? "
                + " WHERE id=?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, total);
            pst.setInt(2, validas);
            pst.setInt(3, invalidas);
            pst.setString(4, observacao);
            pst.setInt(5, idImportacao);
            pst.executeUpdate();
        }
    }

    public static void marcarConcluida(Connection con,
            Integer idImportacao,
            String observacaoFinal) throws Exception {

        String sql = "UPDATE " + DBTables.TBL_IMPORTACOES_EXCEL
                + " SET observacao=? WHERE id=?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, observacaoFinal);
            pst.setInt(2, idImportacao);
            pst.executeUpdate();
        }
    }
}

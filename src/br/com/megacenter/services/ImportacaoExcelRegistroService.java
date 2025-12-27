package br.com.megacenter.services;

import java.sql.*;

public class ImportacaoExcelRegistroService {

    public static Integer criarRegistroInicial(
            Connection conexao,
            String nomeArquivo,
            String caminhoArquivo,
            String usuario
    ) throws Exception {

        String sql = "INSERT INTO tblimportacoes_excel "
                + "(data_importacao, usuario, nome_arquivo, caminho_arquivo, total, validas, invalidas, observacao) "
                + "VALUES (NOW(), ?, ?, ?, 0, 0, 0, 'Registro iniciado')";

        try (PreparedStatement pst = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, usuario);
            pst.setString(2, nomeArquivo);
            pst.setString(3, caminhoArquivo);

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        return null;
    }

    public static void atualizarTotais(
            Connection conexao,
            int idImportacao,
            int total,
            int validas,
            int invalidas,
            String observacao
    ) throws Exception {

        String sql = "UPDATE tblimportacoes_excel SET "
                + "total=?, validas=?, invalidas=?, observacao=? "
                + "WHERE id_importacao=?";

        try (PreparedStatement pst = conexao.prepareStatement(sql)) {
            pst.setInt(1, total);
            pst.setInt(2, validas);
            pst.setInt(3, invalidas);
            pst.setString(4, observacao);
            pst.setInt(5, idImportacao);
            pst.executeUpdate();
        }
    }

    public static void marcarFinalizada(
            Connection conexao,
            int idImportacao,
            String observacao
    ) throws Exception {

        String sql = "UPDATE tblimportacoes_excel SET observacao=? WHERE id_importacao=?";

        try (PreparedStatement pst = conexao.prepareStatement(sql)) {
            pst.setString(1, observacao);
            pst.setInt(2, idImportacao);
            pst.executeUpdate();
        }
    }
}

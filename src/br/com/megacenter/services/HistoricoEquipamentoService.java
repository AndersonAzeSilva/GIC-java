package br.com.megacenter.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HistoricoEquipamentoService {

    public static void registrarHistoricoImportacao(
            Connection conexao,
            int idEquipamento,
            String usuario
    ) throws SQLException {

        String sql = "INSERT INTO historico_equipamentos "
                + "(idequipamento, acao, usuario_responsavel, observacao, data_evento) "
                + "VALUES (?, 'IMPORTAÇÃO EXCEL', ?, 'Equipamento importado via planilha', NOW())";

        try (PreparedStatement pst = conexao.prepareStatement(sql)) {
            pst.setInt(1, idEquipamento);
            pst.setString(2, usuario);
            pst.executeUpdate();
        }
    }
}
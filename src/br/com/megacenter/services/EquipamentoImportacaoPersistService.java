package br.com.megacenter.services;

import br.com.megacenter.dal.ModuloConexao;
import br.com.megacenter.dto.EquipamentoImportacaoDTO;

import java.sql.*;
import java.util.List;

public class EquipamentoImportacaoPersistService {

    public static int salvarImportacao(List<EquipamentoImportacaoDTO> lista,
            String usuario,
            Integer idImportacaoAtual) throws Exception {

        int gravados = 0;

        String sql = "INSERT INTO equipamentos ("
                + "codigo_filial, etiqueta_equipamento, tipo, descricao, setor, funcionario, "
                + "valor, quantidade, codigo_empresa, data_cadastrado, data_saida, status, marca, condicoes_equipamento"
                + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = ModuloConexao.conector();
                PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (EquipamentoImportacaoDTO e : lista) {

                if (!e.valido) {
                    continue;
                }

                // alinhado com seu banco:
                pst.setObject(1, e.filial, Types.INTEGER);               // codigo_filial
                pst.setObject(2, e.etiqueta, Types.INTEGER);             // etiqueta_equipamento
                pst.setString(3, e.tipo);                                // tipo
                pst.setString(4, e.descricao);                           // descricao
                pst.setString(5, e.setor);                               // setor
                pst.setString(6, e.funcionario);                         // funcionario
                pst.setString(7, e.valor);                               // valor (varchar)
                pst.setString(8, e.quantidade);                          // quantidade (varchar)
                pst.setString(9, e.empresa);                             // codigo_empresa (varchar)
                pst.setTimestamp(10, e.dataEntrada);                     // data_cadastrado
                pst.setTimestamp(11, e.dataSaida);                       // data_saida
                pst.setString(12, e.status);                             // status
                pst.setString(13, e.marca);                              // marca
                pst.setString(14, e.condicoes);                          // condicoes_equipamento

                pst.executeUpdate();
                gravados++;

                // Se você tiver o HistoricoEquipamentoService, pode registrar aqui:
                // try (ResultSet rs = pst.getGeneratedKeys()) { ... }
            }

            // ✅ marca importação como concluída
            ImportacaoExcelRegistroService.marcarConcluida(
                    con,
                    idImportacaoAtual,
                    "Importação concluída. Registros gravados: " + gravados
            );
        }

        return gravados;
    }
}

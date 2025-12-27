package br.com.megacenter.services;

import br.com.megacenter.dal.ModuloConexao;
import br.com.megacenter.dto.EquipamentoImportacaoDTO;
import java.sql.*;
import java.util.List;

public class EquipamentoImportacaoPersistService {

    public static int salvarImportacao(
            List<EquipamentoImportacaoDTO> lista,
            String usuario
    ) throws Exception {

        int gravados = 0;

        String sql = "INSERT INTO equipamentos ("
                + "etiqueta_equipamento, codigo_filial, tipo, descricao, setor, funcionario,"
                + "valor, quantidade, codigo_empresa, data_cadastrado, data_saida,"
                + "status, marca, condicoes_equipamento"
                + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conexao = ModuloConexao.conector();
             PreparedStatement pst = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            for (EquipamentoImportacaoDTO e : lista) {

                if (!e.valido) continue;

                pst.setObject(1, e.etiqueta);
                pst.setObject(2, e.filial);
                pst.setString(3, e.tipo);
                pst.setString(4, e.descricao);
                pst.setString(5, e.setor);
                pst.setString(6, e.funcionario);
                pst.setString(7, e.valor);
                pst.setString(8, e.quantidade);
                pst.setString(9, e.empresa);
                pst.setTimestamp(10, e.dataEntrada);
                pst.setTimestamp(11, e.dataSaida);
                pst.setString(12, e.status);
                pst.setString(13, e.marca);
                pst.setString(14, e.condicoes);

                pst.executeUpdate();
                gravados++;
            }
        }

        return gravados;
    }
}

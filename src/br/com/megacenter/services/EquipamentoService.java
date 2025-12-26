/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.megacenter.services;

import br.com.megacenter.dal.ModuloConexao;
import java.sql.*;

/**
 *
 * @author andin
 */
public class EquipamentoService {

    public static int inserirEquipamento(
            Connection conexao,
            PreparedStatement pst,
            ResultSet rs,
            Object[] dados
    ) throws SQLException {

        String sql = "INSERT INTO equipamentos(codigo_filial, etiqueta_equipamento, tipo, descricao, setor,funcionario, valor, quantidade, codigo_empresa,status, marca, "
                + "condicoes_equipamento, data_cadastrado)VALUES( ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, NOW())";

        pst = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        for (int i = 0; i < dados.length; i++) {
            pst.setObject(i + 1, dados[i]);
        }

        pst.executeUpdate();

        rs = pst.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }

        return 0;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.megacenter.screens;

import br.com.megacenter.dal.ModuloConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author JANDERSON
 */
public class Dashboard {

    private static Connection conexao = ModuloConexao.conector();

    // Retorna a quantidade total de equipamentos
    public static int getTotalEquipamentos() {
        String sql = "SELECT COUNT(*) FROM equipamentos";
        try (PreparedStatement pst = conexao.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar total de equipamentos: " + e);
        }
        return 0;
    }

    // Retorna a quantidade de equipamentos agrupados por tipo
    public static String getQuantidadePorTipo() {
        StringBuilder resultado = new StringBuilder();
        String sql = "SELECT tipo, COUNT(*) as quantidade FROM equipamentos GROUP BY tipo";

        try (PreparedStatement pst = conexao.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                int quantidade = rs.getInt("quantidade");
                resultado.append(tipo).append(": ").append(quantidade).append("\n");
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar quantidade por tipo: " + e);
        }
        return resultado.toString();
    }

    // Retorna o valor total dos equipamentos
    public static double getValorTotalEquipamentos() {
        String sql = "SELECT SUM(valor) FROM equipamentos";
        try (PreparedStatement pst = conexao.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar valor total dos equipamentos: " + e);
        }
        return 0.0;
    }

    // ================= TOTAL POR STATUS =================
    public static int getTotalPorStatus(String status) {
        String sql = "SELECT COUNT(*) FROM equipamentos WHERE status = ?";
        try (PreparedStatement pst = conexao.prepareStatement(sql)) {

            pst.setString(1, status);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar status " + status + ": " + e);
        }
        return 0;
    }
}

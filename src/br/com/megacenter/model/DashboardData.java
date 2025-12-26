/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.megacenter.model;

import java.util.Map;

/**
 *
 * @author andin
 */
public class DashboardData {

    private int totalEquipamentos;
    private int ativos;
    private int devolvidos;
    private int pendentes;
    private int reservas;
    private double valorTotal;
    private Map<String, Integer> equipamentosPorTipo;
    private Map<String, Integer> equipamentosPorStatus;

    public int getTotalEquipamentos() {
        return totalEquipamentos;
    }

    public void setTotalEquipamentos(int totalEquipamentos) {
        this.totalEquipamentos = totalEquipamentos;
    }

    public int getAtivos() {
        return ativos;
    }

    public void setAtivos(int ativos) {
        this.ativos = ativos;
    }

    public int getDevolvidos() {
        return devolvidos;
    }

    public void setDevolvidos(int devolvidos) {
        this.devolvidos = devolvidos;
    }

    public int getPendentes() {
        return pendentes;
    }

    public void setPendentes(int pendentes) {
        this.pendentes = pendentes;
    }

    public int getReservas() {
        return reservas;
    }

    public void setReservas(int reservas) {
        this.reservas = reservas;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Map<String, Integer> getEquipamentosPorTipo() {
        return equipamentosPorTipo;
    }

    public void setEquipamentosPorTipo(Map<String, Integer> equipamentosPorTipo) {
        this.equipamentosPorTipo = equipamentosPorTipo;
    }

    public Map<String, Integer> getEquipamentosPorStatus() {
        return equipamentosPorStatus;
    }

    public void setEquipamentosPorStatus(Map<String, Integer> equipamentosPorStatus) {
        this.equipamentosPorStatus = equipamentosPorStatus;
    }
}

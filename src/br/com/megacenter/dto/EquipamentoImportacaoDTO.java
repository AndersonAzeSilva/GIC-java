package br.com.megacenter.dto;

import java.sql.Timestamp;

public class EquipamentoImportacaoDTO {

    public Integer etiqueta;
    public Integer filial;

    public String tipo;
    public String descricao;
    public String setor;
    public String funcionario;

    public String valor;
    public String quantidade;
    public String empresa;

    public Timestamp dataEntrada;
    public Timestamp dataSaida;

    public String status;
    public String marca;
    public String condicoes;

    // Validação
    public boolean valido = true;
    public String erro = "";
}

package br.com.megacenter.utils;

import java.util.*;
import org.apache.poi.ss.usermodel.*;

/**
 * =====================================================
 * UTILITÁRIOS PARA IMPORTAÇÃO DE EXCEL
 * Apache POI 5.x
 * =====================================================
 *
 * O objetivo desta classe é:
 * ✅ Validar se o Excel tem as colunas mínimas obrigatórias
 * ✅ Ler valores de células sem dar erro de tipo (NUMERIC vs STRING)
 * ✅ Mapear colunas por nome (evita erro por ordem diferente)
 */
public class ExcelUtils {

    // =====================================================
    // COLUNAS OBRIGATÓRIAS MÍNIMAS (devem existir no Excel)
    // =====================================================
    public static final List<String> COLUNAS_OBRIGATORIAS = Arrays.asList(
            "Etiqueta",
            "Filial",
            "Tipo",
            "Status"
    );

    // =====================================================
    // VALIDAÇÃO DO CABEÇALHO
    // =====================================================
    public static void validarColunas(Sheet sheet) throws Exception {

        Row header = sheet.getRow(0);
        if (header == null) {
            throw new Exception("Planilha não possui cabeçalho (linha 1 vazia).");
        }

        Set<String> colunasExcel = new HashSet<>();
        DataFormatter formatter = new DataFormatter();

        for (Cell cell : header) {
            String valor = normalizar(formatter.formatCellValue(cell));
            if (!valor.isEmpty()) {
                colunasExcel.add(valor);
            }
        }

        for (String coluna : COLUNAS_OBRIGATORIAS) {
            String obrigatoria = normalizar(coluna);
            if (!colunasExcel.contains(obrigatoria)) {
                throw new Exception(
                        "Coluna obrigatória ausente: " + coluna
                        + "\nColunas encontradas no Excel: " + colunasExcel
                );
            }
        }
    }

    // =====================================================
    // LEITURA SEGURA DA CÉLULA (NUNCA ESTOURA NUMERIC/STRING)
    // =====================================================
    public static String getCellValue(Cell cell) {

        if (cell == null) return "";

        DataFormatter formatter = new DataFormatter();
        return normalizar(formatter.formatCellValue(cell));
    }

    // =====================================================
    // MAPEIA COLUNAS PELO NOME DO CABEÇALHO (NOME -> ÍNDICE)
    // =====================================================
    public static Map<String, Integer> mapearColunas(Sheet sheet) throws Exception {

        Row header = sheet.getRow(0);
        if (header == null) {
            throw new Exception("Planilha não possui cabeçalho (linha 1 vazia).");
        }

        Map<String, Integer> mapa = new HashMap<>();
        DataFormatter formatter = new DataFormatter();

        for (Cell cell : header) {
            String nome = normalizar(formatter.formatCellValue(cell));
            if (!nome.isEmpty()) {
                mapa.put(nome, cell.getColumnIndex());
            }
        }

        return mapa;
    }

    // =====================================================
    // BUSCA O ÍNDICE DE UMA COLUNA PELO NOME (COM ERRO CLARO)
    // =====================================================
    public static int getColumnIndex(Map<String, Integer> mapa, String nomeColuna) throws Exception {

        String chave = normalizar(nomeColuna);

        Integer idx = mapa.get(chave);

        if (idx == null) {
            throw new Exception(
                    "Coluna não encontrada no Excel: " + nomeColuna
                    + "\nColunas disponíveis: " + mapa.keySet()
            );
        }

        return idx;
    }

    // =====================================================
    // NORMALIZA STRINGS (evita erro por espaço/variação leve)
    // =====================================================
    private static String normalizar(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " "); // remove espaços duplicados
    }
}

package br.com.megacenter.utils;

import java.util.*;
import org.apache.poi.ss.usermodel.*;

public class ExcelUtils {

    // Colunas mínimas exigidas
    public static final List<String> COLUNAS_OBRIGATORIAS = Arrays.asList(
        "Etiqueta",
        "Filial",
        "Tipo",
        "Status"
    );

    // =========================
    // VALIDA CABEÇALHO
    // =========================
    public static void validarColunas(Sheet sheet) throws Exception {

        Row header = sheet.getRow(0);
        if (header == null) {
            throw new Exception("Planilha não possui cabeçalho.");
        }

        Set<String> colunasExcel = new HashSet<>();
        DataFormatter formatter = new DataFormatter();

        for (Cell cell : header) {
            String valor = formatter.formatCellValue(cell).trim();
            if (!valor.isEmpty()) {
                colunasExcel.add(valor);
            }
        }

        for (String coluna : COLUNAS_OBRIGATORIAS) {
            if (!colunasExcel.contains(coluna)) {
                throw new Exception("Coluna obrigatória ausente: " + coluna);
            }
        }
    }

    // =========================
    // LEITURA SEGURA
    // =========================
    public static String getCellValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}

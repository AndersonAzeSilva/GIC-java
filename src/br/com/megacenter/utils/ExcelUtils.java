package br.com.megacenter.utils;

import java.text.Normalizer;
import java.util.*;
import org.apache.poi.ss.usermodel.*;

public class ExcelUtils {

    // Mínimo exigido pra aceitar a planilha
    public static final List<String> COLUNAS_OBRIGATORIAS = Arrays.asList(
        "Etiqueta", "Filial"
    );

    public static void validarColunas(Sheet sheet) throws Exception {
        Row header = sheet.getRow(0);
        if (header == null) throw new Exception("Planilha não possui cabeçalho.");

        Map<String, Integer> map = mapearCabecalho(header);

        for (String col : COLUNAS_OBRIGATORIAS) {
            if (!map.containsKey(normalize(col))) {
                throw new Exception("Coluna obrigatória ausente: " + col);
            }
        }
    }

    /** Mapeia: "Etiqueta" -> índice da coluna no Excel (com normalização) */
    public static Map<String, Integer> mapearCabecalho(Row header) {
        Map<String, Integer> map = new HashMap<>();
        DataFormatter fmt = new DataFormatter();

        for (Cell c : header) {
            String nome = fmt.formatCellValue(c);
            if (nome == null) continue;
            nome = nome.trim();
            if (nome.isEmpty()) continue;

            map.put(normalize(nome), c.getColumnIndex());
        }
        return map;
    }

    public static String getCellValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    /** Normaliza: remove acentos, espaços, troca por underscore, lower. */
    public static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        n = n.trim().toLowerCase(Locale.ROOT);
        n = n.replace("ç", "c");
        n = n.replaceAll("[\\s\\-]+", "_");
        return n;
    }

    /** Pega célula pelo nome de coluna do cabeçalho (tolerante a ordem). */
    public static Cell getCellByHeader(Row row, Map<String, Integer> headerMap, String headerName) {
        if (row == null) return null;
        Integer idx = headerMap.get(normalize(headerName));
        if (idx == null) return null;
        return row.getCell(idx);
    }
}

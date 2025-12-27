package br.com.megacenter.services;

import br.com.megacenter.dto.EquipamentoImportacaoDTO;
import br.com.megacenter.utils.ExcelUtils;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.ss.usermodel.*;

public class ImportacaoExcelService {

    public static List<EquipamentoImportacaoDTO> lerExcel(File arquivo) throws Exception {

        if (arquivo == null || !arquivo.getName().toLowerCase().endsWith(".xlsx")) {
            throw new Exception("Formato inválido. Utilize apenas arquivos .xlsx");
        }

        List<EquipamentoImportacaoDTO> lista = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(arquivo);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            ExcelUtils.validarColunas(sheet);

            Row header = sheet.getRow(0);
            Map<String, Integer> headerMap = ExcelUtils.mapearCabecalho(header);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                EquipamentoImportacaoDTO e = new EquipamentoImportacaoDTO();

                e.etiqueta    = parseInt(ExcelUtils.getCellByHeader(row, headerMap, "Etiqueta"));
                e.filial      = parseInt(ExcelUtils.getCellByHeader(row, headerMap, "Filial"));

                e.tipo        = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Tipo"));
                e.descricao   = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Descrição"));
                e.setor       = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Setor"));
                e.funcionario = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Usuário"));

                e.valor       = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Valor"));
                e.quantidade  = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Quantidade"));
                e.empresa     = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Empresa"));

                e.dataEntrada = parseDate(ExcelUtils.getCellByHeader(row, headerMap, "Data da Entrada"));
                e.dataSaida   = parseDate(ExcelUtils.getCellByHeader(row, headerMap, "Data da Saída"));

                e.status      = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Status"));
                e.marca       = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Marca"));
                e.condicoes   = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Condições_equipamento"));

                // ✅ Linha totalmente vazia? (ignora)
                if (isLinhaVazia(e)) continue;

                lista.add(e);
            }
        }

        return lista;
    }

    private static boolean isLinhaVazia(EquipamentoImportacaoDTO e) {
        return e.etiqueta == null
                && e.filial == null
                && (e.tipo == null || e.tipo.trim().isEmpty())
                && (e.descricao == null || e.descricao.trim().isEmpty())
                && (e.setor == null || e.setor.trim().isEmpty())
                && (e.funcionario == null || e.funcionario.trim().isEmpty())
                && (e.valor == null || e.valor.trim().isEmpty())
                && (e.quantidade == null || e.quantidade.trim().isEmpty())
                && (e.empresa == null || e.empresa.trim().isEmpty())
                && e.dataEntrada == null
                && e.dataSaida == null
                && (e.status == null || e.status.trim().isEmpty())
                && (e.marca == null || e.marca.trim().isEmpty())
                && (e.condicoes == null || e.condicoes.trim().isEmpty());
    }

    private static Integer parseInt(Cell cell) throws Exception {
        String v = ExcelUtils.getCellValue(cell);
        if (v.isEmpty()) return null;

        try {
            // permite "001", "1", etc.
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException ex) {
            throw new Exception("Valor inteiro inválido: '" + v + "'");
        }
    }

    private static Timestamp parseDate(Cell cell) throws Exception {

        if (cell == null) return null;

        // Caso 1: Data real do Excel
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return new Timestamp(cell.getDateCellValue().getTime());
        }

        // Caso 2: texto dd/MM/yyyy
        String txt = ExcelUtils.getCellValue(cell);
        if (txt.isEmpty()) return null;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        return new Timestamp(sdf.parse(txt.trim()).getTime());
    }
}

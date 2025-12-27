package br.com.megacenter.services;

import br.com.megacenter.dto.EquipamentoImportacaoDTO;
import br.com.megacenter.utils.ExcelUtils;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.ss.usermodel.*;

public class ImportacaoExcelLeituraService {

    public static List<EquipamentoImportacaoDTO> lerExcel(File arquivo) throws Exception {

        List<EquipamentoImportacaoDTO> lista = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(arquivo);
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // valida cabeçalho mínimo
            ExcelUtils.validarColunas(sheet);

            Row header = sheet.getRow(0);
            Map<String, Integer> headerMap = ExcelUtils.mapearCabecalho(header);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // ignora linha totalmente vazia (Etiqueta e Filial vazias)
                String etiquetaTxt = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Etiqueta"));
                String filialTxt = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Filial"));
                if (etiquetaTxt.isEmpty() && filialTxt.isEmpty()) {
                    continue;
                }

                EquipamentoImportacaoDTO e = new EquipamentoImportacaoDTO();

                // alinhado ao banco (equipamentos)
                e.etiqueta = parseInt(ExcelUtils.getCellByHeader(row, headerMap, "Etiqueta"));
                e.filial = parseInt(ExcelUtils.getCellByHeader(row, headerMap, "Filial"));
                e.tipo = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Tipo"));
                e.descricao = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Descrição"));
                e.setor = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Setor"));
                e.funcionario = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Usuário")); // planilha usa "Usuário"
                e.valor = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Valor"));
                e.quantidade = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Quantidade"));
                e.empresa = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Empresa"));

                // datas (planilha: "Data da Entrada", "Data da Saída")
                e.dataEntrada = parseDate(ExcelUtils.getCellByHeader(row, headerMap, "Data da Entrada"));
                e.dataSaida = parseDate(ExcelUtils.getCellByHeader(row, headerMap, "Data da Saída"));

                e.status = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Status"));
                e.marca = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Marca"));
                e.condicoes = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Condições_equipamento"));

                lista.add(e);
            }
        }

        return lista;
    }

    // =======================
    // AUXILIARES
    // =======================
    private static Integer parseInt(Cell cell) {
        String v = ExcelUtils.getCellValue(cell);
        if (v.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Timestamp parseDate(Cell cell) throws Exception {
        if (cell == null) {
            return null;
        }

        // Data real do Excel
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return new Timestamp(cell.getDateCellValue().getTime());
        }

        // Texto (dd/MM/yyyy)
        String txt = ExcelUtils.getCellValue(cell);
        if (txt.isEmpty()) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        return new Timestamp(sdf.parse(txt).getTime());
    }
}

package br.com.megacenter.services;

import br.com.megacenter.dto.EquipamentoImportacaoDTO;
import br.com.megacenter.utils.ExcelUtils;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * ===================================================== SERVIÇO DE LEITURA DE
 * EXCEL (LEGADO / COMPATIBILIDADE)
 * =====================================================
 *
 * ✔ Usa leitura por CABEÇALHO (não por índice fixo) ✔ Compatível com planilhas
 * fora de ordem ✔ Não grava no banco (apenas preview) ✔ Alinhado com
 * ImportacaoExcelService
 *
 * OBS: Este service existe para manter compatibilidade com chamadas antigas do
 * projeto.
 */
public class ImportacaoExcelLeituraService {

    public static List<EquipamentoImportacaoDTO> lerExcel(File arquivo) throws Exception {

        if (arquivo == null || !arquivo.getName().toLowerCase().endsWith(".xlsx")) {
            throw new Exception("Arquivo inválido. Utilize uma planilha .xlsx");
        }

        List<EquipamentoImportacaoDTO> lista = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(arquivo);
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // ✔ valida cabeçalho mínimo (Etiqueta, Filial)
            ExcelUtils.validarColunas(sheet);

            Row header = sheet.getRow(0);
            Map<String, Integer> headerMap = ExcelUtils.mapearCabecalho(header);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                EquipamentoImportacaoDTO e = new EquipamentoImportacaoDTO();

                e.etiqueta = parseInt(ExcelUtils.getCellByHeader(row, headerMap, "Etiqueta"));
                e.filial = parseInt(ExcelUtils.getCellByHeader(row, headerMap, "Filial"));

                e.tipo = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Tipo"));
                e.descricao = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Descrição"));
                e.setor = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Setor"));
                e.funcionario = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Usuário"));

                e.valor = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Valor"));
                e.quantidade = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Quantidade"));
                e.empresa = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Empresa"));

                e.dataEntrada = parseDate(ExcelUtils.getCellByHeader(row, headerMap, "Data da Entrada"));
                e.dataSaida = parseDate(ExcelUtils.getCellByHeader(row, headerMap, "Data da Saída"));

                e.status = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Status"));
                e.marca = ExcelUtils.getCellValue(ExcelUtils.getCellByHeader(row, headerMap, "Marca"));
                e.condicoes = ExcelUtils.getCellValue(
                        ExcelUtils.getCellByHeader(row, headerMap, "Condições_equipamento")
                );

                // ignora linha totalmente vazia
                if (linhaVazia(e)) {
                    continue;
                }

                lista.add(e);
            }
        }

        return lista;
    }

    // =====================================================
    // AUXILIARES
    // =====================================================
    private static boolean linhaVazia(EquipamentoImportacaoDTO e) {
        return e.etiqueta == null
                && e.filial == null
                && isEmpty(e.tipo)
                && isEmpty(e.descricao)
                && isEmpty(e.setor)
                && isEmpty(e.funcionario)
                && isEmpty(e.valor)
                && isEmpty(e.quantidade)
                && isEmpty(e.empresa)
                && e.dataEntrada == null
                && e.dataSaida == null
                && isEmpty(e.status)
                && isEmpty(e.marca)
                && isEmpty(e.condicoes);
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static Integer parseInt(Cell cell) throws Exception {
        String v = ExcelUtils.getCellValue(cell);
        if (v.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException ex) {
            throw new Exception("Valor inteiro inválido: '" + v + "'");
        }
    }

    private static Timestamp parseDate(Cell cell) throws Exception {

        if (cell == null) {
            return null;
        }

        // Data nativa do Excel
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

        return new Timestamp(sdf.parse(txt.trim()).getTime());
    }
}

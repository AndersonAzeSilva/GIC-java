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

        List<EquipamentoImportacaoDTO> lista = new ArrayList<>();

        try (
            FileInputStream fis = new FileInputStream(arquivo);
            Workbook workbook = WorkbookFactory.create(fis)
        ) {

            Sheet sheet = workbook.getSheetAt(0);
            ExcelUtils.validarColunas(sheet);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                EquipamentoImportacaoDTO e = new EquipamentoImportacaoDTO();

                e.etiqueta    = parseInt(row.getCell(0));
                e.filial      = parseInt(row.getCell(1));
                e.tipo        = ExcelUtils.getCellValue(row.getCell(2));
                e.descricao   = ExcelUtils.getCellValue(row.getCell(3));
                e.setor       = ExcelUtils.getCellValue(row.getCell(4));
                e.funcionario = ExcelUtils.getCellValue(row.getCell(5));
                e.valor       = ExcelUtils.getCellValue(row.getCell(6));
                e.quantidade  = ExcelUtils.getCellValue(row.getCell(7));
                e.empresa     = ExcelUtils.getCellValue(row.getCell(8));
                e.dataEntrada = parseDate(row.getCell(9));
                e.dataSaida   = parseDate(row.getCell(10));
                e.status      = ExcelUtils.getCellValue(row.getCell(11));
                e.marca       = ExcelUtils.getCellValue(row.getCell(12));
                e.condicoes   = ExcelUtils.getCellValue(row.getCell(13));

                lista.add(e);
            }
        }

        return lista;
    }

    // =========================
    // AUXILIARES
    // =========================
    private static Integer parseInt(Cell cell) {
        String v = ExcelUtils.getCellValue(cell);
        return v.isEmpty() ? null : Integer.parseInt(v);
    }

    private static Timestamp parseDate(Cell cell) throws Exception {

        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return new Timestamp(cell.getDateCellValue().getTime());
        }

        String txt = ExcelUtils.getCellValue(cell);
        if (txt.isEmpty()) return null;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        return new Timestamp(sdf.parse(txt).getTime());
    }
}

package br.com.megacenter.services;

import br.com.megacenter.dto.EquipamentoImportacaoDTO;
import br.com.megacenter.utils.ExcelUtils;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;

public class ImportacaoExcelService {

    /**
     * Lê o Excel e devolve uma lista para PREVIEW (não salva no banco).
     *
     * MAPEAMENTO (Excel → Banco): 0 Etiqueta → etiqueta_equipamento (int) 1
     * Filial → codigo_filial (int) 2 Tipo → tipo 3 Descrição → descricao 5
     * Setor → setor 6 Usuário → funcionario 7 Valor → valor (varchar no banco)
     * 8 Quantidade→ quantidade (varchar no banco) 9 Empresa → codigo_empresa 10
     * Entrada → data_cadastrado 11 Saída → data_saida 12 Status → status 13
     * Marca → marca 14 Condições→ condicoes_equipamento
     *
     * Obs: Coluna 4 (Sistema Operacional) não existe na tabela equipamentos.
     */
    public static List<EquipamentoImportacaoDTO> lerExcel(File arquivo) throws Exception {

    List<EquipamentoImportacaoDTO> lista = new ArrayList<>();

    try (
        FileInputStream fis = new FileInputStream(arquivo);
        Workbook workbook = WorkbookFactory.create(fis)
    ) {

        Sheet sheet = workbook.getSheetAt(0);
        ExcelUtils.validarColunas(sheet);

        Map<String, Integer> col = ExcelUtils.mapearColunas(sheet);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null) continue;

            EquipamentoImportacaoDTO e = new EquipamentoImportacaoDTO();

            e.etiqueta    = parseInt(row.getCell(col.get("Etiqueta")));
            e.filial      = parseInt(row.getCell(col.get("Filial")));
            e.tipo        = ExcelUtils.getCellValue(row.getCell(col.get("Tipo")));
            e.descricao   = ExcelUtils.getCellValue(row.getCell(col.get("Descrição")));
            e.setor       = ExcelUtils.getCellValue(row.getCell(col.get("Setor")));
            e.funcionario = ExcelUtils.getCellValue(row.getCell(col.get("Usuário")));
            e.valor       = ExcelUtils.getCellValue(row.getCell(col.get("Valor")));
            e.quantidade  = ExcelUtils.getCellValue(row.getCell(col.get("Quantidade")));
            e.empresa     = ExcelUtils.getCellValue(row.getCell(col.get("Empresa")));
            e.dataEntrada = parseDate(row.getCell(col.get("Data da Entrada")));
            e.dataSaida   = parseDate(row.getCell(col.get("Data da Saída")));
            e.status      = ExcelUtils.getCellValue(row.getCell(col.get("Status")));
            e.marca       = ExcelUtils.getCellValue(row.getCell(col.get("Marca")));
            e.condicoes   = ExcelUtils.getCellValue(row.getCell(col.get("Condições_equipamento")));

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
        if (v == null) {
            return null;
        }
        v = v.trim();
        return v.isEmpty() ? null : Integer.parseInt(v);
    }

    private static Timestamp parseDate(Cell cell) throws Exception {

        if (cell == null) {
            return null;
        }

        // Caso 1: Data real do Excel
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return new Timestamp(cell.getDateCellValue().getTime());
        }

        // Caso 2: texto dd/MM/yyyy
        String txt = ExcelUtils.getCellValue(cell);
        if (txt == null) {
            return null;
        }
        txt = txt.trim();
        if (txt.isEmpty()) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        return new Timestamp(sdf.parse(txt).getTime());
    }

    private static boolean linhaVazia(Row row) {
        // checa algumas colunas-chave: etiqueta, filial, tipo, status
        String etiqueta = ExcelUtils.getCellValue(row.getCell(0));
        String filial = ExcelUtils.getCellValue(row.getCell(1));
        String tipo = ExcelUtils.getCellValue(row.getCell(2));
        String status = ExcelUtils.getCellValue(row.getCell(12));
        return etiqueta.isEmpty() && filial.isEmpty() && tipo.isEmpty() && status.isEmpty();
    }

    public static Map<String, Integer> mapearColunas(Sheet sheet) {

        Map<String, Integer> mapa = new HashMap<>();
        Row header = sheet.getRow(0);

        DataFormatter formatter = new DataFormatter();

        for (Cell cell : header) {
            String nome = formatter.formatCellValue(cell).trim();
            mapa.put(nome, cell.getColumnIndex());
        }

        return mapa;
    }
}

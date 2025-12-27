package br.com.megacenter.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer para destacar importações com inválidas.
 * colunaInvalidas: índice da coluna "Inválidas" no JTable.
 */
public class ImportacaoStatusRenderer extends DefaultTableCellRenderer {

    private final int colunaInvalidas;

    public ImportacaoStatusRenderer(int colunaInvalidas) {
        this.colunaInvalidas = colunaInvalidas;
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        int invalidas = 0;

        try {
            Object invObj = table.getValueAt(row, colunaInvalidas);
            if (invObj != null) {
                String s = invObj.toString().trim();
                if (!s.isEmpty()) invalidas = Integer.parseInt(s);
            }
        } catch (Exception ignored) {
        }

        if (isSelected) {
            // seleção padrão
            return c;
        }

        // Linhas com inválidas: fundo suave avermelhado
        if (invalidas > 0) {
            c.setBackground(new Color(255, 235, 235));
            c.setForeground(Color.BLACK);
        } else {
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);
        }

        return c;
    }
}
